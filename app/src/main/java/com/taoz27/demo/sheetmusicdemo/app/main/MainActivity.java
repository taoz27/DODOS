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

package com.taoz27.demo.sheetmusicdemo.app.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.taoz27.demo.sheetmusicdemo.MyPackage.MP3Player;
import com.taoz27.demo.sheetmusicdemo.R;
import com.taoz27.demo.sheetmusicdemo.app.SheetMusicActivity;
import com.taoz27.demo.sheetmusicdemo.sheet.MusicFile;
import com.taoz27.demo.sheetmusicdemo.sheet.MidiFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** @class ChooseSongActivity
 * The ChooseSongActivity class is a tabbed view for choosing a song to play.
 * There are 3 tabs:
 * - All    (AllSongsActivity)    : Display a list of all songs
 * - Recent (RecentSongsActivity) : Display of list of recently opened songs
 * - Browse (FileBrowserActivity) : Let the user browse the filesystem for songs
 */
public class MainActivity extends FragmentActivity {
    MP3Player player;
    static MainActivity globalActivity;
    TabLayout tabLayout;
    ViewPager viewPager;
    List<Fragment> fragments=new ArrayList<>(4);

    @Override
    public void onCreate(Bundle state) {
        globalActivity = this;
        super.onCreate(state);
        setContentView(R.layout.activity_choose);

        requestReadExternalPermission();

        player=MP3Player.getController(this);
        fragments.add(new Mp3FileFragment());
        fragments.add(new WavFragment());
        fragments.add(new MidFragment());
        final String[] titles={"我的", "转换", "歌谱"};

        tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        viewPager=(ViewPager)findViewById(R.id.viewpager);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static void openMidFile(MusicFile file) {
        globalActivity.doOpenMidFile(file);
    }

    private void doOpenMidFile(MusicFile file) {
        if (file.getType()!= MusicFile.TYPE.MID)return;
        byte[] data = file.getData(this);
        if (data == null || data.length <= 6 || !MidiFile.hasMidiHeader(data)) {
            MainActivity.showErrorDialog("Error: Unable to open song: " + file.toString(), this);
            return;
        }
        updateRecentFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW, file.getUri(), this, SheetMusicActivity.class);
        intent.putExtra(SheetMusicActivity.MidiTitleID, file.toString());
        startActivity(intent);
    }


    /** Show an error dialog with the given message */
    public static void showErrorDialog(String message, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
           }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /** Save the given FileUri into the "recentFiles" preferences.
     *  Save a maximum of 10 recent files.
     */
    public void updateRecentFile(MusicFile recentfile) {
        try {
            SharedPreferences settings = getSharedPreferences("midisheetmusic.recentFiles", 0);
            SharedPreferences.Editor editor = settings.edit();
            JSONArray prevRecentFiles = null;
            String recentFilesString = settings.getString("recentFiles", null);
            if (recentFilesString != null) {
                prevRecentFiles = new JSONArray(recentFilesString);
            }
            else {
                prevRecentFiles = new JSONArray();
            }
            JSONArray recentFiles = new JSONArray();
            JSONObject recentFileJson = recentfile.toJson();
            recentFiles.put(recentFileJson);
            for (int i = 0; i < prevRecentFiles.length(); i++) {
                if (i >= 10) {
                    break; // only store 10 most recent files
                }
                JSONObject file = prevRecentFiles.getJSONObject(i);
                if (!MusicFile.equalJson(recentFileJson, file)) {
                    recentFiles.put(file);
                }
            }
            editor.putString("recentFiles", recentFiles.toString() );
            editor.commit();
        }
        catch (Exception e) {
        }
    }

    String TAG="Main";
    @SuppressLint("NewApi")
    private void requestReadExternalPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "READ permission IS NOT granted...");

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Log.d(TAG, "11111111111111");
            } else {
                // 0 是自己定义的请求coude
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                Log.d(TAG, "222222222222");
            }
        } else {
            Log.d(TAG, "READ permission is granted...");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "requestCode=" + requestCode + "; --->" + permissions.toString()
                + "; grantResult=" + grantResults.toString());
        switch (requestCode) {
            case 0: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    // request successfully, handle you transactions

                } else {

                    // permission denied
                    // request failed
                }

                return;
            }
            default:
                break;

        }
    }
}

