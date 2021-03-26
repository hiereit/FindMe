package ddwucom.mobile.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final int UPDATE_CODE = 200;
    public static final String TAG = "sera";

    String apiAddress;

    String query;
    //ListView DB 가져오기
    ListView lvMylist = null;
    DBHelper helper;
    Cursor cursor;
    MyCursorAdapter adapter;
    SQLiteDatabase myDB;

    //Adpater 추가해야됨.
    ArrayList<ItemInfoDTO> resultList;
    MyParserForItemInfo parser;
    NetworkManager networkManager;
    ImageFileManager imageFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*MY LOST LIST의 UI 요소 가져오기*/
        lvMylist = (ListView)findViewById(R.id.lvMylist);

        //helper 선언
        helper = new DBHelper(this);


        //adpater 설정
        adapter = new MyCursorAdapter(this, R.layout.adapter_my_lost, null);
        lvMylist.setAdapter(adapter);

        resultList = new ArrayList();

        lvMylist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
               // parent.getAdapter().getItem(position);
                String name = cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME));
                String place = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PLACE));

                intent.putExtra("name", name);
                intent.putExtra("place", place);
                startActivity(intent);
            }
        });
        lvMylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                Log.d(TAG, "LongClick!!");

                AlertDialog.Builder alertMsg = new AlertDialog.Builder(view.getContext());
                    alertMsg.setTitle("삭제하시겠습니까?");
                    alertMsg.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myDB = helper.getWritableDatabase();

                            Cursor cursor = (Cursor) parent.getAdapter().getItem(pos);
                            int _id = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_ID));
                            String whereClause = DBHelper.COL_ID + "=?";
                            String whereArgs[] = new String[] {String.valueOf(_id)};

                            myDB.delete(DBHelper.TABLE_NAME, whereClause, whereArgs);

                            dialog.dismiss();
                            adapter.notifyDataSetChanged();
                        }
                    });
                    alertMsg.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertMsg.show();
                    return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME, null);

        adapter.notifyDataSetChanged();
        adapter.changeCursor(cursor);
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //cursor 사용 종료
        if (cursor != null) cursor.close();
    }

    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.btn_map:
                intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                break;
            case R.id.btn_search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.btnQuickRegister:
                intent = new Intent(this, RegisterLostItem.class);
                startActivity(intent);
                break;
        }
    }
}