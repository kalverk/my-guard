package com.myguard.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by kalver on 12/01/18.
 */

public class Debugger {

    private static final String filename = "com.myguard.debug.csv";
    private static final int queueSize = 50;

    private static List<List<Object>> messages = Collections.synchronizedList(new ArrayList<List<Object>>());

    public static void log(Object[] data) {
        List<Object> objects = new ArrayList<>();
        for (Object datum : data) {
            objects.add(datum);
        }
        objects.add(System.currentTimeMillis());
        messages.add(objects);

        if (messages.size() > queueSize) {
            writeQueueToStorage();
            messages = Collections.synchronizedList(new ArrayList<List<Object>>());
        }
    }

    public static void finish() {
        writeQueueToStorage();
        messages = Collections.synchronizedList(new ArrayList<List<Object>>());
    }

    private static void writeQueueToStorage() {
        FileWriter fileWriter = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            fileWriter = new FileWriter(file, file.exists());

            for (List<Object> message : messages) {
                fileWriter.write(String.format("%s\n", valuesToString(message)));
            }

            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            Log.e("General", "Unable to write to output stream", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e) {
                    Log.e("General", "Unable to write to output stream", e);
                }
            }
        }
    }

    private static String valuesToString(List<Object> values) {
        StringBuilder result = new StringBuilder();
        for (Object f : values) {
            result.append(f);
            result.append(",");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
    }

}