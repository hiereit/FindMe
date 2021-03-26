package ddwucom.mobile.finalproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "sera";

    TextView tvIsFound, tvIsFoundSemi;
    EditText etSearchPrdtName, etSearchPlace;
    ListView lvLostItemList;
    String apiAddress;

    MyItemAdapter adapter;
    ArrayList<ItemSearchInfoDTO> resultList;
    MyParserForSearch parser;
    NetworkManager networkManager;
    ItemSearchInfoDTO refDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tvIsFound = findViewById(R.id.tvIsFound);
        tvIsFoundSemi = findViewById(R.id.tvIsFoundSemi);
        etSearchPlace = findViewById(R.id.etSearchPlace);
        etSearchPrdtName = findViewById(R.id.etSearchPrdtName);
        lvLostItemList = findViewById(R.id.lvLostItemList);


        resultList = new ArrayList();
        adapter = new MyItemAdapter(this, R.layout.adapter_item_search, resultList);
        lvLostItemList.setAdapter(adapter);

        Log.d(TAG, "apiAddress : " + apiAddress);

        parser = new MyParserForSearch();
        networkManager = new NetworkManager(this);

        /*MainActivity로 부터 날아온 정보로 검색하기*/
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String place = intent.getStringExtra("place");
        Log.d(TAG, "intent로 넘어온 name과 place : " + name + place);

        if(name != null)
            etSearchPrdtName.setText(name);
        if(place != null)
            etSearchPlace.setText(place);

        //리스트뷰 아이템 클릭 이벤트 리스너 (클릭하면 상세정보 조회가능)
        lvLostItemList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "lv Clicked!!" + position);

                refDTO = (ItemSearchInfoDTO) parent.getAdapter().getItem(position);
                Log.d(TAG, "refDTO.getAtcId() : " + refDTO.getAtcId());

                Intent intent = new Intent(SearchActivity.this, ItemInfoActivity.class);
                intent.putExtra("atcId", refDTO.getAtcId());
                startActivity(intent);
            }
        });
    }


    public void onClick(View v) throws UnsupportedEncodingException {
        switch(v.getId()){
            case R.id.btn_search_item:
                //SearchActivity를 실행하였을 때 기본 검색
                if(TextUtils.isEmpty(etSearchPlace.getText().toString()) && TextUtils.isEmpty(etSearchPlace.getText().toString())){
                    Toast.makeText(this, "항목입력후 다시 검색하세요", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                   apiAddress = ApiExplorer();
                }
                Log.d(TAG, "요청 주소 : " + apiAddress);

                NetworkAsyncTask dataTask = new NetworkAsyncTask();
                dataTask.execute(apiAddress);

                break;
        }

    }
    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() { //ui부분 가능... progress dialog 수행
            super.onPreExecute();
            progressDlg = ProgressDialog.show(SearchActivity.this, "Loading..", "항목을 가져오는 중입니다.");
        }

        @Override
        protected String doInBackground(String... strings) { //network 작업하기
            String address = strings[0];
            String result = null;
            // networking
            result = networkManager.downloadContents(address);
            if(result == null)
                return "Error!!"; // null 값인지 확인

            Log.d(TAG, result);

            //parsing
            resultList = parser.parse(result);
            Log.d(TAG, "XML Result :" + result);

            return result;
        }


        @Override
        protected void onPostExecute(String result) { //doInBackgroud 에서 전달받은 result

            adapter.setList(resultList);    // Adapter 에 결과 List 를 설정 후 notify
            Log.d(TAG, "resultList int SearchActv Szie: " + resultList.size());
            if(resultList.size() > 0){//ObjectUtils.isEmpty(resultList)
                tvIsFound.setVisibility(View.INVISIBLE);
                tvIsFoundSemi.setVisibility(View.INVISIBLE);
                Log.d(TAG, "VISIBLE!!");
            }else{
                tvIsFound.setVisibility(View.VISIBLE);
                tvIsFoundSemi.setVisibility(View.VISIBLE);
                Log.d(TAG, "INVISIBLE!!");
            }
            progressDlg.dismiss();          // progress dialog 사라짐
        }
    }
    public String ApiExplorer() throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder(getResources().getString(R.string.api_search_url));
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + getResources().getString(R.string.api_key));
        if(!TextUtils.isEmpty(etSearchPlace.getText().toString())) {
            urlBuilder.append("&" + URLEncoder.encode("LST_PLACE", "UTF-8") + "=" + URLEncoder.encode(etSearchPlace.getText().toString(), "UTF-8"));
        }
        if(!TextUtils.isEmpty(etSearchPrdtName.getText().toString())) {
            urlBuilder.append("&" + URLEncoder.encode("LST_PRDT_NM","UTF-8") + "=" + URLEncoder.encode(etSearchPrdtName.getText().toString(), "UTF-8"));
        }
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("30", "UTF-8")); /*목록 건수*/

        return urlBuilder.toString();
    }
}
