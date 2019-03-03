package com.taoz27.demo.sheetmusicdemo.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.taoz27.demo.sheetmusicdemo.R;
import com.taoz27.demo.sheetmusicdemo.app.unused.HelpActivity;
import com.taoz27.demo.sheetmusicdemo.app.unused.SettingsActivity;
import com.taoz27.demo.sheetmusicdemo.sheet.ClefSymbol;
import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;
import com.taoz27.demo.sheetmusicdemo.sheet.MidiFile;
import com.taoz27.demo.sheetmusicdemo.sheet.MidiFileException;
import com.taoz27.demo.sheetmusicdemo.sheet.MidiOptions;
import com.taoz27.demo.sheetmusicdemo.sheet.SheetMusic;
import com.taoz27.demo.sheetmusicdemo.sheet.TimeSigSymbol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.zip.CRC32;

/** @class SheetMusicActivity
 *
 * The SheetMusicActivity is the main activity. The main components are:
 * - MidiPlayer : The buttons and speed bar at the top.
 * - Piano : For highlighting the piano notes during playback.
 * - SheetMusic : For highlighting the sheet music notes during playback.
 *
 */
public class SheetMusicActivity extends Activity {

    public static final String MidiTitleID = "MidiTitleID";
    public static final int settingsRequestCode = 1;

    private SheetMusic sheet;    /* The sheet music */
    private RelativeLayout layout; /* THe layout */
    private MidiFile midifile;   /* The midi file to play */
    private MidiOptions options; /* The options for sheet music and sound */
    private long midiCRC;      /* CRC of the midi bytes */
    private Uri uri;
    private String title;
    private FloatingActionButton playFab,saveFab;
    private RelativeLayout.LayoutParams playParams,saveParams;

     /** Create this SheetMusicActivity.
      * The Intent should have two parameters:
      * - data: The uri of the midi file to open.
      * - MidiTitleID: The title of the song (String)
      */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);

        // Parse the MidiFile from the raw bytes
        uri = this.getIntent().getData();
        title = this.getIntent().getStringExtra(MidiTitleID);
        if (title == null) {
            title = uri.getLastPathSegment();
        }
        MusicFile file = new MusicFile(MusicFile.TYPE.MID,uri, title);
        byte[] data;
        try {
            data = file.getData(this);
            midifile = new MidiFile(data, title);
        }
        catch (MidiFileException e) {
            this.finish();
            return;
        }

        // Initialize the settings (MidiOptions).
        // If previous settings have been saved, used those
        options = new MidiOptions(midifile);
        CRC32 crc = new CRC32();
        crc.update(data); 
        midiCRC = crc.getValue();
        SharedPreferences settings = getPreferences(0);
        options.scrollVert = settings.getBoolean("scrollVert", true);
        options.shade1Color = settings.getInt("shade1Color", options.shade1Color);
        options.shade2Color = settings.getInt("shade2Color", options.shade2Color);
        options.showPiano = settings.getBoolean("showPiano", false);
        String json = settings.getString("" + midiCRC, null);
        MidiOptions savedOptions = MidiOptions.fromJson(json);
        if (savedOptions != null) {
            options.merge(savedOptions);
        }
        createView();
        createSheetMusic(options);
    }

    private void createFab(){
        playFab=new FloatingActionButton(this);
        playFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        playFab.setImageResource(R.drawable.play);
//        playFab.setRippleColor(Color.parseColor("#bebebe"));
        playFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri, SheetMusicActivity.this, PlayActivity.class);
                intent.putExtra(SheetMusicActivity.MidiTitleID, title);
                startActivity(intent);
            }
        });
        playParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        playParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        playParams.setMargins(0,0,80,60);

        saveFab=new FloatingActionButton(this);
        saveFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        saveFab.setImageResource(R.drawable.save);
//        saveFab.setRippleColor(Color.parseColor("#bebebe"));
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveImagesDialog();
            }
        });
        saveParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        saveParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        saveParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        saveParams.setMargins(0,0,80,200);
    }
    
    /* Create the MidiPlayer and Piano views */
    void createView() {
        layout = new RelativeLayout(this);
        setContentView(layout);
        createFab();
    }


    /** Create the SheetMusic view with the given options */
    private void createSheetMusic(MidiOptions options) {
        if (sheet != null) {
            layout.removeView(sheet);
        }
        sheet = new SheetMusic(this);
        sheet.init(midifile, options);
        layout.addView(sheet);
        layout.addView(playFab,playParams);
        layout.addView(saveFab,saveParams);
        layout.requestLayout();
        sheet.callOnDraw();
    }


//    /** Always display this activity in landscape mode. */
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
//
//    /** When the menu button is pressed, initialize the menus. */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.sheet_menu, menu);
//        return true;
//    }
//
//    /** Callback when a menu item is selected.
//     *  - Choose Song : Choose a new song
//     *  - Song Settings : Adjust the sheet music and sound options
//     *  - Save As Images: Save the sheet music as PNG images
//     *  - Help : Display the HTML help screen
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//          case R.id.choose_song:
//            chooseSong();
//            return true;
//          case R.id.song_settings:
//            changeSettings();
//            return true;
//          case R.id.save_images:
//            showSaveImagesDialog();
//            return true;
//          case R.id.help:
//            showHelp();
//            return true;
//          default:
//            return super.onOptionsItemSelected(item);
//        }
//    }


    /** To change the sheet music options, start the SettingsActivity.
     *  Pass the current MidiOptions as a parameter to the Intent.
     *  Also pass the 'default' MidiOptions as a parameter to the Intent.
     *  When the SettingsActivity has finished, the onActivityResult()
     *  method will be called.
     */
    private void changeSettings() {
        MidiOptions defaultOptions = new MidiOptions(midifile);
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SettingsActivity.settingsID, options);
        intent.putExtra(SettingsActivity.defaultSettingsID, defaultOptions);
        startActivityForResult(intent, settingsRequestCode);
    }


    /* Show the "Save As Images" dialog */
    private void showSaveImagesDialog() {
         LayoutInflater inflator = LayoutInflater.from(this);
         final View dialogView= inflator.inflate(R.layout.save_images_dialog, null);
         final EditText filenameView = (EditText)dialogView.findViewById(R.id.save_images_filename);
         filenameView.setText(midifile.getFileName().replace("_", " ") );
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle(R.string.save_images_str);
         builder.setView(dialogView);
         builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface builder, int whichButton) {
                 saveAsImages(filenameView.getText().toString());
             }
         });
         builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface builder, int whichButton) {
             }
         });
         AlertDialog dialog = builder.create();
         dialog.show();
    }


    /* Save the current sheet music as PNG images. */
    private void saveAsImages(String name) {
        String filename = name;
        try {
            filename = URLEncoder.encode(name, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
        }
        if (!options.scrollVert) {
            options.scrollVert = true;
            createSheetMusic(options);
        }
        try {
            int numpages = sheet.GetTotalPages();
            for (int page = 1; page <= numpages; page++) {
                Bitmap image= Bitmap.createBitmap(SheetMusic.PageWidth + 40, SheetMusic.PageHeight + 40, Bitmap.Config.ARGB_8888);
                Canvas imageCanvas = new Canvas(image);
                sheet.DrawPage(imageCanvas, page);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/MidiSheetMusic");
                File file = new File(path, "" + filename + page + ".png");
                path.mkdirs();
                OutputStream stream = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 0, stream);
                image = null;
                stream.close();
    
                // Inform the media scanner about the file
                MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null, null);
            }
        }
        catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error saving image to file " + Environment.DIRECTORY_PICTURES + "/MidiSheetMusic/" + filename  + ".png");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        catch (NullPointerException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ran out of memory while saving image to file " + Environment.DIRECTORY_PICTURES + "/MidiSheetMusic/" + filename  + ".png");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    /** Show the HTML help screen. */
    private void showHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    /** This is the callback when the SettingsActivity is finished.
     *  Get the modified MidiOptions (passed as a parameter in the Intent).
     *  Save the MidiOptions.  The key is the CRC checksum of the midi data,
     *  and the value is a JSON dump of the MidiOptions.
     *  Finally, re-create the SheetMusic View with the new options.
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
        if (requestCode != settingsRequestCode) {
            return;
        }
        options = (MidiOptions) 
            intent.getSerializableExtra(SettingsActivity.settingsID);

        // Check whether the default instruments have changed.
        for (int i = 0; i < options.instruments.length; i++) {
            if (options.instruments[i] !=  
                midifile.getTracks().get(i).getInstrument()) {
                options.useDefaultInstruments = false;
            }
        }
        // Save the options. 
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putBoolean("scrollVert", options.scrollVert);
        editor.putInt("shade1Color", options.shade1Color);
        editor.putInt("shade2Color", options.shade2Color);
        editor.putBoolean("showPiano", options.showPiano);
        String json = options.toJson();
        if (json != null) {
            editor.putString("" + midiCRC, json);
        }
        editor.commit();

        // Recreate the sheet music with the new options
        createSheetMusic(options);
    }

    /** When this activity resumes, redraw all the views */
    @Override
    protected void onResume() {
        super.onResume();
        layout.requestLayout();
//        recorder.invalidate();
//        player.invalidate();
//        piano.invalidate();
        if (sheet != null) {
            sheet.invalidate();
        }
        layout.requestLayout();
    }

    /** When this activity pauses, stop the music */
    @Override
    protected void onPause() {
//        if (recorder != null) {
//            recorder.pause();
//        }
        super.onPause();
    }

    private double[][] getFFT2ChromaMatrix() {
        double[][] fft2chromaMatrix = new double[12][8192];
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("fft2chromamatrix.txt")));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            int i = 0;
            while (mLine != null) {
                //process line
                String[] strArr = mLine.split(",");
                for(int j = 0; j < strArr.length; j++) {
                    fft2chromaMatrix[i][j] = Double.parseDouble(strArr[j]);
                }
                mLine = reader.readLine();
                i++;
            }
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return fft2chromaMatrix;
    }
}

