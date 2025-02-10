import java.net.URI
import java.util.*

plugins {
    jacoco
    idea
    java

    id("org.jetbrains.intellij") version "1.17.4"
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
}

group = "com.github.1c-syntax"
version = "0.3.1" // Plugin version

dependencies {
    implementation("io.github.1c-syntax", "bsl-language-server", "feature-bumpantlr-b7a9fdd-DIRTY")
    implementation("com.github.nixel2007", "lsp4intellij", "c9904de68eecd755cdf3690bad17199351418bfc")

    implementation("org.antlr:antlr4-intellij-adaptor:0.1") {
        exclude("org.antlr", "antlr4-runtime")
    }
}

intellij {
    version.set("IC-2022.3")//Corresponds to plugin.xml; for a full list of IntelliJ IDEA releases please see https://www.jetbrains.com/intellij-repository/releases
    pluginName.set("Language 1C (BSL)")
    updateSinceUntilBuild.set(true)
//    patchPluginXml {
//        sinceBuild = sinceBuildVersion
//        untilBuild = untilBuildVersion
//    }
}

tasks.runPluginVerifier {
    ideVersions(listOf("2020.1.4"))
}

tasks.patchPluginXml {
    untilBuild.set("")
    sinceBuild.set("")
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
