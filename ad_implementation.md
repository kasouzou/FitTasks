## 広告表示のロジックフロー

1. **ビルド設定で環境変数を読み込む**
   - `app/build.gradle.kts:15-45` でルートの `.env` を開き、`AD_UNIT_ID` を `envVars` に登録する。
   - `defaultConfig` 内で `buildConfigField("String", "ADMOB_BANNER_AD_UNIT_ID", "\"$adUnitId\"")` を呼び、ビルド時に `BuildConfig.ADMOB_BANNER_AD_UNIT_ID` として利用可能にする。

2. **アクティビティで AdMob を初期化**
   - `MainActivity.kt:22-90` の `onCreate` で `MobileAds.initialize(this)` を呼び、AdMob SDK を準備する。

3. **Compose の各画面で広告コンポーネントを描画**
   - `TaskListScreen`, `TaskEditScreen`, `TimerScreen` の `Scaffold` で `bottomBar` に `FooterBannerAd` を配置し、各画面の下部に共通バナーを表示。
   - いずれも `Modifier.fillMaxWidth()` を設定し、横幅いっぱいに展開することで画面下に安定した広告スペースを確保する。

4. **FooterBannerAd の内部で広告を読み込む**
   - `FooterBannerAd.kt:18-55` はデフォルト引数で `BuildConfig.ADMOB_BANNER_AD_UNIT_ID` を受け取り、空なら描画をスキップ。
   - `LocalConfiguration` と `AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize` で端末幅に合うサイズを計算し<a id="remember"></a>[ `remember` ](#remember_ex)で保持。
   - `AdView` を `remember` して `LaunchedEffect` で `setAdSize` と `loadAd` を呼び、`DisposableEffect` で破棄時に `destroy` する。
   - 最後に `AndroidView` へ `AdView` を渡して Compose エコシステムに埋め込む。

5. **データの流れ**
   - `.env` の `AD_UNIT_ID` → `build.gradle.kts` の `buildConfigField` → `BuildConfig.ADMOB_BANNER_AD_UNIT_ID` → `FooterBannerAd` のデフォルト引数 → `AdView.adUnitId` → AdMob バナー表示。

このファイルは広告の初期化、ID の注入、Compose 表示を順に追える構成になっており、`.env` の `AD_UNIT_ID` を変えれば全画面のバナー広告がそのまま切り替わります。

### 解説セクション
<a id="remember_ex"></a>
### ** `remember` とは？ **
[元の文章に戻る](#remember)  
KotlinのJetpack Composeを触り始めると必ず出会うのが `remember` ですね。これは一言で言うと、**「再描画（再コンポーズ）されても値を忘れないための記憶保持機能」**です。

Flutterエンジニアの方なら、**「StatefulWidgetの `State` クラスの中に変数を定義すること」**とほぼ同じ役割だと考えると一気に理解が進みます。

---

### `remember` を一行ずつ解説

Kotlinではよく以下のように書かれます。

```kotlin
val count = remember { mutableStateOf(0) }
```

#### `val count`
変数 `count` を定義しています。Dartでの `var count` や `final count` に相当します。

#### `remember { ... }`
これが核心です。Composeは画面が更新されるたびに、関数全体を最初から実行し直します（再コンポーズ）。`remember` で囲っていない変数は、再実行されるたびに初期値に戻ってしまいますが、これを使うことで**「前回の実行時の値をメモリに保存しておいてね」**とシステムに頼むことができます。

#### `mutableStateOf(0)`
これは「変更可能な状態」を作る関数です。Dart（Flutter）でいう **`ValueNotifier(0)`** や、Hooksの **`useState(0)`** に非常に近いです。この値が変わると、Composeは自動的に画面の必要な部分だけを書き換えます。

---

### 体系的に見たときのコードの立ち位置

このコードは、UIコンポーネントにおける**「短期的な記憶（ローカルステート）」**という立ち位置にあります。



#### なぜこれが必要なのか？
Flutterの `StatelessWidget` は、親が再描画されると自身も作り直され、内部のローカル変数はリセットされますよね。Composeはクラスではなく「関数」なので、放っておくと関数が走るたびに変数が初期化されます。

* **Flutter:** `State` クラスがあるから変数が維持される。
* **Compose:** クラスがない代わりに `remember` というフックを使って、関数の外側（Composeの管理下）に値を預けておく。

---

### Flutter（Dart）との比較表

| 概念 | Kotlin (Jetpack Compose) | Dart (Flutter) |
| :--- | :--- | :--- |
| **役割** | 状態の記憶 | `State` クラスのプロパティ |
| **状態の定義** | `mutableStateOf(v)` | `ValueNotifier(v)` / `State`内変数 |
| **再描画時の挙動** | `remember` が以前の値を返す | `State` オブジェクトが破棄されない限り維持 |
| **画面回転への耐性** | `remember` は消える（`rememberSaveable` が必要） | `AutomaticKeepAliveClientMixin` 等に近い工夫が必要 |

---

### まとめ

`remember`

1.  **「忘却防止」**: 関数が何度実行されても、計算結果や状態を保持し続ける。
2.  **「計算コストの削減」**: 重い処理を `remember` 内で行えば、再描画のたびに再計算するのを防げる。
3.  **「UIとの連動」**: `mutableStateOf` と組み合わせることで、Flutterの `setState()` のような画面更新のトリガーになる。

注意点として、`remember` は「画面の回転」や「プロセスの終了」が起きると忘れてしまいます。もしスマホを横向きにしても値を覚えておいてほしいなら、**`rememberSaveable`** という上位互換の魔法を使うことになります。
[元の文章に戻る](#remember)
