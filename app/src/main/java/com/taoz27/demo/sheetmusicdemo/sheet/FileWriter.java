package com.taoz27.demo.sheetmusicdemo.sheet;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileWriter {
	public static void writeFile(String fileName, String content) {
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		dir.mkdir();
		File file = new File(dir, fileName);
		FileOutputStream os;
		try {
			os = new FileOutputStream(file);
			os.write(content.getBytes());
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeTrack(String fileName, MidiTrack track) {
		StringBuffer sb = new StringBuffer();
		for(MidiNote note: track.getNotes()) {
			sb.append(note2String(note) + "\n");
		}
		writeFile(fileName, sb.toString());
	}

	public static void writeMidiMatrix(String fileName, MidiFile midiFile) {
		StringBuffer sb = new StringBuffer();
		for(MidiTrack track: midiFile.getTracks()) {
			for(MidiNote note: track.getNotes()) {
				sb.append(note2String(note) + "\n");
			}
		}
		writeFile(fileName, sb.toString());
	}

	public static void writeShortArray(String fileName, short[] array, int length) {
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString(length) + "\n");
		for(int i = 0; i < length; i++) {
			sb.append(Short.toString(array[i]) + "\n");
		}
		writeFile(fileName, sb.toString());
	}
	
	private static String note2String(MidiNote note) {
		return Integer.toString(note.getNumber()) + "," + Integer.toString(note.getStartTime()) + "," + Integer.toString(note.getEndTime());
	}
}
