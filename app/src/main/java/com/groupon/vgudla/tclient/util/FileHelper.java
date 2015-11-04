package com.groupon.vgudla.tclient.util;

import android.content.Context;
import android.util.Log;

import com.groupon.vgudla.tclient.models.Tweet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Helper class to persist/load tweets in a file. Useful for offline testing or when requests are
 * rate-limited
 */
public class FileHelper {

    private static final String TAG = "FileHelper";
    private static final String TWEETS_FILE = "tweets.txt";

    public static void writeToFile(Context context, List<Tweet> tweets, String fileName) {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(tweets);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not open file:" + Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, "Error while writing to file:" + Log.getStackTraceString(e));
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error while closing output stream during write" + Log.getStackTraceString(e));
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error while closing file during write" + Log.getStackTraceString(e));
                }
            }
        }
    }

    public static List<Tweet> readFromFile(Context context, String fileName) {
        FileInputStream fis = null;
        ObjectInputStream is = null;
        List<Tweet> tweets = null;
        try {
            fis = context.openFileInput(fileName);
            is = new ObjectInputStream(fis);
            tweets = (List<Tweet>) is.readObject();
        } catch (ClassNotFoundException ce) {
            Log.e(TAG, "Could not deserialize from file" + fileName);
        } catch (FileNotFoundException e) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    Log.e(TAG, "Could not find file during read" + Log.getStackTraceString(e1));
                }
            }
        } catch (IOException ie) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close file during read" + Log.getStackTraceString(e));
                }
            }
        }
        return tweets;
    }
}
