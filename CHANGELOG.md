# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Please check our [developers guide](https://gitlab.com/tokend/developers-guide)
for further information about branching and tagging conventions.

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
[Unreleased]: https://github.com/tokend/kotlin-wallet/compare/3.2.0...HEAD
