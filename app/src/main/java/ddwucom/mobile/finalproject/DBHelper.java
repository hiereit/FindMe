package ddwucom.mobile.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "my_item_db";
    public final static String TABLE_NAME = "my_item_table";

    //칼럼 선언
    public final static String COL_ID = "_id";
    public final static String COL_TITLE ="title";
    public final static String COL_NAME ="name";
    public final static String COL_PLACE ="place";
    public final static String COL_LOST_DATE ="lost_date";
    public final static String COL_MAIN_CATEGORY ="mainCat";
    public final static String COL_SUB_CATEGORY ="subCat";
    public final static String COL_FILE_PATH ="filePath";

    //생성자
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_NAME + " TEXT, " + COL_PLACE + " TEXT, " + COL_TITLE + " TEXT, " + COL_LOST_DATE
                + " TEXT, " + COL_MAIN_CATEGORY + " TEXT, " + COL_SUB_CATEGORY + " TEXT, "+ COL_FILE_PATH + " TEXT);");

        //샘플 데이터
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '아이폰8', '월곡역 지하철', " +
                "'아이폰 8 (블랙) 핸드폰 분실', '2020-12-01', '휴대폰', '아이폰', null);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '아이폰8', '백주년 기념관', " +
                "'전공서적 분실', '2020-11-21', '도서용품', '학습서적', null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
