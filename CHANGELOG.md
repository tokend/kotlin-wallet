# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Please check our [developers guide](https://gitlab.com/tokend/developers-guide)
for further information about branching and tagging conventions.

## [3.0.1-x.4] 2019-02-22

### Fixed
- Wrong Kotlin type for `UInt32`, was `Long`, now `Int`

## [3.0.1-x.3] 2019-02-22

### Added
- `reference` optional param for `SimplePaymentOp`

### Changed
- XDR version to 4a6e427
- `PaymentV2` and all related `-V2` things to just `Payment`

## [3.0.1-x.2] 2019-02-21

### Fixed
- Wrong Kotlin type for `UInt32`, was `Int`, now `Long`

## [3.0.1-x.1] 2019-02-21

### Added
- Account rules and roles
- Signer rules and roles

### Removed
- `SetOptions` operation, use `ManageSigner` instead
- `AccountType`, use `AccountRole` instead

### Fixed
- Wrong value type for `KeyValueEntryValue`

[Unreleased]: https://github.com/tokend/kotlin-wallet/compare/1.0.13...HEAD
[3.0.1-x.1]: https://github.com/tokend/kotlin-wallet/compare/1.0.13...3.0.1-x.1
[3.0.1-x.2]: https://github.com/tokend/kotlin-wallet/compare/3.0.1-x.1...3.0.1-x.2
[3.0.1-x.3]: https://github.com/tokend/kotlin-wallet/compare/3.0.1-x.2...3.0.1-x.3
[3.0.1-x.4]: https://github.com/tokend/kotlin-wallet/compare/3.0.1-x.3...3.0.1-x.4
