
plugins {
    id("com.android.library")
    id("maven-publish")
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
                "$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}"
            )
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            println("##teamcity[setParameter name='target_release_version' value='${Maven.version}.${Maven.build}']")
        }
        getByName("debug") {
            setProperty(
                "archivesBaseName",
                "$buildDir/outputs/aar/${Maven.artifactId}-${Maven.version}.${Maven.build}"
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
    implementation(Deps.kotlin_stdlib_jdk8)

    // KTOR
    implementation("io.ktor:ktor-client-core:${Deps.versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${Deps.versions.ktor}")
    implementation("io.ktor:ktor-client-android:${Deps.versions.ktor}")

    implementation("io.ktor:ktor-client-gson:${Deps.versions.ktor}")
    //  implementation("io.ktor:ktor-client-logging-native:${Deps.versions.ktor}")

    // Logging for HTTPClient
    implementation("org.slf4j:slf4j-simple:${Deps.versions.slf4j}")
    implementation("com.raygun:raygun4android:4.0.0")
    // Gson Serialisation
    implementation(Deps.gson)

    // Dagger 2 Dependency injection
    implementation(Deps.dagger2)
    kapt(Deps.dagger2_annotation)

    implementation(Deps.awsAndroidS3)

    implementation(Deps.mapboxGeoJson)

    implementation(Deps.moshi)
    implementation(Deps.moshiKotlin)
    kapt(Deps.moshiCodegen)
}

// Test Dependencies
dependencies {

    androidTestImplementation(TestingDeps.androidxTestUnit)
    androidTestImplementation(TestingDeps.androidxTestCore)
    androidTestImplementation(TestingDeps.androidxTestRunner)
    androidTestImplementation(TestingDeps.junit4_legacy)
    // Add MockK dependencies.
    testImplementation(TestingDeps.mockk)

    // ktor mocking libs
    testImplementation(TestingDeps.ktor_base)
    testImplementation(TestingDeps.ktor_jvm)
    testImplementation(TestingDeps.ktor_native)

    // Add JUnit5 dependencies.
    testImplementation(TestingDeps.junit5_jupiter)
    testImplementation(TestingDeps.junit5_jupiter_api)
    testImplementation(TestingDeps.junit5_jupiter_params)
    testRuntimeOnly(TestingDeps.junit5_jupiter_runtime)

    // Add JUnit4 legacy dependencies.
    testImplementation(TestingDeps.junit4_legacy)
    testRuntimeOnly(TestingDeps.junit5_vintage)

    // Add AssertJ dependencies.
    testImplementation(TestingDeps.assertj)

    testImplementation(TestingDeps.mockitoKotlin2)
    testImplementation(TestingDeps.kotlinCoroutineTest)
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
