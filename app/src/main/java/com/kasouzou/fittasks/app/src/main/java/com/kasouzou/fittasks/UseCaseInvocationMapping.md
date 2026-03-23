# UIからユースケースへの呼び出しマップ

## 概要
本プロジェクトでは、UI層（Compose）からドメイン層（UseCase）への呼び出しを直接行わず、**ViewModel** または **MainActivity（最上位の画面遷移管理）** を介して行っています。

## 呼び出し箇所と使用されているユースケース

### 1. `TaskListViewModel.kt` での呼び出し
タスク一覧画面のロジックを担当するViewModel内で、以下のユースケースが実行されています。

| ユースケース名 | 呼び出しメソッド | 役割 |
| :--- | :--- | :--- |
| **`GetTaskGroupsUseCase`** | `init { ... }` | 画面起動時にタスク一覧をリアルタイム監視（Flow）で取得。 |
| **`DeleteTaskGroupUseCase`** | `deleteTaskGroup(group)` | ユーザーがリストのタスクを削除した際に実行。 |

- **ファイルパス**: `app/src/main/java/com/kasouzou/fittasks/ui/TaskListViewModel.kt`

### 2. `MainActivity.kt` での呼び出し
画面遷移と保存処理を管理する最上位のActivity内で、以下のユースケースが直接実行されています。

| ユースケース名 | 呼び出し箇所 | 役割 |
| :--- | :--- | :--- |
| **`SaveTaskGroupUseCase`** | `onSave = { group -> ... }` | `TaskEditScreen` で「保存」ボタンが押された際に実行。 |

- **ファイルパス**: `app/src/main/java/com/kasouzou/fittasks/MainActivity.kt`

---

## UI（Composable）との連携の流れ

各画面（Composable関数）は、ユースケースを直接知ることはありません。代わりに、**コールバック（ラムダ関数）** を通じて上位（ViewModelやActivity）に「イベント」を通知します。

### 例：タスクの保存処理の流れ
1.  **`TaskEditScreen.kt`**: ユーザーが「保存」をタップ。
2.  **イベント通知**: `onSave(updatedGroup)` コールバックが呼ばれる。
3.  **`MainActivity.kt`**: `onSave` の中身として定義された `saveTaskGroupUseCase(group)` を実行。
4.  **UseCase**: リポジトリを介してDBに保存。

### 例：タスクの削除処理の流れ
1.  **`TaskListScreen.kt`**: ユーザーが「削除」をタップ。
2.  **イベント通知**: `onDeleteTask(group)` コールバックが呼ばれる。
3.  **`TaskListViewModel.kt`**: `deleteTaskGroup(group)` メソッドが走り、内部で `deleteUseCase(group)` を実行。

---

## 結論
UI層は「何を表示するか」と「ユーザーが何をしたか」のみを管理し、**「どのようにデータを処理するか（UseCase）」という詳細はViewModelや上位の管理者に委ねられています。** これにより、UIの変更がビジネスロジックに影響を与えにくい、柔軟な設計（クリーンアーキテクチャ）が実現されています。
