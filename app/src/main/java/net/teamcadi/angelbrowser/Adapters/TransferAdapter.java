package net.teamcadi.angelbrowser.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.teamcadi.angelbrowser.Activity_Front.data.TransferSubName;
import net.teamcadi.angelbrowser.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haams on 2018-03-05.
 */

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.ViewHolder> {

    private static final String TAG = TransferAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<TransferSubName> transferSubNameArrayList;

    public TransferAdapter(Context context, ArrayList<TransferSubName> transferSubNameArrayList) {
        this.context = context;
        this.transferSubNameArrayList = transferSubNameArrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View transLineView;
        private TextView transSubName;

        public ViewHolder(View itemView) {
            super(itemView);
            transLineView = (View)itemView.findViewById(R.id.trans_subline);
            transSubName = (TextView)itemView.findViewById(R.id.txt_transfer_subname);
        }
    }


    @Override
    public TransferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.transfer_adapter_items,parent,false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(TransferAdapter.ViewHolder holder, int position) {
        holder.transSubName.setText(transferSubNameArrayList.get(position).getTransSubName());
        // 생성 시에 리스트 꼽을 때 id 값을 넘기고 여기서 불러 온 값을 가지고 와서 비교를 해버리는 것.
        // 비교할 때 그걸 가지고 holder에 view의 색상을 바꿔주는 것
        // 신용산 -> 강남 테스트
        //

        Pattern pattern1 = Pattern.compile("(^1001).*");  // 1호선 패턴
        Pattern pattern2 = Pattern.compile("(^1002).*");  // 2호선 패턴
        Pattern pattern3 = Pattern.compile("(^1003).*");  // 3호선 패턴
        Pattern pattern4 = Pattern.compile("(^1004).*");  // 4호선 패턴
        Pattern pattern5 = Pattern.compile("(^1005).*");  // 5호선 패턴
        Pattern pattern6 = Pattern.compile("(^1006).*");  // 6호선 패턴
        Pattern pattern7 = Pattern.compile("(^1007).*");  // 7호선 패턴
        Pattern pattern8 = Pattern.compile("(^1008).*");  // 8호선 패턴
        Pattern pattern9 = Pattern.compile("(^1009).*");  // 9호선 패턴
        Pattern pattern10 = Pattern.compile("(^1061).*");  // 중앙,경의선 패턴
        Pattern pattern11 = Pattern.compile("(^1065).*");  // 공항철도 패턴
        Pattern pattern12 = Pattern.compile("(^1075).*");  // 분당선 패턴
        Pattern pattern13 = Pattern.compile("(^1077).*");  // 신분당선 패턴

        Matcher matcher1 = pattern1.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 1호선
        Matcher matcher2 = pattern2.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 2호선
        Matcher matcher3 = pattern3.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 3호선
        Matcher matcher4 = pattern4.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 4호선
        Matcher matcher5 = pattern5.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 5호선
        Matcher matcher6 = pattern6.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId())); // 6호선
        Matcher matcher7 = pattern7.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 7호선
        Matcher matcher8 = pattern8.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 8호선
        Matcher matcher9 = pattern9.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 9호선
        Matcher matcher10 = pattern10.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId())); // 중앙,경의선
        Matcher matcher11 = pattern11.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 공항철도
        Matcher matcher12 = pattern12.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 분당선
        Matcher matcher13 = pattern13.matcher(String.valueOf(transferSubNameArrayList.get(position).getTransSubId()));  // 신분당선


        if(matcher1.find()){
            holder.transLineView.setBackgroundColor(Color.parseColor("#0d3692"));
        }
        if(matcher2.find()){
            holder.transLineView.setBackgroundColor(Color.parseColor("#33923d"));
        }
        if(matcher3.find()){
            holder.transLineView.setBackgroundColor(Color.parseColor("#fe5d10"));
        }
        if(matcher4.find()){
            holder.transLineView.setBackgroundColor(Color.parseColor("#00a2d1"));
        }
        if(matcher5.find()){
            holder.transLineView.setBackgroundColor(Color.parseColor("#8b50a4"));
        }
        if(matcher6.find()){
            holder.transLineView.setBackgroundColor(Color.parseColor("#c55c1d"));
        }
        if(matcher7.find()){
            holder.transLineView.setBackgroundColor(Color.parseColor("#54640d"));
        }
        if(matcher8.find()){
            holder.transLineView.setBackgroundColor(Color.parseColor("#f14c82"));
        }
        if(matcher9.find()){
            holder.transLineView.setBackgroundColor(Color.parseColor("#aa9872"));
        }
        if(matcher10.find()){ // 중앙.경의선
            holder.transLineView.setBackgroundColor(Color.parseColor("#73c7a6"));
        }
        if(matcher11.find()){ // 공항철도
            holder.transLineView.setBackgroundColor(Color.parseColor("#3681b7"));
        }
        if(matcher12.find()){ // 분당선
            holder.transLineView.setBackgroundColor(Color.parseColor("#ff8c00"));
        }
        if(matcher13.find()){  // 신분당선
            holder.transLineView.setBackgroundColor(Color.parseColor("#c82127"));
        }


        /*switch (transferSubNameArrayList.get(position).getTransSubId()){
            case 1004:

                break;
            case 1004000430:
                holder.transLineView.setBackgroundColor(Color.parseColor("#00a2d1"));
                break;

            case 1004000431:
                holder.transLineView.setBackgroundColor(Color.parseColor("#00a2d1"));
                break;

            case 1004000432:
                holder.transLineView.setBackgroundColor(Color.parseColor("#00a2d1"));
                break;

            case 1002000224:
                holder.transLineView.setBackgroundColor(Color.parseColor("#33923d"));

            case 1002000226:
                holder.transLineView.setBackgroundColor(Color.parseColor("#33923d"));
                break;

            case 1002000225:
                holder.transLineView.setBackgroundColor(Color.parseColor("#33923d"));
                break;

            case 1002000223:
                holder.transLineView.setBackgroundColor(Color.parseColor("#33923d"));
                break;

            case 1002000222:
                holder.transLineView.setBackgroundColor(Color.parseColor("#33923d"));
                break;
        }*/
    }

    @Override
    public int getItemCount() {
        return transferSubNameArrayList.size();
    }
}
