package com.kasouzou.fittasks.ui // UI パッケージ

import androidx.lifecycle.ViewModel // ViewModel を継承するための基本クラス
import androidx.lifecycle.viewModelScope // コルーチンをライフサイクルに合わせるスコープ
import com.kasouzou.fittasks.domain.model.TaskGroup // タスクグループモデル
import kotlinx.coroutines.Job // コルーチンのキャンセルを管理するための Job
import kotlinx.coroutines.delay // 一定時間待つための関数
import kotlinx.coroutines.flow.MutableStateFlow // 状態を保持する StateFlow の可変版
import kotlinx.coroutines.flow.StateFlow // 読み取り専用のステートフロー
import kotlinx.coroutines.flow.asStateFlow // MutableStateFlow を StateFlow に変換
import kotlinx.coroutines.flow.update // 状態を更新する際に利用する拡張関数
import kotlinx.coroutines.launch // コルーチン起動

data class TimerUiState( // タイマー画面の表示状態データクラス
    val taskGroup: TaskGroup, // 表示対象グループ
    val currentTaskIndex: Int = 0, // 現在のタスク位置
    val remainingSeconds: Long = 0, // 残り秒数
    val isRunning: Boolean = false, // タイマーが動いているか
    val isFinished: Boolean = false // 最後まで完了しているか
) // データクラス終了

class TimerViewModel(private val taskGroup: TaskGroup) : ViewModel() { // タスクグループを受け取る ViewModel

    private val _uiState = MutableStateFlow( // 内部状態を保持する MutableStateFlow
        TimerUiState( // 初期状態オブジェクト
            taskGroup = taskGroup, // グループを渡す
            remainingSeconds = taskGroup.durationPerTaskSeconds // 初期残秒数をタスク継続時間で設定
        ) // TimerUiState の閉じ
    ) // MutableStateFlow の閉じ
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow() // 外部に公開する読み取り専用状態

    private var timerJob: Job? = null // 現在のタイマージョブを保持

    init { // ViewModel 初期化時の処理
        startTimer() // 自動でタイマーを開始
    } // init 閉じ

    fun startTimer() { // タイマー再開処理
        if (_uiState.value.isFinished) return // 完了済みなら再開しない
        
        _uiState.update { it.copy(isRunning = true) } // 実行中フラグをセット
        
        timerJob?.cancel() // 既存のジョブがあればキャンセル
        timerJob = viewModelScope.launch { // 新しいジョブを起動
            while (_uiState.value.isRunning) { // 実行中はループ
                delay(1000L) // 1秒待つ
                tick() // 秒経過処理
            } // while 閉じ
        } // launch 閉じ
    } // startTimer 閉じ

    fun pauseTimer() { // 一時停止処理
        _uiState.update { it.copy(isRunning = false) } // 実行フラグを落とす
        timerJob?.cancel() // ジョブをキャンセル
    } // pauseTimer 閉じ

    fun stopTimer() { // 停止処理
        pauseTimer() // 一時停止と同じ挙動
    } // stopTimer 閉じ

    private fun tick() { // 1秒ごとの更新処理
        _uiState.update { state -> // 現在状態をスナップショット
            if (state.remainingSeconds > 0) { // まだ秒数がある場合
                state.copy(remainingSeconds = state.remainingSeconds - 1) // 1 秒減少
            } else { // 秒数がなくなったら
                val nextIndex = state.currentTaskIndex + 1 // 次のタスクインデックス
                if (nextIndex < taskGroup.tasks.size) { // タスクが残っていれば
                    state.copy( // 次のタスクへ移行
                        currentTaskIndex = nextIndex, // インデックス更新
                        remainingSeconds = taskGroup.durationPerTaskSeconds // 秒数をリセット
                    ) // state.copy 閉じ
                } else { // 最後のタスクまで完了
                    state.copy( // 完了状態に遷移
                        isRunning = false, // 実行フラグオフ
                        isFinished = true, // 完了フラグオン
                        remainingSeconds = 0 // 残りゼロ
                    ) // state.copy 閉じ
                } // else 閉じ
            } // if 閉じ
        } // update 閉じ
        
        if (_uiState.value.isFinished) { // 完了時はジョブを止める
            timerJob?.cancel() // キャンセル
        } // if 閉じ
    } // tick 閉じ

    fun skipTask() { // タスクを飛ばす処理
        _uiState.update { state -> // 現在状態を基に更新
            val nextIndex = state.currentTaskIndex + 1 // 次のタスク
            if (nextIndex < taskGroup.tasks.size) { // 次が存在すれば
                state.copy( // タスクを進める
                    currentTaskIndex = nextIndex, // インデックス更新
                    remainingSeconds = taskGroup.durationPerTaskSeconds // 秒数リセット
                ) // state.copy 閉じ
            } else { // もうタスクがなければ
                state.copy( // 完了状態へ
                    isRunning = false, // 実行停止
                    isFinished = true, // 完了フラグオン
                    remainingSeconds = 0 // 秒数ゼロ
                ) // state.copy 閉じ
            } // if 閉じ
        } // update 閉じ
    } // skipTask 閉じ
} // TimerViewModel クラス閉じ
