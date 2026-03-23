package com.kasouzou.fittasks // このファイルが属するパッケージを定義

import android.os.Bundle // Android の画面状態を保持する Bundle クラスをインポート
import androidx.activity.ComponentActivity // Compose と連携するベースアクティビティの継承元
import androidx.activity.compose.setContent // Compose の UI をアクティビティに設定する関数
import androidx.activity.enableEdgeToEdge // 画面のエッジまで描画するための補助関数
import androidx.compose.runtime.* // remember や state を使うための Compose ライブラリ
import androidx.compose.ui.platform.LocalContext // Compose でコンテキストを取得するためのプロバイダ
import androidx.lifecycle.viewmodel.compose.viewModel // Compose から ViewModel を取得する関数
import androidx.room.Room // Room データベースビルダーを使うためのクラス
import com.google.android.gms.ads.MobileAds // AdMob 初期化用
import com.kasouzou.fittasks.data.local.FitTasksDatabase // ローカルデータベース定義を読み込む
import com.kasouzou.fittasks.data.repository.RoomTaskRepository // Room を使ったリポジトリ実装
import com.kasouzou.fittasks.domain.model.TaskGroup // タスクグループのドメインモデル
import com.kasouzou.fittasks.domain.usecase.SaveTaskGroupUseCase // タスク保存ユースケース
import com.kasouzou.fittasks.ui.* // UI 関連の画面やコンポーネントをまとめてインポート
import com.kasouzou.fittasks.ui.theme.FitTasksTheme // アプリの共通テーマ
import kotlinx.coroutines.CoroutineScope // Compose 内でコルーチンを構築するためのスコープ
import kotlinx.coroutines.Dispatchers // コルーチンのディスパッチャーを指定するためのオブジェクト
import kotlinx.coroutines.launch // コルーチン起動用の launch 関数

class MainActivity : ComponentActivity() { // ComponentActivity を継承したエントリポイント
    override fun onCreate(savedInstanceState: Bundle?) { // ライフサイクルの onCreate をオーバーライド
        super.onCreate(savedInstanceState) // 親クラスの onCreate を呼んで標準処理を実行
        enableEdgeToEdge() // ステータスバー・ナビバーまで描画領域を広げる
        MobileAds.initialize(this) // AdMob を初期化して広告を利用可能にする
        setContent { // Compose で画面内容をセット
            FitTasksTheme { // 共通テーマでラッピング
                val context = LocalContext.current // Compose から現在の Context を取り出す
                val database = remember {
                    Room.databaseBuilder(context, FitTasksDatabase::class.java, "fittasks-db")
                            .build()
                } // データベースは Compose の再コンポジションでも保持しつつ構築
                val repository = remember {
                    RoomTaskRepository(database.taskGroupDao())
                } // Dao を渡してリポジトリを生成
                val saveTaskGroupUseCase = remember {
                    SaveTaskGroupUseCase(repository)
                } // タスク保存ユースケースを構築

                var currentScreen by remember {
                    mutableStateOf<Screen>(Screen.TaskList)
                } // 表示中画面を状態として保持

                when (val screen = currentScreen) { // 現在表示すべき画面を判定
                    is Screen.TaskList -> { // タスクリスト画面を表示
                        val taskListViewModel: TaskListViewModel =
                                viewModel( // Compose から ViewModel を取得TaskGroup
                                        factory =
                                                TaskListViewModelFactory(
                                                        repository
                                                ) // リポジトリを渡したファクトリを使う
                                )
                        TaskListScreen( // タスクリスト Composable を描画
                                onAddTask = {
                                    currentScreen = Screen.TaskEdit(null)
                                }, // タスク追加ボタンで編集画面へ遷移
                                onEditTask = { group ->
                                    currentScreen = Screen.TaskEdit(group)
                                }, // 編集リクエストで選択グループを渡す
                                onStartTimer = { group ->
                                    currentScreen = Screen.Timer(group)
                                }, // タイマー起動で Timer 画面へ
                                onDeleteTask = { group ->
                                    taskListViewModel.deleteTaskGroup(group)
                                }, // 削除は ViewModel に任せる
                                viewModel = taskListViewModel // 取得した ViewModel を渡す
                        )
                    }
                    is Screen.TaskEdit -> { // 新規・編集画面
                        TaskEditScreen( // 新規/既存グループで編集画面を表示
                                taskGroup = screen.taskGroup, // 編集対象のグループ
                                onBack = { currentScreen = Screen.TaskList }, // 戻るボタンでリスト画面へ
                                onSave = { group -> // 保存時に usecase を通す
                                    CoroutineScope(Dispatchers.Main).launch { // UI スレッドでコルーチンを立ち上げ
                                        saveTaskGroupUseCase(group) // データ保存を実行
                                        currentScreen = Screen.TaskList // 保存後にリストへ戻す
                                    }
                                }
                        )
                    }
                    is Screen.Timer -> { // タイマー画面
                        TimerScreen( // タイマー画面を表示
                                taskGroup = screen.taskGroup, // Timer に表示対象を渡す
                                onBack = { currentScreen = Screen.TaskList } // 戻るとリスト画面へ
                        )
                    }
                }
            }
        }
    }
}

sealed interface Screen { // 画面遷移の状態を定義するシールドインターフェース
    object TaskList : Screen // リスト画面を表すシングルトンオブジェクト
    data class TaskEdit(val taskGroup: TaskGroup?) : Screen // 編集画面用データクラス
    data class Timer(val taskGroup: TaskGroup) : Screen // タイマー画面用データクラス
}
