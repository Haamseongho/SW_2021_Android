package net.teamcadi.angelbrowser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.teamcadi.angelbrowser.R;

import java.util.List;

/**
 * Created by haams on 2017-12-15.
 */

public class SearchAdapter extends BaseAdapter{

    private Context context;
    private List<String> list;
    ViewHolder viewHolder;

    public SearchAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        private TextView subwayNm;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.subway_listview_items,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.subwayNm = (TextView)convertView.findViewById(R.id.filtered_subName);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = ((ViewHolder)convertView.getTag());
        }
        viewHolder.subwayNm.setText(list.get(position));

        return convertView;
    }
}
