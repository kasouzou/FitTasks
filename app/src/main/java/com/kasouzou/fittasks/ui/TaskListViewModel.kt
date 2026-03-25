package com.kasouzou.fittasks.ui // UI パッケージ

import androidx.lifecycle.ViewModel // ViewModel クラス
import androidx.lifecycle.viewModelScope // CoroutineScope を ViewModel に紐づける
import com.kasouzou.fittasks.domain.model.TaskGroup // タスクグループモデル
import com.kasouzou.fittasks.domain.usecase.DeleteTaskGroupUseCase // タスク削除ユースケース
import com.kasouzou.fittasks.domain.usecase.GetTaskGroupsUseCase // タスク取得ユースケース
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch // コルーチン起動

class TaskListViewModel( // タスクリスト画面の ViewModel
    getTaskGroupsUseCase: GetTaskGroupsUseCase, // タスク取得用
    private val deleteTaskGroupUseCase: DeleteTaskGroupUseCase // タスク削除用
) : ViewModel() { // ViewModel を継承

    // stateIn を使用して Flow を一定時間キャッシュし、かつ初期値を即座に決定するように変更
    val uiState: StateFlow<TaskListUiState> = getTaskGroupsUseCase()
        .map { groups -> TaskListUiState.Success(groups) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskListUiState.Loading
        )

    fun deleteTaskGroup(group: TaskGroup) { // タスク削除処理
        viewModelScope.launch { // 非同期で削除
            deleteTaskGroupUseCase(group) // 削除実行
        } // launch 閉じ
    } // deleteTaskGroup 閉じ
} // ViewModel クラス閉じ

sealed interface TaskListUiState { // UI 状態をシールドインターフェースで表現
    object Loading : TaskListUiState // 読み込み中
    data class Success(val groups: List<TaskGroup>) : TaskListUiState // 成功状態
    data class Error(val message: String) : TaskListUiState // エラー状態
} // sealed interface 閉じ
