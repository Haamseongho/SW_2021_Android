package net.teamcadi.angelbrowser.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.teamcadi.angelbrowser.Activity_Front.data.TimeLineData;
import net.teamcadi.angelbrowser.R;

import java.util.ArrayList;

/**
 * Created by haams on 2018-01-17.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private ArrayList<TimeLineData> timeLineDataList;
    private Context context;
    private static final String TAG = TimeLineAdapter.class.getSimpleName();

    public TimeLineAdapter(ArrayList<TimeLineData> timeLineDataList, Context context) {
        this.timeLineDataList = timeLineDataList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtArrivalTime; // 도착 시간
        private TextView txtArrivalMsg; // 도착 메세지
        private TextView txtArrivalStatNm; // 종착역
        private TextView txtStationType;  // 열차 유형

        public ViewHolder(View itemView) {
            super(itemView);
            txtArrivalTime = (TextView)itemView.findViewById(R.id.txt_timeline_barvlDt);    // 몇 초 후 도착
            txtArrivalMsg = (TextView)itemView.findViewById(R.id.txt_timeline_arvlMsg2);    // 종료메세지
            txtArrivalStatNm = (TextView)itemView.findViewById(R.id.txt_timeline_arrival); // 종착역
            txtStationType = (TextView)itemView.findViewById(R.id.txt_timeline_statnType);  // 전철 종류
        }
    }


    @Override
    public TimeLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.timeline_items_info, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(TimeLineAdapter.ViewHolder holder, int position) {
        holder.txtArrivalTime.setText(timeLineDataList.get(position).getArrivalTime()); // 도착 시간 알림 (몇 초 뒤 도착)
        holder.txtArrivalMsg.setText(timeLineDataList.get(position).getArrivalMsg()); // 도착 전 까지 알리는 메세지
        holder.txtArrivalStatNm.setText(timeLineDataList.get(position).getArrivalStatNm()); // 종착역 알림
        holder.txtStationType.setText(timeLineDataList.get(position).getStationType()); // 열차 종류
    }

    @Override
    public int getItemCount() {
        return timeLineDataList.size();
    }
}
