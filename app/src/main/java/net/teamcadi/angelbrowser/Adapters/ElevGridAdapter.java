package net.teamcadi.angelbrowser.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.teamcadi.angelbrowser.Activity_Back.data.ElevatorPic;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.PhotoViewActivity;
import net.teamcadi.angelbrowser.Activity_Front.data.ElevPicData;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by haams on 2018-01-13.
 */

public class ElevGridAdapter extends BaseAdapter {

    private ArrayList<ElevPicData> elevPicList; // GridView에 들어갈 사진 데이터 리스트
    private Context context;
    private Network network;
    private SharedPrefStorage sharedPrefStorage;
    private static final String serverUrl = "http://13.125.93.27:2721";

    public ElevGridAdapter(ArrayList<ElevPicData> elevPicList, Context context) {
        this.elevPicList = elevPicList;
        this.context = context;
        network = Network.getNetworkInstance();
        sharedPrefStorage = new SharedPrefStorage(context);
    }

    @Override
    public int getCount() {
        return elevPicList.size();
    }

    @Override
    public Object getItem(int position) {
        return elevPicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder {
        ImageView elevPicView;
        TextView elevTxtSpot;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.elevator_grid_view_items, parent, false);
        }
        final Holder holder = new Holder();
        holder.elevPicView = (ImageView) convertView.findViewById(R.id.elev_Grid_ImgViews);
        holder.elevTxtSpot = (TextView) convertView.findViewById(R.id.elev_Grid_Item_Spot);

        Glide.with(context).asBitmap().load(elevPicList.get(position).getPhoto())
                .into(holder.elevPicView);

        holder.elevPicView.setOnClickListener(new elevImgClickListener(position));

        holder.elevTxtSpot.setText(elevPicList.get(position).getTitle());

        return convertView;
    }

    private class elevImgClickListener implements View.OnClickListener {
        int position;
        public elevImgClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            showLargerThanBefore(position);
        }
    }

    private void showLargerThanBefore(int position) {
        Intent picIntent = new Intent(context,PhotoViewActivity.class);
        picIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        picIntent.putExtra("elevPic",elevPicList.get(position).getPhoto());
        context.startActivity(picIntent); // 스택 줄이기
    }
}
