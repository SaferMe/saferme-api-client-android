plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(Versions.compile_sdk)
    defaultConfig {
        minSdkVersion(Versions.min_sdk)
        targetSdkVersion(Versions.target_sdk)
        versionCode = Maven.build
        versionName = "${Maven.version}.${Maven.build}"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // For Kotlin sources.
    tasks.withType < org.jetbrains.kotlin.gradle.tasks.KotlinCompile > {
        kotlinOptions.jvmTarget = "1.8"
    }

    lintOptions.isAbortOnError = true

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
    }

    buildTypes {
        getByName("release") {
            setProperty("archivesBaseName", "$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            println("##teamcity[setParameter name='target_release_version' value='${Maven.version}.${Maven.build}']")
        }
        getByName("debug") {
            setProperty("archivesBaseName", "$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        exclude("META-INF/ktor-http.kotlin_module")
        exclude("META-INF/kotlinx-io.kotlin_module")
        exclude("META-INF/atomicfu.kotlin_module")
        exclude("META-INF/ktor-utils.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-io.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
        exclude("META-INF/ktor-http-cio.kotlin_module")
        exclude("META-INF/ktor-client-core.kotlin_module")
    }
}

// Project dependencies
dependencies {
    // Kotlin stdlib
    implementation(Deps.kotlin_stdlib_jdk8)

    // KTOR
    implementation("io.ktor:ktor-client-core:${Deps.versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${Deps.versions.ktor}")
    implementation("io.ktor:ktor-client-gson:${Deps.versions.ktor}")
    //  implementation("io.ktor:ktor-client-logging-native:${Deps.versions.ktor}")

    // Logging for HTTPClient
    implementation("org.slf4j:slf4j-simple:${Deps.versions.slf4j}")

    // Gson Serialisation
    implementation(Deps.gson)

    // Dagger 2 Dependency injection
    implementation(Deps.dagger2)
    kapt(Deps.dagger2_annotation)
}

// Test Dependencies
dependencies {

        // Add MockK dependencies.
        testImplementation(TestingDeps.mockk)

        // ktor mocking libs
        testImplementation(TestingDeps.ktor_base)
        testImplementation(TestingDeps.ktor_jvm)
        testImplementation(TestingDeps.ktor_native)

        // Add JUnit5 dependencies.
        testImplementation(TestingDeps.junit5_jupiter)
        testRuntimeOnly(TestingDeps.junit5_jupiter_runtime)
        testImplementation(TestingDeps.junit5_jupiter_params)

        // Add JUnit4 legacy dependencies.
        testImplementation(TestingDeps.junit4_legacy)
        testRuntimeOnly(TestingDeps.junit5_vintage)

        // Add AssertJ dependencies.
        testImplementation(TestingDeps.assertj)
}

// Apply jacoco config (For test Coverage Reports)
apply {
    from("jacoco.gradle")
}

publishing {
    repositories {
        maven {
            name = "GitHub"
            url = uri(uri("${Maven.gprBaseUrl}/${Maven.gprRepoOwner}/${Maven.gprRepoId}"))
            credentials {
                username = Maven.gprUser
                password = Maven.gprKey
            }
        }
    }
    publications {

        register("ProductionRelease", MavenPublication::class) {
            groupId = Maven.group
            artifactId = "${Maven.artifactId}-release"
            Maven.build = -2
            version = "${Maven.version}.${Maven.build}"
            artifact("$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}-release.aar")

            pom {
                withXml {
                    // add dependencies to pom
                    val dependencies = asNode().appendNode("dependencies")
                    configurations.implementation.get().dependencies.forEach {
                        if (it.group != null &&
                            "unspecified" != it.name &&
                            it.version != null
                        ) {

                            val dependencyNode = dependencies.appendNode("dependency")
                            dependencyNode.appendNode("groupId", it.group)
                            dependencyNode.appendNode("artifactId", it.name)
                            dependencyNode.appendNode("version", it.version)
                        }
                    }
                }
            }
        }

        register("TestRelease", MavenPublication::class) {
            groupId = Maven.group
            artifactId = "${Maven.artifactId}-test"
            Maven.build = -2
            version = "${Maven.version}.${Maven.build}"
            artifact("$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}-debug.aar")

            pom {
                withXml {
                    // add dependencies to pom
                    val dependencies = asNode().appendNode("dependencies")
                    configurations.implementation.get().dependencies.forEach {
                        if (it.group != null &&
                            "unspecified" != it.name &&
                            it.version != null
                        ) {

                            val dependencyNode = dependencies.appendNode("dependency")
                            dependencyNode.appendNode("groupId", it.group)
                            dependencyNode.appendNode("artifactId", it.name)
                            dependencyNode.appendNode("version", it.version)
                        }
                    }
                }
            }
        }
    }
}
