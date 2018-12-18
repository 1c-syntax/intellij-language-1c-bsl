plugins {
    id("com.github.hierynomus.license") version "0.14.0"
    id("org.sonarqube") version "2.6.2"
}

sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "1c-syntax")
        property("sonar.projectKey", "1c-syntax_idea-language-1c-bsl")
        property("sonar.exclusions", "**/vendor/**/*.*, **/gen/**/*.*")
    }
}
