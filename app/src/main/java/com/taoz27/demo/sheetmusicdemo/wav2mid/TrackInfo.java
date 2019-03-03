package com.taoz27.demo.sheetmusicdemo.wav2mid;

import java.io.Serializable;

/**
 * Created by Marc on 27.05.2015.
 */
public class TrackInfo implements Serializable {
    private boolean enabled;
    private int start_at;
    private String track_name;

    public TrackInfo(){
        enabled = true;
        start_at = 0;
        track_name = "new track";
    }

    public TrackInfo(TrackInfo ti) {
        enabled = ti.getEnabled();
        start_at = ti.getStartAt();
        track_name = "copy of " + ti.getTrackName();
    }

    public void setEnabled(boolean enable) {
        enabled = enable;
    }
    public void setStartAt(int startAt) {
        start_at = startAt;
    }

    public int getStartAt(){
        return start_at;
    }
    public boolean getEnabled() {
        return enabled;
    }

    public String getTrackName() {
        return track_name;
    }

    public void setTrackName(String name) {
        track_name = name;
    }
}
