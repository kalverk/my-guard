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
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), String.format("%s_%s.csv", new Date().getTime(), sensorName).replaceAll(" ", "_"));
            bufferedWriter = new BufferedWriter(new FileWriter(file, file.exists()));
            bufferedWriter.write(String.format("%s\n", valuesToString(data)));
            bufferedWriter.flush();
        } catch (Exception e) {
            Log.e("General", "Unable to write to output stream", e);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
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
