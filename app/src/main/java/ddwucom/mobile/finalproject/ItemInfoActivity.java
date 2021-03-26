package ddwucom.mobile.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ItemInfoActivity extends AppCompatActivity {

    public static final String TAG = "sera";

    String apiAddress;

    /*UI요소*/
    TextView tvDetailTitle, tvDetailName, tvDetailDate_Hor, tvDetailPlace;
    TextView tvDetailUniq, tvDetailID, tvDetailCat;
    TextView tvDetailOrgNm_st, tvDetailOrgTel;
    ImageView imgDetail;

    /*DATA*/
    ItemInfoDTO dto;
    MyParserForItemInfo parser;
    NetworkManager networkManager;
    ImageFileManager imageFileManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*findView*/
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailDate_Hor = findViewById(R.id.tvDetailDate_Hor);
        tvDetailPlace = findViewById(R.id.tvDetailPlace);
        tvDetailUniq = findViewById(R.id.tvDetailUniq);
        tvDetailID = findViewById(R.id.tvDetailID);
        tvDetailCat = findViewById(R.id.tvDetailCat);
        tvDetailOrgNm_st = findViewById(R.id.tvDetailOrgNm_St);
        tvDetailOrgTel = findViewById(R.id.tvDetailOrgTel);
        imgDetail = findViewById(R.id.imgDetail);

        dto = new ItemInfoDTO();
        parser = new MyParserForItemInfo();
        networkManager = new NetworkManager(this);
        imageFileManager = new ImageFileManager(this);

        Intent intent = getIntent();
        String atcId = intent.getStringExtra("atcId");

        Log.d(TAG, "atcID in Oncreate : " + atcId);
        try {
            apiAddress = ApiExplorer(atcId);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "apiAddress in Oncreate : " + apiAddress);

        NetworkAsyncTask dataTask = new NetworkAsyncTask();
        dataTask.execute(apiAddress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 임시 파일 삭제
        imageFileManager.clearTemporaryFiles();
    }
    public void onClick(View v){
        switch (v.getId()){
            //전화 다이얼로그 intent 열기
            case R.id.tvDetailOrgTel:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvDetailOrgTel.getText().toString()));
                startActivity(dialIntent);
                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, ItemInfoDTO> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() { //ui부분 가능... progress dialog 수행
            super.onPreExecute();
            progressDlg = ProgressDialog.show(ItemInfoActivity.this, "Loading..", "세부항목을 가져오는 중...");
        }

        @Override
        protected ItemInfoDTO doInBackground(String... strings) { //network 작업하기
            String address = strings[0];
            String result = null;
            ItemInfoDTO resultDTO;

            // networking
            result = networkManager.downloadContents(address);
            if(result == null)
                return null; // null 값인지 확인

            Log.d(TAG, result);

            //parsing
            resultDTO = parser.parse(result);

            //image저장
            Bitmap bitmap = networkManager.downloadImage(resultDTO.getItemImageFileLink());
            Log.d(TAG, "doInBack : bitmap : " + bitmap);
            Log.d(TAG, "doInBack : result.getItemImageFileLink() : " + resultDTO.getItemImageFileLink());
            resultDTO.setImgBitmap(bitmap);

            return resultDTO;
        }

        @Override
        protected void onPostExecute(ItemInfoDTO result) { //doInBackgroud 에서 전달받은 result
            //image저장
            Log.d(TAG, "onPost : bitmap : " + result.getImgBitmap());
            Log.d(TAG, "onPost : result.getItemImageFileLink() : " + result.getItemImageFileLink());

            if(result.getImgBitmap() != null)
                imgDetail.setImageBitmap(result.getImgBitmap());

            //NULL 값 처리리
           String lstLoc = result.getLostLoc();
            if(TextUtils.isEmpty(lstLoc)) lstLoc = "";

            String lstPlace = result.getLostPlace();
            if(TextUtils.isEmpty(lstPlace)) lstPlace = "";

            String lstSeNm = result.getLostSeName();
            if(TextUtils.isEmpty(lstSeNm)) lstSeNm = "";

            tvDetailTitle.setText(result.getTitle());
            tvDetailName.setText(result.getItemName());
            tvDetailDate_Hor.setText(result.getLostDate() + " / " + result.getLostHor() + "시");
            tvDetailPlace.setText(result.getLostLoc() + " / " + result.getLostPlace() + " / "+ result.getLostSeName());
            tvDetailUniq.setText(result.getUniq());
            tvDetailID.setText(result.getAtcId());
            tvDetailCat.setText(result.getCategory());
            tvDetailOrgNm_st.setText(result.getOrgName() + " (" + result.getItemStatus() + ")");
            tvDetailOrgTel.setText(result.getOrgTel());

            progressDlg.dismiss();          // progress dialog 사라짐
        }
    }
    public String ApiExplorer(String atcId) throws UnsupportedEncodingException {
        Log.d(TAG, "ATC_ID : " + atcId);
        StringBuilder urlBuilder;
        urlBuilder = new StringBuilder(getResources().getString(R.string.api_info_url));
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + getResources().getString(R.string.api_key));
        urlBuilder.append("&" + URLEncoder.encode("ATC_ID", "UTF-8") + "=" + URLEncoder.encode(atcId, "UTF-8")); /*관리ID*/

        return urlBuilder.toString();
    }
}
