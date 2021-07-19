package net.teamcadi.angelbrowser.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.StationInfoActivity;
import net.teamcadi.angelbrowser.Activity_Front.data.CommonSubData;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.Sqlite.DBhelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by haams on 2018-01-19.
 */

public class CommonStationAdapter extends RecyclerView.Adapter<CommonStationAdapter.ViewHolder> {

    private static final String TAG = CommonStationAdapter.class.getSimpleName();
    private ArrayList<CommonSubData> commonDataList;
    private Context context;
    private Network network;
    private DBhelper dBhelper;
    private SQLiteDatabase db;


    public CommonStationAdapter(ArrayList<CommonSubData> commonDataList, Context context) {
        this.commonDataList = commonDataList;
        this.context = context;
        network = Network.getNetworkInstance();
        dBhelper = new DBhelper(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCommonSubName;
        private TextView txtCommonSubLine;
        private TextView txtCommonElevLocation;
        private TextView txtCommonRptCount;

        public ViewHolder(View itemView) {
            super(itemView);

            txtCommonSubName = (TextView) itemView.findViewById(R.id.txt_common_statNm);
            txtCommonSubLine = (TextView) itemView.findViewById(R.id.txt_common_subline);
            txtCommonElevLocation = (TextView) itemView.findViewById(R.id.txt_common_location);
            txtCommonRptCount = (TextView) itemView.findViewById(R.id.txt_common_reportCount);
        }
    }

    @Override
    public CommonStationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.common_sub_recyclerview_items, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(CommonStationAdapter.ViewHolder holder, int position) {
        /*
        역 이름 입력해서 넣으면 statNm으로 우선 서버에 요청을 보내어 거기에 관련된 데이터들 중 역이름, 역호선, 제목 (포스팅 갯수는 알아서...)
        을 가지고 온다.
        가지고 온 정보는 모두 Sqlite에 반복문으로 넣어서 들어가게 하고 이를 리사이클러뷰의 아이템들로 보여주게 한다(카드뷰)
        그러고 삭제할 경우는 Sqlite에 대한 내용만 삭제하도록 진행한다.
         */
        holder.txtCommonSubName.setText(commonDataList.get(position).getSubname()); // 역 이름 순서대로 넣기
        holder.txtCommonSubLine.setText(commonDataList.get(position).getSubline()); // 역 호선 순서대로 넣어주기
        holder.txtCommonElevLocation.setText(commonDataList.get(position).getLocation()); // 역 제보 Elevator 위치 순서대로 넣어주기
        holder.txtCommonRptCount.setText("+" + commonDataList.get(position).getCount() + "개의 제보");   // 사이즈 만큼만 넣어주기

        holder.itemView.setOnClickListener(new ItemClickListener(position, holder.txtCommonSubName));
    }

    @Override
    public int getItemCount() {
        return commonDataList.size();
    }

    public void removeAtPostion(int position) {
        if (position < commonDataList.size()) {
            commonDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void move(int fromPosition, int toPosition) {
        final String subname = commonDataList.get(fromPosition).getSubname();
        final String subline = commonDataList.get(fromPosition).getSubline();
        final String location = commonDataList.get(fromPosition).getLocation();
        final int count = commonDataList.get(fromPosition).getCount();
        commonDataList.remove(fromPosition);
        commonDataList.add(toPosition, new CommonSubData(subname, subline, location, count));
        notifyItemMoved(fromPosition, toPosition);
    }

    private class ItemClickListener implements View.OnClickListener {
        int position;
        TextView subname;

        public ItemClickListener(int position, TextView txtCommonSubName) {
            this.position = position;
            this.subname = txtCommonSubName;
        }

        @Override
        public void onClick(View v) {
            final AlertDialog.Builder dlg = new AlertDialog.Builder(context);
            dlg.setTitle("확인하기");
            dlg.setMessage("자주가는 역을 삭제 또는 역 내용 확인하기");
            dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, String.valueOf(subname));
                    String sql = "DELETE FROM reportTB WHERE subname = " + "'" + subname.getText().toString() + "'";

                    db = dBhelper.getWritableDatabase();
                    db.execSQL(sql);
                    // DB서 지우기
                    removeAtPostion(position);
                    //db.close();
                }
            });

            dlg.setNegativeButton("내용 확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    network.getChatProxy().sendMessageToServer(subname.getText().toString() + "역", new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Log.i(TAG, response.body() + "send well");
                                context.startActivity(new Intent(context, StationInfoActivity.class).
                                        putExtra("statNm", subname.getText().toString() + "역").
                                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG, t.toString());
                        }
                    });
                }
            });

            dlg.show();
        }
    }
}
