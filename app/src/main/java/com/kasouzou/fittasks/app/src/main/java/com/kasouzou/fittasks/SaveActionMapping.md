# 保存ボタンの定義とユースケースへの接続マップ

## 概要
本プロジェクトにおける「保存」ボタンは、特定の画面（Composable）内に定義されています。しかし、その画面自体がユースケースを直接実行するのではなく、**イベントを上位（Activity）に通知し、上位がユースケースを実行する** という「委譲」の仕組みを採用しています。

## 1. 保存ボタンの定義箇所
保存ボタンは、**`TaskEditScreen.kt`** 内の `Scaffold` の `floatingActionButton`（画面右下の浮いたボタン）として定義されています。

- **ファイルパス**: `app/src/main/java/com/kasouzou/fittasks/ui/TaskEditScreen.kt`
- **コード例**:
    ```kotlin
    floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = {
                // ここで引数として渡された onSave コールバックを実行
                onSave(TaskGroup(id = id, startTime = startTime, endTime = endTime, tasks = tasks))
            },
            ...
        ) {
            Icon(Icons.Default.Check, contentDescription = "保存")
            Text("保存")
        }
    }
    ```

## 2. ユースケースを直接呼び出しているか？
**いいえ、`TaskEditScreen`（UI）はユースケースを直接呼び出しません。**

UI（`TaskEditScreen`）は、「保存ボタンが押された」というイベントを、`onSave` という名前のコールバック関数に渡して通知するだけです。

実際にユースケースを呼び出しているのは、この画面を生成・管理している **`MainActivity.kt`** です。

---

## 3. 保存処理の具体的な連鎖（バケツリレーの仕組み）

1.  **UI (`TaskEditScreen.kt`)**:
    保存ボタンが押されると、コンストラクタで受け取った `onSave` ラムダ（関数）を実行し、現在の入力内容（`TaskGroup`）を渡します。
    ```kotlin
    onClick = { onSave(updatedGroup) }
    ```

2.  **管理役 (`MainActivity.kt`)**:
    `TaskEditScreen` を呼び出す際に、`onSave` の中身（具体的な処理内容）を以下のように定義して渡しています。
    ```kotlin
    // MainActivity.kt 内
    TaskEditScreen(
        onSave = { group -> 
            // ここで初めて UseCase を直接呼び出す
            CoroutineScope(Dispatchers.Main).launch {
                saveTaskGroupUseCase(group) // ユースケースの実行
                currentScreen = Screen.TaskList // 保存後にリスト画面へ戻す
            }
        }
    )
    ```

## 結論
この設計により、**「何を表示し、どう入力させるか（UI）」**と**「入力されたデータをどう保存するか（Business Logic）」**が明確に分離されています。
UI側は「保存の仕組み」を知らなくてもよいため、将来的に保存先をDBからクラウドに変えたり、テスト用の保存ロジックに差し替えたりすることが容易になります。
