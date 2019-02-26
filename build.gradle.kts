import java.net.URI
import java.util.*

plugins {
    jacoco
    idea
    java
    id("org.jetbrains.intellij") version "0.3.12"
    id("com.github.hierynomus.license") version "0.14.0"
    id("org.sonarqube") version "2.6.2"
}

repositories {
    mavenCentral()
    maven {
        url = URI("https://dl.bintray.com/jetbrains/intellij-plugin-service")
    }
    maven {
        url = URI("https://dl.bintray.com/antlr/maven/")
    }
    maven {
        url = URI("https://jitpack.io")
    }
}

group = "org.github._1c_syntax.intellij.bsl"
version = "0.2.0" // Plugin version

dependencies {
    compile("com.github.1c-syntax", "bsl-parser", "0.4.0")
    compile("com.github.1c-syntax", "bsl-language-server", "0.3.0")
    compile("com.github.NipunaRanasinghe", "lsp4intellij", "4a1062ce3d")

    compile("org.antlr:antlr4-jetbrains-adapter:3.0.alpha.2") {
        exclude(group = "com.jetbrains")
    }
}

intellij {
    version = "IC-2018.3" //Corresponds to 183.4284.85 from plugin.xml; for a full list of IntelliJ IDEA releases please see https://www.jetbrains.com/intellij-repository/releases
    pluginName = "Language 1C (BSL)"
}

tasks.jacocoTestReport {
    reports {
        xml.setEnabled(true)
    }
}

license {
    header = rootProject.file("license/HEADER.txt")
    ext["year"] = "2018-" + Calendar.getInstance().get(Calendar.YEAR)
    ext["name"] = "Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>"
    ext["project"] = "IntelliJ Language 1C (BSL) Plugin"
    strictCheck = true
    exclude("**/*.png")
    exclude("**/*.txt")
    exclude("**/*.xml")
    mapping("java", "SLASHSTAR_STYLE")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
//    options.incremental = true
//    options.fork = true
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
