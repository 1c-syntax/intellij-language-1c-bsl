# idea-language-1c-bsl

[![pipeline status](https://gitlab.com/nixel2007/idea-language-1c-bsl/badges/master/pipeline.svg)](https://gitlab.com/nixel2007/idea-language-1c-bsl/commits/master)

Сводный проект, призванный достич трех целей:

* разработка около-BNF грамматики для 1С/OneScript с возможностью генерации лексера/парсера, AST и AST-visitors - проект [BSL Parser](./bslparser)
* разработка Language Server для 1С/OneScript на базе [Language Server Protocol](https://microsoft.github.io/language-server-protocol/) - проект [BSL Language Server](./languageserver)
* разработка плагина для семейства редакторов JetBrains IntelliJ (IntelliJ IDEA, Rider, WebStorm, etc) - проект [IntelliJ Language 1C (BSL) Plugin](./intellij-bsl)

Для упрощения разработки все три задачи решаются в рамках одного репозитория. В будущем, вероятно, они будут разделены.

## License

Все файлы исходных кодов распространяются на условиях [GNU LGPL v3.0](./COPYING.LESSER.md)
