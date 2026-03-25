# FitTasks 開発ログ

## 2026-03-25 (テーマ設定の強化)
- [x] テーマ設定の拡充 (ダークモード、ライトモード、システム設定のサポート)
- [x] システムカラー (Material You Dynamic Color) の導入
- [x] PreferenceRepository / DataStorePreferenceRepository に Dynamic Color 設定を追加
- [x] PreferenceViewModel / PreferenceViewModelFactory の更新
- [x] SettingsScreen にシステムカラーのトグルスイッチを追加
- [x] MainActivity から FitTasksTheme への設定値の伝達
- [x] 各言語の strings.xml にテーマ設定関連の文字列を追加・翻訳

## 2026-03-22 (MVP完成)
- [x] プロジェクト構成の確認
- [x] 現状の実装（サンプルデータ表示）の確認
- [x] 開発ログの作成
- [x] ドメインモデルの強化 (TaskGroup に endTime を追加し、LocalTime を使用)
- [x] 時間計算ロジックの実装 (TaskGroup.durationPerTaskSeconds)
- [x] UIの改善 (時間の表示、タスクあたりの時間を表示)
- [x] クリーンアーキテクチャへの移行 (Repository, UseCase の導入)
- [x] UIのさらなる改善 (かわいいデザインの追求、パステルカラーの活用)
- [x] タスク追加・編集機能の実装 (TaskEditScreen の強化)
- [x] タイマー機能の実装 (TimerViewModel, TimerScreen)
- [x] データの永続化 (Room データベースへの対応完了)
- [x] リストからの編集・削除機能の実装
- [x] ビルドエラーの修正 (Kotlin 2.2.10 / AGP 9.1.0 環境下での KSP/Room バージョン整合性対応)
- [x] フッターバナー広告の実装 (AdMob 初期化、Compose 連携、.env 参照)
- [x] 多言語対応の実装 (日本語、英語、中国語、韓国語、DataStoreによる設定保存、初回起動時の設定画面)

### 次のステップ
- [ ] タイマー実行中のバックグラウンド動作 (Foreground Service) の検討
- [ ] 完了時のアニメーション追加 (紙吹雪など)
- [ ] 1分未満のタスクがある場合の表示最適化

## 🚀 完全完成へのロードマップ (Roadmap to Completion)

### フェーズ 1: 基本機能の安定化 (Foundation & Stability) - 完了 ✨
- [x] クリーンアーキテクチャの基盤構築
- [x] タスクグループの作成・保存 (Room データベースへの移行完了)
- [x] タイマー基本ロジックと画面遷移
- [x] タスクグループの編集・削除
- [x] バリデーション (開始時間 < 終了時間、タスクが空でないこと)
- [x] 開発環境のビルド安定化
- [x] 多言語対応の基盤構築 (Strings リソース、DataStore)

### フェーズ 2: UXの向上と「かわいさ」の追求 (UI/UX Polish)
... (以下略)
