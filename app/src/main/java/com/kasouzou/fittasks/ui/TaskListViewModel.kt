package com.kasouzou.fittasks.ui // UI パッケージ

import androidx.lifecycle.ViewModel // ViewModel クラス
import androidx.lifecycle.viewModelScope // CoroutineScope を ViewModel に紐づける
import com.kasouzou.fittasks.domain.model.TaskGroup // タスクグループモデル
import com.kasouzou.fittasks.domain.usecase.DeleteTaskGroupUseCase // タスク削除ユースケース
import com.kasouzou.fittasks.domain.usecase.GetTaskGroupsUseCase // タスク取得ユースケース
import kotlinx.coroutines.flow.MutableStateFlow // 状態を保持する StateFlow
import kotlinx.coroutines.flow.StateFlow // 読み取り専用 StateFlow
import kotlinx.coroutines.flow.asStateFlow // MutableStateFlow を変換
import kotlinx.coroutines.launch // コルーチン起動

class TaskListViewModel( // タスクリスト画面の ViewModel
    private val getTaskGroupsUseCase: GetTaskGroupsUseCase, // タスク取得用
    private val deleteTaskGroupUseCase: DeleteTaskGroupUseCase // タスク削除用
) : ViewModel() { // ViewModel を継承

    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Loading) // 内部状態を初期化
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow() // 外部公開用読み取り専用状態

    init { // 初期化処理
        loadTaskGroups() // タスク一覧を読み込む
    } // init 閉じ

    private fun loadTaskGroups() { // タスクグループ取得
        viewModelScope.launch { // コルーチンを起動
            getTaskGroupsUseCase().collect { groups -> // Flow を収集
                _uiState.value = TaskListUiState.Success(groups) // 成功ステートをセット
            } // collect 閉じ
        } // launch 閉じ
    } // loadTaskGroups 閉じ

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
