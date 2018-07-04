package com.shiyan.androidlib.videolist;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.shiyan.androidlib.R;

import java.util.ArrayList;
import java.util.List;

public class VideolistActivity extends Activity {

    RecyclerView rv_videolist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);
        rv_videolist = (RecyclerView) findViewById(R.id.rv_videolist);
        GridLayoutManager gm = new GridLayoutManager(this,4);
        rv_videolist.setLayoutManager(gm);
        //添加分割线

    //    rv_videolist.addItemDecoration();

        List<VideoBean> localVideos =getLocalVideos();
        Log.i("test3","video mun = "+localVideos.size());
        VidelListAdapter adapter = new VidelListAdapter(this,localVideos);
        rv_videolist.setAdapter(adapter);
    }

    /**
     * 获取本地视频list
     * @return
     */
    public List<VideoBean> getLocalVideos(){
        List<VideoBean> list = new ArrayList<>();
        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
        // 视频其他信息的查询条件
        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION};

        Cursor cursor = getContentResolver().query(MediaStore.Video.Media
                        .EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);
        if (cursor != null){
            while (cursor.moveToNext()){
                VideoBean item = new VideoBean();
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = getContentResolver().query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    item.setImgPath(thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                }
                item.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media
                        .DATA)));
                item.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video
                        .Media.DURATION)));
                list.add(item);
            }
        }
        return list;
    }

}
