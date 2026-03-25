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
3. [機能](#機能)
4. [ディレクトリ構成](#ディレクトリ構成)
5. [開発環境構築](#開発環境構築)
6. [トラブルシューティング](#トラブルシューティング)

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

## 機能

- **タスク管理**: タスクグループ（開始・終了時間、タスクリスト）の作成・編集・削除
- **自動時間配分**: 指定した時間範囲をタスク数で均等に分割
- **自動進行タイマー**: 各タスクの持ち時間を計測し、自動で次のタスクへ遷移
- **多言語対応**: 日本語、英語、中国語（簡体字）、韓国語に対応。初回起動時の言語選択および設定画面からの変更が可能
- **広告表示**: AdMobによるバナー広告表示

<p align="right">(<a href="#top">トップへ</a>)</p>

## ディレクトリ構成

<!-- Treeコマンドを使ってディレクトリ構成を記載 -->
```text
.
├── app
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src
│       ├── androidTest
│       ├── main
│       │   ├── AndroidManifest.xml
│       │   ├── java
│       │   │   └── com
│       │   │       └── kasouzou
│       │   │           └── fittasks
│       │   │               ├── MainActivity.kt
│       │   │               ├── data
│       │   │               │   ├── local
│       │   │               │   │   ├── Converters.kt
│       │   │               │   │   ├── FitTasksDatabase.kt
│       │   │               │   │   ├── dao
│       │   │               │   │   │   └── TaskGroupDao.kt
│       │   │               │   │   └── entity
│       │   │               │   │       ├── TaskGroupEntity.kt
│       │   │               │   │       └── TaskItemEntity.kt
│       │   │               │   └── repository
│       │   │               │       ├── DataStorePreferenceRepository.kt
│       │   │               │       └── RoomTaskRepository.kt
│       │   │               ├── domain
│       │   │               │   ├── model
│       │   │               │   │   └── TaskGroup.kt
│       │   │               │   ├── repository
│       │   │               │   │   ├── PreferenceRepository.kt
│       │   │               │   │   └── TaskRepository.kt
│       │   │               │   └── usecase
│       │   │               │       ├── DeleteTaskGroupUseCase.kt
│       │   │               │       ├── GetTaskGroupsUseCase.kt
│       │   │               │       ├── LanguageUseCases.kt
│       │   │               │       └── SaveTaskGroupUseCase.kt
│       │   │               └── ui
│       │   │                   ├── LanguageSelectionScreen.kt
│       │   │                   ├── PreferenceViewModel.kt
│       │   │                   ├── SettingsScreen.kt
│       │   │                   ├── TaskEditScreen.kt
│       │   │                   ├── TaskListScreen.kt
│       │   │                   ├── TaskListViewModel.kt
│       │   │                   ├── TaskListViewModelFactory.kt
│       │   │                   ├── TimerScreen.kt
│       │   │                   ├── TimerViewModel.kt
│       │   │                   ├── components
│       │   │                   │   ├── FooterBannerAd.kt
│       │   │                   │   └── TaskGroupCard.kt
│       │   │                   └── theme
│       │   │                       ├── Color.kt
│       │   │                       ├── Theme.kt
│       │   │                       └── Type.kt
│       │   └── res
│       │       ├── values
│       │       ├── values-en
│       │       ├── values-ko
│       │       └── values-zh-rCN
│       └── test
│           └── java
│               └── com
│                   └── kasouzou
│                       └── fittasks
│                           ├── ExampleUnitTest.kt
│                           ├── domain
│                           │   └── model
│                           │       └── TaskGroupTest.kt
│                           └── ui
│                               └── TimerViewModelTest.kt
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
├── LOG.md
├── README.md
└── settings.gradle.kts
```

<p align="right">(<a href="#top">トップへ</a>)</p>

## 開発環境構築

1. Android Studio をインストールする。
1. Android SDK を導入する。
1. SDK パスを `local.properties` に設定する。

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

1. 依存関係を取得する。

```bash
./gradlew dependencies
```

<p align="right">(<a href="#top">トップへ</a>)</p>

## トラブルシューティング

- `SDK location not found.` が出る場合は `local.properties` の `sdk.dir` を確認してください。
- ビルドキャッシュが原因の不具合が疑われる場合は `./gradlew clean` を実行してください。

<p align="right">(<a href="#top">トップへ</a>)</p>
