package com.example.mediaplayer10.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.mediaplayer10.bean.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicUtils {

    public static List<Music> getMusicData(Context context) {
        List<Music> list = new ArrayList<Music>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        System.out.println("aaaxxxx");
        System.out.println(cursor);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Music music = new Music();
                music.musicName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                music.musicSinger = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                music.musicAlbum = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                music.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                music.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                music.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if (music.size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (music.musicName.contains("-")) {
                        String[] str = music.musicName.split("-");
                        music.musicSinger = str[0];
                        music.musicName = str[1];
                    }
                    list.add(music);
                }
            }
            // 释放资源
            cursor.close();
        }

        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }
}
