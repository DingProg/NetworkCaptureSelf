package com.ding.networkcaptureself;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ding.library.CaptureInfoInterceptor;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new CaptureInfoInterceptor())
                .build();

        testNet(null);
    }


    public void testNet(View view) {

        Request request =  new Request.Builder()
                .addHeader("test1","testValue1")
                .addHeader("test2","testValue2")
                .url("http://www.baidu.com/test1")
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

        Request request1 =  new Request.Builder()
                .addHeader("test1","testValue1")
                .addHeader("test2","testValue2")
                .url("http://www.baidu.com")
                .get()
                .build();

        okHttpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }
}
