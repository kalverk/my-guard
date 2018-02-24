package com.myguard;

import android.content.Context;
import android.util.Log;

import com.myguard.util.AWS;
import com.myguard.util.Debugger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

/**
 * Created by kalver on 29/01/18.
 */

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Context context;
    private final Thread.UncaughtExceptionHandler oldHandler;

    public CustomExceptionHandler(Context context) {
        this.context = context;
        this.oldHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        writeExceptionToFile(e);

        Debugger.finish();

        if (oldHandler != null) {
            oldHandler.uncaughtException(t, e);
        } else {
            System.exit(2);
        }
    }

    private void writeExceptionToFile(Throwable throwable) {
        try {
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));

            FileOutputStream fileOutputStream = context.openFileOutput(String.format("%s-%s.%s", System.currentTimeMillis(), UUID.randomUUID(), "log"), Context.MODE_PRIVATE);
            fileOutputStream.write(sw.toString().getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e("General", "Unable to write to output stream", e);
        }
    }

    public static void uploadErrors(Context context) {
        File[] files = context.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".log");
            }
        });

        for (File file : files) {
            System.out.println(file.getAbsolutePath());
            AWS.uploadData(context, file);
        }
    }

}