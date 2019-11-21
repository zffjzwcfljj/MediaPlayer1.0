package com.example.mediaplayer10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediaplayer10.Service.MusicService;
import com.example.mediaplayer10.adapter.MyAdapter;
import com.example.mediaplayer10.bean.Music;
import com.example.mediaplayer10.util.MusicUtils;
import com.example.mediaplayer10.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    int flag =1;
    private Button btnStart, btnStop, btnNext, btnPre;
    private TextView txtInfo;
    private ListView listView;
    private SeekBar seekBar;
    private MusicService musicService = new MusicService();
    private Handler handler;// 处理改变进度条事件
    int UPDATE = 0x101;
    private boolean autoChange, manulChange;// 判断是进度条是自动改变还是手动改变
    private boolean isPause;// 判断是从暂停中恢复还是重新播放

    private List<Music> musicList = new ArrayList<>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (PermissionUtils.isGrantExternalRW(this, 1)) return ;

        setContentView(R.layout.activity_main);
        initView();


        MyAdapter adapter = new MyAdapter(MainActivity.this,musicList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Music music = musicList.get(position);

            }
        });

//        btnStart = (Button) findViewById(R.id.start_stop1);
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    /**
//                     * 引入flag作为标志，当flag为1 的时候，此时player内没有东西，所以执行musicService.play()函数
//                     * 进行第一次播放，然后flag自增二不再进行第一次播放
//                     * 当再次点击“开始/暂停”按钮次数即大于1 将执行暂停或继续播放goplay()函数
//                     */
//                    if (flag == 1) {
//                        musicService.play();
//                        flag++;
//                    } else {
//                        if (!musicService.mediaplayer.isPlaying()) {
//                            musicService.goPlay();
//                        } else if (musicService.mediaplayer.isPlaying()) {
//                            musicService.pause();
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.i("LAT", "开始异常！");
//                }
//
//            }
//        });
//
//
//        btnPre = (Button) findViewById(R.id.pre1);
//        btnPre.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    musicService.previous();
//                } catch (Exception e) {
//                    Log.i("LAT", "上一曲异常！");
//                }
//
//            }
//        });
//
//        btnNext = (Button) findViewById(R.id.next1);
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    musicService.next();
//                } catch (Exception e) {
//                    Log.i("LAT", "下一曲异常！");
//                }
//
//            }
//        });
//
//        seekBar = (SeekBar) findViewById(R.id.bar1);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {//用于监听SeekBar进度值的改变
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {//用于监听SeekBar开始拖动
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {//用于监听SeekBar停止拖动  SeekBar停止拖动后的事件
//                int progress = seekBar.getProgress();
//                Log.i("TAG:", "" + progress + "");
//                int musicMax = musicService.mediaplayer.getDuration(); //得到该首歌曲最长秒数
//                int seekBarMax = seekBar.getMax();
//                musicService.mediaplayer
//                        .seekTo(musicMax * progress / seekBarMax);//跳到该曲该秒
//                autoChange = true;
//                manulChange = false;
//            }
//        });
//
//
//        Thread thread = new Thread((Runnable) this);// 自动改变进度条的线程
//        //实例化一个handler对象
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                //更新UI
//                int mMax = musicService.mediaplayer.getDuration();//最大秒数
//                if (msg.what == UPDATE) {
//                    try {
//                        seekBar.setProgress(msg.arg1);
//                        txtInfo.setText(setPlayInfo(msg.arg2 / 1000, mMax / 1000));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    seekBar.setProgress(0);
//                    txtInfo.setText("播放已经停止");
//                }
//            }
//        };
//        thread.start();

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
        adapter = new MyAdapter(this,musicList);
        listView.setAdapter(adapter);
    }


//    //设置当前播放的信息
//    private String setPlayInfo(int position,int max) {
//        String info = "正在播放:  " + musicService.musicName + "\t\t";
//        int pMinutes = 0;
//        while (position >= 60) {
//            pMinutes++;
//            position -= 60;
//        }
//        String now = (pMinutes < 10 ? "0" + pMinutes : pMinutes) + ":"
//                + (position < 10 ? "0" + position : position);
//
//        int mMinutes = 0;
//        while (max >= 60) {
//            mMinutes++;
//            max -= 60;
//        }
//        String all = (mMinutes < 10 ? "0" + mMinutes : mMinutes) + ":"
//                + (max < 10 ? "0" + max : max);
//
//        return info + now + " / " + all;
//    }
//
//
////    @Override
//    public void run() {
//        int position, mMax, sMax;
//        while (!Thread.currentThread().isInterrupted()) {
//            if (musicService.mediaplayer != null && musicService.mediaplayer.isPlaying()) {
//                position = musicService.getCurrentProgress();//得到当前歌曲播放进度(秒)
//                mMax = musicService.mediaplayer.getDuration();//最大秒数
//                sMax = seekBar.getMax();//seekBar最大值，算百分比
//                Message m = handler.obtainMessage();//获取一个Message
//                m.arg1 = position * sMax / mMax;//seekBar进度条的百分比
//                m.arg2 = position;
//                m.what = UPDATE;
//                handler.sendMessage(m);
//                //  handler.sendEmptyMessage(UPDATE);
//                try {
//                    Thread.sleep(1000);// 每间隔1秒发送一次更新消息
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


}
