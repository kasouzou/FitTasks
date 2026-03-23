# ViewModelの概念とプロジェクト内での役割

## ViewModelとは？
Android開発における **ViewModel** は、UI（画面）に表示するためのデータを保持し、ユーザーの操作（ビジネスロジック）を処理する役割を担うコンポーネントです。

### 主な特徴
1.  **状態の保持 (State Management)**: 画面の回転や設定変更（ダークモードへの切り替えなど）が発生しても、データを失わずに保持し続けます。
2.  **UIとデータの橋渡し**: リポジトリやユースケースからデータを取得し、Jetpack Composeが描画しやすい形（State）に加工して提供します。
3.  **UIからの独立**: ViewModelは「ボタン」や「テキスト」などの具体的なUI部品を直接操作しません。代わりに「現在の状態」を公開し、UIがそれを観察（Observe）して描画を更新します。

---

## 本プロジェクトでの該当箇所

このプロジェクトでは、UI層の一部として以下のディレクトリとファイルがViewModelに関連しています。

### 1. 関連ディレクトリ
- `app/src/main/java/com/kasouzou/fittasks/ui/`

### 2. ViewModelの実装ファイル
現在、以下の2つのViewModelがメインのロジックを担当しています。

| ファイル名 | 担当画面 | 主な役割 |
| :--- | :--- | :--- |
| **`TaskListViewModel.kt`** | タスク一覧画面 | タスクグループの一覧取得、削除処理の実行。 |
| **`TimerViewModel.kt`** | タイマー画面 | 残り時間のカウントダウン、タスクの自動切り替え、進捗管理。 |

### 3. 生成補助（Factory）
ViewModelが外部から依存関係（リポジトリやユースケース）を受け取る場合、`ViewModelProvider.Factory` という仕組みを使ってインスタンス化します。

- **`TaskListViewModelFactory.kt`**: `TaskListViewModel` を生成する際に、必要な `GetTaskGroupsUseCase` や `DeleteTaskGroupUseCase` を注入するための「工場」の役割を果たします。

---

## 処理のフロー例（タスク削除の場合）

1.  **UI (TaskListScreen.kt)**: ユーザーが「削除」ボタンをタップ。
2.  **ViewModel (TaskListViewModel.kt)**: UIからのイベントを受け取り、内部で保持している `deleteTaskGroupUseCase` を呼び出す。
3.  **Domain (DeleteTaskGroupUseCase.kt)**: リポジトリを介してデータの削除を指示。
4.  **Data (RoomTaskRepository.kt)**: データベースから実際にデータを削除。
5.  **ViewModel (TaskListViewModel.kt)**: 最新のデータリストを再取得し、UIに反映（自動で画面が更新される）。

このように、ViewModelを介在させることで **「見た目（UI）」と「データの扱い（UseCase）」をきれいに分離** しています。
