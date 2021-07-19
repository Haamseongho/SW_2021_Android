package net.teamcadi.angelbrowser.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.teamcadi.angelbrowser.Activity_Back.data.ChatMessage;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.data.SubwayInfoData;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by haams on 2017-12-26.
 */

public class SubwayInfoAdapter extends BaseAdapter {

    private static final String TAG = "SubwayInfoAdapter";
    private Context context;
    private ArrayList<SubwayInfoData> subwayInfoList;
    private Network network;
    private SharedPrefStorage pref;

    public SubwayInfoAdapter(Context context, ArrayList<SubwayInfoData> subwayInfoList) {
        this.context = context;
        this.subwayInfoList = subwayInfoList;
        pref = new SharedPrefStorage(context);
    }

    public SubwayInfoAdapter(Context context) {
        this.context = context;
        subwayInfoList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return subwayInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return subwayInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder {
        TextView entrcNm;
        TextView infraInfo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = (LayoutInflater.from(context)).inflate(R.layout.subway_info_items, parent, false);
        }

        final Holder holder = new Holder();
        holder.entrcNm = (TextView) convertView.findViewById(R.id.entrc_number);
        holder.infraInfo = (TextView) convertView.findViewById(R.id.infra_info);
        holder.entrcNm.setText(subwayInfoList.get(position).getEntrcNm());
        holder.infraInfo.setText(subwayInfoList.get(position).getInfraInfo());
        holder.entrcNm.setTextSize(14);
        holder.infraInfo.setTextSize(10);

        for(int i=0;i<subwayInfoList.size();i++){
            Log.i(TAG, String.valueOf(subwayInfoList.get(i)));
        }
        return convertView;
    }
}
