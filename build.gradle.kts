import java.net.URI
import java.util.*

plugins {
    jacoco
    idea
    java

    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("org.cadixdev.licenser") version "0.6.1"
    id("org.sonarqube") version "6.0.1.5171"
    id("com.github.ben-manes.versions") version "0.52.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = URI("https://www.jetbrains.com/intellij-repository/releases")
    }
    maven {
        url = URI("https://jitpack.io")
    }
    intellijPlatform {
        defaultRepositories()
        releases()
        marketplace()
    }
}

group = "com.github.1c-syntax"
version = "0.3.1" // Plugin version

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2023.3")

        plugin("com.redhat.devtools.lsp4ij", "0.11.0")
    }
    implementation("io.github.1c-syntax:bsl-language-server:0.24.0:exec")

    implementation("org.antlr:antlr4-intellij-adaptor:0.1") {
        exclude("org.antlr", "antlr4-runtime")
    }
}

intellijPlatform{
    pluginConfiguration {
        id = "org.1c-syntax.language-1c-bsl"
        name = "Language 1C (BSL)"
        version = "0.3.1"

        vendor {
            name = "1c-syntax"
            email = "nixel2007@gmail.com"
            url = "https://github.com/1c-syntax"
        }
        description = "The Language 1C (BSL) plugin provides support of 1C:Enterprise framework in IntelliJ Platform-based IDEs."
        changeNotes = "Initial release"

        ideaVersion {
            sinceBuild = "233"
            untilBuild = "330.*"
        }
    }

    pluginVerification {
        ides {
            ide("2023.3")
        }
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        xml.outputLocation.set(File("${layout.buildDirectory.get()}/reports/jacoco/test/jacoco.xml"))
    }
}

license {
    header(rootProject.file("license/HEADER.txt"))
    newLine(false)
    ext["year"] = "2018-" + Calendar.getInstance().get(Calendar.YEAR)
    ext["name"] = "Alexey Sosnoviy <labotamy@gmail.com>, Nikita Fedkin <nixel2007@gmail.com>"
    ext["project"] = "IntelliJ Language 1C (BSL) Plugin"
    exclude("**/*.png")
    exclude("**/*.txt")
    exclude("**/*.xml")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.runIde {
    val property = properties.getOrDefault("sandboxDirectory", "") as Any
    systemProperty("idea.plugins.path", property)
    jvmArgs("-Xmx1g")
}

sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "1c-syntax")
        property("sonar.projectKey", "1c-syntax_intellij-language-1c-bsl")
        property("sonar.projectName", "IntelliJ Language 1C (BSL) Plugin")
        property("sonar.exclusions", "**/vendor/**/*.*, **/gen/**/*.*")
    }
}
