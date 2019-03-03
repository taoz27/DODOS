/**
 * 功能点：
 * 1.提供将midi文件转换为整页那种的歌谱
 * 2.提供一个页面，用于播放midi文件，页面包括：
 *      五线谱、钢琴琴键
 *
 * 难点：
 * 1.整页的显示
 * 2.琴键时长的获取及显示
 *
 * 1.修改已有界面的颜色
 */
package com.taoz27.demo.sheetmusicdemo.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taoz27.demo.sheetmusicdemo.R;
import com.taoz27.demo.sheetmusicdemo.sheet.ClefSymbol;
import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;
import com.taoz27.demo.sheetmusicdemo.sheet.MidiFile;
import com.taoz27.demo.sheetmusicdemo.sheet.MidiFileException;
import com.taoz27.demo.sheetmusicdemo.sheet.MidiOptions;
import com.taoz27.demo.sheetmusicdemo.sheet.MidiPlayer;
import com.taoz27.demo.sheetmusicdemo.sheet.Piano;
import com.taoz27.demo.sheetmusicdemo.sheet.SheetMusic;
import com.taoz27.demo.sheetmusicdemo.sheet.TimeSigSymbol;

import java.util.zip.CRC32;

import static com.taoz27.demo.sheetmusicdemo.app.SheetMusicActivity.MidiTitleID;

public class PlayActivity extends Activity {
    public static PlayActivity playActivity;

    RelativeLayout layout;
    SheetMusic sheet;
    MidiFile midifile;
    MidiOptions options;
    MidiPlayer player;
    Piano piano;
    LinearLayout back;
    TextView title;

    private long midiCRC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playActivity=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.avtivity_play);
        back=(LinearLayout)findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        title=(TextView)findViewById(R.id.title);

        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);

        // Parse the MidiFile from the raw bytes
        Uri uri = this.getIntent().getData();
        String titleT = this.getIntent().getStringExtra(MidiTitleID);
        if (titleT == null) {
            titleT = uri.getLastPathSegment();
        }
        MusicFile file = new MusicFile(MusicFile.TYPE.MID,uri, titleT);
        this.setTitle(titleT);
        byte[] data;
        try {
            data = file.getData(this);
            midifile = new MidiFile(data, titleT);
        }
        catch (MidiFileException e) {
            this.finish();
            return;
        }
        title.setText(midifile.toString());

        // Initialize the settings (MidiOptions).
        // If previous settings have been saved, used those
        options = new MidiOptions(midifile);
        CRC32 crc = new CRC32();
        crc.update(data);
        midiCRC = crc.getValue();
        SharedPreferences settings = getPreferences(0);
        options.scrollVert = settings.getBoolean("scrollVert", false);
        options.instruments[0]=27;
        options.shade1Color = settings.getInt("shade1Color", options.shade1Color);
        options.shade2Color = settings.getInt("shade2Color", options.shade2Color);
        options.showPiano = settings.getBoolean("showPiano", true);
        String json = settings.getString("" + midiCRC, null);
        MidiOptions savedOptions = MidiOptions.fromJson(json);
        if (savedOptions != null) {
            options.merge(savedOptions);
        }
        createView();
        createSheetMusic(options);
    }

    /* Create the MidiPlayer and Piano views */
    void createView() {
//        layout = new RelativeLayout(this);
//        piano = new Piano(this);
//        player=new MidiPlayer(this);
//        setContentView(layout);
//        layout.requestLayout();
        piano=(Piano)findViewById(R.id.piano);
        player=(MidiPlayer)findViewById(R.id.player);

    }

    private void createSheetMusic(MidiOptions options) {
        if (sheet != null) {
//            layout.removeView(sheet);
        }
        if (!options.showPiano) {
            piano.setVisibility(View.GONE);
        }
        else {
            piano.setVisibility(View.VISIBLE);
        }

//        sheet = new SheetMusic(this);
        sheet=(SheetMusic)findViewById(R.id.sheet);
        sheet.init(midifile, options);

        player.SetMidiFile(midifile,options,sheet);
        player.SetPiano(piano);

        piano.SetMidiFile(midifile, options, player);
        piano.SetShadeColors(options.shade1Color, options.shade2Color);

//        piano.setId(View.generateViewId());
//        layout.addView(piano);

//        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.BELOW,piano.getId());
//        layout.addView(sheet,params);

//        params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        layout.addView(player,params);
//        layout.requestLayout();
        sheet.callOnDraw();
    }

    /** When this activity resumes, redraw all the views */
    @Override
    protected void onResume() {
        super.onResume();
//        layout.requestLayout();
        player.invalidate();
        piano.invalidate();
        if (sheet != null) {
            sheet.invalidate();
        }
//        layout.requestLayout();
    }

    /** When this activity pauses, stop the music */
    @Override
    protected void onPause() {
        if (player!=null)player.Pause();
        super.onPause();
    }
}
