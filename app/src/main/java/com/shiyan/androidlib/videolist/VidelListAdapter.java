package com.shiyan.androidlib.videolist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.shiyan.androidlib.R;

import java.util.List;

public class VidelListAdapter extends RecyclerView.Adapter<VidelListAdapter.VideosViewHolder> {
    private Context context;
    private List<VideoBean> videos;

    public VidelListAdapter(Context context, List<VideoBean> videos) {
        this.context = context;
        this.videos = videos;
    }

    @Override
    public VideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout_video,null);
        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosViewHolder holder, final int position) {
        Bitmap bitmap = BitmapFactory.decodeFile(videos.get(position).getImgPath());
        holder.iv.setImageBitmap(bitmap);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Video2Activity.class);
                intent.putExtra("videopath",videos.get(position).getPath());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return videos.size();
    }
    public static class VideosViewHolder extends RecyclerView.ViewHolder{

        public ImageView iv;
        public VideosViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_video);
        }
    }
}
