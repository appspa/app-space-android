# App-Space-Android

## 简化使用

想要更快地使用XUpdate，降低集成的难度，支持断点续传下载等拓展功能，可以尝试使用[XUpdateAPI](https://github.com/xuexiangjys/XUpdateAPI).

## X系列库快速集成

为了方便大家快速集成X系列框架库，我提供了一个空壳模版供大家参考使用: https://github.com/xuexiangjys/TemplateAppProject

---

## 特征

* 支持post和get两种版本检查方式，支持自定义网络请求。

* 支持设置只在wifi下进行版本更新。

* 支持静默下载（后台更新）、自动版本更新。

* 提供界面友好的版本更新提示弹窗，可自定义其主题样式。

* 支持自定义版本更新检查器、版本更新解析器、版本更新提示器、版本更新下载器、版本更新安装、出错处理。

* 支持MD5文件校验、版本忽略、版本强制更新等功能。

* 支持自定义文件校验方法【默认是MD5校验】。

* 支持自定义请求API接口。

* 兼容Android6.0～11.0。

* 支持中文和英文两种语言显示（国际化）。

* 支持Flutter插件使用：[flutter_xupdate](https://github.com/xuexiangjys/flutter_xupdate)。

* 支持React-Native插件使用：[react-native-xupdate](https://github.com/xuexiangjys/react-native-xupdate)。



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

以下是版本说明，选择一个即可。

* androidx版本：2.0.0及以上

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

