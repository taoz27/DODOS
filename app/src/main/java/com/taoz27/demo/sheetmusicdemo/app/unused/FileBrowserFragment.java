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

package com.taoz27.demo.sheetmusicdemo.app.unused;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.taoz27.demo.sheetmusicdemo.R;
import com.taoz27.demo.sheetmusicdemo.app.IconArrayAdapter;
import com.taoz27.demo.sheetmusicdemo.app.main.MainActivity;
import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class FileBrowserFragment extends Fragment {
    private Activity activity;
    private View mainView;
    private ArrayList<MusicFile> filelist; /* List of files in the directory */
    private String directory;            /* Current directory being displayed */
    private TextView directoryView;      /* TextView showing directory name */
    private ListView listView;
    private String rootdir;              /* The top level root directory */
    private IconArrayAdapter<MusicFile> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.file_browser,container,false);
        listView=(ListView)mainView.findViewById(R.id.list);
        activity=getActivity();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicFile file = (MusicFile) listView.getAdapter().getItem(position);
                if (file.isDirectory()) {
                    FileBrowserFragment.this.loadDirectory(file.getUri().getPath());
                    return;
                }
                else {
                    MainActivity.openMidFile(file);
                }
            }
        });
        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        rootdir = Environment.getExternalStorageDirectory().getAbsolutePath();
        directoryView = (TextView) mainView.findViewById(R.id.directory);
        SharedPreferences settings = activity.getPreferences(0);
        String lastBrowsedDirectory = settings.getString("lastBrowsedDirectory", null);
        if (lastBrowsedDirectory == null) {
            lastBrowsedDirectory = rootdir;
        }
        loadDirectory(lastBrowsedDirectory);
    }

    /* Scan the files in the new directory, and store them in the filelist.
     * Update the UI by refreshing the list adapter.
     */
    private void loadDirectory(String newdirectory) {
        if (newdirectory.equals("../")) {
            try {
                directory = new File(directory).getParent();
            }
            catch (Exception e) {
            }
        }
        else {
            directory = newdirectory;
        }
        SharedPreferences.Editor editor = activity.getPreferences(0).edit();
        editor.putString("lastBrowsedDirectory", directory);
        editor.commit();
        directoryView.setText(directory);

        filelist = new ArrayList<MusicFile>();
        ArrayList<MusicFile> sortedDirs = new ArrayList<MusicFile>();
        ArrayList<MusicFile> sortedFiles = new ArrayList<MusicFile>();
        if (!newdirectory.equals(rootdir)) {
            String parentDirectory = new File(directory).getParent() + "/";
            Uri uri = Uri.parse("file://" + parentDirectory);
            sortedDirs.add(new MusicFile(MusicFile.TYPE.MID,uri, parentDirectory));
        }
        try {
            File dir = new File(directory);
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file == null) {
                        continue;
                    }
                    String filename = file.getName();
                    if (file.isDirectory()) {
                        Uri uri = Uri.parse("file://" + file.getAbsolutePath() + "/");
                        MusicFile fileuri = new MusicFile(MusicFile.TYPE.MID,uri, uri.getPath());
                        sortedDirs.add(fileuri);
                    }
                    else if (filename.endsWith(".mid") || filename.endsWith(".MID") ||
                             filename.endsWith(".midi") || filename.endsWith(".MIDI")) {
                        
                        Uri uri = Uri.parse("file://" + file.getAbsolutePath());
                        MusicFile fileuri = new MusicFile(MusicFile.TYPE.MID,uri, uri.getLastPathSegment());
                        sortedFiles.add(fileuri);
                    }
                }
            }
        }
        catch (Exception e) {
        }

        if (sortedDirs.size() > 0) {
            Collections.sort(sortedDirs, sortedDirs.get(0));
        }
        if (sortedFiles.size() > 0) {
            Collections.sort(sortedFiles, sortedFiles.get(0));
        }
        filelist.addAll(sortedDirs);
        filelist.addAll(sortedFiles);
        adapter = new IconArrayAdapter<>(activity, android.R.layout.simple_list_item_1, filelist);
        listView.setAdapter(adapter);
    }
}


