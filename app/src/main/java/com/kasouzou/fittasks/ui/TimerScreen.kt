package com.kasouzou.fittasks.ui // UI パッケージ

import androidx.compose.animation.* // アニメーション API
import androidx.compose.animation.core.* // アニメーションのコア
import androidx.compose.foundation.background // 背景描画用修飾子
import androidx.compose.foundation.layout.* // レイアウト用 Compose API
import androidx.compose.foundation.lazy.LazyColumn // 縦スクロールリスト
import androidx.compose.foundation.lazy.itemsIndexed // インデックス付きのリスト要素
import androidx.compose.foundation.shape.CircleShape // 円形の Shape
import androidx.compose.foundation.shape.RoundedCornerShape // 角丸 Shape
import androidx.compose.material.icons.Icons // アイコンカタログ
import androidx.compose.material.icons.filled.ArrowBack // 戻るアイコン
import androidx.compose.material.icons.filled.Check // チェックアイコン
import androidx.compose.material.icons.filled.Pause // 一時停止アイコン
import androidx.compose.material.icons.filled.PlayArrow // 再生アイコン
import androidx.compose.material.icons.filled.SkipNext // スキップアイコン
import androidx.compose.material3.* // Material3 コンポーネント
import androidx.compose.runtime.* // Compose 状態管理
import androidx.compose.ui.Alignment // 配置オプション
import androidx.compose.ui.Modifier // 修飾子
import androidx.compose.ui.draw.clip // クリップ修飾子
import androidx.compose.ui.graphics.Color // 色定義
import androidx.compose.ui.graphics.luminance // 色の明るさを調べる
import androidx.compose.ui.res.stringResource // stringResource
import androidx.compose.ui.text.font.FontWeight // フォント設定
import androidx.compose.ui.unit.dp // dp 単位
import androidx.compose.ui.unit.sp // sp 単位
import com.kasouzou.fittasks.R // R クラス
import com.kasouzou.fittasks.domain.model.TaskGroup // タスクグループモデル
import com.kasouzou.fittasks.domain.model.TaskItem // 個別タスクモデル
import com.kasouzou.fittasks.ui.components.FooterBannerAd // 底部広告
import java.util.Locale // Locale を使ったフォーマット

@OptIn(ExperimentalMaterial3Api::class) // Experimental API に対応
@Composable // 描画関数であることを示す
fun TimerScreen( // タイマー画面の Composable
    taskGroup: TaskGroup, // 表示対象のタスクグループ
    onBack: () -> Unit // 戻るアクション
) { // 関数本体
    val viewModel = remember { TimerViewModel(taskGroup) } // viewModel を remember で保持
    val uiState by viewModel.uiState.collectAsState() // UI 状態を監視

    Scaffold( // 上下バー付きレイアウト
        modifier = Modifier.navigationBarsPadding(),
        topBar = { // 上部バー
            TopAppBar( // 通常の AppBar
                title = { Text(stringResource(R.string.timer_title), fontWeight = FontWeight.Bold) }, // タイトル
                navigationIcon = { // ナビゲーションアイコン
                    IconButton(onClick = onBack) { // 戻るボタン
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_desc)) // 戻るアイコン
                    } // IconButton 閉じ
                }, // navigationIcon 閉じ
                colors = TopAppBarDefaults.topAppBarColors( // 色設定
                    containerColor = MaterialTheme.colorScheme.background // 背景色
                ) // colors 閉じ
            ) // TopAppBar 閉じ
        }, // topBar 閉じ
        bottomBar = { // 下部広告バー
            FooterBannerAd( // 広告コンポーネント
                modifier = Modifier.fillMaxWidth() // 横幅いっぱい
            ) // FooterBannerAd 閉じ
        } // bottomBar 閉じ
    ) { innerPadding -> // 内部コンテンツ
        Column( // 縦方向のレイアウト
            modifier = Modifier // Modifier チェーン
                .padding(innerPadding) // Scaffold による余白
                .fillMaxSize() // 画面全体
                .padding(24.dp), // 内側パディング
            horizontalAlignment = Alignment.CenterHorizontally // 横寄せ中心
        ) { // Column 本体
            Text( // タスクグループの時間表示
                text = "${taskGroup.startTime} - ${taskGroup.endTime}", // 開始/終了時間
                style = MaterialTheme.typography.titleMedium, // タイポグラフィ
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) // 半透明
            ) // Text 閉じ

            Spacer(modifier = Modifier.height(32.dp)) // 上下のすき間

            val currentTask = uiState.taskGroup.tasks.getOrNull(uiState.currentTaskIndex) // 現在タスクを取得
            
            if (uiState.isFinished) { // 完了状態なら
                FinishedDisplay(onBack) // 完了表示を見せる
            } else if (currentTask != null) { // 現在タスクがある場合
                TimerDisplay( // タイマー中央表示
                    task = currentTask, // 現在タスク
                    remainingSeconds = uiState.remainingSeconds, // 残り秒数
                    isRunning = uiState.isRunning, // 実行中フラグ
                    onToggleTimer = { // 再生/一時停止処理
                        if (uiState.isRunning) viewModel.pauseTimer() else viewModel.startTimer() // 状態に応じて切り替え
                    }, // onToggleTimer 閉じ
                    onSkip = { viewModel.skipTask() } // スキップハンドラ
                ) // TimerDisplay 閉じ
            } // if/else 閉じ

            Spacer(modifier = Modifier.height(48.dp)) // さらに間隔

            Text( // 次のタスク見出し
                text = stringResource(R.string.next_tasks), // 見出しテキスト
                style = MaterialTheme.typography.labelLarge, // ラベルスタイル
                modifier = Modifier.align(Alignment.Start), // 左寄せ
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) // 半透明
            ) // Text 閉じ
            
            Spacer(modifier = Modifier.height(16.dp)) // 間隔

            LazyColumn( // 次のタスク一覧
                verticalArrangement = Arrangement.spacedBy(12.dp), // アイテム間隔
                modifier = Modifier.fillMaxWidth() // 横幅いっぱい
            ) { // LazyColumn ブロック
                itemsIndexed(uiState.taskGroup.tasks) { index, task -> // 各タスクごとに処理
                    val isCurrent = index == uiState.currentTaskIndex // 現在タスク判定
                    val isDone = index < uiState.currentTaskIndex // すでに完了したか
                    
                    MiniTaskItem( // 小さいタスクリストアイテム
                        task = task, // タスクデータ
                        isCurrent = isCurrent, // 現在かどうか
                        isDone = isDone // 完了済みか
                    ) // MiniTaskItem 閉じ
                } // itemsIndexed 閉じ
            } // LazyColumn 閉じ
        } // Column 閉じ
    } // Scaffold 閉じ
} // TimerScreen 閉じ

@Composable // Compose 関数
fun TimerDisplay( // タイマー中央の表示部
    task: TaskItem, // 対象タスク
    remainingSeconds: Long, // 残り秒数
    isRunning: Boolean, // 実行中フラグ
    onToggleTimer: () -> Unit, // 再生/一時停止
    onSkip: () -> Unit // スキップ
) { // 本体
    val minutes = remainingSeconds / 60 // 分
    val seconds = remainingSeconds % 60 // 秒
    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds) // 時刻文字列

    Column( // 中央表示レイアウト
        horizontalAlignment = Alignment.CenterHorizontally, // 中央寄せ
        modifier = Modifier.fillMaxWidth() // 横幅いっぱい
    ) { // Column 本体
        Surface( // タスクタイトルの背景
            color = task.color, // タスク色
            shape = RoundedCornerShape(20.dp), // 角を丸める
            modifier = Modifier.padding(bottom = 24.dp) // 下マージン
        ) { // Surface 本体
            val contentColor = if (task.color.luminance() > 0.5f) Color.Black else Color.White // タスク色に応じた文字色
            Text( // タスク名
                text = task.title, // タイトルテキスト
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), // パディング
                style = MaterialTheme.typography.headlineSmall, // フォントスタイル
                fontWeight = FontWeight.Bold, // 太字
                color = contentColor // 対比を考えた色
            ) // Text 閉じ
        } // Surface 閉じ

        Box( // 円形タイマー背景
            contentAlignment = Alignment.Center, // 中央揃え
            modifier = Modifier // 修飾子
                .size(240.dp) // サイズ
                .clip(CircleShape) // 円形にクリップ
                .background(task.color.copy(alpha = 0.1f)) // 背景色
        ) { // Box 本体
            Text( // 残り時間表示
                text = timeStr, // フォーマット済み文字列
                style = MaterialTheme.typography.displayLarge.copy( // フォントサイズ調整
                    fontWeight = FontWeight.Bold, // 太字
                    fontSize = 64.sp // 文字サイズ
                ), // copy 閉じ
                color = task.color // タスク色
            ) // Text 閉じ
        } // Box 閉じ

        Spacer(modifier = Modifier.height(40.dp)) // コントロールとタイマーの間隔

        Row( // 再生/スキップボタンを横に並べる
            verticalAlignment = Alignment.CenterVertically, // 垂直方向の中央揃え
            horizontalArrangement = Arrangement.spacedBy(32.dp) // 水平方向の間隔
        ) { // Row 本体
            FloatingActionButton( // 再生/一時停止ボタン
                onClick = onToggleTimer, // トグル処理
                containerColor = task.color, // 背景色
                contentColor = if (task.color.luminance() > 0.5f) Color.Black else Color.White, // アイコン色
                shape = CircleShape, // 円形
                modifier = Modifier.size(72.dp) // サイズ
            ) { // FAB 本体
                Icon( // 再生/一時停止アイコン
                    if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, // 状態に応じたアイコン
                    contentDescription = if (isRunning) stringResource(R.string.pause_desc) else stringResource(R.string.play_desc), // アクセシビリティ説明
                    modifier = Modifier.size(36.dp) // アイコンサイズ
                ) // Icon 閉じ
            } // FloatingActionButton 閉じ

            IconButton( // スキップボタン
                onClick = onSkip, // スキップ処理
                modifier = Modifier // 修飾子
                    .size(56.dp) // サイズ
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape) // 背景と形状
            ) { // IconButton 本体
                Icon(Icons.Default.SkipNext, contentDescription = stringResource(R.string.skip_desc)) // スキップアイコン
            } // IconButton 閉じ
        } // Row 閉じ
    } // Column 閉じ
} // TimerDisplay 閉じ

@Composable // Compose 関数
fun FinishedDisplay(onBack: () -> Unit) { // 終了表示
    Column( // 縦方向の中央揃え
        horizontalAlignment = Alignment.CenterHorizontally, // 横方向中央
        verticalArrangement = Arrangement.Center, // 縦方向中央
        modifier = Modifier.padding(vertical = 40.dp) // 上下余白
    ) { // Column 本体
        Icon( // チェックアイコン
            Icons.Default.Check, // アイコン
            contentDescription = "Finished", // 説明
            tint = MaterialTheme.colorScheme.primary, // 色
            modifier = Modifier.size(120.dp) // サイズ
        ) // Icon 閉じ
        Text( // 成功メッセージ
            stringResource(R.string.great_job), // テキスト
            style = MaterialTheme.typography.headlineMedium, // フォント
            fontWeight = FontWeight.Bold // 太字
        ) // Text 閉じ
        Text( // サブテキスト
            stringResource(R.string.all_tasks_completed), // テキスト
            style = MaterialTheme.typography.bodyLarge, // フォント
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) // 半透明
        ) // Text 閉じ
        Spacer(modifier = Modifier.height(40.dp)) // 間隔
        Button(onClick = onBack) { // 戻るボタン
            Text(stringResource(R.string.back_to_list)) // テキスト
        } // Button 閉じ
    } // Column 閉じ
} // FinishedDisplay 閉じ

@Composable // Compose 関数
fun MiniTaskItem( // タスク一覧アイテム
    task: TaskItem, // タスクデータ
    isCurrent: Boolean, // 現在タスクか
    isDone: Boolean // 完了済みか
) { // 本体
    val backgroundColor = if (isCurrent) task.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface // 背景色
    val borderColor = if (isCurrent) task.color else Color.Transparent // 枠線色
    
    Surface( // アイテム全体の背景
        color = backgroundColor, // 背景色を設定
        shape = RoundedCornerShape(16.dp), // 角丸
        modifier = Modifier // 修飾子
            .fillMaxWidth() // 横幅いっぱい
            .height(56.dp), // 高さ固定
        border = if (isCurrent) androidx.compose.foundation.BorderStroke(2.dp, borderColor) else null // 枠線
    ) { // Surface 本体
        Row( // 横並びレイアウト
            verticalAlignment = Alignment.CenterVertically, // 垂直方向中央揃え
            modifier = Modifier.padding(horizontal = 16.dp) // 横パディング
        ) { // Row 本体
            Box( // 状態インジケーター
                modifier = Modifier // 修飾子
                    .size(12.dp) // サイズ
                    .clip(CircleShape) // 円形
                    .background(if (isDone) Color.Gray else task.color) // 完了/未完了で色変え
            ) // Box 閉じ
            Spacer(modifier = Modifier.width(16.dp)) // 間隔
            Text( // タスク名
                text = task.title, // テキスト
                modifier = Modifier.weight(1f), // 残り幅で伸ばす
                style = MaterialTheme.typography.bodyMedium, // フォント
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal, // 現在なら太字
                color = if (isDone) Color.Gray else MaterialTheme.colorScheme.onSurface, // 完了なら薄く
                textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null // 完了なら取り消し線
            ) // Text 閉じ
            
            if (isDone) { // 完了済みならチェックマーク
                Spacer(modifier = Modifier.weight(1f)) // 空白で右寄せ
                Icon( // チェックアイコン
                    Icons.Default.Check, // アイコン
                    contentDescription = null, // アクセシビリティ省略
                    tint = Color.Gray, // 色
                    modifier = Modifier.size(16.dp) // サイズ
                ) // Icon 閉じ
            } // if 閉じ
        } // Row 閉じ
    } // Surface 閉じ
} // MiniTaskItem 閉じ
