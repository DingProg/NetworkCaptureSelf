package com.ding.library.internal.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtil {

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e("delete file", "delete file no exists " + file.getAbsolutePath());
        }
    }


    /**
     * 判断是否有SD卡
     */
    public static boolean sdcardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
