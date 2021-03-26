package ddwucom.mobile.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapActivity  extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "sera";

    final static int PERMISSION_REQ_CODE = 100;
    public static String Address;
    public static String placeTel;
    public static LatLng cameraLocation;

    /*UI*/
    private GoogleMap mGoogleMap;
    private Marker centerMarker;
    private MarkerOptions markerOptions;
    private EditText etFindPlace;

    /*DATA*/
    private PlacesClient placesClient;
    private LocationManager locManager;
    private String bestProvider;

    private AddressResultReceiver addressResultReceiver;
    private LatLngResultReceiver latLngResultReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //        IntentService가 생성하는 결과 수신용 ResultReceiver
        addressResultReceiver = new AddressResultReceiver(new Handler());
        latLngResultReceiver = new LatLngResultReceiver(new Handler());

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //getCurrentLocation();
        etFindPlace = findViewById(R.id.etFindPlace);

        mapLoad();
        //PlaceClient 초기화 >> API에 세부정보를 요청
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        placesClient = Places.createClient(this);

        //MarkerOptions 초기화
        markerOptions = new MarkerOptions();
        markerOptions.title("현재 위치");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        bestProvider = LocationManager.GPS_PROVIDER;
        //LocationManager.
    }
    @Override
    protected void onPause() {
        super.onPause();
        /*위치 조사 종료 - 반드시 추가!*/
        locManager.removeUpdates(locListener);
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnFindPlace:
                startLatLngService();

                break;
            case R.id.btnFindPolice:
                mGoogleMap.clear();
                searchStart(PlaceType.POLICE);
                break;
            case R.id.btnFindSubway:
                mGoogleMap.clear();
                searchStart(PlaceType.SUBWAY_STATION);
                break;
            case R.id.btnFindCVS:
                mGoogleMap.clear();
                searchStart(PlaceType.CONVENIENCE_STORE);
                break;
            case R.id.btnFindBusStation:
                mGoogleMap.clear();
                searchStart(PlaceType.BUS_STATION);
                break;
        }
    }

    private void mapLoad() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    /*-----------------------!!Google Place API관련!!------------------------*/
    /*입력된 유형의 주변 정보를 검색*/
    private void searchStart(String type) {
        Double lat, lng;
//        Location currentLoc = getLastLocation();
//
//        if(!TextUtils.isEmpty(etFindPlace.getText().toString())){
//
//        }else{
//            lat = currentLoc.getLatitude();
//            lng = currentLoc.getLongitude();
//        }
        lat = cameraLocation.latitude;
        lng = cameraLocation.longitude;

        new NRPlaces.Builder().listener(placesListener)
                .key(getResources().getString(R.string.google_api_key))
                .latlng(lat, lng)
                .radius(500)    //반경 100m
                .type(type)     //type은 여러가지가 있다..
                .build()
                .execute();
    }
    PlacesListener placesListener = new PlacesListener() {
        @Override
        public void onPlacesSuccess(final List<Place> places) {
            Log.d(TAG,"Adding Marker");
            runOnUiThread(new Runnable() { //UI 요소에 접근하여야 할 경우,
                @Override
                public void run() {
                    //마커 추가
                    for(noman.googleplaces.Place place : places){
                        markerOptions.title(place.getName());
                        getPlaceDetail(place.getPlaceId());
                        Log.d(TAG, "Map > getPlaceDetail(place.getPlaceId()); 후의 Address 값 : " + Address);
//                        markerOptions.snippet(Address);
                        markerOptions.snippet(placeTel);
                        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                        markerOptions.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        Marker newMarker = mGoogleMap.addMarker(markerOptions);
                        newMarker.setTag(place.getPlaceId());
                        newMarker.showInfoWindow();
                        Log.d(TAG, place.getName() + " : " + place.getPlaceId());
                    }
                }
            });
        }
        @Override
        public void onPlacesFinished() {        }
        @Override
        public void onPlacesFailure(PlacesException e) {        }
        @Override
        public void onPlacesStart() {        }
    };
    /*Place ID 의 장소에 대한 세부정보 획득*/
    private void getPlaceDetail(String placeId) {
        List<com.google.android.libraries.places.api.model.Place.Field> placeFields
                = Arrays.asList(
                        com.google.android.libraries.places.api.model.Place.Field.ID,
                com.google.android.libraries.places.api.model.Place.Field.NAME,
                com.google.android.libraries.places.api.model.Place.Field.PHONE_NUMBER,
                com.google.android.libraries.places.api.model.Place.Field.ADDRESS);
                //com.google.android.libraries.places.api.model.Place.Field.);

        //FetchPlaceRequest 요청을 만든다
        final FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        //placesClient에 요청을 전달
        //placesClient.fetchPlace(request).addOnSuccessListener().addOnFailureListener(); 이걸 늘린게 아래
        placesClient.fetchPlace(request).addOnSuccessListener( new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse response) {
                        //응답 에서 place 객체를 받아온다.
                        com.google.android.libraries.places.api.model.Place place = response.getPlace();
                        Address = place.getAddress();
                        placeTel = place.getPhoneNumber();
                        Log.d(TAG, "Place Founds :" + place.getName());
                        Log.d(TAG, "Phone Founds :" + place.getPhoneNumber());
                        Log.d(TAG, "Address Founds :" + place.getAddress());
                    }
                }
        ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //서버에서 일어난 Api예외사항이면 로그를 찍고, statusCode를 찍어본다.
                        if(e instanceof ApiException){
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.e(TAG, "Place not Found : " + statusCode + " " + e.getMessage());
                        }
                    }
                }
        );
    }

    /*-----------------------!!onMapReady!!------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng currentLoc;
        String loc;
        Log.d(TAG, "Map Ready");

        if (checkPermission()) { //현재 위치 표시해줌
            mGoogleMap.setMyLocationEnabled(true);
        }
        mGoogleMap.setOnMyLocationButtonClickListener(locationButtonClickListener);
        mGoogleMap.setOnMyLocationClickListener(locationClickListener);

        Log.d(TAG, "MapActiviy L: " + getLastLocation());
        if(getLastLocation() != null){
            currentLoc = new LatLng(getLastLocation().getLatitude(), getLastLocation().getLongitude());
        }else{ //getLastLocation()이 null 값이면, 학교로 초기위치 지정
            currentLoc = new LatLng(R.string.init_lat, R.string.init_lng);
        }
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

        loc = String.format("위도 : %6f" + "+\n경도 : %6f", currentLoc.latitude, currentLoc.longitude);

        //마커옵션 지정
        MarkerOptions options = new MarkerOptions();
        options.position(currentLoc);
        options.title("현재 위치");
        //options.snippet(loc);

        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //마커찍기
        centerMarker = mGoogleMap.addMarker(options);
        centerMarker.setPosition(currentLoc);
        centerMarker.showInfoWindow();

        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener(){
            @Override
            public void onCameraMove() {
                LatLng loc = mGoogleMap.getCameraPosition().target;
                //Toast.makeText(MapActivity.this, "lat: " + loc.latitude + "lng: " + loc.longitude, Toast.LENGTH_SHORT).show();
                cameraLocation = loc;
            }
        });

    }

    GoogleMap.OnMyLocationButtonClickListener locationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() { //방사형 현재위치 눌렀을 때
                    //자신의 현재위치로 이동.. 카메라도 이동
                    //Toast.makeText(MapActivity.this, "button Clicked!!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            };
    GoogleMap.OnMyLocationClickListener locationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
//                    Toast.makeText(MapActivity.this,
//                            String.format("현재 위치 : (%f, %f)", location.getLatitude(), location.getLongitude()),
//                            Toast.LENGTH_SHORT).show();
                }
            };
    //OnMpaReadyCallback
    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            /*-------------------------------이벤트 리스너 정의------------------------*/
            //마커의 window 클릭시
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    getPlaceDetail(PlaceType.POLICE);
                }
            });

            //구글맵을 클릭했을 때
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                }
            });

            //구글맵을 롱클릭했을 때
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {

                }
            });
        }


    };
    /* 위치 정보 수신 리스너 생성 */
    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {
            String location;

            LatLng currentLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
            location = String.format("위도 : %6f\n 경도 : %6f", currentLoc.latitude, currentLoc.longitude);

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

            //마커의 옵션지정 (position/ title/ snippet/icon)
            markerOptions.position(currentLoc);
            markerOptions.title("위치TItle");
            markerOptions.snippet(location);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            //마커찍기
            centerMarker = mGoogleMap.addMarker(markerOptions);
            centerMarker.showInfoWindow();
            centerMarker.setPosition(currentLoc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    /*현재 사용 중인 Provider 로부터 전달 받은 최종 위치의 주소 확인 - 권한 확인 필요*/
    private Location getLastLocation(){
        if (checkPermission()) {
            Location lastLocation = locManager.getLastKnownLocation(bestProvider);
//            double latitude = lastLocation.getLatitude();
//            double longitude = lastLocation.getLongitude();
            return lastLocation;
        }
        else
            return null;
    }

    /*위치 조사 시작 - 권한 확인 필요*/
    private void getCurrentLocation() {
        if (checkPermission()) {
            try {
                locManager.requestLocationUpdates(bestProvider, 5000, 10, locListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
    /* 위도/경도 → 주소 변환 ResultReceiver */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String addressOutput = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                if (addressOutput == null) addressOutput = "";
                //etAddress.setText(addressOutput);
            }
        }
    }
    /* 주소 → 위도/경도 변환 IntentService 실행 */
    private void startLatLngService() {
        String address = etFindPlace.getText().toString();
        Intent intent = new Intent(this, FetchLatLngIntentService.class);
        intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, address);
        startService(intent);
    }

    /* 주소 → 위도/경도 변환 ResultReceiver */
    class LatLngResultReceiver extends ResultReceiver {
        public LatLngResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String lat;
            String lng;
            ArrayList<LatLng> latLngList = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                latLngList = (ArrayList<LatLng>) resultData.getSerializable(Constants.RESULT_DATA_KEY);
                if (latLngList == null) {
                    lat = "No Lat";
                    lng = "No LNg";
                } else {
                    LatLng latlng = latLngList.get(0);
                    lat = String.valueOf(latlng.latitude);
                    lng = String.valueOf(latlng.longitude);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

                    MarkerOptions options = new MarkerOptions();
                    options.position(latlng);
                    options.title(etFindPlace.getText().toString());
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    //마커찍기
                    centerMarker = mGoogleMap.addMarker(options);
                    centerMarker.setPosition(latlng);
                    centerMarker.showInfoWindow();

                }
                //etFindPlace.setText("LatLng : " + lat + lng);
            } else {
                etFindPlace.setText(getString(R.string.no_address_found));
                etFindPlace.setText(getString(R.string.no_address_found));
            }
        }
    }


    /*위치 관련 권한 확인 메소드 - 필요한 부분이 여러 곳이므로 메소드로 구성*/
    /*ACCESS_FINE_LOCATION - 상세 위치 확인에 필요한 권한
      ACCESS_COARSE_LOCATION - 대략적 위치 확인에 필요한 권한*/
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQ_CODE);
                return false;
            } else
                return true;
        }
        return false;
    }

    /*권한승인 요청에 대한 사용자의 응답 결과에 따른 수행*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    /*권한을 승인받았을 때 수행하여야 하는 동작 지정*/

                } else {
                    /*사용자에게 권한 제약에 따른 안내*/
                    Toast.makeText(this, "Permissions are not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
