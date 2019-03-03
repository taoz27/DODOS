/*
 * Copyright (c) 2011-2013 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.taoz27.demo.sheetmusicdemo.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.taoz27.demo.sheetmusicdemo.app.main.MainActivity;
import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/** @class RecentSongsActivity
 * The RecentSongsActivity class displays a list of songs
 * that were recently accessed.  The list comes from the
 * SharedPreferences ????
 */
public class RecentSongsFragment extends Fragment {
    private Activity activity;
    private ListView listView;
    private ArrayList<MusicFile> filelist; /* List of recent files opened */
    private IconArrayAdapter<MusicFile> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity=getActivity();
        listView=new ListView(activity);
        // Load the list of songs
        loadFileList();
        adapter = new IconArrayAdapter<>(activity, android.R.layout.simple_list_item_1, filelist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicFile file = (MusicFile) listView.getAdapter().getItem(position);
                MainActivity.openMidFile(file);
            }
        });
        return listView;
    }

    private void loadFileList() {
        filelist = new ArrayList<MusicFile>();
        SharedPreferences settings = activity.getSharedPreferences("midisheetmusic.recentFiles", 0);
        String recentFilesString = settings.getString("recentFiles", null);
        if (recentFilesString == null) {
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray(recentFilesString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                MusicFile file = MusicFile.fromJson(obj, activity);
                if (file != null) {
                    filelist.add(file);
                }
            }
        }
        catch (Exception e) {
        }
    }
            
    @Override
    public void onResume() {
        super.onResume();
        loadFileList();
    }
}


