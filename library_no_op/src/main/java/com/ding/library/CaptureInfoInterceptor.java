package com.ding.library;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;


/**
 * author:DingDeGao
 * time:2019-10-30-16:45
 * function: CaptureInfoInterceptor
 */
public final class CaptureInfoInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    return chain.proceed(chain.request());
  }
}
