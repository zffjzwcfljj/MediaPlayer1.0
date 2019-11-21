package com.example.mediaplayer10;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {
    private Button btnNext, btnPre;
    private ImageView btnStart,liebiao;
    private ListView listView;
    private SeekBar seekBar;
    private MusicService musicService = MusicService.getMusicService;
    private Handler handler;// 处理改变进度条事件
    int UPDATE = 0x101;
    int flag = 1,count =0;
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

        System.out.println("create");


        if (PermissionUtils.isGrantExternalRW(this, 1)) return ;

        setContentView(R.layout.activity_main);
        initView();

        MyAdapter adapter = new MyAdapter(MainActivity.this,musicList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MusicService.getMusicService.playClick(position);
                ((ImageView)findViewById(R.id.start_stop1)).setImageResource(R.drawable.play_1);
                initViewPic();

                    Intent intent = new Intent(MainActivity.this, MusicContent.class);
                    intent.putExtra("index", position);
                    startActivity(intent);


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

        btnStart = (ImageView) findViewById(R.id.start_stop1);
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
                    ((ImageView)findViewById(R.id.start_stop1)).setImageResource(res);
            }
        });


        btnPre = (Button) findViewById(R.id.pre1);
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    musicService.previous();
                    if (!musicService.mediaplayer.isPlaying()) {
                        res = R.drawable.timeout_1;
                    } else if (musicService.mediaplayer.isPlaying()) {
                        res = R.drawable.play_1;
                    }
                    if (res != 0)
                        ((ImageView)findViewById(R.id.start_stop1)).setImageResource(res);
                } catch (Exception e) {
                    Log.i("LAT", "上一曲异常！");
                }

            }
        });

        btnNext = (Button) findViewById(R.id.next1);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    musicService.next();
                    if (!musicService.mediaplayer.isPlaying()) {
                        res = R.drawable.timeout_1;
                    } else if (musicService.mediaplayer.isPlaying()) {
                        res = R.drawable.play_1;
                    }
                    if (res != 0)
                        ((ImageView)findViewById(R.id.start_stop1)).setImageResource(res);
                } catch (Exception e) {
                    Log.i("LAT", "下一曲异常！");
                }

            }
        });

        seekBar = (SeekBar) findViewById(R.id.bar1);
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


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //检验是否获取权限，如果获取权限，外部存储会处于开放状态，会弹出一个toast提示获得授权
                    String sdCard = Environment.getExternalStorageState();
                    if (sdCard.equals(Environment.MEDIA_MOUNTED)){
                        Toast.makeText(this,"获得授权",Toast.LENGTH_LONG).show();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "buxing", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 初始化view
     */
    private void initView() {
        listView = (ListView) findViewById(R.id.list_view);
        musicList = new ArrayList<>();
        //把扫描到的音乐赋值给list
        musicList = MusicUtils.getMusicData(this);
        MusicService.getMusicService.musicList = musicList;
        adapter = new MyAdapter(this,musicList);
        listView.setAdapter(adapter);

        List<String> musicSrcList = new ArrayList<>();
        for (Music music : musicList) {
            musicSrcList.add(music.path);
        }

        MusicService.getMusicService.musicSrcList = musicSrcList;
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


    public void initViewPic() {
        Music music = MusicService.getMusicService.currentMusic;
        Bitmap bitmap = ArtworkUtils.getArtwork(this, music.musicName, music.id, music.musicAlbumId);
        ((ImageView)findViewById(R.id.album1)).setImageBitmap(bitmap);
        if (bitmap == null)
            ((ImageView)findViewById(R.id.album1)).setImageResource(R.drawable.star);

    }

    @Override
    protected void onStart() {

        System.out.println("start");

        super.onStart();
        if (!musicService.mediaplayer.isPlaying())
            res = R.drawable.timeout_1;
        else if (musicService.mediaplayer.isPlaying())
            res = R.drawable.play_1;
        if (res != 0)
            ((ImageView)findViewById(R.id.start_stop1)).setImageResource(res);

    }
}
