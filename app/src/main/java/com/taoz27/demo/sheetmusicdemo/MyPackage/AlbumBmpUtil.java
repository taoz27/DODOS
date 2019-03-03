package com.taoz27.demo.sheetmusicdemo.MyPackage;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.taoz27.demo.sheetmusicdemo.R;

import java.io.IOException;

/**
 * Created by taoz27 on 2017/4/10.
 */
public class AlbumBmpUtil {
    public static Bitmap getAlbumBmp(Context context,Uri uri){
        Bitmap bitmap=null;
        MediaMetadataRetriever myRetriever = new MediaMetadataRetriever();

        String location=uri.toString();
        if (location.startsWith("file:///android_asset/")) {
            AssetManager manager = context.getAssets();
            String filepath = location.replace("file:///android_asset/", "");
            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = manager.openFd(filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            myRetriever.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),
                    fileDescriptor.getDeclaredLength());
        }else {
            Uri selectedAudio = uri;
            myRetriever.setDataSource(context, selectedAudio);
        }

        byte[] artwork= myRetriever.getEmbeddedPicture();
        if (artwork != null) {
            bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
        } else {
            bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        }

        return bitmap;
    }
}
