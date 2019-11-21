package com.example.mediaplayer10;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.mediaplayer10.Service.MusicService;
import com.example.mediaplayer10.Service.MusicServiceListener;
import com.example.mediaplayer10.adapter.MyAdapter;
import com.example.mediaplayer10.bean.Music;
import com.example.mediaplayer10.util.ArtworkUtils;
import com.example.mediaplayer10.util.MusicUtils;
import com.example.mediaplayer10.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicContent extends AppCompatActivity {
    private Button btnNext, btnPre, btnBack;
    private ImageView btnStart,liebiao,ablum;
    private TextView name,singer;
    private SeekBar seekBar;
    private MusicService musicService = MusicService.getMusicService;
    private Handler handler;// 处理改变进度条事件
    int UPDATE = 0x101;
    private boolean autoChange, manulChange;// 判断是进度条是自动改变还是手动改变
    private boolean isPause;// 判断是从暂停中恢复还是重新播放

    private List<Music> musicList = new ArrayList<>();
    private MyAdapter adapter;
    public int res = 0;

    public Timer timer;
    public TimerTask timerTask;

    public Music music;


@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.music_content);

    if (!musicService.mediaplayer.isPlaying()) {
        res = R.drawable.timeout_1;
    } else if (musicService.mediaplayer.isPlaying()) {
        res = R.drawable.play_1;
    }
    if (res != 0)
        ((ImageView)findViewById(R.id.start_stop)).setImageResource(res);

    Intent intent = getIntent();
    int index = intent.getIntExtra("index", 0);
    music = MusicService.getMusicService.musicList.get(index);

    initView();

    btnBack = (Button) findViewById(R.id.back_button);
    btnBack.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v){
            finish();

        }
    });


    liebiao = (ImageView) findViewById(R.id.liebiao);
    liebiao.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (MusicService.getMusicService.randomPlay == false ){
                res = R.drawable.random;
                MusicService.getMusicService.randomPlay = true;

            } else {
                res = R.drawable.loop_1;
                MusicService.getMusicService.randomPlay = false;
            }
            if (res != 0)
                ((ImageView)findViewById(R.id.liebiao)).setImageResource(res);
        }
    });

    btnStart = (ImageView) findViewById(R.id.start_stop);
    btnStart.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!musicService.mediaplayer.isPlaying()) {
                musicService.goPlay();
                res = R.drawable.play_1;
            } else if (musicService.mediaplayer.isPlaying()) {
                musicService.pause();
                res = R.drawable.timeout_1;
            }
            if (res != 0)
                ((ImageView)findViewById(R.id.start_stop)).setImageResource(res);
        }
    });


    btnPre = (Button) findViewById(R.id.pre);
    btnPre.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                musicService.previous();
                music = MusicService.getMusicService.currentMusic;
                initView();
                if (!musicService.mediaplayer.isPlaying()) {
                    res = R.drawable.timeout_1;
                } else if (musicService.mediaplayer.isPlaying()) {
                    res = R.drawable.play_1;
                }
                if (res != 0)
                    ((ImageView)findViewById(R.id.start_stop)).setImageResource(res);
            } catch (Exception e) {
                Log.i("LAT", "上一曲异常！");
            }

        }
    });

    btnNext = (Button) findViewById(R.id.next);
    btnNext.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                musicService.next();
                music = MusicService.getMusicService.currentMusic;
                initView();
                if (!musicService.mediaplayer.isPlaying()) {
                    res = R.drawable.timeout_1;
                } else if (musicService.mediaplayer.isPlaying()) {
                    res = R.drawable.play_1;
                }
                if (res != 0)
                    ((ImageView)findViewById(R.id.start_stop)).setImageResource(res);
            } catch (Exception e) {
                Log.i("LAT", "下一曲异常！");
            }

        }
    });

    seekBar = (SeekBar) findViewById(R.id.bar);
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {//用于监听SeekBar进度值的改变

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {//用于监听SeekBar开始拖动

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {//用于监听SeekBar停止拖动  SeekBar停止拖动后的事件
            int progress = seekBar.getProgress();
            Log.i("TAG:", "" + progress + "");
            int musicMax = musicService.mediaplayer.getDuration(); //得到该首歌曲最长秒数
            int seekBarMax = seekBar.getMax();
            musicService.mediaplayer
                    .seekTo(musicMax * progress / seekBarMax);//跳到该曲该秒
            autoChange = true;
            manulChange = false;
        }
    });


    // 计时器循环刷新播放进度
    timer = new Timer();
    timerTask = new TimerTask() {
        @Override
        public void run() {
            MediaPlayer player = MusicService.getMusicService.mediaplayer;
            if (player.isPlaying()) {
                double duration = player.getDuration();
                double currentPosition = player.getCurrentPosition();
                if (duration == 0) return ;
                seekBar.setProgress((int)(currentPosition * 100 / duration), true);
            }
        }
    };
    timer.schedule(timerTask, 0, 10);

    // 播放歌曲回调
    MusicService.getMusicService.setMusicServiceListener(new MusicServiceListener() {
        @Override
        public void playCallBack(int pos) {
            Music music = musicList.get(pos);
            ((ImageView)findViewById(R.id.album1)).setImageResource(music.musicAlbumId);
        }
    });

}




    //设置当前播放的信息
    private String setPlayInfo(int position,int max) {
        String info = "正在播放:  " + musicService.musicName + "\t\t";
        int pMinutes = 0;
        while (position >= 60) {
            pMinutes++;
            position -= 60;
        }
        String now = (pMinutes < 10 ? "0" + pMinutes : pMinutes) + ":"
                + (position < 10 ? "0" + position : position);

        int mMinutes = 0;
        while (max >= 60) {
            mMinutes++;
            max -= 60;
        }
        String all = (mMinutes < 10 ? "0" + mMinutes : mMinutes) + ":"
                + (max < 10 ? "0" + max : max);

        return info + now + " / " + all;
    }


    //    @Override
    public void run() {
        int position, mMax, sMax;
        while (!Thread.currentThread().isInterrupted()) {
            if (musicService.mediaplayer != null && musicService.mediaplayer.isPlaying()) {
                position = musicService.getCurrentProgress();//得到当前歌曲播放进度(秒)
                mMax = musicService.mediaplayer.getDuration();//最大秒数
                sMax = seekBar.getMax();//seekBar最大值，算百分比
                Message m = handler.obtainMessage();//获取一个Message
                m.arg1 = position * sMax / mMax;//seekBar进度条的百分比
                m.arg2 = position;
                m.what = UPDATE;
                handler.sendMessage(m);
                //  handler.sendEmptyMessage(UPDATE);
                try {
                    Thread.sleep(1000);// 每间隔1秒发送一次更新消息
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void initView() {
        name = (TextView) findViewById(R.id.musicName);
        name.setText(music.musicName);

        singer = (TextView) findViewById(R.id.singer);
        singer.setText(music.musicSinger);

        Bitmap bitmap = ArtworkUtils.getArtwork(this, music.musicName, music.id, music.musicAlbumId);
        ((ImageView)findViewById(R.id.picture)).setImageBitmap(bitmap);
        if (bitmap == null)
            ((ImageView)findViewById(R.id.picture)).setImageResource(R.drawable.star);
    }

}
