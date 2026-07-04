import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

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

dependencies {
    // Загрузчик BSL Language Server (скачивание/распаковка релиза с GitHub)
    implementation("io.github.1c-syntax:utils:0.8.0")

    compileOnly("org.jspecify:jspecify:1.0.0")

    intellijPlatform {
        intellijIdeaCommunity("2025.1")

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
            sinceBuild = "251"
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
            // Верифицируем против нижней границы (since-build 251). recommended() при открытом
            // until-build подтягивает ещё не опубликованные сборки и падает на их resolve.
            // Свежие сборки (2025.3+) не берём: с 253 IDEA Community больше не публикуется под
            // координатами ideaIC (слита в единый дистрибутив, координата intellijIdea).
            create(IntelliJPlatformType.IntellijIdeaCommunity, "2025.1")
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
    }
}

changelog {
    repositoryUrl = "https://github.com/1c-syntax/intellij-language-1c-bsl"
    groups.empty()
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
