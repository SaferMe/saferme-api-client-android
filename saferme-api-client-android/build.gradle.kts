
plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdk = ApiLibVersions.compileSdk
    defaultConfig {
        minSdk = ApiLibVersions.minSdk
        targetSdk = ApiLibVersions.targetSdk
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
                "$buildDir/outputs/aar/${ObsidianMaven.artifactId}-${ObsidianMaven.version}.${ObsidianMaven.build}"
            )
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            println("##teamcity[setParameter name='target_release_version' value='${ObsidianMaven.version}.${ObsidianMaven.build}']")
        }
        getByName("debug") {
            setProperty(
                "archivesBaseName",
                "$buildDir/outputs/aar/${ObsidianMaven.artifactId}-${ObsidianMaven.version}.${ObsidianMaven.build}"
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
    implementation(ApiLibDeps.kotlinStdlibJdk8)

    // KTOR
    implementation("io.ktor:ktor-client-core:${ApiLibDeps.Versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${ApiLibDeps.Versions.ktor}")
    implementation("io.ktor:ktor-client-android:${ApiLibDeps.Versions.ktor}")

    implementation("io.ktor:ktor-client-gson:${ApiLibDeps.Versions.ktor}")
    //  implementation("io.ktor:ktor-client-logging-native:${Deps.versions.ktor}")

    // Logging for HTTPClient
    implementation("org.slf4j:slf4j-simple:${ApiLibDeps.Versions.slf4j}")
    implementation("com.raygun:raygun4android:4.0.0")
    // Gson Serialisation
    implementation(ApiLibDeps.gson)

    // Dagger 2 Dependency injection
    implementation(ApiLibDeps.dagger2)
    kapt(ApiLibDeps.dagger2_annotation)

    implementation(ApiLibDeps.mapboxGeoJson)
}

// Test Dependencies
dependencies {

    androidTestImplementation(ApiLibTestingDeps.androidxTestUnit)
    androidTestImplementation(ApiLibTestingDeps.androidxTestCore)
    androidTestImplementation(ApiLibTestingDeps.androidxTestRunner)
    androidTestImplementation(ApiLibTestingDeps.junit4)
    // Add MockK dependencies.
    testImplementation(ApiLibTestingDeps.mockk_io)

    // ktor mocking libs
    testImplementation(ApiLibTestingDeps.ktor_base)
    testImplementation(ApiLibTestingDeps.ktor_jvm)
    testImplementation(ApiLibTestingDeps.ktor_native)

    // Add JUnit5 dependencies.
    testImplementation(ApiLibTestingDeps.junit5_jupiter)
    testImplementation(ApiLibTestingDeps.junit5_jupiter_api)
    testImplementation(ApiLibTestingDeps.junit5_jupiter_params)
    testRuntimeOnly(ApiLibTestingDeps.junit5_jupiter_runtime)

    // Add JUnit4 legacy dependencies.
    testImplementation(ApiLibTestingDeps.junit4)
    testRuntimeOnly(ApiLibTestingDeps.junit5_vintage)

    // Add AssertJ dependencies.
    testImplementation(ApiLibTestingDeps.assertj)

    testImplementation(ApiLibTestingDeps.mockitoKotlin2)
    testImplementation(ApiLibTestingDeps.kotlinCoroutineTest)
}

// Apply jacoco config (For test Coverage Reports)
apply {
    from("jacoco.gradle")
}

publishing {
    repositories {
        maven {
            name = "GitHub"
            url = uri(uri("${ObsidianMaven.gprBaseUrl}/${ObsidianMaven.gprRepoOwner}/${ObsidianMaven.gprRepoId}"))
            credentials {
                username = ObsidianMaven.gprUser
                password = ObsidianMaven.gprKey
            }
        }
    }
    publications {

        register("ProductionRelease", MavenPublication::class) {
            groupId = ObsidianMaven.group
            artifactId = "${ObsidianMaven.artifactId}-release"
            version = "${ObsidianMaven.version}.${ObsidianMaven.build}"
            artifact("$buildDir/outputs/aar/${ObsidianMaven.artifactId}-${ObsidianMaven.version}.${ObsidianMaven.build}-release.aar")

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
            groupId = ObsidianMaven.group
            artifactId = "${ObsidianMaven.artifactId}-test"
            version = "${ObsidianMaven.version}.${ObsidianMaven.build}"
            artifact("$buildDir/outputs/aar/${ObsidianMaven.artifactId}-${ObsidianMaven.version}.${ObsidianMaven.build}-debug.aar")

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
