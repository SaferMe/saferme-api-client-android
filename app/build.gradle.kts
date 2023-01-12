import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.library")
    id("maven-publish")
    id("kotlin-parcelize")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = Versions.compileSdk
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // For Kotlin sources.
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    testOptions {
        unitTests.all {
            it.useJUnit()
        }
    }

    lintOptions.isAbortOnError = true

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
        getByName("androidTest").java.srcDir("src/androidTest/kotlin")
    }

    buildTypes {
        getByName("release") {
            setProperty(
                "archivesBaseName",
                "${Maven.artifactId}-${Maven.version}.${Maven.build}"
            )
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            println("##teamcity[setParameter name='target_release_version' value='${Maven.version}.${Maven.build}']")
        }
        getByName("debug") {
            setProperty(
                "archivesBaseName",
                "${Maven.artifactId}-${Maven.version}.${Maven.build}"
            )
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        resources.excludes.add("META-INF/ktor-http.kotlin_module")
        resources.excludes.add("META-INF/kotlinx-io.kotlin_module")
        resources.excludes.add("META-INF/atomicfu.kotlin_module")
        resources.excludes.add("META-INF/ktor-utils.kotlin_module")
        resources.excludes.add("META-INF/kotlinx-coroutines-io.kotlin_module")
        resources.excludes.add("META-INF/kotlinx-coroutines-core.kotlin_module")
        resources.excludes.add("META-INF/ktor-http-cio.kotlin_module")
        resources.excludes.add("META-INF/ktor-client-core.kotlin_module")
    }
}

// Project dependencies
dependencies {
    // Kotlin stdlib
    implementation(Libs.kotlin_stdlib_jdk8)

    // KTOR
    implementation(Libs.ktor_client_core)
    implementation(Libs.ktor_client_cio)
    implementation(Libs.ktor_client_android)
    implementation(Libs.ktor_client_gson)
    implementation(Libs.ktor_client_auth)
    //  implementation("io.ktor:ktor-client-logging-native:_")

    // Logging for HTTPClient
    implementation(Libs.slf4j_simple)
    implementation(Libs.raygun4android)
    // Gson Serialisation
    implementation(Libs.gson)

    // Dagger 2 Dependency injection
    implementation(Libs.dagger)
    kapt(Libs.dagger_compiler)

    implementation(Libs.aws_android_sdk_s3)

    implementation(Libs.geojson)

    implementation(Libs.moshi)
    implementation(Libs.moshi_kotlin)
    kapt(Libs.moshi_kotlin_codegen)
}

// Test Dependencies
dependencies {

    androidTestImplementation(Libs.junit_ktx)
    androidTestImplementation(Libs.androidx_test_core)
    androidTestImplementation(Libs.androidx_test_runner)
    androidTestImplementation(Libs.junit)
    // Add MockK dependencies.
    testImplementation(Libs.mockk)

    // ktor mocking libs
    testImplementation(Libs.ktor_client_mock)
    testImplementation(Libs.ktor_client_mock_jvm)

    // Add JUnit5 dependencies.
    testImplementation(Libs.junit_jupiter)
    testImplementation(Libs.junit_jupiter_api)
    testImplementation(Libs.junit_jupiter_params)
    testRuntimeOnly(Libs.junit_jupiter_engine)

    // Add JUnit4 legacy dependencies.
    testImplementation(Libs.junit)
    testRuntimeOnly(Libs.junit_vintage_engine)

    // Add AssertJ dependencies.
    testImplementation(Libs.assertj_core)

    testImplementation(Libs.mockito_kotlin)
    testImplementation(Libs.kotlinx_coroutines_test)
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
                            val version = versionFor("${it.group}:${it.name}:${it.version}")
                            dependencyNode.appendNode("groupId", it.group)
                            dependencyNode.appendNode("artifactId", it.name)
                            dependencyNode.appendNode("version", version)
                        }
                    }
                }
            }
        }

        register("TestRelease", MavenPublication::class) {
            groupId = Maven.group
            artifactId = "${Maven.artifactId}-test"
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
