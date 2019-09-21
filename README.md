# Android 语言切换

## 使用
### 1. 添加Gradle依赖
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'com.github.SerenadeHL:LocaleManager:1.0.1'
}
```
### 2. 在你的BaseActivity中
```kotlin
override fun attachBaseContext(newBase: Context?) {
    super.attachBaseContext(LocaleManager.getContext(newBase))
}
```
### 3. 在你的Application中
```kotlin
override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(LocaleManager.getContext(base))
}

override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    LocaleManager.inject(this)
}

override fun onCreate() {
    super.onCreate()
    //设置初次安装后默认语言，如不设置，则取系统默认语言
    LocaleManager.setDefaultLocale(Locale.SIMPLIFIED_CHINESE)
    LocaleManager.inject(this)
}
```
### 4. 切换语言
```kotlin
LocaleManager.setLocale(context,Locale.SIMPLIFIED_CHINESE)
```
