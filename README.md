# TokenD Kotlin wallet

This library implements transactions and keys management for TokenD-related projects.

## Installation

For **Gradle** add following lines to your project's `build.gradle`:
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://maven.tokend.org" }
    }
}

dependencies {
    ...
    compile "org.tokend:wallet:1.0.8"
}

```

## Usage examples

Key management and signing:

```kotlin
val SEED = "SBUFJEEK7FMWXPE4HGOWQZPHZ4V5TFKGSF664RAGT24NS662MKTQ7J6S".toCharArray()
private val DATA = "TokenD is awesome".toByteArray()

val account = Account.fromSecretSeed(SEED)
val decoratedSignature = account.signDecorated(DATA)
```

Transaction creation:

```kotlin
val SOURCE_ACCOUNT_ID = "GDVJSBSBSERR3YP3LKLHTODWEFGCSLDWDIODER3CKLZXUMVPZOPT4MHY"
val SEED = "SBUFJEEK7FMWXPE4HGOWQZPHZ4V5TFKGSF664RAGT24NS662MKTQ7J6S".toCharArray()
val NETWORK = NetworkParams("Example Test Network")

val operation = CreateBalanceOp(SOURCE_ACCOUNT_ID, "OLE")

val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_ID)
                    .addOperation(Operation.OperationBody.ManageBalance(operation))
                    .setMemo(Memo.MemoText("TokenD is awesome"))
                    .build()

val account = Account.fromSecretSeed(SEED)
transaction.addSignature(account)

val envelope = transaction.getEnvelope().toBase64()
```