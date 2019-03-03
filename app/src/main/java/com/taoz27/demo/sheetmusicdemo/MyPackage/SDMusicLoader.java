package com.taoz27.demo.sheetmusicdemo.MyPackage;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;

import java.util.List;

/**
 * Created by taoz27 on 2017/4/16.
 */
public class SDMusicLoader {
    private static SDMusicLoader instance;

    private static ContentResolver contentResolver;

    private Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
    };
    private String sortOrder = MediaStore.Audio.Media.DATA;

    public static SDMusicLoader getInstance(ContentResolver resolver){
        if (instance==null){
            contentResolver=resolver;
            instance=new SDMusicLoader();
        }
        return instance;
    }

    public void getMusicList(List<MusicFile> musicList, MusicFile.TYPE type){
        loadMusics(musicList,type);
    }

    public MusicFile loadMusicByPath(String path, MusicFile.TYPE targetType){
        MusicFile musicFile=null;
        Cursor cursor=contentResolver.query(contentUri,projection,"_data like ?",new String[]{"%"+path+"%"},null);
        if (cursor!=null&&cursor.moveToFirst()){
            int displayNameCol = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int urlCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            String title = cursor.getString(displayNameCol);
            String url = cursor.getString(urlCol);
            Uri uri=Uri.parse(url);
            musicFile = new MusicFile(targetType, uri,title);
        }else {
            Log.e(this.toString(),"loadMusicByPath fail:"+path);
        }
        return musicFile;
    }

    private void loadMusics(List<MusicFile> musicList, MusicFile.TYPE targetType){
        Cursor cursor = contentResolver.query(contentUri, projection, null, null, sortOrder);
        if (cursor == null) {
            Log.e(this.toString(), "query error!");
        } else if (!cursor.moveToFirst()) {
            Log.e(this.toString(), "query result is null!");
        } else {
            int displayNameCol = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int urlCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String title = cursor.getString(displayNameCol);
                String url = cursor.getString(urlCol);
                if (title.endsWith(".mp3")&&targetType== MusicFile.TYPE.MP3 ||
                        title.endsWith(".mid")&&targetType== MusicFile.TYPE.MID||
                        title.endsWith(".wav")&&targetType== MusicFile.TYPE.WAV){
                    Uri uri=Uri.parse(url);
                    MusicFile MusicFile = new MusicFile(targetType,uri,title);
                    musicList.add(MusicFile);
                }else {
                    continue;
                }
            } while (cursor.moveToNext());
        }
    }
}
