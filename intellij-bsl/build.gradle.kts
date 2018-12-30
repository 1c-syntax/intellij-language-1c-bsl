import java.util.Calendar
import java.net.URI

plugins {
    id("org.jetbrains.intellij") version "0.3.12"
    jacoco
    idea
    java
    id("com.github.hierynomus.license")
}

repositories {
    mavenCentral()
    maven {
        url = URI("https://dl.bintray.com/jetbrains/intellij-plugin-service")
    }
    maven {
        url = URI("https://dl.bintray.com/antlr/maven/")
    }
}

group = "org.github._1c_syntax.intellij.bsl"
version = "1.0" // Plugin version

dependencies {
    compile(project(":languageserver"))
    compile(project(":bslparser"))
    compile("org.antlr:antlr4-jetbrains-adapter:3.0.alpha.2") {
        exclude(group = "com.jetbrains")
    }
}

intellij {
    version = "IC-2018.3" //Corresponds to 183.4284.85 from plugin.xml; for a full list of IntelliJ IDEA releases please see https://www.jetbrains.com/intellij-repository/releases
    pluginName = "Language 1C (BSL)"
    setPlugins("com.github.gtache.lsp:1.4.0")
}

tasks.jacocoTestReport {
    reports {
        xml.setEnabled(true)
    }
}

//tasks.test {
//    jacoco {
//        append = false
//    }
//}

license {
    header = rootProject.file("license/HEADER.txt")
    ext["year"] = Calendar.getInstance().get(Calendar.YEAR)
    ext["name"] = "Alexey Sosnoviy <labotamy@yandex.ru>, Nikita Gryzlov <nixel2007@gmail.com>"
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
