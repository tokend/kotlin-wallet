# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Please check our [developers guide](https://gitlab.com/tokend/developers-guide)
for further information about branching and tagging conventions.

## [3.7.0] 2021-11-25

### Added
- `Destroyable` implementation in `Account`
- `equal` and `hashCode` methods for `Account`

### Changed
- From now `Account` is always a complete keypair with a private key.
To verify signatures use `Account.verifySignature` static methods
- Updated XDR version to `7708446fd03153bf0d99b299c9df054dd9788c7c`
- Updated Kotlin version to 1.4.10

### Fixed
- Inability to convert true `UInt64` amounts from and to precised

### Removed
- Positive number restriction on transaction salt
- `canSign` method from account (see above)
- `Account.fromAccountId` and `Account.fromPublicKey` methods (see above)

## [3.6.5] 2020-08-12

### Changed
- Updated XDR version to `d639694`

## [3.6.4] 2020-07-15

### Changed
- XDR strings are now encoded end decoded in UTF-8

## [3.6.3] 2020-01-20

### Added
- `Serializable` marker to `NetworkParams`

## [3.6.2] 2019-12-25

### Fixed
- Incorrect salt transform in `Transaction` constructor, use
true absolute value now

## [3.6.1] 2019-11-26

### Added
- `Transaction` constructor from XDR `TransactionEnvelope`

### Changed
- Signing-related `Transaction` methods are now static

## [3.6.0] 2019-11-05

### Changed
- Updated XDR version to `78afc23`

## [3.5.0] 2019-09-24

### Changed
- Updated XDR version to `bfc2e7b`
- Updated TokenD Maven repo domain

## [3.4.2] 2019-09-06

### Fixed
- XDR decoding on devices running Java 7

### Changed
- ProGuard rules

## [3.4.1] 2019-09-05

### Changed
- ProGuard rules

## [3.4.0] 2019-09-04

### Added
- Ability to decode XDR models: call `.fromXdr` or `.fromBase64` methods
of the required class in Kotlin or use `*.Decoder` static member in Java
- ProGuard rules
- Ability to add a collection of operation bodies to
`TransactionBuilder`

### Removed
- Apache encoding libraries

### Changed
- Updated XDR version to `9199f20`

## [3.3.0] 2019-07-15

### Added
- Ability to add `DecoratedSignature` to the transaction directly

## [3.2.0] 2019-06-18

### Changed
- Updated XDR version to `cd889b0`

## [3.1.0] 2019-05-27

### Added
- Ability to add transaction signers with the `TransactionBuilder`

### Changed
- Updated way of XDR generation, check out Readme
- Updated XDR version to `3.3.0` (`c8561fd`)

## [3.0.1] 2019-02-28

### Added
- `reference` optional param for `SimplePaymentOp`
- Account rules and roles
- Signer rules and roles

### Changed
- XDR version to `7e06563`
- `PaymentV2` and all related `-V2` things to just `Payment`

### Fixed
- Wrong value type for `KeyValueEntryValue`

[3.0.1]: https://github.com/tokend/kotlin-wallet/compare/1.0.13...3.0.1
[3.1.0]: https://github.com/tokend/kotlin-wallet/compare/3.0.1...3.1.0
[3.2.0]: https://github.com/tokend/kotlin-wallet/compare/3.1.0...3.2.0
[3.3.0]: https://github.com/tokend/kotlin-wallet/compare/3.2.0...3.3.0
[3.4.0]: https://github.com/tokend/kotlin-wallet/compare/3.3.0...3.4.0
[3.4.1]: https://github.com/tokend/kotlin-wallet/compare/3.4.0...3.4.1
[3.4.2]: https://github.com/tokend/kotlin-wallet/compare/3.4.1...3.4.2
[3.5.0]: https://github.com/tokend/kotlin-wallet/compare/3.4.2...3.5.0
[3.6.0]: https://github.com/tokend/kotlin-wallet/compare/3.5.0...3.6.0
[3.6.1]: https://github.com/tokend/kotlin-wallet/compare/3.6.0...3.6.1
[3.6.2]: https://github.com/tokend/kotlin-wallet/compare/3.6.1...3.6.2
[3.6.3]: https://github.com/tokend/kotlin-wallet/compare/3.6.2...3.6.3
[3.6.4]: https://github.com/tokend/kotlin-wallet/compare/3.6.3...3.6.4
[3.6.5]: https://github.com/tokend/kotlin-wallet/compare/3.6.4...3.6.5
[3.7.0]: https://github.com/tokend/kotlin-wallet/compare/3.6.5...3.7.0
[Unreleased]: https://github.com/tokend/kotlin-wallet/compare/3.7.0...HEAD
