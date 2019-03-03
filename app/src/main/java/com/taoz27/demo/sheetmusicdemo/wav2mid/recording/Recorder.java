package com.taoz27.demo.sheetmusicdemo.wav2mid.recording;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.text.format.Time;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dino on 22.04.2015.
 */
public class Recorder {

PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private ExtAudioRecorder recorder;
    private String filePath;
    private boolean uncompressed = true;
    private int audioSource =  MediaRecorder.AudioSource.MIC;
    private int sampleRate = 44100;
    private long elapsedSeconds;
    private Time startTime = new Time();
    private Time endTime = new Time();
    private Timer timer;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO ;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    public Recorder (String filePath) {
            recorder =  new  ExtAudioRecorder(    uncompressed,
               audioSource,
                sampleRate,
                channelConfig,
                audioFormat);
        this.filePath = filePath;
        recorder.setOutputFile(filePath);

    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isUncompressed() {
        return uncompressed;
    }

    public void setUncompressed(boolean uncompressed) {
        this.uncompressed = uncompressed;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void recordForNSeconds(int n)
    {

         if(n == 0)
             throw new InvalidParameterException("duration should be greater than 0!");
       startRecording();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
               stopRecording();
            }
        };
        timer.schedule(timerTask,n*1000);


    }

    public void startRecording()
    {
        recorder.prepare();
        recorder.start();
        startTime.setToNow();
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
               Time time = new Time();
                time.setToNow();
                long oldTime = elapsedSeconds;
                elapsedSeconds = (time.toMillis(true) - startTime.toMillis(true)) / 1000;
                pcs.firePropertyChange("elapsedSeconds",oldTime,elapsedSeconds);
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void stopRecording()
    {
        recorder.stop();
        recorder.release();
       timer.cancel();
    }

    public boolean isRecording()
    {
        return recorder.getState() == ExtAudioRecorder.State.RECORDING;
    }


    public boolean deleteLastRecording()
    {
        boolean deleted = false;
        File file = new File(filePath);
        if(file.exists())
        {
            deleted = file.delete();
        }
        return deleted;
    }

    public void addRecordingTimeChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }


}
