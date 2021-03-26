package ddwucom.mobile.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyItemAdapter extends BaseAdapter {
    public static final String TAG = "sera";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<ItemSearchInfoDTO> list;
    private NetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;

    public MyItemAdapter(Context context, int resource, ArrayList<ItemSearchInfoDTO> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        networkManager = new NetworkManager(context);
        imageFileManager = new ImageFileManager(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()  {
        return list.size();
    }

    @Override
    public ItemSearchInfoDTO getItem(int position)  {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView with position : " + position);
        View view = convertView;
        ViewHolder viewHolder = null;

        if(view == null){
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvSearchTitle = view.findViewById(R.id.tvSearchTitle);
            viewHolder.tvSearchName = view.findViewById(R.id.tvSearchName);
            viewHolder.tvSearchId = view.findViewById(R.id.tvSearchId);
            viewHolder.tvSearchPlace = view.findViewById(R.id.tvSearchPlace);
            viewHolder.tvSearchDate = view.findViewById(R.id.tvSearchDate);
            viewHolder.tvSearchCategory = view.findViewById(R.id.tvSearchCategory);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        ItemSearchInfoDTO dto = list.get(position);

        viewHolder.tvSearchTitle.setText(dto.getTitle());
        viewHolder.tvSearchName.setText(dto.getItemName());
        viewHolder.tvSearchId.setText(dto.getAtcId());
        viewHolder.tvSearchPlace.setText(dto.getLostPlace());
        viewHolder.tvSearchDate.setText(dto.getLostDate());
        viewHolder.tvSearchCategory.setText(dto.getCategory());

        return view;
    }
    public void setList(ArrayList<ItemSearchInfoDTO> list) {
        this.list = list;
        notifyDataSetChanged();
    }
     // ※ findViewById() 호출 감소를 위해 필수로 사용할 것
    static class ViewHolder {
        public TextView tvSearchTitle = null;
        public TextView tvSearchName = null;
         public TextView tvSearchId = null;
        public TextView tvSearchPlace = null;
        public TextView tvSearchDate = null;
         public TextView tvSearchCategory = null;
    }
}
