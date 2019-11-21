package com.example.mediaplayer10.Service;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.mediaplayer10.bean.Music;

import java.io.File;
import java.util.List;
import java.util.Random;

public class MusicService {
    public List<String> musicSrcList;
    public List<Music> musicList;
    public MediaPlayer mediaplayer;
    public int musicPos; // 当前播放的歌曲在List中的下标,flag为标致
    public String musicName;

    public Music currentMusic;  // 当前正在播放的歌曲

    public boolean randomPlay; // 随机播放 默认不随机

    public static MusicService getMusicService = new MusicService();

    public MusicServiceListener musicServiceListener;

    public MusicService(){
        super();
        mediaplayer = new MediaPlayer();
    }


    /**
     *修改获取到的MP3文件的名字在TextView页面中使用
     */
    public void setPlayName(String dataSource) {
        File file = new File(dataSource);
        String name = file.getName();
        int index = name.lastIndexOf(".");
        musicName = name.substring(0, index);
    }


    public void play() {
        try {
            mediaplayer.reset(); //重置多媒体
            String dataSource = musicSrcList.get(musicPos);//得到当前播放音乐的路径
            setPlayName(dataSource);//截取歌名
            // 指定参数为音频文件
            mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaplayer.setDataSource(dataSource);//为多媒体对象设置播放路径
            mediaplayer.prepare();//准备播放
            mediaplayer.start();//开始播放
            //setOnCompletionListener 当当前多媒体对象播放完成时发生的事件
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    next();//如果当前歌曲播放完毕,自动播放下一首.
                }
            });

            currentMusic = musicList.get(musicPos);

            if (musicServiceListener != null) {
                musicServiceListener.playCallBack(musicPos);
            }

        } catch (Exception e) {
            Log.v("MusicService", e.getMessage());
        }
    }

    //继续播放
    public  void goPlay(){
        int position = getCurrentProgress();
        mediaplayer.seekTo(position);//设置当前MediaPlayer的播放位置，单位是毫秒。
        try {
            mediaplayer.prepare();//  同步的方式装载流媒体文件。
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaplayer.start();
    }

    // 获取当前进度
    public int getCurrentProgress() {
        if (mediaplayer != null & mediaplayer.isPlaying()) {
            return mediaplayer.getCurrentPosition();
        } else if (mediaplayer != null & (!mediaplayer.isPlaying())) {
            return mediaplayer.getCurrentPosition();
        }
        return 0;
    }

    public void playClick(int pos) {
        musicPos = pos;
        play();

    }

    public void next() {
        Random random = new Random();
        if (randomPlay) {
            musicPos = random.nextInt(musicSrcList.size());
        } else {
            musicPos = musicPos == musicSrcList.size() - 1 ? 0 : musicPos + 1;
        }
        play();
    }

    public void previous() {
        Random random = new Random();
        if (randomPlay) {
            musicPos = random.nextInt(musicSrcList.size());
        } else {
            musicPos = musicPos == 0 ? musicSrcList.size() - 1 : musicPos - 1;
        }
        play();
    }

    // 暂停播放
    public void pause() {
        if (mediaplayer != null && mediaplayer.isPlaying()){
            mediaplayer.pause();
        }
    }

    public MusicServiceListener getMusicServiceListener() {
        return musicServiceListener;
    }

    public void setMusicServiceListener(MusicServiceListener musicServiceListener) {
        this.musicServiceListener = musicServiceListener;
    }
}

