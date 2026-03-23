package com.kasouzou.fittasks.ui // このファイルが属する UI パッケージを示す

import androidx.lifecycle.ViewModel // ViewModel の基本クラスを使う
import androidx.lifecycle.ViewModelProvider // ViewModelProvider.Factory を実装するために必要
import com.kasouzou.fittasks.domain.repository.TaskRepository // タスク情報を取得/操作するリポジトリ
import com.kasouzou.fittasks.domain.usecase.DeleteTaskGroupUseCase // タスク削除ユースケース
import com.kasouzou.fittasks.domain.usecase.GetTaskGroupsUseCase // タスク一覧取得ユースケース

@Suppress("UNCHECKED_CAST") // 型チェック警告を suppression するアノテーション
class TaskListViewModelFactory( // ViewModel を作るファクトリクラス
    private val repository: TaskRepository // リポジトリを保持し ViewModel に渡す
) : ViewModelProvider.Factory { // Factory インターフェースを実装
    override fun <T : ViewModel> create(modelClass: Class<T>): T { // ViewModel をインスタンス化するメソッド
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) { // 引数が TaskListViewModel 互換であれば
            val getUseCase = GetTaskGroupsUseCase(repository) // タスク取得ユースケースを構築
            val deleteUseCase = DeleteTaskGroupUseCase(repository) // タスク削除ユースケースを構築
            return TaskListViewModel(getUseCase, deleteUseCase) as T // 明示的キャストして返す
        }
        throw IllegalArgumentException("Unknown ViewModel class") // 想定外の ViewModel なら例外を投げる
    } // create メソッドの終了
} // ファクトリクラスの終了
