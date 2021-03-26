package ddwucom.mobile.finalproject;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import 	android.content.ContentResolver;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class MyCursorAdapter extends CursorAdapter {
    public static final String TAG = "sera";
    final int PERMISSION_REQ_CODE = 100;    // Permission 요청 코드

    LayoutInflater inflater;
    int layout;
    private NetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;
    ContentResolver resolver;

    public MyCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;

        imageFileManager = new ImageFileManager(context);
        networkManager = new NetworkManager(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) v.getTag();

        String imgURL = cursor.getString(cursor.getColumnIndex(DBHelper.COL_FILE_PATH));
        String mainCat = cursor.getString(cursor.getColumnIndex(DBHelper.COL_MAIN_CATEGORY));
        String subCat = cursor.getString(cursor.getColumnIndex(DBHelper.COL_SUB_CATEGORY));
        Log.d(TAG,"CursorAdapter1 > Category " + mainCat +  subCat);
        if(TextUtils.isEmpty(mainCat) || TextUtils.isEmpty(subCat)){
            if(TextUtils.isEmpty(mainCat))
                mainCat = "대분류";
            if(TextUtils.isEmpty(subCat))
                subCat = "소분류";
        }
        Log.d(TAG,"CursorAdapter2 > Category " + mainCat +  subCat);
        if(holder.tvTitle == null){
            holder.tvTitle = v.findViewById(R.id.tvTitle);
            holder.tvName = v.findViewById(R.id.tvName);
            holder.tvPlace = v.findViewById(R.id.tvPlace);
            holder.tvDate = v.findViewById(R.id.tvDate);
            holder.tvCategory = v.findViewById(R.id.tvCategory);
            holder.imgLostItem = v.findViewById(R.id.imgLostItem);
        }
        //TextView 설정
        holder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_TITLE)));
        holder.tvName.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME)));
        holder.tvPlace.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PLACE)));
        holder.tvDate.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_LOST_DATE)));
        holder.tvCategory.setText( mainCat + " > " + subCat);

        //ImageView 설정
        Log.d(TAG, "cursorAdap) imgLostItem Link ID > " + cursor.getString(cursor.getColumnIndex(DBHelper.COL_ID)) + " : " + imgURL);
        if(imgURL != null) {
            holder.imgLostItem.setImageBitmap(imageFileManager.getBitmapFromExternal(imageFileManager.getFileNameFromUrl(imgURL)));
            //holder.imgLostItem.setImageBitmap(imageFileManager.getBitmapFromExternal("JPEG_20201222_151722_4386257652103148292.jpg"));
        }else { //Default 사진 설정
           holder.imgLostItem.setImageResource(R.drawable.ic_no_photo);
        }
    }
    static class ViewHolder {
        public ViewHolder(){
            tvTitle = null;
            tvName = null;
            tvPlace = null;
            tvDate = null;
            tvCategory = null;
            imgLostItem = null;
        }
        TextView tvTitle;
        TextView tvName;
        TextView tvDate;
        TextView tvCategory;
        TextView tvPlace;
        ImageView imgLostItem;
    }
}
