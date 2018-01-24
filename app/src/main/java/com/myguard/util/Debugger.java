package com.myguard.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

/**
 * Created by kalver on 12/01/18.
 */

public class Debugger {

    public static void writeToOutputStream(String sensorName, Object[] data) {
        FileWriter fileWriter = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), String.format("%s.csv", sensorName).replaceAll(" ", "_"));
            fileWriter = new FileWriter(file, file.exists());
            fileWriter.write(String.format("%s\n", valuesToString(data)));
            fileWriter.flush();
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

    private static String valuesToString(Object[] values) {
        StringBuilder result = new StringBuilder();
        for (Object f : values) {
            result.append(f);
            result.append(",");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
    }

}
