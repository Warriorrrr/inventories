plugins {
    alias(libs.plugins.conventions.java)
    alias(libs.plugins.conventions.publishing)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper)

    testImplementation(libs.paper)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(libs.mockito)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

earthmc {
    publishing {
        public = true
        repositoryName = "warrior"
        repositoryUrl = "https://repo.warriorrr.dev"
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}
