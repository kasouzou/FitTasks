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
nannanananan
[元の文章に戻る](#remember)






