package com.taoz27.demo.sheetmusicdemo.sheet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rpang on 10/19/15.
 */
public class Segment {
    private int startTime; // Start time (inclusive) of this segment in terms of pulses.
    private int endTime; // End time (inclusive) of this segment in terms of pulses.
    private Set<Integer> noteNumbers; // Numbers of all notes in this segment.
    private Set<MidiNote> notes; // All notes in this segment.
    private double[] chromaFeature  = new double[12];

    public Segment(int startTime) {
        this.startTime = startTime;
        this.endTime = startTime;
        this.noteNumbers = new HashSet<Integer>();
        this.notes = new HashSet<MidiNote>();
        for(int i = 0; i < chromaFeature.length; i++) {
            chromaFeature[i] = 0.0;
        }
    }

    public boolean complete(int endTime) {
        if(setEndTime(endTime)) {
            notesToNoteNumbers();
            computeChromaFeature();
            return true;
        } else {
            return false;
        }
    }

    public double[] getChromaFeature() {
        return chromaFeature;
    }

    public void addNotes(Set<MidiNote> notes) {
        this.notes.addAll(notes);
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public Set<Integer> getNoteNumbers() {
        return noteNumbers;
    }

    public Set<MidiNote> getNotes() {
        return notes;
    }

    public boolean setEndTime(int endTime) {
        if(endTime <= this.startTime) {
            return false;
        }
        this.endTime = endTime;
        return true;
    }

    private void notesToNoteNumbers() {
        for(MidiNote note: notes) {
            noteNumbers.add(note.getNumber());
        }
    }

    private void computeChromaFeature() {
        int[] count = new int[chromaFeature.length];
        for(int i = 0; i < chromaFeature.length; i++) {
            count[i] = 0;
        }
        for(int noteNumber: noteNumbers) {
            count[(noteNumber - 5) % 12]++;
        }
        for(int i = 0; i < count.length; i++) {
            if(count[i] == 0) {
                chromaFeature[i] = 0.0;
            } else {
                chromaFeature[i] = count[i] / Math.sqrt(count[i]);
            }
        }
    }
}

