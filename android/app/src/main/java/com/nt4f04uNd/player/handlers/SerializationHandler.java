package com.nt4f04uNd.player.handlers;

import android.content.Context;
import android.content.pm.PackageManager;

import com.nt4f04uNd.player.Constants;
import com.nt4f04uNd.player.player.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.flutter.Log;

public class SerializationHandler {

    public static void init(Context appContext) {
        SerializationHandler.appContext = appContext;
    }

    private static Context appContext;

    public static String getFlutterAppPath() {
        try {
            String directory = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0).applicationInfo.dataDir;
            return directory + "/app_flutter/";
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(Constants.LogTag, "Error Package name not found", e);
            return "<error>";
        }
    }


    public static String loadJSON(String uri) {
        String json;
        FileInputStream fileInputStream;
        try {
            File file = new File(uri);
            fileInputStream = new FileInputStream(file);
            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    /** Reads playlist and songs jsons and gets needed songs **/
    public static ArrayList<Song> getPlaylistSongs() {
        JSONArray jsonPlaylist;
        JSONArray jsonSongs;
        ArrayList<Song> songs = new ArrayList(0);
        try {
            jsonPlaylist = new JSONArray(SerializationHandler.loadJSON(SerializationHandler.getFlutterAppPath() + "playlist.json"));
            jsonSongs = new JSONArray(SerializationHandler.loadJSON(SerializationHandler.getFlutterAppPath() + "songs.json"));

            for (int i = 0; i < jsonPlaylist.length(); i++) {
                for (int j = 0; j < jsonSongs.length(); j++) {
                    JSONObject songJsonObject = jsonSongs.getJSONObject(j);
                    if (songJsonObject.getInt("id") == jsonPlaylist.getInt(i)) {
                        songs.add(Song.fromJson(songJsonObject));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return songs;
        }
    }
}
