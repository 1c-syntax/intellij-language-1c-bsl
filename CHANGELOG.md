<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Language 1C (BSL) Changelog

## [Unreleased]

### Added

- Syntax highlighting via a bundled TextMate grammar for BSL and its query language (SDBL).
- Automatic download of the BSL Language Server on first use, with a fallback to a configured external `bsl-language-server.jar`.
- Settings for enabling the server, using an external jar and passing additional JVM options.
- Reporting of plugin errors to the GitHub issue tracker from the IDE error dialog.

### Changed

- Language features (diagnostics, completion, hover, go to definition, find usages, formatting, folding, structure view) are now provided by the [BSL Language Server](https://github.com/1c-syntax/bsl-language-server) through [LSP4IJ](https://github.com/redhat-developer/lsp4ij).
- Build migrated to the IntelliJ Platform Gradle Plugin 2.x, targeting IntelliJ Platform 2025.3+ and Java 21.

### Removed

- Legacy ANTLR-based parser, PSI, file types, syntax highlighter, commenter and brace matcher, replaced by the language server and TextMate grammar.

[Unreleased]: https://github.com/1c-syntax/intellij-language-1c-bsl/commits/master
