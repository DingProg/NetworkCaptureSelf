package com.ding.library;

import com.ding.library.internal.CaptureEntity;
import com.ding.library.internal.utils.CacheUtils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;


/**
 * author:DingDeGao
 * time:2019-10-30-16:45
 * function: CaptureInfoInterceptor
 */
@SuppressWarnings("all")
public final class CaptureInfoInterceptor implements Interceptor {

  private static final Charset UTF8 = Charset.forName("UTF-8");

  @Override public Response intercept(Chain chain) throws IOException {

    Request request = chain.request();

    CaptureEntity captureEntity = new CaptureEntity();

    RequestBody requestBody = request.body();
    boolean hasRequestBody = requestBody != null;

    Connection connection = chain.connection();
    Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
    String requestStartMessage = request.method() + "   " + protocol;
    if (hasRequestBody) {
      requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
    }

    captureEntity.requestMethod = requestStartMessage;
    captureEntity.requestUrl = request.url().toString();


    StringBuffer headerBuffer = new StringBuffer();
    if (hasRequestBody) {
      if (requestBody.contentType() != null) {
        headerBuffer.append("Content-Type: " + requestBody.contentType()).append("\n");
      }
      if (requestBody.contentLength() != -1) {
        headerBuffer.append("Content-Length: " + requestBody.contentLength()).append("\n");
      }
    }

    Headers headers = request.headers();
    for (int i = 0, count = headers.size(); i < count; i++) {
      String name = headers.name(i);
      // Skip headers from the request body as they are explicitly logged above.
      if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
        headerBuffer.append(name + ": " + headers.value(i)).append("\n");
      }
    }
    captureEntity.requestHeader = headerBuffer.toString();

    if((!bodyEncoded(request.headers()))) {
      Buffer buffer = new Buffer();
      if(requestBody != null) {
        requestBody.writeTo(buffer);

        Charset charset = UTF8;
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
          charset = contentType.charset(UTF8);
        }

        StringBuffer requestBodyBuffer = new StringBuffer();
        if (isPlaintext(buffer)) {
          if (requestBody != null && requestBody instanceof FormBody) {
            FormBody formBody=(FormBody) requestBody;
            for (int i = 0; i < formBody.size(); i++) {
              requestBodyBuffer.append(formBody.name(i)).append(":")
                      .append(formBody.value(i)).
                      append("\n");
            }
          }
         // requestBodyBuffer.append(buffer.readString(charset)).append("\n");

          requestBodyBuffer.append(request.method()
                  + " (" + requestBody.contentLength() + "-byte body)").append("\n");
        } else {
          requestBodyBuffer.append(request.method() + " (binary "
                  + requestBody.contentLength() + "-byte body omitted)").append("\n");
        }

        captureEntity.requestBody = requestBodyBuffer.toString();
      }
    }

    long startNs = System.nanoTime();
    Response response;

    try {
      response = chain.proceed(request);
    } catch (Exception e) {
      captureEntity.responseBody = "HTTP FAILED:"+ e;
      CacheUtils.getInstance().saveCapture(request.url().toString(),captureEntity);
      throw e;
    }
    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

    ResponseBody responseBody = response.body();
    long contentLength = responseBody.contentLength();

    captureEntity.responseStatus = ("<-- " + response.code() + ' ' + response.message() + ' '
            + response.request().url() + " (" + tookMs + "ms" +  "" + ')');

    Headers respHeaders = response.headers();
    StringBuffer responseHeader = new StringBuffer();
    for (int i = 0, count = respHeaders.size(); i < count; i++) {
      responseHeader.append(respHeaders.name(i) + ": " + respHeaders.value(i)).append("\n");
    }
    captureEntity.responseHeader = responseHeader.toString();

    if(!bodyEncoded(response.headers())) {
      BufferedSource source = responseBody.source();
      source.request(Long.MAX_VALUE); // Buffer the entire body.
      Buffer buffer = source.buffer();

      Charset charset = UTF8;
      MediaType contentType = responseBody.contentType();
      if (contentType != null) {
        charset = contentType.charset(UTF8);
      }

      if (!isPlaintext(buffer)) {
        //captureData.append("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)").append("\n");
        captureEntity.responseBody = "非文本信息";
        CacheUtils.getInstance().saveCapture(request.url().toString(),captureEntity);
        return response;
      }

      if (contentLength != 0) {
        captureEntity.responseBody = buffer.clone().readString(charset);
      }

      captureEntity.responseStatus = captureEntity.responseStatus + "<-- END HTTP (" + buffer.size() + "-byte body)";
    }

    CacheUtils.getInstance().saveCapture(request.url().toString(),captureEntity);

    return response;
  }


  static boolean isPlaintext(Buffer buffer) {
    try {
      Buffer prefix = new Buffer();
      long byteCount = buffer.size() < 64 ? buffer.size() : 64;
      buffer.copyTo(prefix, 0, byteCount);
      for (int i = 0; i < 16; i++) {
        if (prefix.exhausted()) {
          break;
        }
        int codePoint = prefix.readUtf8CodePoint();
        if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
          return false;
        }
      }
      return true;
    } catch (EOFException e) {
      return false; // Truncated UTF-8 sequence.
    }
  }

  private boolean bodyEncoded(Headers headers) {
    String contentEncoding = headers.get("Content-Encoding");
    return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
  }
}
