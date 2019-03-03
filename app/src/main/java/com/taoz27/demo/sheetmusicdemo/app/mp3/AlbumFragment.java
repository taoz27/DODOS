package com.taoz27.demo.sheetmusicdemo.app.mp3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.taoz27.demo.sheetmusicdemo.MyPackage.CircleRotateView;
import com.taoz27.demo.sheetmusicdemo.MyPackage.MP3Player;
import com.taoz27.demo.sheetmusicdemo.R;

import loner.jni.ImageUtil;

/**
 * Created by taoz27 on 2017/11/10.
 */

public class AlbumFragment extends Fragment{
    private View mainView;
    private ImageView blurIv;
    private CircleRotateView crView;
    private Button loopBtn,speedBtn,settingBtn;

    private MP3Player player;
    private String curPlay="";
    private Bitmap curBlurBmp,defaultBmp;
    private boolean isRunning,loopMode;
    private int loopA,loopB,loopV=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_album,container,false);
        blurIv=(ImageView)mainView.findViewById(R.id.blur);
        crView=(CircleRotateView)mainView.findViewById(R.id.cr);
        loopBtn=(Button)mainView.findViewById(R.id.loop);
        speedBtn=(Button)mainView.findViewById(R.id.speed);
        settingBtn=(Button)mainView.findViewById(R.id.setting);
        loopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        switch (loopV){
                            case 0:
                                loopBtn.setText("A");
                                break;
                            case 1:
                                loopBtn.setText("B");
                                loopA=player.getCurrentPosition();
                                break;
                            case 2:
                                loopBtn.setText("C");
                                loopB=player.getCurrentPosition();
                                loopMode=true;
                                break;
                            case 3:
                                loopBtn.setText("LOOP");
                                loopMode=false;
                                break;
                        }
                        loopV++;loopV%=4;
                    }
                });
            }
        });

        player=MP3Player.getController(getActivity());
        crView.setAlbumPlayer(player);
        defaultBmp= BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        isRunning=true;
        loopMode=false;
        new Thread(){
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (curBlurBmp == null ||
                            (player != null && !curPlay.endsWith(player.getMusicName()))) {
                        curPlay=player.getMusicName();
                        if (player.getCurrentAlbum() != null) {
                            curBlurBmp = ImageUtil.fastBlur(player.getCurrentAlbum(), 50);
                        } else {
                            curBlurBmp = ImageUtil.fastBlur(defaultBmp, 50);
                        }
                        if (!isRunning)return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                blurIv.setImageBitmap(curBlurBmp);
                            }
                        });
                    }
                    if (loopMode){
                        if (player.getCurrentPosition()>loopB){
                            if (!isRunning)return;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    player.seekTo(loopA);
                                }
                            });
                        }
                    }
                }
            }
        }.start();

        return mainView;
    }

    @Override
    public void onDestroy() {
        isRunning=false;
        super.onDestroy();
    }
}
