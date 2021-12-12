import java.net.URI
import java.util.*

plugins {
    jacoco
    idea
    java
    id("org.jetbrains.intellij") version "1.3.0"
    id("com.github.hierynomus.license") version "0.16.1"
    id("org.sonarqube") version "3.3"
    id("com.github.ben-manes.versions") version "0.39.0"
}

repositories {
    mavenCentral()
    maven {
        url = URI("https://www.jetbrains.com/intellij-repository/releases")
    }
    maven {
        url = URI("https://jitpack.io")
    }
}

group = "com.github.1c-syntax"
version = "0.3.0" // Plugin version

dependencies {
    implementation("com.github.1c-syntax", "bsl-language-server", "127eb34db65c70ebcf6553785472b4723111d590")
    implementation("com.github.nixel2007", "lsp4intellij", "c9904de68eecd755cdf3690bad17199351418bfc")

    implementation("org.antlr:antlr4-intellij-adaptor:0.1") {
        exclude("org.antlr", "antlr4-runtime")
    }
}

intellij {
    version.set("212.5457.46") //Corresponds to plugin.xml; for a full list of IntelliJ IDEA releases please see https://www.jetbrains.com/intellij-repository/releases
    pluginName.set("Language 1C (BSL)")
    updateSinceUntilBuild.set(true)
}

tasks.patchPluginXml {
    untilBuild.set("2030.0")
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
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
    mapping("java", "SLASHSTAR_STYLE")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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
