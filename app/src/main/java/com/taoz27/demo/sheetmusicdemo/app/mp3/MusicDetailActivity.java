package com.taoz27.demo.sheetmusicdemo.app.mp3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.taoz27.demo.sheetmusicdemo.MyPackage.CircleRotateView;
import com.taoz27.demo.sheetmusicdemo.MyPackage.MP3Player;
import com.taoz27.demo.sheetmusicdemo.MyPackage.MP3PlayerFragment;
import com.taoz27.demo.sheetmusicdemo.R;
import com.taoz27.demo.sheetmusicdemo.lrc.ILrcView;
import com.taoz27.demo.sheetmusicdemo.lrc.LrcDataBuilder;
import com.taoz27.demo.sheetmusicdemo.lrc.LrcRow;
import com.taoz27.demo.sheetmusicdemo.lrc.LrcView;
import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;

import java.util.List;

/**
 * Created by taoz27 on 2017/5/7.
 */

public class MusicDetailActivity extends FragmentActivity implements View.OnClickListener{
    LinearLayout backView;
    TextView curTime,totalTime,title;
    SeekBar progress;
    Button prev,play,next;

    AlbumFragment albumFragment;
    LyricFragment lyricFragment;
    ViewPager viewPager;
    MP3Player controller;
    boolean isPausing,isRunning;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                if (controller != null) {
                    if (controller.isPlaying()) {
                        controller.pause();
                        isPausing=true;
                        play.setBackgroundResource(R.drawable.play);
                    } else {
                        if (isPausing) {
                            controller.resume();
                        }else {
                            controller.startPlay();
                        }
                        play.setBackgroundResource(R.drawable.pause);
                    }
                }
                break;
            case R.id.next:
                controller.playNext();
                break;
            case R.id.prev:
                controller.playFore();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        backView=(LinearLayout)findViewById(R.id.back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title=(TextView)findViewById(R.id.title);
        curTime=(TextView)findViewById(R.id.currentTime);
        totalTime=(TextView)findViewById(R.id.totalTime);
        progress=(SeekBar)findViewById(R.id.progress);
        prev=(Button)findViewById(R.id.prev);
        play=(Button)findViewById(R.id.play);
        next=(Button)findViewById(R.id.next);
        controller=MP3Player.getController(this);
        isPausing=false;
        initView();

        albumFragment=new AlbumFragment();
        lyricFragment=new LyricFragment();
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return position==0?albumFragment:lyricFragment;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        isRunning=true;
        new Thread(){
            @Override
            public void run() {
                while (isRunning){
                    try {
                        handler.sendEmptyMessage(0x123);
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                if (controller == null||controller.getCurrentMusic()==null) return;
                title.setText(controller.getCurrentMusic().getDisplayName());
                totalTime.setText(controller.getDuration() / 1000 / 60 + ":" + controller.getDuration() / 1000 % 60);
                curTime.setText(controller.getCurrentPosition() / 1000 / 60 + ":" + controller.getCurrentPosition() / 1000 % 60);
                progress.setMax(controller.getDuration());
                progress.setProgress(controller.getCurrentPosition());
                if (controller.isPlaying())
                    play.setBackgroundResource(R.drawable.pause);
                else
                    play.setBackgroundResource(R.drawable.play);
            }
        }
    };

    void initView(){
        play.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    controller.seekTo(seekBar.getProgress());
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        isRunning=false;
        super.onDestroy();
    }
}
