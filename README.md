<div id="top"></div>

# FitTasks
<!-- シールド一覧 -->
<!-- 該当するプロジェクトの中から任意のものを選ぶ-->
<p style="display: inline">
  <!-- Android, Kotlin, Compose -->
  <img src="https://img.shields.io/badge/-Android-3DDC84.svg?logo=android&style=for-the-badge&logoColor=white">
  <img src="https://img.shields.io/badge/-Kotlin-7F52FF.svg?logo=kotlin&style=for-the-badge&logoColor=white">
  <img src="https://img.shields.io/badge/-Jetpack%20Compose-4285F4.svg?logo=jetpackcompose&style=for-the-badge&logoColor=white">
</p>

## 目次

1. [概要](#概要)
2. [環境](#環境)
3. [ディレクトリ構成](#ディレクトリ構成)
4. [開発環境構築](#開発環境構築)
5. [トラブルシューティング](#トラブルシューティング)

<!-- プロジェクトについて -->

## 概要

時間を決めてTodoを並べると、1タスクにかけていい時間が自動で出てタイマーで計測開始。「今から35分で家を出る。」「でもやりたいことが5つある」そんな時に、考えなくても1つ7分と教えてくれる。時間の使い方を、数字で見える化するアプリです。

<p align="right">(<a href="#top">トップへ</a>)</p>

## 環境

| 言語・フレームワーク  | バージョン |
| --------------------- | ---------- |
| Kotlin                | 2.2.10     |
| Jetpack Compose (BOM) | 2024.09.00 |
| Android Gradle Plugin | 9.1.0      |
| Minimum SDK           | 27         |
| Target SDK            | 36         |

<p align="right">(<a href="#top">トップへ</a>)</p>

## ディレクトリ構成

<!-- Treeコマンドを使ってディレクトリ構成を記載 -->
```text
.
├── app
│   ├── build
│   │   ├── generated
│   │   ├── intermediates
│   │   ├── kotlin
│   │   └── outputs
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src
│       ├── androidTest
│       ├── main
│       │   └── java
│       │       └── com
│       │           └── kasouzou
│       │               └── fittasks
│       │                   ├── MainActivity.kt
│       │                   ├── model
│       │                   │   └── TaskGroup.kt
│       │                   └── ui
│       │                       ├── TaskListScreen.kt
│       │                       ├── components
│       │                       │   └── TaskGroupCard.kt
│       │                       └── theme
│       │                           ├── Color.kt
│       │                           ├── Theme.kt
│       │                           └── Type.kt
│       └── test
├── build.gradle.kts
├── GEMINI.md
├── gradle
│   ├── gradle-daemon-jvm.properties
│   ├── libs.versions.toml
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties
├── gradlew
├── gradlew.bat
├── kls_database.db
├── local.properties
├── README.md
└── settings.gradle.kts
```

<p align="right">(<a href="#top">トップへ</a>)</p>




