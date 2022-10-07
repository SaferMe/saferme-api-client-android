// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    // More info on buildscript vs allprojects: https://stackoverflow.com/a/30159149/2085356
    dependencies {
        classpath(Libs.com_android_tools_build_gradle)
        classpath(Libs.kotlin_gradle_plugin)
        classpath(Libs.android_junit5)
        classpath(Libs.ktlint_gradle)

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files.
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
    }
}

task("clean") {
    delete(rootProject.buildDir)
}

subprojects {
    configurations.all {
        resolutionStrategy {
            eachDependency {
            }
        }
    }
}
