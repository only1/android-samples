package com.only1.test_savelog;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveLog {
	private static String TAG = SaveLog.class.getSimpleName();

    private static String DIRECTORY_NAME = "LogFolder";
	private static String FILE_NAME = "log_arm";
    private static String FILE_EXTENSION = "txt";
    private static String SEPARATOR = "_";

    private String mFilePath = null;
    private boolean mStop = true;

    private SimpleDateFormat dateFormat = null;
    private Date date = null;

	public SaveLog() {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DIRECTORY_NAME;
        File directory = new File(dirPath);

        if (!directory.exists()) {
            directory.mkdir();
        }

		mFilePath = dirPath + "/";
        dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        date = new Date(System.currentTimeMillis());

        mFilePath += FILE_NAME + SEPARATOR + dateFormat.format(date) + "." + FILE_EXTENSION;
        // ex: /sdcard/log_arm_201507201300.txt
	}

    public void start() {
        mStop = false;
        new LogThread().execute(mFilePath);
    }

    public void stop() {
        mStop = true;
    }

    private class LogThread extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String path = strings[0];

            try {

                ArrayList<String> commandLine = new ArrayList<String>();
                commandLine.add("logcat"); // If you have more parameter, add should parameters.

                Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(path));

                String line = null;
                while (!mStop) {
                    line = reader.readLine();
                    if (line != null) {
                        date.setTime(System.currentTimeMillis());
                        writer.write(dateFormat.format(date) + " : " + line);
                        writer.newLine();
                    }
                }

                if (reader != null) reader.close();
                if (writer != null) writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
