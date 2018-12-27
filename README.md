# intellij-language-1c-bsl

[![Build Status](https://travis-ci.org/1c-syntax/intellij-language-1c-bsl.svg?branch=master)](https://travis-ci.org/1c-syntax/intellij-language-1c-bsl)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=1c-syntax_intellij-language-1c-bsl&metric=alert_status)](https://sonarcloud.io/dashboard?id=1c-syntax_intellij-language-1c-bsl)
[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=1c-syntax_intellij-language-1c-bsl&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=1c-syntax_intellij-language-1c-bsl)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=1c-syntax_intellij-language-1c-bsl&metric=coverage)](https://sonarcloud.io/dashboard?id=1c-syntax_intellij-language-1c-bsl)

Сводный проект, призванный достичь трех целей:

* разработка около-BNF грамматики для 1С/OneScript с возможностью генерации лексера/парсера, AST - проект [BSL Parser](./bslparser)
* разработка Language Server для 1С/OneScript на базе [Language Server Protocol](https://microsoft.github.io/language-server-protocol/) как единого движка для реализации функций автодополнения, перехода к определению, данных Синтакс-Помощника и прочего в различных редакторах - проект [BSL Language Server](./languageserver)
* разработка плагина для семейства редакторов JetBrains IntelliJ (IntelliJ IDEA, Rider, WebStorm, etc) - проект [IntelliJ Language 1C (BSL) Plugin](./intellij-bsl)

Для упрощения разработки все три задачи решаются в рамках одного репозитория. В будущем, вероятно, они будут разделены.

## License

Все файлы исходных кодов распространяются на условиях [GNU LGPL v3.0](./COPYING.LESSER.md)
