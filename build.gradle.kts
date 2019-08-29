import java.net.URI
import java.util.*

plugins {
    jacoco
    idea
    java
    id("org.jetbrains.intellij") version "0.4.10"
    id("com.github.hierynomus.license") version "0.15.0"
    id("org.sonarqube") version "2.7.1"
    id("com.github.ben-manes.versions") version "0.22.0"
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
    //compile("com.github.1c-syntax", "bsl-parser", "0.7.1")
    compile("com.github.1c-syntax", "bsl-language-server", "0.10.2")
    compile("com.github.nixel2007", "lsp4intellij", "6e87ac8eba9ea2972d1d978aacc25e44bb290207")

    compile("org.antlr:antlr4-jetbrains-adapter:3.0.alpha.2") {
        exclude(group = "com.jetbrains")
    }
}

intellij {
    version = "IC-2019.2.1" //Corresponds to 192.6262.58 from plugin.xml; for a full list of IntelliJ IDEA releases please see https://www.jetbrains.com/intellij-repository/releases
    pluginName = "Language 1C (BSL)"
    updateSinceUntilBuild = true
}

tasks.patchPluginXml {
    setUntilBuild("2022.0")
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
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
