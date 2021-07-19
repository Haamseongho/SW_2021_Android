package net.teamcadi.angelbrowser.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.StationInfoActivity;
import net.teamcadi.angelbrowser.Activity_Front.data.ClosestSubData;
import net.teamcadi.angelbrowser.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by haams on 2018-01-20.
 */

public class ClosestSubwayAdapter extends RecyclerView.Adapter<ClosestSubwayAdapter.ViewHolder> {

    private static final String TAG = ClosestSubwayAdapter.class.getSimpleName();
    private ArrayList<ClosestSubData> closestSubDataArrayList;
    private Context context;
    private Network network;

    public ClosestSubwayAdapter(ArrayList<ClosestSubData> closestSubDataArrayList, Context context) {
        this.closestSubDataArrayList = closestSubDataArrayList;
        this.context = context;
        network = Network.getNetworkInstance();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView closestSubName;
        TextView closestSubLine;
        TextView closestSubLine2;
        TextView closestSubLine3;
        TextView closestSubLine4;

        public ViewHolder(View itemView) {
            super(itemView);

            closestSubName = (TextView) itemView.findViewById(R.id.closest_subway_name);
            closestSubLine = (TextView) itemView.findViewById(R.id.closest_subway_line);
            closestSubLine2 = (TextView) itemView.findViewById(R.id.closest_subway_line2);
            closestSubLine3 = (TextView) itemView.findViewById(R.id.closest_subway_line3);
            closestSubLine4 = (TextView) itemView.findViewById(R.id.closest_subway_line4);

        }
    }

    @Override
    public ClosestSubwayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.closest_subway_list_items, parent, false);
        ViewHolder vh = new ClosestSubwayAdapter.ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ClosestSubwayAdapter.ViewHolder holder, int position) {
        holder.closestSubName.setText(closestSubDataArrayList.get(position).getSubname());
        holder.closestSubLine.setText(closestSubDataArrayList.get(position).getSubline());
        holder.closestSubLine2.setText(closestSubDataArrayList.get(position).getSubline2());
        holder.closestSubLine3.setText(closestSubDataArrayList.get(position).getSubline3());
        holder.closestSubLine4.setText(closestSubDataArrayList.get(position).getSubline4());

        switch (closestSubDataArrayList.get(position).getSubline()) {
            case "1호선":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line1);
                break;
            case "2호선":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line2);
                break;
            case "3호선":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line3);
                break;
            case "4호선":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line4);
                break;
            case "5호선":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line5);
                break;
            case "6호선":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line6);
                break;
            case "7호선":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line7);
                break;
            case "8호선":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line8);
                break;
            case "9호선":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line9);
                break;
            case "경의중앙":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line10);
                break;
            case "분당":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line11);
                break;
            case "공항철도":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line12);
                break;
            case "신분당":
                holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line13);
                break;
        }
        if (!(holder.closestSubLine2.getText().toString().equals(""))) {
            holder.closestSubLine2.setVisibility(View.VISIBLE);
            switch (closestSubDataArrayList.get(position).getSubline2()) {
                case "1호선":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line1);
                    break;
                case "2호선":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line2);
                    break;
                case "3호선":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line3);
                    break;
                case "4호선":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line4);
                    break;
                case "5호선":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line5);
                    break;
                case "6호선":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line6);
                    break;
                case "7호선":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line7);
                    break;
                case "8호선":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line8);
                    break;
                case "9호선":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line9);
                    break;
                case "경의중앙":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line10);
                    break;
                case "분당":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line11);
                    break;
                case "공항철도":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line12);
                    break;
                case "신분당":
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line13);
                    break;
            }
        }
        if (!(holder.closestSubLine3.getText().toString().equals(""))) {
            holder.closestSubLine3.setVisibility(View.VISIBLE);
            switch (closestSubDataArrayList.get(position).getSubline3()) {
                case "1호선":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line1);
                    break;
                case "2호선":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line2);
                    break;
                case "3호선":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line3);
                    break;
                case "4호선":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line4);
                    break;
                case "5호선":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line5);
                    break;
                case "6호선":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line6);
                    break;
                case "7호선":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line7);
                    break;
                case "8호선":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line8);
                    break;
                case "9호선":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line9);
                    break;
                case "경의중앙":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line10);
                    break;
                case "분당":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line11);
                    break;
                case "공항철도":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line12);
                    break;
                case "신분당":
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line13);
                    break;
            }
        }
        if (!(holder.closestSubLine4.getText().toString().equals(""))) {
            holder.closestSubLine4.setVisibility(View.VISIBLE);
            switch (closestSubDataArrayList.get(position).getSubline4()) {
                case "1호선":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line1);
                    break;
                case "2호선":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line2);
                    break;
                case "3호선":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line3);
                    break;
                case "4호선":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line4);
                    break;
                case "5호선":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line5);
                    break;
                case "6호선":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line6);
                    break;
                case "7호선":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line7);
                    break;
                case "8호선":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line8);
                    break;
                case "9호선":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line9);
                    break;
                case "경의중앙":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line10);
                    break;
                case "분당":
                    holder.closestSubLine.setBackgroundResource(R.drawable.round_circle_line11);
                    holder.closestSubLine2.setBackgroundResource(R.drawable.round_circle_line11);
                    holder.closestSubLine3.setBackgroundResource(R.drawable.round_circle_line11);
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line11);
                    break;
                case "공항철도":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line12);
                    break;
                case "신분당":
                    holder.closestSubLine4.setBackgroundResource(R.drawable.round_circle_line13);
                    break;
            }
        }
        holder.itemView.setOnClickListener(new ItemClickListener(position, holder.closestSubName, holder.closestSubLine));

    }

    @Override
    public int getItemCount() {
        return closestSubDataArrayList.size();
    }

    private class ItemClickListener implements View.OnClickListener {
        private int position;
        private String subname;
        private String subline;

        public ItemClickListener(int position, TextView closestSubName, TextView closestSubLine) {
            this.position = position;
            this.subname = closestSubName.getText().toString();
            this.subline = closestSubLine.getText().toString();
        }

        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(context, StationInfoActivity.class);

            network.getChatProxy().sendMessageToServer(subname + "역", new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        intent.putExtra("subname", subname);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }
    }
}
