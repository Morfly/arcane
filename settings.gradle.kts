pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "arcane"

include(
    ":arcane-compiler",
    ":arcane-runtime",
    ":arcane-annotations",
)
