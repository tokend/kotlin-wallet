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
    compile "org.tokend:wallet:3.0.1"
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
val SEED = "SBUFJEEK7FMWXPE4HGOWQZPHZ4V5TFKGSF664RAGT24NS662MKTQ7J6S".toCharArray()
val NETWORK = NetworkParams("Example Test Network")

val sourceAccount = Account.fromSecretSeed(SEED)
val operation = CreateBalanceOp(SOURCE_ACCOUNT_ID, "OLE")

val transaction = TransactionBuilder(NETWORK, sourceAccount.accountId)
                    .addOperation(Operation.OperationBody.ManageBalance(operation))
                    .setMemo(Memo.MemoText("TokenD is awesome"))
                    .addSigner(sourceAccount)
                    .build()

val envelope = transaction.getEnvelope().toBase64()
```


## XDR Update
XDR's are added as a git submodule so to get them after clone run the following command:
```
git submodule update --remote --init
```
XDR generation requires Ruby. For initial dependencies installation run the following command:
```
bundle install
```
To update XDR classes run the following command:
```
rake xdr:generate
```
