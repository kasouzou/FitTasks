# ViewModelとFactoryの役割・データフロー
## TaskListViewModel
- ViewModelはUIとドメインの間に立ち、状態を`MutableStateFlow`で保持して画面に公開(`uiState`)します。
- `GetTaskGroupsUseCase`を`viewModelScope.launch`で`collect`し、`TaskListUiState.Success(groups)`へ更新することで、リポジトリから流れてきたグループの一覧をUIが受け取れるようにします。
- `deleteTaskGroup(group)`からは`DeleteTaskGroupUseCase`に処理を委ね、非同期で削除を実行。UIは削除ボタンのonClickでこのメソッドを呼びます。
- `TaskListUiState`は`Loading/Success/Error`を定義し、UIは状態に応じて適切な描画を選びます。
- 現状`init`で`loadTaskGroups()`が呼ばれていないので、data flowを動かすには明示的に`loadTaskGroups()`を起動する必要があります。

## TimerViewModel
- `TimerViewModel`はひとつの`TaskGroup`を受け取り、`TimerUiState`（タスクのリスト/インデックス/残秒/実行/完了フラグ）を`MutableStateFlow`で保持します。
- コンストラクタ直後に`startTimer()`を呼び、`timerJob`で1秒ごとのループ(`delay(1000)`+`tick()`)を実行。`tick()`は残秒を減らし、次のタスクへ切り替えるか、全タスク完了なら`isFinished`を立てます。
- `startTimer()`/`pauseTimer()`/`stopTimer()`/`skipTask()`はすべて`_uiState.update`で状態を変え、その結果をUIが`collectAsState()`などで描画します。
- UIからは再生/一時停止/スキップの操作をメソッド呼び出しとしてViewModelに伝え、ViewModelが状態を更新。StateFlowが唯一の状態ソースとして機能します。

## TaskListViewModelFactory
- 引数付きコンストラクタの`TaskListViewModel`をAndroidの`ViewModelProvider`から生成するために、`ViewModelProvider.Factory`を実装します。
- `create()`で`TaskListViewModel`を判定し、`TaskRepository`から`GetTaskGroupsUseCase`と`DeleteTaskGroupUseCase`を生成、それらを渡してViewModelを返します。
- UI側は`ViewModelProvider(this, TaskListViewModelFactory(taskRepository)).get(TaskListViewModel::class.java)`とするだけで、依存の組み立てを気にせずViewModelを取得できます。
- 将来的に他のViewModelも扱うなら`create()`内に追加ロジックを実装していきます。

## 全体フロー例（タスクリスト画面）
1. UIはファクトリ経由で`TaskListViewModel`を取得。
2. ViewModelが`loadTaskGroups()`でFlowをcollectし、成功状態を`uiState`に流す。
3. UIは`uiState`を参照してリストやステータスメッセージを描画。
4. ユーザーが削除操作を行うとViewModelの`deleteTaskGroup()`を呼び、削除後はFlowが再発火して新しいリストがUIに反映される。

必要であれば、これらのViewModelの値をCompose画面とどう結ぶか、具体的に画面側での`collectAsState()`やボタンの`onClick`と紐付ける例も追加できます。
