package ddwucom.mobile.finalproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterLostItem extends AppCompatActivity {
    final static String TAG = "sera";

    private final static int REQUEST_TAKE_THUMBNAIL = 100;
    private static final int REQUEST_TAKE_PHOTO = 200;

    /*layout 요소*/
    private EditText etRegTitle, etRegName, etRegPlace, etRegDate;
    private Spinner mainCatSpinner, subCatSpinner;
    private ArrayAdapter<String> arrayAdapter;
    private ImageView imgReg;
    private String mCurrentPhotoPath;

    /*DBHelper*/
    DBHelper helper;

    PendingIntent sender = null;
    AlarmManager alarmManager = null;

    String photoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*layout요소 찾아놓기*/
        etRegTitle = findViewById(R.id.etRegTitle);
        etRegName = findViewById(R.id.etRegName);
        etRegPlace = findViewById(R.id.etRegPlace);
        etRegDate = findViewById(R.id.etRegDate);

        mainCatSpinner = findViewById(R.id.mainCatSpinner);
        subCatSpinner = findViewById(R.id.subCatSpinner);

        imgReg = findViewById(R.id.imgReg);

        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                (String[])getResources().getStringArray(R.array.spinner_mainCat));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainCatSpinner.setAdapter(arrayAdapter);

        initSubCateorySpinner();

        //helper 선언
        helper = new DBHelper(this);


        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        createNotificationChannel();    //앱을 실행하자 마자 채널 생성

        imgReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

                Log.d(TAG, "File : " + mCurrentPhotoPath);
            }
        });
    }
    public void onClick(View v){
        Intent intent = null;
        SQLiteDatabase myDB = helper.getWritableDatabase();

        switch(v.getId()){
            //등록완료 버튼을 누르면 알람 생성 1분 후
            case R.id.btnRegComplete:
                //DB 데이터 삽입 작업 수행
                String mainCat, subCat;
                String title = etRegTitle.getText().toString();
                String name = etRegName.getText().toString();
                String place = etRegPlace.getText().toString();
                String date = etRegDate.getText().toString();
                if(mainCatSpinner.getSelectedItemPosition() == 0){
                    Toast.makeText(this, "대분류를 선택해주세요", Toast.LENGTH_SHORT).show();
                    mainCat = "대분류 없음";
                    subCat = "";
                } else{
                  if(subCatSpinner.getSelectedItem() != null){
                      mainCat = mainCatSpinner.getSelectedItem().toString();
                      subCat = subCatSpinner.getSelectedItem().toString();
                  }else{
                      mainCat = mainCatSpinner.getSelectedItem().toString();
                      subCat = "소분류 없음";
                  }

                }
                String imgPath = photoPath;

                Log.d(TAG, "Regi Activity) imgPath : " +  imgPath);
                ContentValues row = new ContentValues();

                row.put(DBHelper.COL_TITLE, title);
                row.put(DBHelper.COL_NAME, name);
                row.put(DBHelper.COL_PLACE, place);
                row.put(DBHelper.COL_LOST_DATE, date);
                row.put(DBHelper.COL_MAIN_CATEGORY, mainCat);
                row.put(DBHelper.COL_SUB_CATEGORY, subCat);
                row.put(DBHelper.COL_FILE_PATH, imgPath);

                myDB.insert(DBHelper.TABLE_NAME, null, row);

                /*알람 설정*/
                intent  = new Intent(this, RepeatReceiver.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                sender = PendingIntent.getBroadcast(this, 0, intent, 0);

                //정확도가 떨어지는 반복 알람 설정 시
			    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, sender);
                break;
        }
        Toast.makeText(this, "데이터 삽입 완료", Toast.LENGTH_SHORT).show();
        helper.close();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, RepeatReceiver.class);
        sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (sender != null) alarmManager.cancel(sender);
    }

    private void createNotificationChannel() { //채널 생성 코드
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);       // strings.xml 에 채널명 기록
            String description = getString(R.string.channel_description);       // strings.xml에 채널 설명 기록
            int importance = NotificationManager.IMPORTANCE_DEFAULT;    // 알림의 우선순위 지정
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);    // CHANNEL_ID 지정
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);  // 채널 생성
            notificationManager.createNotificationChannel(channel);
        }
    }
    /*--------------------------사진에 관한 --------------------------------------------------*/
    /*현재 시간 정보를 사용하여 파일 정보 생성*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    /*원본 사진 파일 저장*/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;

            try {
                photoFile = createImageFile();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(photoFile != null){

                Uri photoUri = FileProvider.getUriForFile(this,
                        "ddwucom.mobile.finalproject.fileprovider",
                        photoFile);
                photoPath = photoUri.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imgReg.getWidth();
        int targetH = imgReg.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imgReg.setImageBitmap(bitmap);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_THUMBNAIL && resultCode == RESULT_OK) {
            Bundle extra = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extra.get("data");

            imgReg.setImageBitmap(imageBitmap);

        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
        //Log.d
    }
    public void initSubCateorySpinner(){
        mainCatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch(pos){
                    case 0:
                        subCatSpinner.setAdapter(null);
                        break;
                    case 1:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_bag);
                        break;
                    case 2:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_acc);
                        break;
                    case 3:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_book);
                        break;
                    case 4:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_doc);
                        break;
                    case 5:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_indust);
                        break;
                    case 6:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_shoppingbag);
                        break;
                    case 7:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_sports);
                        break;
                    case 8:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_musical);
                        break;
                    case 9:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_finance);
                        break;
                    case 10:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_cloth);
                        break;
                    case 11:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_car);
                        break;
                    case 12:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_electronic);
                        break;
                    case 13:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_wallet);
                        break;
                    case 14:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_certifi);
                        break;
                    case 15:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_com);
                        break;
                    case 16:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_card);
                        break;
                    case 17:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_cash);
                        break;
                    case 18:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_phone);
                        break;
                    case 19:
                        setSubCarSpinnerAdapterItem(R.array.spinner_mCat_etc);
                        break;
                    case 20:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setSubCarSpinnerAdapterItem(int arr_res){
        if(arrayAdapter != null){
            subCatSpinner.setAdapter(null);
            arrayAdapter = null;
        }
        if(mainCatSpinner.getSelectedItemPosition() > 1){

        }
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                (String[])getResources().getStringArray(arr_res));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subCatSpinner.setAdapter(arrayAdapter);
    }
}

