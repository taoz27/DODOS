package com.taoz27.demo.sheetmusicdemo.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.taoz27.demo.sheetmusicdemo.R;
import com.taoz27.demo.sheetmusicdemo.app.main.MainActivity;
import com.taoz27.demo.sheetmusicdemo.sheet.ClefSymbol;
import com.taoz27.demo.sheetmusicdemo.sheet.MidiPlayer;
import com.taoz27.demo.sheetmusicdemo.sheet.TimeSigSymbol;

/** @class MidiSheetMusicActivity
 * This is the launch activity for MidiSheetMusic.
 * It simply displays the splash screen, and a button to choose a song.
 */
public class LogoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        new Handler().postDelayed(imageRunnable,500);
    }

    /** Start the ChooseSongActivity when the "Choose Song" button is clicked */
    private void chooseSong() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /** Load all the resource images */
    private void loadImages() {
        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);
        MidiPlayer.LoadImages(this);
    }

    Runnable imageRunnable=new Runnable() {
        @Override
        public void run() {
            loadImages();
            chooseSong();
        }
    };
}

