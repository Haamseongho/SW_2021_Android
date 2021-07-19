package net.teamcadi.angelbrowser.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.teamcadi.angelbrowser.Activity_Back.data.ReportDetailInfo;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.ReportInfoDetailActivity;
import net.teamcadi.angelbrowser.Activity_Front.data.Report;
import net.teamcadi.angelbrowser.R;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

/**
 * Created by haams on 2018-01-06.
 */

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder> {

    private static final String TAG = ReportListAdapter.class.getSimpleName();
    private ArrayList<Report> reportArrayList;
    private Context context;
    private Network network; // 어댑터 꼽으면서 서버 연동 진행 >> 자세히 보기 눌렀을 때 position 값에 따라서 데이터 가져올 것 정하기
    // 걍 여기서 정보 찾아서 보내 그러고 받으면 되지!! 굿굿..!
    private Intent rptDetIntent; // 제보 자세히 보기 인텐트

    public ReportListAdapter(ArrayList<Report> reportArrayList, Context context) {
        this.reportArrayList = reportArrayList;
        this.context = context;
        network = Network.getNetworkInstance();
        rptDetIntent = new Intent(context, ReportInfoDetailActivity.class);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView repTitle; // 제목
        private TextView repName; // 작성자
        private TextView repTime; // 시간
        private TextView repMoreData; // 더보기

        public ViewHolder(View itemView) {
            super(itemView);
            repName = (TextView) itemView.findViewById(R.id.txt_report_name);
            repTitle = (TextView) itemView.findViewById(R.id.txt_report_title);
            repTime = (TextView) itemView.findViewById(R.id.txt_report_time);
            repMoreData = (TextView) itemView.findViewById(R.id.txt_report_moreData);
        }
    }

    @Override
    public ReportListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.sub_info_report_list_items, parent, false);
        ViewHolder vh = new ViewHolder(itemView);

        return vh;
    }

    @Override
    public void onBindViewHolder(ReportListAdapter.ViewHolder holder, int position) {
        holder.repTitle.setText(reportArrayList.get(position).getRptTitle());
        holder.repName.setText(reportArrayList.get(position).getRptName());
        holder.repTime.setText(String.valueOf(reportArrayList.get(position).getRptDate()));
        holder.repMoreData.setText("자세히보기");

        holder.repMoreData.setOnClickListener(new repDataClickListener(position));
    }

    @Override
    public int getItemCount() {
        return reportArrayList.size();
    }

    private class repDataClickListener implements View.OnClickListener {
        int position;

        public repDataClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_report_moreData:
                    // 포지션 값에 따라서 제목이랑 작성자 그리고 시간을 가지고 내용이랑 사진 같이 가져오기
                    getReportDetailInfoFromServer(position);
                    break;
            }
        }
    }

    // End-Point >> /report/station/info/detail
    private void getReportDetailInfoFromServer(int position) {
      /*  RequestBody title = RequestBody.create(MultipartBody.FORM, reportArrayList.get(position).getRptTitle()); // title >> 포지션 값에 따라
        RequestBody name = RequestBody.create(MultipartBody.FORM, reportArrayList.get(position).getRptName());  // name >> 작성자
        RequestBody time = RequestBody.create(MultipartBody.FORM, reportArrayList.get(position).getRptDate());  // time >> 작성 시간*/

        network.getReportProxy().getReportDetailInfoFromServer(reportArrayList.get(position).getRptTitle(),
                reportArrayList.get(position).getRptName(), reportArrayList.get(position).getRptDate(), new Callback<ReportDetailInfo>() {
                    @Override
                    public void onResponse(Call<ReportDetailInfo> call, Response<ReportDetailInfo> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, response.body().getName() + " // " + response.body().getTitle() + "// " + response.body().getTime());
                            Log.i(TAG, String.valueOf(response.body().getPhoto().get(0).getFilepath() + "//" + response.body().getPhoto().get(0).getFilename()));
                            rptDetIntent.putExtra("name", response.body().getName());
                            rptDetIntent.putExtra("title", response.body().getTitle());
                            rptDetIntent.putExtra("contents", response.body().getContents());

                            // 포토1의 파일이 있을 경우
                            if (response.body().getPhoto().size() != 0)
                                rptDetIntent.putExtra("photo1", response.body().getPhoto().get(0).getFilepath());
                            // 포토2의 파일이 있을 경우
                            if (response.body().getPhoto2().size() != 0)
                                rptDetIntent.putExtra("photo2", response.body().getPhoto2().get(0).getFilepath());
                            // 포토3의 파일이 있을 경우
                            if (response.body().getPhoto3().size() != 0)
                                rptDetIntent.putExtra("photo3", response.body().getPhoto3().get(0).getFilepath());

                            context.startActivity(rptDetIntent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ReportDetailInfo> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
    }
}
