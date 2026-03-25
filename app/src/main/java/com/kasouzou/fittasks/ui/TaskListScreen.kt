package com.kasouzou.fittasks.ui // UI パッケージを明示

import androidx.compose.foundation.layout.* // レイアウト用の Compose API
import androidx.compose.foundation.lazy.LazyColumn // 縦スクロールリストを描くためのコンポーズ
import androidx.compose.foundation.lazy.items // リストアイテムのループ処理
import androidx.compose.foundation.shape.RoundedCornerShape // 角丸形状を指定する Shape
import androidx.compose.material.icons.Icons // アイコンセット
import androidx.compose.material.icons.filled.Add // 追加アイコン
import androidx.compose.material.icons.filled.Settings // 設定アイコン
import androidx.compose.material3.* // Material3 コンポーネント全般
import androidx.compose.runtime.Composable // Composable アノテーション
import androidx.compose.runtime.collectAsState // Flow を Compose で監視するヘルパー
import androidx.compose.runtime.getValue // By デリゲート取得
import androidx.compose.ui.Alignment // 配置位置の定数
import androidx.compose.ui.Modifier // Compose 用修飾子
import androidx.compose.ui.res.stringResource // stringResource をインポート
import androidx.compose.ui.text.font.FontWeight // フォントの太さ
import androidx.compose.ui.unit.dp // dp 単位
import androidx.lifecycle.viewmodel.compose.viewModel // ViewModel を Compose から取得
import com.kasouzou.fittasks.R // R クラスをインポート
import com.kasouzou.fittasks.ui.components.FooterBannerAd // 底部広告コンポーネント
import com.kasouzou.fittasks.ui.components.TaskGroupCard // タスクカード UI

@OptIn(ExperimentalMaterial3Api::class) // Experimental API を利用することを宣言
@Composable // この関数が Compose の描画関数であることを示す
fun TaskListScreen( // タスクリスト画面の Composable
    onAddTask: () -> Unit, // タスク追加アクション
    onEditTask: (com.kasouzou.fittasks.domain.model.TaskGroup) -> Unit, // 編集アクション
    onStartTimer: (com.kasouzou.fittasks.domain.model.TaskGroup) -> Unit, // タイマー開始アクション
    onDeleteTask: (com.kasouzou.fittasks.domain.model.TaskGroup) -> Unit, // 削除アクション
    onSettingsClick: () -> Unit, // 設定画面への遷移アクション
    viewModel: TaskListViewModel // 表示ロジックを担う ViewModel
) { // 関数本体
    val uiState by viewModel.uiState.collectAsState() // ViewModel の状態を Compose で監視

    Scaffold( // 上部/下部バーを持つ基本レイアウト
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(), // 全面サイズかつシステムバーを考慮
        topBar = { // 上部バーのラムダ
            CenterAlignedTopAppBar( // 中央揃えのアプリバー
                title = { // タイトル領域
                    Text( // タイトル文字列
                        stringResource(R.string.app_name), // 表示テキストをリソースから取得
                        fontWeight = FontWeight.Bold, // 太字
                        color = MaterialTheme.colorScheme.primary // メインカラー
                    ) // Text 閉じ
                }, // title 閉じ
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors( // アプリバー色設定
                    containerColor = MaterialTheme.colorScheme.background // 背景色をテーマから取得
                ) // colors 閉じ
            ) // CenterAlignedTopAppBar 閉じ
        }, // topBar 閉じ
        bottomBar = { // 底部広告バー
            FooterBannerAd( // 広告を描くコンポーネント
                modifier = Modifier.fillMaxWidth() // 横幅いっぱい
            ) // FooterBannerAd 閉じ
        }, // bottomBar 閉じ
        floatingActionButton = { // FAB のラムダ
            FloatingActionButton( // フローティングアクションボタン
                onClick = onAddTask, // クリックでタスク追加
                containerColor = MaterialTheme.colorScheme.primary, // 背景色
                contentColor = MaterialTheme.colorScheme.onPrimary, // アイコン色
                shape = RoundedCornerShape(16.dp) // 角を丸める
            ) { // FAB 内部
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_task_group_desc)) // プラスアイコン
            } // FloatingActionButton 閉じ
        } // floatingActionButton 閉じ
    ) { innerPadding -> // Scaffold 本体のコンテンツラムダ
        Box( // 内部を包む Box
            modifier = Modifier // Modifier チェーン
                .padding(innerPadding) // Scaffold による内側余白
                .fillMaxSize() // 画面全体を使う
        ) { // Box 本体
            when (val state = uiState) { // UI ステートで条件分岐
                is TaskListUiState.Loading -> { // 読み込み中ステート
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) // 真ん中にローディング
                } // Loading 閉じ
                is TaskListUiState.Success -> { // 成功ステート
                    if (state.groups.isEmpty()) {
                        EmptyTaskList(onAddTask = onAddTask)
                    } else {
                        LazyColumn( // タスク一覧を縦に並べる
                            modifier = Modifier // Modifier チェーン
                                .fillMaxSize() // 全体サイズ
                                .padding(horizontal = 16.dp), // 横余白
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp) // 上下パディング
                        ) { // LazyColumn スコープ
                            items(state.groups) { group -> // 各タスクグループをアイテム化
                                TaskGroupCard( // タスクカードを描画
                                    group = group, // グループデータ
                                    onClick = { onStartTimer(group) }, // タップでタイマー画面へ
                                    onEdit = { onEditTask(group) }, // 編集アクションを伝搬
                                    onDelete = { onDeleteTask(group) } // 削除アクション
                                ) // TaskGroupCard 閉じ
                            } // items 閉じ
                        } // LazyColumn 閉じ
                    }
                } // Success 閉じ
                is TaskListUiState.Error -> { // エラーステート
                    Text( // エラー表示を追加しておく
                        stringResource(R.string.error_occurred), // 表示メッセージ
                        color = MaterialTheme.colorScheme.error // エラーカラー
                    ) // Text 閉じ
                } // Error 閉じ
            } // when 閉じ
        } // Box 閉じ
    } // Scaffold 閉じ
} // TaskListScreen 閉じ

@Composable
fun EmptyTaskList(onAddTask: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = stringResource(R.string.no_tasks_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.no_tasks_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onAddTask,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.create_first_task), style = MaterialTheme.typography.titleMedium)
        }
    }
}
