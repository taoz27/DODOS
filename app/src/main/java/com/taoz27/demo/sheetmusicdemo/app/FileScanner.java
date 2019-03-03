package com.taoz27.demo.sheetmusicdemo.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import com.taoz27.demo.sheetmusicdemo.MyPackage.SDMusicLoader;
import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/** @class ScanMidiFiles
 * The ScanMidiFiles class is used to scan for midi files
 * on a background thread.
 */
public class FileScanner extends AsyncTask<Integer, Integer, ArrayList<MusicFile> > {
    private ArrayList<MusicFile> songlist;
    private File rootdir;
    private Activity activity;
    private OnLoadDone onLoadDone;
    private MusicFile.TYPE type;

    public FileScanner() {}

    public void setActivity(Activity activity, OnLoadDone onLoadDone, MusicFile.TYPE type) {
        this.activity = activity;
        this.onLoadDone=onLoadDone;
        this.type=type;
    }

    @Override
    protected void onPreExecute() {
        songlist = new ArrayList<>();
        try {
            rootdir = Environment.getExternalStorageDirectory();
        }
        catch (Exception e) {}
    }

    @Override
    protected ArrayList<MusicFile> doInBackground(Integer... params) {
        loadAssetMidiFiles();
        loadMidiFilesFromProvider(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        return songlist;
    }

    @Override
    protected void onPostExecute(ArrayList<MusicFile> result) {
        if (onLoadDone!=null){
            onLoadDone.onLoadDone(result);
        }
    }

    @Override
    protected void onCancelled() {
        this.activity = null;
    }
    /** Load all the sample midi songs from the assets directory into songlist.
     *  Look for files ending with ".mid"
     */

    void loadAssetMidiFiles() {
        try {
            AssetManager assets = activity.getResources().getAssets();
            String[] files = assets.list("");
            for (String path: files) {
                switch (type){
                    case MP3:
                        if (path.endsWith(".mp3")) {
                            Uri uri = Uri.parse("file:///android_asset/" + path);
                            MusicFile file = new MusicFile(type,uri, path);
                            songlist.add(file);
                        }
                        break;
                    case WAV:
                        if (path.endsWith(".wav")) {
                            Uri uri = Uri.parse("file:///android_asset/" + path);
                            MusicFile file = new MusicFile(type,uri, path);
                            songlist.add(file);
                        }
                        break;
                    case MID:
                        if (path.endsWith(".mid")) {
                            Uri uri = Uri.parse("file:///android_asset/" + path);
                            MusicFile file = new MusicFile(type,uri, path);
                            songlist.add(file);
                        }
                        break;
                }
            }
        }
        catch (IOException e) {
        }
    }


    /** Look for midi files (with mime-type audio/midi) in the
     * internal/external storage. Add them to the songlist.
     */
    private void loadMidiFilesFromProvider(Uri content_uri) {
        ContentResolver resolver = activity.getContentResolver();
        SDMusicLoader loader=SDMusicLoader.getInstance(resolver);
        loader.getMusicList(songlist,type);
//        String columns[] = {
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.MIME_TYPE
//        };
//        String selection = null;
//        switch (type){
//            case MP3:
//                selection=MediaStore.Audio.Media.MIME_TYPE + " LIKE '%mp3%'";
//                break;
//            case WAV:
//                selection=MediaStore.Audio.Media.MIME_TYPE + " LIKE '%wav%'";
//                break;
//            case MID:
//                selection=MediaStore.Audio.Media.MIME_TYPE + " LIKE '%mid%'";
//                break;
//        }
//        Cursor cursor = resolver.query(content_uri, columns, selection, null, null);
//        if (cursor == null) {
//            return;
//        }
//        if (!cursor.moveToFirst()) {
//            cursor.close();
//            return;
//        }
//
//        do {
//            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
//            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//            int mimeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
//
//            long id = cursor.getLong(idColumn);
//            String title = cursor.getString(titleColumn);
//            String mime = cursor.getString(mimeColumn);
//
////            if (mime.endsWith("/midi") || mime.endsWith("/mid")) {
//                Uri uri = Uri.withAppendedPath(content_uri, "" + id);
//                MusicFile file = new MusicFile(type,uri, title);
//                songlist.add(file);
////            }
//        } while (cursor.moveToNext());
//        cursor.close();
    }

    /** Given a directory, add MIDI files (ending in .mid) to the songlist.
     * If the directory contains subdirectories, call this method recursively.
     */
    private void loadMidiFilesFromDirectory(File dir, int depth) throws IOException {
        if (isCancelled()) {
            return;
        }
        if (depth > 10) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            if (isCancelled()) {
                return;
            }
            if (file.getName().endsWith(".mid") || file.getName().endsWith(".MID") ||
                file.getName().endsWith(".midi")) {
                Uri uri = Uri.parse("file://" + file.getAbsolutePath());
                String displayName = uri.getLastPathSegment();
                MusicFile song = new MusicFile(type,uri, displayName);
                songlist.add(song);
            }
        }
        for (File file : files) {
            if (isCancelled()) {
                return;
            }
            try {
                if (file.isDirectory()) {
                    loadMidiFilesFromDirectory(file, depth+1);
                }
            }
            catch (Exception e) {}
        }
    }

    public interface OnLoadDone{
        void onLoadDone(ArrayList<MusicFile> data);
    }
}
