package com.taoz27.demo.sheetmusicdemo.MyPackage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.taoz27.demo.sheetmusicdemo.R;
import com.taoz27.demo.sheetmusicdemo.app.mp3.MusicDetailActivity;
import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;

/**
 * Created by taoz27 on 2017/4/6.
 */
public class MP3PlayerFragment extends Fragment implements View.OnClickListener{
    private MP3Player controller;
    private MusicFile currentMusic;

    private View mainLayout;
    private ImageView pauseBtn,album;
    TextView currentTime,totalTime,title;
    SeekBar progress;

    boolean isRunning;
    boolean isPausing=false;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pause:
                if (controller != null) {
                    if (controller.isPlaying()) {
                        controller.pause();
                        isPausing=true;
                        pauseBtn.setImageResource(R.drawable.play);
                    } else {
                        if (isPausing) {
                            controller.resume();
                        }else {
                            controller.startPlay();
                        }
                        pauseBtn.setImageResource(R.drawable.pause);
                    }
                }
                break;
        }
    }

    public MP3PlayerFragment(){
        if (controller==null){
            controller= MP3Player.getController(getActivity());
        }
//        controller.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                controller.playNext();
//            }
//        });
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainLayout=inflater.inflate(R.layout.fragment_mp3player,null);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(getContext(), MusicDetailActivity.class));
            }
        });
        album=(ImageView)mainLayout.findViewById(R.id.album);
        pauseBtn =(ImageView) mainLayout.findViewById(R.id.pause);
        currentTime=(TextView)mainLayout.findViewById(R.id.currentTime);
        totalTime=(TextView)mainLayout.findViewById(R.id.totalTime);
        title=(TextView)mainLayout.findViewById(R.id.title);
        progress=(SeekBar)mainLayout.findViewById(R.id.progress);

        initView();
        return mainLayout;
    }

    void initView(){
        pauseBtn.setOnClickListener(this);

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

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                if (controller == null||controller.getCurrentMusic()==null) return;
                if (currentMusic==null||currentMusic!=controller.getCurrentMusic()){
                    currentMusic=controller.getCurrentMusic();
                    title.setText(currentMusic.getDisplayName());
                    if (currentMusic.getAlbum()!=null)
                        album.setImageBitmap(currentMusic.getAlbum());
                    else
                        album.setImageResource(R.drawable.logo);
                }
                totalTime.setText(controller.getDuration() / 1000 / 60 + ":" + controller.getDuration() / 1000 % 60);
                currentTime.setText(controller.getCurrentPosition() / 1000 / 60 + ":" + controller.getCurrentPosition() / 1000 % 60);
                progress.setMax(controller.getDuration());
                progress.setProgress(controller.getCurrentPosition());
                if (controller.isPlaying())
                    pauseBtn.setImageResource(R.drawable.pause);
                else
                    pauseBtn.setImageResource(R.drawable.play);
            }
        }
    };

    @Override
    public void onDestroyView() {
        isRunning=false;
        super.onDestroyView();
    }
}
