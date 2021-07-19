package net.teamcadi.angelbrowser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.teamcadi.angelbrowser.Activity_Front.data.ElevatorInfoData;
import net.teamcadi.angelbrowser.R;

import java.util.ArrayList;

/**
 * Created by haams on 2017-12-26.
 */

public class ElevatorInfoAdapter extends BaseAdapter {
    private ArrayList<ElevatorInfoData> elvDataList;
    private Context context;

    public ElevatorInfoAdapter(ArrayList<ElevatorInfoData> elvDataList, Context context) {
        this.elvDataList = elvDataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return elvDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return elvDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder {
        TextView elvName;
        TextView elvCoverage;
        TextView elvLocation;
        TextView elvNumber;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = (LayoutInflater.from(context)).inflate(R.layout.elevator_info_items, parent, false);
        }

        Holder holder = new Holder();

        holder.elvName = (TextView) convertView.findViewById(R.id.elevator_name);
        holder.elvCoverage = (TextView) convertView.findViewById(R.id.elevator_coverage);
        holder.elvLocation = (TextView) convertView.findViewById(R.id.elevator_location);
        holder.elvNumber = (TextView) convertView.findViewById(R.id.elv_number_id);

        holder.elvName.setText(elvDataList.get(position).getElvName());
        holder.elvCoverage.setText(elvDataList.get(position).getElvCoverage());
        holder.elvLocation.setText(elvDataList.get(position).getElvLocation());
        holder.elvNumber.setText(elvDataList.get(position).getNum());

        holder.elvNumber.setTextSize(10);
        holder.elvName.setTextSize(10);
        holder.elvCoverage.setTextSize(12);
        holder.elvLocation.setTextSize(12);

        return convertView;
    }
}
