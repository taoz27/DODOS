package com.taoz27.demo.sheetmusicdemo.MyPackage;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.util.Log;
import android.widget.Toast;

import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by taoz27 on 2017/4/16.
 */
public class MP3Player implements CircleRotateView.AlbumPlayer{
    private static MP3Player controller;
    private static Context context;
    private static MediaPlayer player;
    private static MusicFile currentMusic;
    public static short equalizerLevel[]=new short[2];
    public static int equalizerFrequency[];
    private static Equalizer equalizer;
    private static BassBoost bassBoost;
    private static List<MusicFile> musicList=new ArrayList<>();
    private static int currentPlayingNumbering=-1;

    public enum PlayMode{SINGLE_CYCLE,ALL_CYCLE,SHUFFLE_PLAYBACK}
    PlayMode currentPlayMode= PlayMode.ALL_CYCLE;

    public static MP3Player getController(Context c){
        if (controller==null){
            context=c;
            controller=new MP3Player();
        }
        return controller;
    }

    float speed=1.0f;
    public void changeSpeed(float speed){
        this.speed=speed;
        int seek=getCurrentPosition();
        startPlay(currentMusic);
        seekTo(seek);
    }

    public void setMusicList(List<MusicFile> musicList){
        this.musicList=musicList;
    }
    public void playFore(){
        if (currentPlayingNumbering<=0){
            startPlay(musicList.size() - 1);
        }else {
            startPlay(currentPlayingNumbering-1);
        }
    }
    public void playNext(){
        if (currentPlayMode== PlayMode.ALL_CYCLE) {
            if (currentPlayingNumbering + 1 < musicList.size()) {
                startPlay(currentPlayingNumbering + 1);
            }else {
                startPlay(0);
            }
        }else if (currentPlayMode== PlayMode.SHUFFLE_PLAYBACK){
            startPlay(new Random().nextInt(musicList.size()));
        }else {
            if (currentPlayingNumbering + 1 < musicList.size()) {
                startPlay(currentPlayingNumbering + 1);
            }else {
                startPlay(0);
            }
        }
    }
    public void startPlay(){
        startPlay(0);
    }
    public void startPlay(int i){
        if (currentPlayingNumbering==i)return;
        currentPlayingNumbering=i;
        currentMusic=musicList.get(i);
        startPlay(currentMusic);
    }
    public void startPlay(MusicFile currentMusic){
        play(currentMusic);
        // TODO: 2017/5/7 处理这个
//        MusicPlayerApplication.myView.setBitmap(currentMusic.getAlbumBitmap());
//        MusicPlayerApplication.myView.rotate(true,0);
    }

    public void startPlay(String path){
//        try {
//            player.reset();
//            player.setDataSource(path);
//            player.prepare();
//            player.start();
//            totalTime.setText(player.getDuration() / 1000 / 60 + ":" + player.getDuration() / 1000 % 60);
//            progress.setMax(player.getDuration());
//            MusicPlayerApplication.myView.setBitmap(BitmapFactory.decodeResource(getActivity().getResources(), android.R.mipmap.sym_def_app_icon));
//            MusicPlayerApplication.myView.rotate(true,0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        isRunning=true;
//        new Thread(){
//            @Override
//            public void run() {
//                while (isRunning){
//                    try {
//                        handler.sendEmptyMessage(0x123);
//                        sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
    }

    public void changeMode(){
        if (currentPlayMode == PlayMode.SINGLE_CYCLE) {
            currentPlayMode = PlayMode.ALL_CYCLE;
            setLooping(false);
        } else if (currentPlayMode == PlayMode.ALL_CYCLE) {
            currentPlayMode = PlayMode.SHUFFLE_PLAYBACK;
            setLooping(false);
        } else if (currentPlayMode == PlayMode.SHUFFLE_PLAYBACK) {
            currentPlayMode = PlayMode.SINGLE_CYCLE;
            setLooping(true);
        }
    }

    public PlayMode getCurrentPlayMode(){
        return currentPlayMode;
    }

    private void setDataSource(MusicFile musicFile) throws IOException{
        String location=musicFile.getUri().toString();
        if (location.startsWith("file:///android_asset/")) {
            AssetManager manager = context.getAssets();
            String filepath = location.replace("file:///android_asset/", "");
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (needExit){
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                    playNext();
                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e(this.toString(), what + "  " + extra);
                    return false;
                }
            });
            AssetFileDescriptor fileDescriptor = manager.openFd(filepath);
            player.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),
                    fileDescriptor.getDeclaredLength());
        }else {
            player.setDataSource(musicFile.getUri().toString());
        }
    }

    public void play(MusicFile currentMusic){
        this.currentMusic=currentMusic;
        try {
            player.reset();

            setDataSource(currentMusic);
//            player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying(){
        return player.isPlaying();
    }

    @Override
    public String getMusicName() {
        return currentMusic.getDisplayName();
    }

    @Override
    public Bitmap getCurrentAlbum() {
        return currentMusic.getAlbum();
    }

    public void pause(){
        player.pause();
    }

    public void resume(){
        player.start();
    }

    public void setLooping(boolean looping){
        player.setLooping(looping);
    }

    public boolean getLooping(){
        return player.isLooping();
    }

    public MusicFile getCurrentMusic(){
        return currentMusic;
    }

    public void seekTo(int progress){
        player.seekTo(progress);
    }

    public int getDuration(){
        return player.getDuration();
    }

    public int getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public int getBandLevel(int brand){
        return equalizer.getBandLevel((short)brand);
    }

    public void setBandLevel(int brand,int level){
        equalizer.setBandLevel((short)brand,(short)level);
//        SharedPreferencesController.getController(context).write(Config.SOUND_EFFECT_KEY_BRAND + brand, level);
    }

    public void setStrength(int level){
        bassBoost.setStrength((short) level);
//        SharedPreferencesController.getController(context).write(Config.SOUND_EFFECT_KEY_BASS,level);
    }

    public void close(){
        if (equalizer!=null) equalizer.release();
        if (bassBoost!=null)bassBoost.release();
        player.stop();
        player.release();
    }

    boolean needExit=false, cancel =false;
    Thread timeThread;
    public void setTimer(final int time, final boolean isStopRightNow){
        if (timeThread!=null&&timeThread.isAlive()){
            cancel =true;
        }
        if (time==0){
            Toast.makeText(context,"定时停止已取消", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"程序将在"+time+"分钟后退出", Toast.LENGTH_SHORT).show();
            timeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        int i=0;
                        while (!cancel){
                            sleep(100);
                            if (++i>=time*600){
                                if (isStopRightNow||!isPlaying()){
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }else {
                                    needExit = true;
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            cancel =false;
            timeThread.start();
        }
    }

    private MP3Player(){
        initPlayer();
        initEqualizer();
        initBassBoost();
    }

    private void initPlayer(){
        if (player!=null){
            player.release();
            player=null;
        }
        player=new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (needExit){
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                playNext();
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(this.toString(), what + "  " + extra);
                return false;
            }
        });
    }
    private void initEqualizer() {
        equalizer = new Equalizer(0, player.getAudioSessionId());
        equalizer.setEnabled(true);
        equalizerLevel = equalizer.getBandLevelRange();
        Log.e(this.toString(),equalizerLevel[0]+" "+equalizerLevel[1]);
        short brands = equalizer.getNumberOfBands();
        equalizerFrequency=new int[brands];
        for (short i = 0; i < brands; i++) {
            equalizerFrequency[i]=equalizer.getCenterFreq(i) / 1000;
            equalizer.setBandLevel(i, (short)0);
        }
    }
    private void initBassBoost() {
        bassBoost = new BassBoost(0, player.getAudioSessionId());
        bassBoost.setEnabled(true);
        bassBoost.setStrength((short) 0);
    }
}
