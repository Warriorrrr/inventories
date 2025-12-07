plugins {
    alias(libs.plugins.conventions.publishing)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper)
}

earthmc {
    publishing {
        public = true
        repositoryName = "warrior"
        repositoryUrl = "https://repo.warriorrr.dev"
    }
}
