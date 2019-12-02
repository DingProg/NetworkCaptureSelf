package com.ding.library.internal.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ding.library.internal.CaptureEntity;
import com.ding.library.internal.ui.CaptureContentAdapter;
import com.ding.library.internal.ui.UIItemEntity;
import com.ding.library.internal.ui.UISubItemVH;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * author:DingDeGao
 * time:2019-11-01-11:30
 * function: default function
 */
public class GetCaptureDataUtils {

    public static void getData(Context context, final String parentFileName, final String fileName, final CallBack callBack){
        Toast.makeText(context,"正在加载，请稍后...",Toast.LENGTH_SHORT).show();
        new AsyncTask<Void,Void,List<CaptureContentAdapter.Entity>>(){

            @Override
            protected List<CaptureContentAdapter.Entity> doInBackground(Void... voids) {
                String str = CacheUtils.getInstance().getCaputre(parentFileName,fileName);
                CaptureEntity captureEntity = JSON.parseObject(str, CaptureEntity.class);

                List<CaptureContentAdapter.Entity> list = new ArrayList<>();
                if(captureEntity == null) return list;

                list.add(new CaptureContentAdapter.Entity("请求方式",captureEntity.requestMethod));
                list.add(new CaptureContentAdapter.Entity("请求URL",captureEntity.requestUrl));
                if(!TextUtils.isEmpty(captureEntity.requestHeader)) {
                    list.add(new CaptureContentAdapter.Entity("请求Header", captureEntity.requestHeader));
                }
                if(!TextUtils.isEmpty(captureEntity.requestBody)){
                    list.add(new CaptureContentAdapter.Entity("请求体",captureEntity.requestBody));
                }

                list.add(new CaptureContentAdapter.Entity("响应状态",captureEntity.responseStatus));
                list.add(new CaptureContentAdapter.Entity("响应Header",captureEntity.responseHeader));


                list.add(new CaptureContentAdapter.Entity("响应体",formatJson(captureEntity.responseBody)));

                return list;
            }

            private String formatJson(String str){
                String json;
                try{
                    if (str.startsWith("{")) {
                        JSONObject jsonObject = new JSONObject(str);
                        json = jsonObject.toString(4);
                    } else if (str.startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(str);
                        json = jsonArray.toString(4);
                    } else {
                        json = str;
                    }

                }catch (Exception e){
                    json = str;
                }
                return json;
            }

            @Override
            protected void onPostExecute(List<CaptureContentAdapter.Entity> list) {
                super.onPostExecute(list);
                if(callBack != null){
                    callBack.success(list);
                }
            }
        }.execute();
    }


    public static void getSlidData(final CallBackSlide callBackSlide){
        new AsyncTask<Void,Void,List<UIItemEntity>>(){

            @Override
            protected List<UIItemEntity> doInBackground(Void... voids) {
                List<String> capture = CacheUtils.getInstance().getCapture();
                List<UIItemEntity> list = new ArrayList<>();
                for (int i = 0; i < capture.size(); i++) {
                    String str = capture.get(i);
                    UIItemEntity uiItemEntity = new UIItemEntity();
                    uiItemEntity.mExpanded = (i == 0);
                    uiItemEntity.name = str;
                    uiItemEntity.subFileList = getSubItemList(str);
                    if(uiItemEntity.subFileList.size() > 0) {
                        list.add(uiItemEntity);
                    }else{
                        CacheUtils.getInstance().deleteValidtyFileDir(str);
                    }
                }
                return list;
            }


            public List<UISubItemVH.SubEntity> getSubItemList(String parentFileName) {
                List<UISubItemVH.SubEntity> list = new ArrayList<>();
                List<String> capture = CacheUtils.getInstance().getCapture(parentFileName);
                for (String s : capture) {
                    if(CacheUtils.getInstance().checkValidity(s)){
                        list.add(new UISubItemVH.SubEntity(s, parentFileName));
                    }else{
                        CacheUtils.getInstance().deleteValidtyFileCapture(parentFileName,s);
                    }
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<UIItemEntity> uiItemEntities) {
                super.onPostExecute(uiItemEntities);
                if(callBackSlide != null){
                    callBackSlide.success(uiItemEntities);
                }
            }
        }.execute();
    }

    public interface CallBack{
        void success(List<CaptureContentAdapter.Entity> list);
    }

    public interface CallBackSlide{
        void success(List<UIItemEntity> list);
    }
}
