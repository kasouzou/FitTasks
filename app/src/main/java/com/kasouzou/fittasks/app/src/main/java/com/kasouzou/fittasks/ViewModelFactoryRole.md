# ViewModelにおけるFactoryクラスの役割

## 概要
Android開発（特にJetpack ComposeやViewシステム）において、**Factoryクラス**（`ViewModelProvider.Factory`）は、引数を持つViewModelのインスタンスを生成するための「特注の工場」の役割を担います。

## なぜFactoryが必要なのか？

### 1. 依存関係の注入 (Dependency Injection)
Androidの標準的な仕組みでは、`ViewModel` は引数のない空のコンストラクタを持つものしか自動生成できません。
しかし、クリーンアーキテクチャを採用する本プロジェクトでは、`ViewModel` は `UseCase` や `Repository` を必要とします。

- **役割**: Factoryが `Repository` などの「材料」をあらかじめ預かり、`ViewModel` を作る際にそれらを正しくコンストラクタに流し込みます。

### 2. ライフサイクル管理との連携
「なぜ `val vm = TaskListViewModel(...)` と直接書かないのか？」という疑問に対する答えです。

- **理由**: `ViewModel` は画面回転などの構成変更（Configuration Change）が発生しても破棄されない特殊な生存期間を持ちます。
- **役割**: `ViewModelProvider` を介してFactoryを使うことで、Android OSが「既存のインスタンスを再利用するのか、新しく作るのか」を適切に判断できるようになります。Factoryは、新しく作る必要がある場合にのみ呼び出されます。

### 3. 生成ロジックの隠蔽 (Encapsulation)
ActivityやComposable（UI層）が、「どのUseCaseが必要で、どのRepositoryが必要か」という詳細な組み立て手順をすべて知っている必要はありません。

- **役割**: 複雑な依存関係の組み立て（リポジトリからユースケースを作るなど）をFactory内部に閉じ込めることで、UI側は「このFactoryを使ってViewModelを取得する」という1行のコードだけで済むようになります。

---

## 本プロジェクトでの具体例 (`TaskListViewModelFactory.kt`)

このプロジェクトでは、以下のようにFactoryが動作しています。

```kotlin
class TaskListViewModelFactory(
    private val repository: TaskRepository // 1. 材料（リポジトリ）を預かる
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 2. 必要なユースケースをその場で組み立てる
        val getUseCase = GetTaskGroupsUseCase(repository)
        val deleteUseCase = DeleteTaskGroupUseCase(repository)
        
        // 3. ViewModelに注入して「完成品」をシステムに渡す
        return TaskListViewModel(getUseCase, deleteUseCase) as T
    }
}
```

## まとめ
Factoryクラスは、**「外部から受け取った依存関係を組み合わせて、Android OSのライフサイクル管理に適した形でViewModelを安全に生成する」**ためのブリッジ（橋渡し役）として不可欠なコンポーネントです。
