//package com.taoz27.demo.sheetmusicdemo.wav2mid;
//
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//
//import com.taoz27.demo.sheetmusicdemo.R;
//import com.taoz27.demo.sheetmusicdemo.wav2mid.midi.MidiException;
//import com.taoz27.demo.sheetmusicdemo.wav2mid.midi.MidiValues;
//import com.taoz27.demo.sheetmusicdemo.wav2mid.midi.ProjectToMidiConverter;
//import com.taoz27.demo.sheetmusicdemo.wav2mid.note.MusicalInstrument;
//import com.taoz27.demo.sheetmusicdemo.wav2mid.note.MusicalKey;
//import com.taoz27.demo.sheetmusicdemo.wav2mid.note.NoteEvent;
//import com.taoz27.demo.sheetmusicdemo.wav2mid.note.NoteName;
//import com.taoz27.demo.sheetmusicdemo.wav2mid.note.Project;
//import com.taoz27.demo.sheetmusicdemo.wav2mid.note.Track;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.AbstractMap;
//import java.util.List;
//
//
//public class ConvertingTestActivity extends ActionBarActivity implements View.OnClickListener {
//
//    private Button convertButton;
//
//    @Override
//    public void onClick(View v) {
//        if(v.getId() == R.id.convertButton)
//        {
//            Log.d("CONVERT","Conversion started");
//            File file;
//            File folder = new File(Environment.getExternalStorageDirectory() + "/soundtastic");
//            file = new File(folder.getAbsolutePath(), "test.midi");
//
//            if (!folder.exists()) {
//                folder.mkdir();
//            }
//            if(file.exists())
//            {
//                file.delete();
//            }
//            WavConverter converter = new WavConverter();
//            MidiValues midiValues1= converter.convertToMidi(Environment.getExternalStorageDirectory()+"/TestFIle.wav");
//
//            List<AbstractMap.SimpleEntry<Integer,Integer>> noteMap = midiValues1.generateNoteMap();
//
//        /*
//        === print noteMap to Android Studio console
//        for(int i = 0; i < noteMap.size(); i++)
//        {
//            Log.d("noteOutPut:", noteMap.get(i).getKey().toString() + " " +
//                    noteMap.get(i).getValue().toString());
//                    noteMap.get(i).getValue().toString());
//        }
//        ===*/
//
//
//
//            Track firstTrack = new Track(MusicalKey.VIOLIN, MusicalInstrument.ACOUSTIC_GRAND_PIANO);
//        Project.getInstance().setName("Test project");
//            Project.getInstance().setBeatsPerMinute(60);
//            int currentTicks = 0;
//            for(int i = 0; i < noteMap.size(); i++)
//            {
//                NoteName noteName = NoteName.getNoteNameFromMidiValue(noteMap.get(i).getKey());
//                NoteEvent note_begin = new NoteEvent(noteName, true);
//                firstTrack.addNoteEvent(currentTicks, note_begin);
//                currentTicks += midiValues1.getNoteLength(noteMap.get(i).getValue()) * Project.getInstance().getBeatsPerMinute() * 8;
//
//                Log.d("NOTELENGTH", Double.toString(midiValues1.getNoteLength(noteMap.get(i).getValue())));
//                Log.d("CURRENTTICKS", Integer.toString(currentTicks));
//
//                NoteEvent note_end = new NoteEvent(noteName, false);
//                firstTrack.addNoteEvent(currentTicks, note_end);
//            }
//
//            Project.getInstance().addTrack("first", firstTrack);
//
//            ProjectToMidiConverter procConverter = new ProjectToMidiConverter();
//
//            try{
//                procConverter.writeProjectAsMidi(Project.getInstance(), file);
//
//            }catch(IOException e){
//                e.printStackTrace();
//            } catch (MidiException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_converting_test);
//        convertButton =(Button) findViewById(R.id.convertButton);
//        convertButton.setOnClickListener(ConvertingTestActivity.this);
//    }
//}
