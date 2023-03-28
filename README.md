# App-Space-Android

* 支持中文和英文两种语言显示（国际化）。

* 支持Flutter插件使用：[app-space-flutter](https://github.com/appspa/app-space-flutter)。


## 1、快速集成指南

> 目前支持主流开发工具AndroidStudio的使用，直接配置build.gradle，增加依赖即可.

### 2.1、Android Studio导入方法，添加Gradle依赖

1.先在项目根目录的 `build.gradle` 的 repositories 添加:
```
allprojects {
     repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

2.然后在应用项目(一般是app)的 `build.gradle` 的 dependencies 添加:



```
dependencies {
  ...
  // androidx版本
  implementation 'com.github.appspa:app-space-android:1.0.1'
}
```



### 2.2、初始化

在Application进行初始化配置：

```
AppSpace.get()
    .debug(true)
    .isWifiOnly(true)                                               //默认设置只在wifi下检查版本更新
    .isGet(true)                                                    //默认设置使用get请求检查版本
    .isAutoMode(false)                                              //默认设置非自动模式，可根据具体使用配置
    .param("currentMd5", UpdateUtils.getBaseApkMd5(this))//差量更新使用
    .param("currentVersionCode", UpdateUtils.getVersionCode(this))
    .param("appId", "641d82026c4a440df47b6be7")
    .setOnUpdateFailureListener(new OnUpdateFailureListener() {     //设置版本更新出错的监听
        @Override
        public void onFailure(UpdateError error) {
            if (error.getCode() != CHECK_NO_NEW_VERSION) {          //对不同错误进行处理
                ToastUtils.toast(error.toString());
            }
        }
    })
    .supportSilentInstall(true)                                     //设置是否支持静默安装，默认是true
    .setIUpdateHttpService(new OKHttpUpdateHttpService())           //这个必须设置！实现网络请求功能。
    .init(this);                                                    //这个必须初始化
```

