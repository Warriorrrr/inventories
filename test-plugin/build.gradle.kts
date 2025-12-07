plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(rootProject)
    compileOnly(libs.paper)
}

tasks {
    runServer {
        minecraftVersion(libs.versions.paper.get().substringBefore("-"))
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        @Suppress("UnstableApiUsage")
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}
