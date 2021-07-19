package net.teamcadi.angelbrowser.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.teamcadi.angelbrowser.Activity_Front.data.ChatArray;
import net.teamcadi.angelbrowser.R;

import java.util.ArrayList;

/**
 * Created by haams on 2018-02-13.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private ArrayList<ChatArray> chatArrayList;
    private Context context;
    private TextView setColorChatView;
    private CardView setColorCardView;

    public ChatListAdapter(ArrayList<ChatArray> chatArrayList, Context context) {
        this.chatArrayList = chatArrayList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView chatBox; // sender의 값이 true 이면 보낸 사람 쪽이니 색 변동 진행.
        TextView txtChatView; // sender의 값이 true 이면 보낸 사람 쪽이니 색 변동 진행 .

        public ViewHolder(View itemView) {
            super(itemView);
            txtChatView = (TextView) itemView.findViewById(R.id.chat_contents);
            chatBox = (CardView) itemView.findViewById(R.id.chatBox);
        }
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.chat_list_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ViewHolder holder, int position) {
        holder.txtChatView.setText(chatArrayList.get(position).getChatContents());

        // Linking
        this.setColorCardView = holder.chatBox;
        this.setColorChatView = holder.txtChatView;
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    // chat send & chat receive
    public void addAtPosition(int position, String chatting, boolean sender) {
        // true (Client -> Server)
        if (sender) {
            this.setColorChatView.setTextColor(Color.WHITE);
            this.setColorCardView.setCardBackgroundColor(Color.rgb(73, 167, 235));
        } else {
            this.setColorChatView.setTextColor(Color.rgb(89, 87, 87));
            this.setColorCardView.setCardBackgroundColor(Color.rgb(241, 241, 241));
        }
        chatArrayList.add(position, new ChatArray(chatting.toString()));
        notifyItemInserted(position);
    }

    public void resetAll() {
        chatArrayList.clear();
        notifyDataSetChanged();
    }
}
