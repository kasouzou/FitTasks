# 実装メソッドの呼び出しフローと依存注入（DI）について

## 概要
本プロジェクトでは、Dagger Hilt や Koin などの外部 DI ライブラリを使用せず、**手動による依存注入（Manual Dependency Injection）** を採用しています。これにより、依存関係の可視性を高め、シンプルな構造を維持しています。

## 1. 依存関係の組み立て（Composition Root）
`MainActivity.kt` がアプリの「構成ルート」として機能し、起動時に必要な具象クラスのインスタンスを生成・保持します。

- **データベース**: `FitTasksDatabase` を Room でビルド。
- **リポジトリ**: `RoomTaskRepository`（具象クラス）を生成。この際、`TaskRepository` インターフェースとして扱われます。
- **ユースケース**: 各 UseCase にリポジトリのインスタンスを注入します。

```kotlin
// MainActivity.kt での組み立て例
val database = remember { Room.databaseBuilder(...).build() }
val repository = remember { RoomTaskRepository(database.taskGroupDao()) }
val saveTaskGroupUseCase = remember { SaveTaskGroupUseCase(repository) }
```

## 2. ViewModel への注入（Factory の役割）
Android の `ViewModel` は、引数を持つ場合に `ViewModelProvider.Factory` を必要とします。

- `MainActivity` から `repository` を `TaskListViewModelFactory` に渡します。
- `Factory` 内部で、必要なユースケース（`GetTaskGroupsUseCase`, `DeleteTaskGroupUseCase`）を生成し、`TaskListViewModel` のコンストラクタに注入します。

## 3. メソッド呼び出しのライフサイクル
UI 層でアクションが発生してから、実際にデータが処理されるまでの流れは以下の通りです。

1.  **UI (Composable)**: `saveTaskGroupUseCase(group)` を実行。
2.  **Domain (UseCase)**: `repository.saveTaskGroup(group)` を呼び出す。
    - ここで呼ばれる `repository` はインターフェースであり、具体的な実装は関知しません。
3.  **Data (Repository Impl)**: `RoomTaskRepository.saveTaskGroup`（具象メソッド）が実行される。
4.  **Data (DAO)**: `TaskGroupDao` を介して SQLite への保存処理が行われる。

## 4. Flutter (Riverpod) との対比
Flutter の Riverpod と比較すると、以下のようになります。

| 特徴 | Flutter (Riverpod) | 本プロジェクト (Manual DI) |
| :--- | :--- | :--- |
| **インスタンス管理** | `Provider` がグローバルなメモリ空間で保持 | `MainActivity` が `remember` で保持 |
| **依存の取得** | `ref.read(provider)` で必要な時に取得 | コンストラクタ経由で上位から下位へ渡す |
| **具象クラスの隠蔽** | `Provider<Repository>` で抽象化 | `TaskRepository` インターフェースで抽象化 |

## 結論
この構造により、UI 層やドメイン層は「データがどのように保存されるか（Room なのか、メモリなのか）」を知る必要がなく、ドメインモデルに基づいた操作のみに集中できます。これがクリーンアーキテクチャにおける「依存性の逆転（DIP）」の実現形態です。
