package com.kasouzou.fittasks.ui // UI パッケージ

import androidx.compose.foundation.background // 背景修飾子
import androidx.compose.foundation.clickable // クリック可能にする
import androidx.compose.foundation.layout.* // レイアウト API
import androidx.compose.foundation.lazy.LazyColumn // リスト
import androidx.compose.foundation.lazy.items // アイテムリスト
import androidx.compose.foundation.shape.CircleShape // 円形 Shape
import androidx.compose.foundation.shape.RoundedCornerShape // 角丸 Shape
import androidx.compose.material.icons.Icons // アイコンセット
import androidx.compose.material.icons.filled.ArrowBack // 戻るアイコン
import androidx.compose.material.icons.filled.Delete // 削除アイコン
import androidx.compose.material.icons.filled.WatchLater // 時計アイコン
import androidx.compose.material3.* // Material3 コンポーネント
import androidx.compose.runtime.* // 状態管理
import androidx.compose.ui.Alignment // 配置オプション
import androidx.compose.ui.Modifier // 修飾子
import androidx.compose.ui.draw.clip // クリップ
import androidx.compose.ui.graphics.Color // 色
import androidx.compose.ui.text.font.FontWeight // フォント太さ
import androidx.compose.ui.unit.dp // dp
import com.kasouzou.fittasks.domain.model.TaskGroup // タスクグループモデル
import com.kasouzou.fittasks.domain.model.TaskItem // タスクアイテムモデル
import com.kasouzou.fittasks.ui.components.FooterBannerAd // 底部広告
import com.kasouzou.fittasks.ui.theme.* // テーマカラー
import java.time.Duration // 時間差を扱うクラス
import java.time.LocalTime // 時刻を表すクラス
import java.util.Locale // ロケール

@OptIn(ExperimentalMaterial3Api::class) // Experimental API の使用を明示
@Composable // Compose 関数宣言
fun TaskEditScreen( // タスク追加/編集画面
    taskGroup: TaskGroup? = null, // 編集対象のグループ（null なら新規）
    onBack: () -> Unit, // 戻るアクション
    onSave: (TaskGroup) -> Unit // 保存アクション
) { // 本体
    var startTime by remember { mutableStateOf(taskGroup?.startTime ?: LocalTime.of(9, 0)) } // 開始時間の状態
    var endTime by remember { mutableStateOf(taskGroup?.endTime ?: LocalTime.of(10, 0)) } // 終了時間の状態
    var taskName by remember { mutableStateOf("") } // 入力中のタスク名状態
    
    val pastelColors = listOf( // 色候補リスト
        PastelPink, PastelBlue, PastelGreen, PastelYellow, PastelPurple, PastelOrange,
        SoftPink, SoftBlue
    ) // 色リスト閉じ
    var selectedColor by remember { mutableStateOf(pastelColors[0]) } // 選択中の色

    val tasks = remember {  // タスクリストの状態
        mutableStateListOf<TaskItem>().apply { // MutableStateList を初期化
            taskGroup?.let { addAll(it.tasks) } // 編集時は既存タスクを追加
        } // apply 閉じ
    } // remember 閉じ

    var showStartTimePicker by remember { mutableStateOf(false) } // 開始時刻ピッカー表示フラグ
    var showEndTimePicker by remember { mutableStateOf(false) } // 終了時刻ピッカー表示フラグ

    Scaffold( // Scaffold レイアウト
        topBar = { // 上部バー
            TopAppBar( // タイトルバー
                title = { Text(if (taskGroup == null) "タスク追加 ✨" else "タスク編集 ✏️", fontWeight = FontWeight.Bold) }, // 条件付きタイトル
                navigationIcon = { // 戻るアイコン
                    IconButton(onClick = onBack) { // IconButton
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back") // 戻るアイコン
                    } // IconButton 閉じ
                } // navigationIcon 閉じ
            ) // TopAppBar 閉じ
        }, // topBar 閉じ
        bottomBar = { // 底部広告
            FooterBannerAd( // 底部広告表示
                modifier = Modifier.fillMaxWidth() // 横幅いっぱい
            ) // FooterBannerAd 閉じ
        } // bottomBar 閉じ
    ) { innerPadding -> // Scaffold 本体
        Column( // 全体を縦並び
            modifier = Modifier // 修飾子
                .padding(innerPadding) // Scaffold の余白
                .padding(16.dp) // 内側余白
                .fillMaxSize(), // 全体サイズ
            verticalArrangement = Arrangement.spacedBy(16.dp) // 縦方向の間隔
        ) { // Column 本体
            Card( // 時刻設定カード
                modifier = Modifier.fillMaxWidth(), // 横幅いっぱい
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) // 背景色
            ) { // Card 本体
                Column( // 縦レイアウト
                    modifier = Modifier.padding(16.dp), // 内側余白
                    verticalArrangement = Arrangement.spacedBy(8.dp) // 間隔
                ) { // Column 本体
                    Text("時間設定", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) // セクション見出し
                    
                    Row( // 開始/終了の横並び
                        modifier = Modifier.fillMaxWidth(), // 横幅いっぱい
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // 横間隔
                    ) { // Row 本体
                        OutlinedCard( // 開始時間カード
                            onClick = { showStartTimePicker = true }, // クリックでピッカー表示
                            modifier = Modifier.weight(1f) // 均等幅
                        ) { // Card 本体
                            Column(modifier = Modifier.padding(12.dp)) { // テキストを縦並び
                                Text("開始時間", style = MaterialTheme.typography.labelSmall) // ラベル
                                Text(startTime.toString(), style = MaterialTheme.typography.titleLarge) // 時刻
                            } // Column 閉じ
                        } // OutlinedCard 閉じ
                        
                        OutlinedCard( // 終了時間カード
                            onClick = { showEndTimePicker = true }, // クリックでピッカー
                            modifier = Modifier.weight(1f) // 均等幅
                        ) { // Card 本体
                            Column(modifier = Modifier.padding(12.dp)) { // テキスト縦並び
                                Text("終了時間", style = MaterialTheme.typography.labelSmall) // ラベル
                                Text(endTime.toString(), style = MaterialTheme.typography.titleLarge) // 時刻
                            } // Column 閉じ
                        } // OutlinedCard 閉じ
                    } // Row 閉じ
                    
                    Row( // 所要時間表示
                        modifier = Modifier.fillMaxWidth(), // 横幅
                        horizontalArrangement = Arrangement.spacedBy(8.dp), // 横間隔
                        verticalAlignment = Alignment.CenterVertically // 垂直中央揃え
                    ) { // Row 本体
                        var durationInput by remember(startTime, endTime) {  // 所要時間入力状態
                            mutableStateOf(Duration.between(startTime, endTime).toMinutes().toString())  // Duration を文字列化
                        } // remember 閉じ
                        
                        OutlinedTextField( // 所要時間入力欄
                            value = durationInput, // 現在値
                            onValueChange = {  // 変更時処理
                                durationInput = it // 入力値を更新
                                it.toLongOrNull()?.let { minutes -> // 数字なら
                                    if (minutes >= 0) { // 負でなければ
                                        endTime = startTime.plusMinutes(minutes) // 終了時間を再計算
                                    } // if 閉じ
                                } // let 閉じ
                            }, // onValueChange 閉じ
                            label = { Text("所要時間 (分)") }, // ラベル
                            modifier = Modifier.weight(1f), // 横幅
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions( // 数字キーボード
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number // 数字限定
                            ), // KeyboardOptions 閉じ
                            singleLine = true // 1 行固定
                        ) // OutlinedTextField 閉じ
                        
                        val duration = Duration.between(startTime, endTime) // Duration を再計算
                        val durationMinutes = duration.toMinutes() // 分単位
                        
                        Text( // 合計表示
                            text = "合計: ${durationMinutes}分", // テキスト
                            style = MaterialTheme.typography.bodyMedium, // フォント
                            fontWeight = FontWeight.Bold, // 太字
                            modifier = Modifier.padding(start = 8.dp) // 左余白
                        ) // Text 閉じ
                    } // Row 閉じ
                } // Column 閉じ
            } // Card 閉じ

            Card( // タスク一覧カード
                modifier = Modifier.fillMaxWidth(), // 横幅
            ) { // Card 本体
                Column( // 縦レイアウト
                    modifier = Modifier.padding(16.dp), // 内側余白
                    verticalArrangement = Arrangement.spacedBy(12.dp) // 間隔
                ) { // Column 本体
                    Text("タスクを追加", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) // 見出し
                    
                    Row( // 入力欄と追加ボタン
                        modifier = Modifier.fillMaxWidth(), // 横幅いっぱい
                        verticalAlignment = Alignment.CenterVertically, // 垂直中央
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // 横間隔
                    ) { // Row 本体
                        OutlinedTextField( // タスク名入力
                            value = taskName, // 入力値
                            onValueChange = { taskName = it }, // 更新
                            label = { Text("タスク名") }, // ラベル
                            modifier = Modifier.weight(1f), // 横幅
                            singleLine = true // 単一行
                        ) // OutlinedTextField 閉じ
                        Button( // 追加ボタン
                            onClick = { // 押下時処理
                                if (taskName.isNotBlank()) { // 空白でなければ
                                    tasks.add(TaskItem(taskName, selectedColor)) // タスクを追加
                                    taskName = "" // 文字列をクリア
                                } // if 閉じ
                            }, // onClick 閉じ
                            enabled = taskName.isNotBlank(), // 入力があれば有効
                            shape = RoundedCornerShape(12.dp) // 角丸
                        ) { // Button 本体
                            Text("追加") // ボタンテキスト
                        } // Button 閉じ
                    } // Row 閉じ
                    
                    Row( // カラー選択
                        modifier = Modifier.fillMaxWidth(), // 横幅
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // 間隔
                    ) { // Row 本体
                        pastelColors.forEach { color -> // 色リストをループ
                            Box( // 色スワッチ
                                modifier = Modifier // 修飾子
                                    .size(32.dp) // サイズ
                                    .clip(CircleShape) // 円形
                                    .background(color) // 背景色
                                    .clickable { selectedColor = color } // タップで選択
                                    .padding(2.dp) // 内側余白
                            ) { // Box 本体
                                if (selectedColor == color) { // 選択中の色なら
                                    Box( // 選択マーク
                                        modifier = Modifier // 修飾子
                                            .fillMaxSize() // 全体
                                            .clip(CircleShape) // 円形
                                            .background(Color.Black.copy(alpha = 0.2f)) // 暗い半透明
                                    ) // 内側 Box 閉じ
                                } // if 閉じ
                            } // Box 閉じ
                        } // forEach 閉じ
                    } // Row 閉じ
                } // Column 閉じ
            } // Card 閉じ

            Text("タスク一覧 (${tasks.size})", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) // タスク数の表示

            LazyColumn( // タスク一覧
                modifier = Modifier.weight(1f), // 縦方向の伸縮
                verticalArrangement = Arrangement.spacedBy(8.dp) // 項目間隔
            ) { // LazyColumn ブロック
                items(tasks) { task -> // 各タスク
                    Surface( // タスク行の背景
                        modifier = Modifier.fillMaxWidth(), // 横幅
                        color = task.color.copy(alpha = 0.2f), // 半透明背景
                        shape = MaterialTheme.shapes.medium, // 形状
                        border = androidx.compose.foundation.BorderStroke(1.dp, task.color) // 枠線
                    ) { // Surface 本体
                        Row( // 横並び
                            verticalAlignment = Alignment.CenterVertically, // 中央揃え
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp) // パディング
                        ) { // Row 本体
                            Box( // 状態インジケーター
                                modifier = Modifier // 修飾子
                                    .size(16.dp) // サイズ
                                    .clip(CircleShape) // 円形
                                    .background(task.color) // 背景色
                            ) // Box 閉じ
                            Spacer(modifier = Modifier.width(12.dp)) // 間隔
                            Text( // タスク名
                                text = task.title, // テキスト
                                modifier = Modifier.weight(1f), // 残り幅
                                style = MaterialTheme.typography.bodyLarge // フォント
                            ) // Text 閉じ
                            IconButton(onClick = { tasks.remove(task) }) { // 削除ボタン
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error) // 削除アイコン
                            } // IconButton 閉じ
                        } // Row 閉じ
                    } // Surface 閉じ
                } // items 閉じ
            } // LazyColumn 閉じ

            if (tasks.isNotEmpty()) { // タスクがあるときに所要時間を表示
                val durationPerTask = if (tasks.isEmpty()) 0 else Duration.between(startTime, endTime).toMinutes() / tasks.size // 1 タスクあたりの分数
                Text( // 平均時間表示
                    text = "1タスクあたり: 約${durationPerTask}分", // テキスト
                    style = MaterialTheme.typography.bodySmall, // フォント
                    modifier = Modifier.align(Alignment.CenterHorizontally), // 中央寄せ
                    color = MaterialTheme.colorScheme.onSurfaceVariant // 色
                ) // Text 閉じ
            } // if 閉じ

            Button( // 保存ボタン
                onClick = { // 押下時
                    onSave(TaskGroup( // 保存アクションを呼ぶ
                        id = taskGroup?.id ?: 0, // 既存 ID があれば使う
                        startTime = startTime, // 開始時間
                        endTime = endTime, // 終了時間
                        tasks = tasks.toList() // タスクリストをコピー
                    )) // TaskGroup 閉じ
                }, // onClick 閉じ
                modifier = Modifier.fillMaxWidth(), // 横幅いっぱい
                enabled = tasks.isNotEmpty() && Duration.between(startTime, endTime).toMinutes() > 0, // 必須条件
                shape = RoundedCornerShape(16.dp) // 角丸
            ) { // Button 本体
                Text("保存", style = MaterialTheme.typography.titleMedium) // ボタンラベル
            } // Button 閉じ
        } // Column 閉じ
    } // Scaffold 閉じ

    if (showStartTimePicker) { // 開始時刻ピッカー表示条件
        TimePickerDialog( // ピッカー表示
            onDismissRequest = { showStartTimePicker = false }, // 閉じる処理
            initialTime = startTime, // 初期時刻
            onTimeSelected = {  // 選択時
                startTime = it // 反映
                showStartTimePicker = false // フラグオフ
            } // onTimeSelected 閉じ
        ) // TimePickerDialog 閉じ
    } // if 閉じ

    if (showEndTimePicker) { // 終了ピッカー表示
        TimePickerDialog( // ピッカー表示
            onDismissRequest = { showEndTimePicker = false }, // 閉じる
            initialTime = endTime, // 初期時刻
            onTimeSelected = {  // 選択時
                endTime = it // 反映
                showEndTimePicker = false // フラグオフ
            } // onTimeSelected 閉じ
        ) // TimePickerDialog 閉じ
    } // if 閉じ
} // TaskEditScreen 閉じ

@OptIn(ExperimentalMaterial3Api::class) // Experimental API を使う
@Composable // Compose 関数
fun TimePickerDialog( // 時刻ピッカー用ダイアログ
    onDismissRequest: () -> Unit, // ダイアログ閉じる処理
    initialTime: LocalTime, // 初期時刻
    onTimeSelected: (LocalTime) -> Unit // 時刻選択ハンドラ
) { // 本体
    val timePickerState = rememberTimePickerState( // ピッカーステート
        initialHour = initialTime.hour, // 初期時間
        initialMinute = initialTime.minute, // 初期分
        is24Hour = true // 24 時間制
    ) // rememberTimePickerState 閉じ

    AlertDialog( // ダイアログ
        onDismissRequest = onDismissRequest, // 閉じる処理
        confirmButton = { // 確定ボタン
            TextButton(onClick = { // ボタン押下
                onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute)) // 選ばれた時刻を通知
            }) { // TextButton 本体
                Text("OK") // テキスト
            } // TextButton 閉じ
        }, // confirmButton 閉じ
        dismissButton = { // キャンセルボタン
            TextButton(onClick = onDismissRequest) { // 押下で閉じる
                Text("キャンセル") // テキスト
            } // TextButton 閉じ
        }, // dismissButton 閉じ
        title = { Text("時間を選択") }, // タイトル
        text = { // 本文
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { // 中央配置
                TimePicker(state = timePickerState) // タイムピッカー
            } // Box 閉じ
        } // text 閉じ
    ) // AlertDialog 閉じ
} // TimePickerDialog 閉じ
