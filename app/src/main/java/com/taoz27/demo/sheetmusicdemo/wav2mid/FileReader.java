package com.taoz27.demo.sheetmusicdemo.wav2mid;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Dino on 29.04.2015.
 */
public class FileReader {

    public byte[] readFile(String filePath)
    {
        try {
            File file = new File(filePath);

            if(file.exists()) {
                InputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[(int) file.length()];
                fis.read(buffer, 0, buffer.length);
                fis.close();

//                InputStream stream=getClass().getResourceAsStream("/assets/TestFile.wav");
//                stream.read(buffer);
                return buffer;
            }
            else
            {
                Log.e("ERROR","File "+filePath+" doesn't exist");
                return null;
            }
        }
        catch(Exception ex)
        {
            Log.e("ERROR",ex.getMessage());
            return null;
        }

    }
}
