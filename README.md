# intellij-language-1c-bsl

[![Build](https://github.com/1c-syntax/intellij-language-1c-bsl/actions/workflows/build.yml/badge.svg)](https://github.com/1c-syntax/intellij-language-1c-bsl/actions/workflows/build.yml)
[![JetBrains Marketplace](https://img.shields.io/jetbrains/plugin/v/io.github.1c-syntax.language-1c-bsl.svg)](https://plugins.jetbrains.com/plugin/io.github.1c-syntax.language-1c-bsl)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=1c-syntax_intellij-language-1c-bsl&metric=alert_status)](https://sonarcloud.io/dashboard?id=1c-syntax_intellij-language-1c-bsl)

Плагин для семейства IDE на платформе JetBrains IntelliJ (IntelliJ IDEA, PyCharm, WebStorm, Rider и т.д.)
для поддержки языка 1C (BSL) — языка [1С:Предприятие 8](https://v8.1c.ru) и [OneScript](https://oscript.io).

## Возможности

- **Языковые функции** (диагностики, автодополнение, переход к определению, hover, форматирование,
  поиск ссылок, symbols и др.) — от [BSL Language Server](https://github.com/1c-syntax/bsl-language-server),
  подключённого через [LSP4IJ](https://github.com/redhat-developer/lsp4ij).
- **Подсветка синтаксиса** — TextMate-грамматика BSL/SDBL (встроена в плагин).

Сам BSL Language Server скачивается автоматически с [GitHub-релизов](https://github.com/1c-syntax/bsl-language-server/releases)
при первом запуске (загрузчик — из [1c-syntax/utils](https://github.com/1c-syntax/utils)); при желании можно
указать путь к внешнему `bsl-language-server.jar`.

## Требования

- IntelliJ Platform **2025.1+** (since-build `251`).
- Установленный плагин [LSP4IJ](https://plugins.jetbrains.com/plugin/23257-lsp4ij) (ставится автоматически как зависимость).
- Для native-image сборки сервера — поддерживаемая ОС (Windows / Linux / macOS); для внешнего jar — установленная Java.

## Настройка

`Settings/Preferences → Languages & Frameworks → 1C (BSL)`:

- **Language server / enabled** — запускать ли BSL Language Server.
- **Use external jar** — использовать внешний `bsl-language-server.jar` вместо автоматической загрузки.
- **Path to language server** — путь к внешнему jar (используется при включённом «Use external jar»).
- **Language server Java opts** — дополнительные JVM-опции сервера (пробрасываются через `_JAVA_OPTIONS`).

Остальные параметры (канал релизов, GitHub-токен, каталог установки) пока хранятся в настройках плагина
со значениями по умолчанию и в UI не выведены.

## Сборка

```bash
./gradlew buildPlugin        # собрать плагин (build/distributions/*.zip)
./gradlew runIde             # запустить IDE с установленным плагином
./gradlew verifyPlugin       # проверка совместимости (Plugin Verifier)
```

## License

Все файлы исходных кодов распространяются на условиях [GNU LGPL v3.0](./COPYING.LESSER.md)
