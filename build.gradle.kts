import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.util.Calendar

plugins {
    java
    jacoco
    idea
    id("org.jetbrains.intellij.platform") version "2.6.0"
    id("com.github.hierynomus.license") version "0.16.1"
    id("org.sonarqube") version "7.3.1.8318"
    id("com.github.ben-manes.versions") version "0.54.0"
}

group = "io.github.1c-syntax"
version = "0.3.0" // Plugin version

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
        intellijIdeaCommunity("2024.2")

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
            sinceBuild = "242"
            untilBuild = provider { null }
        }
    }

    pluginVerification {
        ides {
            // Явно фиксируем IDE для верификации: recommended() при открытом until-build
            // подтягивает ещё не опубликованные сборки и падает на их resolve.
            ide("IC", "2024.2")
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

license {
    header = rootProject.file("license/HEADER.txt")
    ext["year"] = "2018-" + Calendar.getInstance().get(Calendar.YEAR)
    ext["name"] = "Alexey Sosnoviy <labotamy@gmail.com>, Nikita Fedkin <nixel2007@gmail.com>"
    ext["project"] = "IntelliJ Language 1C (BSL) Plugin"
    strictCheck = true
    exclude("**/*.png")
    exclude("**/*.txt")
    exclude("**/*.xml")
    exclude("**/*.json")
    mapping("java", "SLASHSTAR_STYLE")
}

sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "1c-syntax")
        property("sonar.projectKey", "1c-syntax_intellij-language-1c-bsl")
        property("sonar.projectName", "IntelliJ Language 1C (BSL) Plugin")
        property("sonar.exclusions", "**/vendor/**/*.*, **/gen/**/*.*, **/textmate/**/*.*")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${layout.buildDirectory.get()}/reports/jacoco/test/jacoco.xml"
        )
    }
}
