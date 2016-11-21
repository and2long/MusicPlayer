package com.and2long.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.and2long.musicplayer.R;
import com.and2long.musicplayer.bean.SongBean;

import java.util.ArrayList;

/**
 * Created by L on 2016/11/1.
 */

public class MusicListAdapter extends RecyclerView.Adapter {

    private ArrayList<SongBean> list;
    private Context context;
    private OnItemClickListener listener;

    public MusicListAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<SongBean> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_song_detail, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.tvFileName.setText(list.get(position).getTitle());
        myHolder.tvSinger.setText(list.get(position).getSinger());
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    listener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvFileName;
        TextView tvSinger;

        public MyHolder(View itemView) {
            super(itemView);
            tvFileName = (TextView) itemView.findViewById(R.id.tv_file_name);
            tvSinger = (TextView) itemView.findViewById(R.id.tv_singer);
        }
    }
}

