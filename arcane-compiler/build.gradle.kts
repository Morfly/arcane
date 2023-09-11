import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "io.morfly"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://jetbrains.bintray.com/intellij-third-party-dependencies")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

dependencies {
    implementation(projects.arcaneAnnotations)
    compileOnly(kotlin("compiler-embeddable"))
    compileOnly("com.jetbrains.intellij.platform:core:232.9559.62")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.5.0")
}