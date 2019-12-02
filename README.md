# 概览
一个基于OKHttp的实用抓包小工具

# 工具界面截图
![](https://github.com/DingProg/NetworkCaptureSelf/blob/master/screen/lanuch.png)
![](https://github.com/DingProg/NetworkCaptureSelf/blob/master/screen/main.png)


# 支持功能

- 自带分类接口
- 抓包数据以时间为纬度,默认存储到手机缓存下 /Android/Data/包名/Cache/capture/ 下
- 支持Http协议的抓包，分类请求方式/请求URL/请求Header/请求体/响应状态/响应Header/响应体
- 支持一键复制对应的状态
- 响应体如果是JSON，支持自动格式化
- 抓包数据，默认缓存一天


# 快速接入
[![](https://jitpack.io/v/DingProg/NetworkCaptureSelf.svg)](https://jitpack.io/#DingProg/NetworkCaptureSelf)

```gradle
allprojects {
	repositories {
	   maven { url 'https://jitpack.io' }
	}
}

dependencies {
    debugImplementation 'com.github.DingProg.NetworkCaptureSelf:library:v1.0.1'
    releaseImplementation 'com.github.DingProg.NetworkCaptureSelf:library_no_op:v1.0.1'
}
```

在你的全局OkHttp中添加 Interceptor
```java
new OkHttpClient.Builder()
        .addInterceptor(new CaptureInfoInterceptor())
        .build();
```

# 注意事项
注意接入时  debugImplementation 和 releaseImplementation区别，releaseImplementation中不包含任何其他代码

如果您的项目中还有buildType 是develop，那么developImplementation
请依赖 'com.github.DingProg.NetworkCaptureSelf:library:v1.0.1'