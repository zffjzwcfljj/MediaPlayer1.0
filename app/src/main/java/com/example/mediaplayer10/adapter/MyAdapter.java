package com.example.mediaplayer10.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mediaplayer10.MainActivity;
import com.example.mediaplayer10.bean.Music;
import com.example.mediaplayer10.R;
import com.example.mediaplayer10.util.MusicUtils;

import java.util.List;

    public class MyAdapter extends BaseAdapter {
        private Context context;
        private List<Music> list;
        public MyAdapter(MainActivity mainActivity, List<Music> list) {
            this.context = mainActivity;
            this.list = list;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                //引入布局
                view = View.inflate(context, R.layout.music_item, null);
                //实例化对象
                holder.musicName= (TextView) view.findViewById(R.id.music_name);
                holder.musicSinger = (TextView) view.findViewById(R.id.music_singer);
                holder.musicAlbum = (TextView) view.findViewById(R.id.music_album);
                holder.musicDuration = (TextView) view.findViewById(R.id.music_duration);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            //给控件赋值
            holder.musicName.setText(list.get(i).musicName.toString());
            holder.musicSinger.setText(list.get(i).musicSinger.toString());
            holder.musicAlbum.setText(list.get(i).musicAlbum.toString());
            //时间需要转换一下
            int duration = list.get(i).duration;
            String time = MusicUtils.formatTime(duration);
            holder.musicDuration.setText(time);

            return view;
        }
        class ViewHolder{
            TextView musicName;//歌曲名
            TextView musicSinger;//歌手
            TextView musicDuration;//时长
            TextView musicAlbum;//专辑

        }


    private int resourceId;


}
