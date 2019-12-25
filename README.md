# TokenD Kotlin wallet

This library implements transactions and keys management for TokenD-related projects.

## Installation

For **Gradle** add following lines to your project's `build.gradle`:
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://maven.tokend.io" }
    }
}

dependencies {
    ...
    compile "org.tokend:wallet:3.6.2"
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

Decoding:
```kotlin
val txResultEncoded = "AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAJAAAAAAAAAADMOKDasWPzpJIqN9sWipdvcjEZRTnGBvUezXbEd6rKMAAAAAAAAAAA"
val txResult = TransactionResult.fromBase64(txResultEncoded)
```

## ProGuard
As long as you don't decode XDRs (`.fromXdr(...)`) no extra ProGuard
rules are required. Otherwise add following lines to your
project's `proguard-rules.pro`:
```proguard
# Wallet
-keep class org.tokend.wallet.xdr.* { *; }
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
```
## XDR Update
XDR sources are located in [TokenD XDR repository](https://github.com/tokend/xdr/).
You can generate new XDRs using our Docker-based XDR generator.
[Docker](https://www.docker.com/) is required to perform this action.

In order to generate new XDRs run `generateXDR` script with a source revision (tag or branch or commit) as an argument:

```bash
./generateXDR.sh master
```
