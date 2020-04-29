package com.ding.library.internal.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.ding.library.internal.CaptureEntity;
import com.ding.library.internal.DiskIOThreadExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

@SuppressWarnings("all")
public class CacheUtils {
    private static final CacheUtils INSTANCE = new CacheUtils();

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_SS");

    private static String captureFilePath;
    private DiskIOThreadExecutor diskIOThreadExecutor;
    private SharedPreferences sp;
    private String todayStr = "";

    private CacheUtils() {
        File file;
        if (FileUtil.sdcardAvailable() && ContextCompat.checkSelfPermission(CaptureContext.appContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            file = new File(CaptureContext.appContext.getExternalCacheDir(), "capture");
        } else {
            file = new File(CaptureContext.appContext.getCacheDir(), "capture");
        }

        if (!file.exists()) {
            file.mkdirs();
        }
        captureFilePath = file.getAbsolutePath();

        diskIOThreadExecutor = new DiskIOThreadExecutor();

        sp = CaptureContext.appContext.getSharedPreferences("captrue_url_sp", Context.MODE_PRIVATE);

        SimpleDateFormat tempSdf = new SimpleDateFormat("yyyy_MM_dd");
        todayStr = tempSdf.format(new Date());
    }

    public static CacheUtils getInstance() {
        return INSTANCE;
    }

    public String getUrl(String key) {
        return sp.getString(key, "");
    }

    public void saveCapture(final String url, final CaptureEntity value) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String saveUrl = url;
                if (url.contains("?")) {
                    saveUrl = saveUrl.substring(0, saveUrl.indexOf("?"));
                }
                String key = urlMd5(saveUrl);
                sp.edit().putString(key, saveUrl).apply();
                checkOrCreateFilePath(key);
                File file = new File(captureFilePath + "/" + key + "/" + getCurrentTime() + ".txt");
                BufferedSink bufferedSink = null;
                try {
                    file.createNewFile();
                    bufferedSink = Okio.buffer(Okio.sink(file));
                    bufferedSink.writeString(JSON.toJSONString(value), StandardCharsets.UTF_8);
                    bufferedSink.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedSink != null) {
                        try {
                            bufferedSink.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        diskIOThreadExecutor.execute(runnable);
    }

    public String getCaputre(String parentKey, String key) {
        File file = new File(captureFilePath + "/" + parentKey + "/" + key);
        if (file.exists()) {
            BufferedSource bufferedSource = null;
            try {
                bufferedSource = Okio.buffer(Okio.source(file));
                String readString = bufferedSource.readString(StandardCharsets.UTF_8);
                return readString;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedSource != null) {
                    try {
                        bufferedSource.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "";
    }

    private void checkOrCreateFilePath(String key) {
        File file = new File(captureFilePath + "/" + key + "/");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public List<String> getCapture() {
        File file = new File(captureFilePath);
        return getFileList(file);
    }

    public List<String> getCapture(String key) {
        File file = new File(captureFilePath + "/" + key);
        return getFileList(file);
    }

    private List<String> getFileList(File file) {
        List<String> list = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                list.add(file1.getName());
            }
        }
        return list;
    }


    /**
     * 抓包数据 一天的有效期
     * @param fileName
     *
     */
    public boolean checkValidity(String fileName) {
        try {
            String str = fileName.substring(0, 10);
            return todayStr.equals(str);
        } catch (Exception e) {
            return false;
        }
    }


    public void deleteValidtyFileDir(String fileDir) {
        deleteValidtyFile(captureFilePath + "/" + fileDir);
    }

    public void deleteValidtyFileCapture(String parentFileName, String fileName) {
        deleteValidtyFile(captureFilePath + "/" + parentFileName + "/" + fileName);
    }


    private void deleteValidtyFile(String fileName) {
        try {
            File file = new File(fileName);
            file.delete();
        } catch (Exception e) {

        }
    }


    private String getCurrentTime() {
        Date date = new Date();
        return sdf.format(date);
    }

    private String urlMd5(String url) {
        return ByteString.encodeUtf8(url).md5().hex();
    }

    public void cleanCache() {
        FileUtil.deleteFile(new File(captureFilePath));
        sp.edit().clear().apply();
    }

}
