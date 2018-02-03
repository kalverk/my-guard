package com.myguard;

import com.myguard.util.Debugger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by kalver on 29/01/18.
 */

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String error = sw.toString();

        Debugger.log(new Object[]{error});
        Debugger.finish();

        //TODO can send here via HTTP

        if (oldHandler != null) {
            oldHandler.uncaughtException(t, e);
        } else {
            System.exit(2);
        }
    }
}