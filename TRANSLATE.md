> # 要件定義書
> 多言語対応（日本語・英語・中国語・韓国語）をアプリに組み込むための、具体的な要件定義と実装設計をまとめました。 既存の MainActivity.kt やリポジトリパターンの構成 を活かしつつ、Android 標準の機能と Jetpack Compose を組み合わせた現実的な設計です。
> 
> 1. 言語リソースの定義（Resources）
>    Android では res/values/strings.xml を言語ごとに用意するのが標準です。
>    | 言語 | フォルダ名 | 備考 |
>    |---|---|---|
>    | デフォルト (日本語) | values | strings.xml |
>    | 英語 | values-en | strings.xml |
>    | 中国語 (簡体字) | values-zh-rCN | strings.xml |
>    | 韓国語 | values-ko | strings.xml |
>    初回起動時メッセージの定義例
>    各言語の strings.xml に以下のキーを用意します。
> 
> * welcome_message: 「言語を選択してください」
> * change_later_hint: 「この設定は後で設定画面から変更できます」
> 
> 2. 機能要件（Functional Requirements）
>    ① 言語選択ロジック
> 
> * 初回起動判定: アプリ内に「初回起動フラグ」を保持し、true の場合のみ言語選択画面（LanguageSelectionScreen）を表示する。
> * 永続化: ユーザーが選択した言語コード（ja, en, zh, ko）を DataStore（Dart でいう shared_preferences の進化版）に保存する。
>   ② UI/UX 仕様
> * 初回起動画面:
>   
>   * 言語リスト（日本語、English、中文、한국어）を提示。
>   * 選択した言語に合わせて、その場で「後で設定から変えられます」という案内文の言語も切り替わる。
> * 設定画面:
>   
>   * 現在の言語を表示。
>   * タップすると再選択用のダイアログまたは画面に遷移。
> 
> 3. 技術スタックと設計（Technical Design）
>    既存の TaskRepository の構造に倣い、設定管理用のリポジトリを新設します。
>    A. 永続化層（Data Layer）
>    PreferenceRepository インターフェースを作成し、言語設定を保存します。
> 
> * fun getLanguage(): Flow
> * suspend fun saveLanguage(code: String)
>   B. ユースケース層（Domain Layer）
>   SaveTaskGroupUseCase.kt と同様の構成で、言語変更用のユースケースを作成します。
> * ChangeLanguageUseCase(val repository: PreferenceRepository)
>   C. プレゼンテーション層（UI Layer）
>   MainActivity.kt の setContent 内で、言語状態を監視（Observe）します。
>   // MainActivity.kt への組み込みイメージ
>   val selectedLang by preferenceViewModel.language.collectAsState()
> 
> // 言語設定が変更されたら Locale を更新する処理を副作用（LaunchedEffect）で実行 LaunchedEffect(selectedLang) { updateLocale(selectedLang) // Android 13以降は LocaleManager を使用 }
> 
> 4. 画面遷移ロジックの拡張
>    MainActivity.kt で定義されている Screen シールドインターフェース に、言語選択画面を追加します。
>    sealed interface Screen {
>    object LanguageSetup : Screen // 追加：初回起動用
>    object TaskList : Screen
>    data class TaskEdit(val taskGroup: TaskGroup?) : Screen
>    data class Timer(val taskGroup: TaskGroup) : Screen
>    object Settings : Screen // 追加：設定画面
>    }
> 5. まとめ：実装の優先順位
> 
> * リソース作成: strings.xml を 4 言語分作成する。
> * 基盤作成: 言語設定を保存する DataStore と Repository を実装する。
> * 初期画面実装: MainActivity の when (currentScreen) ブロック に LanguageSetup を追加する。
> * 案内文の追加: 「設定で変えられる旨」を UI のフッター部分に配置する。
>   体系的に見たときのコードの立ち位置
>   この多言語対応は、アプリ全体の**「基盤（Infrastructure）」を強化する作業です。TaskGroup.kt が扱う「タスクの内容」そのものではなく、それを「どう見せるか（Localization）」**を制御するレイヤーを追加することになります。
>   次にお手伝いできることはありますか？
>   例えば、Android 13 以上で推奨されている「アプリ別言語設定（Per-app language preferences）」の実装コードを作成したり、初回起動画面（LanguageSelectionScreen）の具体的な Compose UI デザイン案を作成したりすることが可能です。


