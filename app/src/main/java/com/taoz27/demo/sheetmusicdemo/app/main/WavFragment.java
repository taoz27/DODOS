/*
 * Copyright (c) 2011-2012 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.taoz27.demo.sheetmusicdemo.app.main;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.taoz27.demo.sheetmusicdemo.MyPackage.SDMusicLoader;
import com.taoz27.demo.sheetmusicdemo.R;
import com.taoz27.demo.sheetmusicdemo.app.FileScanner;
import com.taoz27.demo.sheetmusicdemo.app.IconArrayAdapter;
import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;
import com.taoz27.demo.sheetmusicdemo.wav2mid.WavConverter;

import java.util.ArrayList;


/** @class AllSongsActivity
 * The AllSongsActivity class is used to display a list of
 * songs to choose from.  The list is created from the songs
 * shipped with MidiSheetMusic (in the assets directory), and 
 * also by searching for midi files in the internal/external 
 * device storage.
 *
 * When a song is chosen, this calls the SheetMusicAcitivty, passing
 * the raw midi byte[] data as a parameter in the Intent.
 */ 
public class WavFragment extends Fragment implements TextWatcher {

    /** The complete list of midi files */
    ArrayList<MusicFile> songlist;

    /** Textbox to filter the songs by name */
    View mainView;
    EditText filterText;
    ListView listView;
    ProgressBar progressBar;

    /** Task to scan for midi files */
    FileScanner scanner;

    IconArrayAdapter<MusicFile> adapter;
    Activity context;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_wav,container,false);
        listView=(ListView)mainView.findViewById(R.id.list);
        progressBar=(ProgressBar)mainView.findViewById(R.id.progress);
        context=getActivity();
        
        /* If we're restarting from an orientation change,
         * load the saved song list.
         */
        if (songlist != null) {
            adapter = new IconArrayAdapter<>(context, android.R.layout.simple_list_item_1, songlist);
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                WavConverter converter=new WavConverter();
                converter.setOnConverteDone(new WavConverter.OnConverteDone() {
                    @Override
                    public void onConverteDone() {
                        final String path= Environment.getExternalStorageDirectory() + "/SheetMusicDemo/"+
                                adapter.getItem(position).toString().replace(".wav","")+".mid";
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                MainActivity.showErrorDialog("转换完成:"+path,getActivity());
                            }
                        });
                    }
                });
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
//                    }
//                });
//                    }
//                });
//                try {
//                    Thread.sleep(300);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                converter.convertToMidi(songlist.get(position).getUri().toString(),adapter.getItem(position).toString().replace(".wav",""));
            }
        });
        return mainView;
    }

    Handler handler=new Handler();

    @Override
    public void onResume() {
        super.onResume();
        scanForSongs();
    }


    /** Scan the SD card for midi songs.  Since this is a lengthy
     *  operation, perform the scan in a background thread.
     */
    public void scanForSongs() {
        if (scanner != null) {
            return;
        }
        scanner = new FileScanner();
        scanner.setActivity(getActivity(), new FileScanner.OnLoadDone() {
            @Override
            public void onLoadDone(ArrayList<MusicFile> data) {
                scanDone(data);
            }
        }, MusicFile.TYPE.WAV);
        scanner.execute(0);
    }

    public void scanDone(ArrayList<MusicFile> newfiles) {
        if (newfiles == null) return;
        if (songlist==null)songlist=new ArrayList<>();
        else songlist.clear();

        songlist.addAll(newfiles);
        scanner = null;

        adapter = new IconArrayAdapter<>(context, android.R.layout.simple_list_item_1, songlist);
        listView.setAdapter(adapter);

        filterText = (EditText) mainView.findViewById(R.id.name_filter);
        filterText.addTextChangedListener(this);
        filterText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }


    /** As text is entered in the filter box, filter the list of
     *  midi songs to display.
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

   
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
}

