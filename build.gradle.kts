import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

plugins {
    java
    jacoco
    idea
    id("org.jetbrains.intellij.platform") version "2.17.0"
    id("org.jetbrains.changelog") version "2.5.0"
    id("com.diffplug.spotless") version "7.0.4"
    id("org.sonarqube") version "7.3.1.8318"
    id("com.github.ben-manes.versions") version "0.54.0"
    id("me.qoomon.git-versioning") version "6.4.4"
}

group = "io.github.1c-syntax"

// Версия вычисляется git-versioning по тегу/ветке/коммиту (как в других проектах 1c-syntax).
// Экранируем '$', чтобы плейсхолдеры интерпретировал git-versioning, а не Kotlin.
// ${ref.slug} используется вместо ${ref}: имена веток могут содержать '/', недопустимые в версии.
version = "0.0.0-SNAPSHOT"
gitVersioning.apply {
    refs {
        describeTagFirstParent = false
        tag("v(?<tagVersion>[0-9].*)") {
            version = "\${ref.tagVersion}\${dirty}"
        }
        branch("master") {
            version = "\${describe.tag.version}.\${describe.distance}-SNAPSHOT\${dirty}"
        }
        branch(".+") {
            version = "\${ref.slug}-\${commit.short}\${dirty}"
        }
    }
    rev {
        version = "\${commit.short}\${dirty}"
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// --- github-api multi-release JAR workaround -------------------------------------------------
// github-api поставляется как многорелизный JAR (Multi-Release: true). Classloader плагинов
// IntelliJ игнорирует META-INF/versions/** (JBR/IDEA-220300), из-за чего в рантайме грузится
// Java 8-заглушка org.kohsuke.github.extras.HttpClientGitHubConnector, чьи методы бросают
// UnsupportedOperationException, а нужного конструктора (HttpClient) в ней нет — скачивание
// BSL LS падает с NoSuchMethodError. «Расплющиваем» jar: поднимаем классы из META-INF/versions/11
// в корень, чтобы classloader видел реальную реализацию для Java 11+. Оригинальный github-api
// исключаем из utils и подключаем расплющенный + его транзитивные зависимости отдельно.
val githubApiSource by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    add(githubApiSource.name, "org.kohsuke:github-api:1.327")
}

val githubApiArtifact = githubApiSource.incoming.artifactView {
    componentFilter { it is ModuleComponentIdentifier && it.module == "github-api" }
}.files.elements.map { it.single().asFile }

val githubApiTransitives = githubApiSource.incoming.artifactView {
    componentFilter { it is ModuleComponentIdentifier && it.module != "github-api" }
}.files

val flattenGithubApiJar by tasks.registering(Jar::class) {
    archiveFileName = "github-api-flattened.jar"
    destinationDirectory = layout.buildDirectory.dir("flattened-libs")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // 1) версия для Java 11 — поднимаем в корень, она должна перекрыть базовые заглушки
    from(zipTree(githubApiArtifact)) {
        include("META-INF/versions/11/**")
        eachFile { path = path.removePrefix("META-INF/versions/11/") }
        includeEmptyDirs = false
    }
    // 2) остальные классы и ресурсы, кроме версий и исходного манифеста
    from(zipTree(githubApiArtifact)) {
        exclude("META-INF/versions/**", "META-INF/MANIFEST.MF")
    }
}

dependencies {
    // Загрузчик BSL Language Server (скачивание/распаковка релиза с GitHub). github-api тащим
    // не транзитивно, а расплющенным (см. блок выше про Multi-Release/IDEA-220300).
    implementation("io.github.1c-syntax:utils:0.9.0") {
        exclude(group = "org.kohsuke", module = "github-api")
    }
    implementation(files(flattenGithubApiJar))
    implementation(githubApiTransitives)

    // JSpecify-аннотации нужны и в main, и в тестах (в т.ч. платформенных), поэтому implementation,
    // а не compileOnly: джар крошечный, аннотации с CLASS-retention.
    implementation("org.jspecify:jspecify:1.0.0")

    // JUnit 4 не поставляется IntelliJ Platform Gradle Plugin автоматически; нужен и для наших
    // тестов (org.junit), и для базовых классов платформы (junit.framework.TestCase).
    testImplementation("junit:junit:4.13.2")
    // Мок загрузчика в платформенном тесте провайдера.
    testImplementation("org.mockito:mockito-core:5.14.2")

    intellijPlatform {
        // С 2025.3 (253) IDEA Community слита в единый дистрибутив — координата intellijIdea.
        intellijIdea("2025.3")

        // TextMate-подсветка для .bsl/.os
        bundledPlugin("org.jetbrains.plugins.textmate")
        // LSP-клиент
        plugin("com.redhat.devtools.lsp4ij:0.20.1")

        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Language 1C (BSL)"
        version = project.version.toString()
        ideaVersion {
            sinceBuild = "253"
            untilBuild = provider { null }
        }
        // «What's new» на Marketplace берётся из CHANGELOG.md через плагин changelog.
        changeNotes = provider {
            with(changelog) {
                renderItem(
                    (getOrNull(project.version.toString()) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }
    }

    pluginVerification {
        ides {
            // IDE для верификации подбираем динамически из фида JetBrains, чтобы не хардкодить
            // версии (ломаются при смене координат) и автоматически покрывать новые релизы
            // (2026.1, 2026.2, …). С 253 IDEA — единый дистрибутив (тип intellijIdea). Начинаем
            // с нижней границы (since-build 253), только стабильный канал.
            select {
                types = listOf(IntelliJPlatformType.IntellijIdea)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = "253"
            }
        }
    }

    // Подпись плагина сертификатом (secrets задаются в CI)
    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    // Публикация в JetBrains Marketplace
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // Два канала: стабильные релизы (тег `vX.Y.Z`) → `default`, пре-релизы (тег с суффиксом,
        // напр. `vX.Y.Z-rc.1`) → `eap`. Версию задаёт git-versioning по тегу, суффикс = наличие `-`.
        channels = listOf(
            if (project.version.toString().contains('-')) "eap" else "default"
        )
    }
}

changelog {
    repositoryUrl = "https://github.com/1c-syntax/intellij-language-1c-bsl"
    groups.empty()
    // По умолчанию версия changelog'а = версия проекта (её git-versioning считает по ref'у). На
    // master это SNAPSHOT, поэтому для `patchChangelog` в релизном workflow версию задаём явно
    // через -PchangelogVersion=X.Y.Z (из тега релиза), не завися от текущего git-ref.
    version = providers.gradleProperty("changelogVersion")
        .orElse(provider { project.version.toString() })
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

spotless {
    java {
        licenseHeaderFile(rootProject.file("license/HEADER.txt"), "package ").updateYearWithLatest(true)
    }
}

sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "1c-syntax")
        property("sonar.projectKey", "1c-syntax_intellij-language-1c-bsl")
        property("sonar.projectName", "IntelliJ Language 1C (BSL) Plugin")
        property("sonar.exclusions", "**/vendor/**, **/gen/**, **/textmate/**")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${layout.buildDirectory.get()}/reports/jacoco/test/jacoco.xml"
        )
    }
}
