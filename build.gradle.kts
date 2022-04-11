// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    // More info on buildscript vs allprojects: https://stackoverflow.com/a/30159149/2085356
    dependencies {
        classpath(GradlePlugins.gradle)
        classpath(GradlePlugins.kotlin)
        classpath(GradlePlugins.junit5)
        classpath("org.jlleitschuh.gradle:ktlint-gradle:9.2.0")
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
                if ("org.jacoco" == requested.group) {
                    useVersion(Versions.jacoco)
                }
            }
        }
    }
}
