package com.myguard.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kalver on 12/01/18.
 */

public class Debugger {

    private static final Map<String, FileOutputStream> streamWriterMap = new HashMap<>();

    public static void writeToOutputStream(String sensorName, Object[] data) {
        try {
            if (!streamWriterMap.containsKey(sensorName)) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), String.format("%s_%s.csv", new Date().getTime(), sensorName).replaceAll(" ", "_"));
                Log.d("General", "Created file " + file.getAbsolutePath());
                streamWriterMap.put(sensorName, new FileOutputStream(file));
            }
            streamWriterMap.get(sensorName).write(String.format("%s\n", valuesToString(data)).getBytes());
        } catch (IOException e) {
            Log.e("General", "Unable to write to output stream", e);
        }
    }

    public static void closeStreams() {
        for (Map.Entry<String, FileOutputStream> entry : streamWriterMap.entrySet()) {
            try {
                entry.getValue().close();
                streamWriterMap.remove(entry.getKey());
            } catch (IOException e) {
                Log.e("General", "Unable to close file", e);
            }
        }
    }

    private static String valuesToString(Object[] values) {
        StringBuilder result = new StringBuilder();
        for (Object f : values) {
            result.append(f);
            result.append(",");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
    }

}
