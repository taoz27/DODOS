package com.taoz27.demo.sheetmusicdemo.app.mp3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taoz27.demo.sheetmusicdemo.MyPackage.MP3Player;
import com.taoz27.demo.sheetmusicdemo.R;
import com.taoz27.demo.sheetmusicdemo.lrc.ILrcView;
import com.taoz27.demo.sheetmusicdemo.lrc.LrcDataBuilder;
import com.taoz27.demo.sheetmusicdemo.lrc.LrcRow;
import com.taoz27.demo.sheetmusicdemo.lrc.LrcView;

import java.util.List;

/**
 * Created by taoz27 on 2017/11/10.
 */

public class LyricFragment extends Fragment {
    private View mainView;
    private LrcView lrcView;

    private MP3Player player;
    private boolean isRunning;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_lyric,container,false);
        lrcView=(LrcView)mainView.findViewById(R.id.lrc);

        List<LrcRow> lrcRows = new LrcDataBuilder().BuiltFromAssets(getActivity(), "test2.lrc");
        lrcView.setLrcData(lrcRows);
        player=MP3Player.getController(getActivity());
        lrcView.setLrcViewSeekListener(new ILrcView.LrcViewSeekListener() {
            @Override
            public void onSeek(LrcRow currentlrcrow, long Currenselectrowtime) {
                if (player != null) {
                    player.seekTo((int) Currenselectrowtime);
                }
            }
        });
        isRunning=true;
        new Thread(){
            @Override
            public void run() {
                try {
                    while (isRunning) {
                        sleep(100);
                        if (player!=null&&player.isPlaying()){
                            if (!isRunning)return;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lrcView.seekLrcToTime(player.getCurrentPosition());
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
