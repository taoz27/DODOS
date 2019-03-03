package com.taoz27.demo.sheetmusicdemo.wav2mid.midi;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/*
 *
 */
public class MidiValues {
    private int beatsPerMinute;
    private int chunkSize;   // 1300        44.100 / 1300 = 34. 1 second / 34 = 0,029 second = 1 note
    private int sampleRate;  // 44.100 values / second
    private double chunkLength;
    private List<Integer> midiNum;

    public MidiValues(int bpm_, int chunkSize_, int sampleRate_) {
        beatsPerMinute = bpm_;
        chunkSize = chunkSize_;
        sampleRate = sampleRate_;
        midiNum = new ArrayList<Integer>();

        chunkLength = 1 / ( (double) sampleRate / (double) chunkSize);
    }

    public void addMidiNum(int midiNum_)
    {
        midiNum.add(midiNum_);
    }

    public List<SimpleEntry<Integer,Integer>> generateNoteMap()
    {
        List<SimpleEntry<Integer,Integer>> noteMap = new ArrayList<SimpleEntry<Integer,Integer>>();

        int lastNote = 0;
        int actualNumb = 0;
        SimpleEntry<Integer, Integer> lastSe =  null;
        for(int i = 0; i < midiNum.size(); i++)
        {
            if(lastSe == null || lastSe.getKey() != midiNum.get(i))
            {
                lastSe = new SimpleEntry<Integer, Integer>(midiNum.get(i), 1);
                noteMap.add(lastSe);
            }
            else
                lastSe.setValue(lastSe.getValue()+1);
        }

        return noteMap;
    }

    public double getNoteLength(int numb)
    {
        return chunkLength * numb;
    }

    public int getBeatsPerMinute()
    {
        return beatsPerMinute;
    }
    public List<Integer> getMidiValues()
    {
        return midiNum;
    }

    public void setMidiValues(List<Integer> val) {midiNum = val;}

}