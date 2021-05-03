Performance
===========

It has the same function as [hugo](https://github.com/JakeWharton/hugo), but implemented as kotlin compiler plugin,
so that support Kotlin Multiplatform

Requirement
-----------------
- Android Studio verion >= 4.2
- Kotlin version >= 1.5

Run Android/iOS
---------------
- Step 1：run ```./install``` publish gradle-plugin、kotin-plugin、kotlin-native-plugin to local maven
- Step 2：build&install ```androidApp``` module for Android
- Step 3：config&build&install ```iosApp``` module for iOS


Debug
------------------

- Step 1：run ```./install``` publish gradle-plugin、kotin-plugin、kotlin-native-plugin to local maven
- Step 2：run ```./debugJVM``` to debug kotin-plugin module
- Step 3：run ```./debugNative``` to debug kotlin-native-plugin module