object GradlePlugins {
    object Versions {
        val gradle: String = "7.0.3"
        val kotlin: String = "1.5.31"
        val junit5: String = "1.8.0.0"
    }

    val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val junit5 = "de.mannodermaus.gradle.plugins:android-junit5:${Versions.junit5}"
}

object Versions {
    const val compileSdk = 30
    const val targetSdk = 30
    const val minSdk = 23
    const val jacoco = "0.8.7"
}


object Deps {
    object Versions {
        val arch_comp: String = "2.0.0"
        val design: String = "1.0.0"
        val gson: String = "2.8.5"
        val dagger2: String = "2.25.2"
        val junit5: String = "5.2.0"
        val crayon: String = "0.1.0"
        val ktor: String = "1.2.5"
        val slf4j: String = "1.6.1"
        val mapboxGeoJson: String = "5.8.0"
    }

    val kotlinStdlibJdk8 =
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${GradlePlugins.Versions.kotlin}"
    val gson = "com.google.code.gson:gson:${Versions.gson}"
    val dagger2 = "com.google.dagger:dagger:${Versions.dagger2}"
    val dagger2_annotation = "com.google.dagger:dagger-compiler:${Versions.dagger2}"
    val mapboxGeoJson = "com.mapbox.mapboxsdk:mapbox-sdk-geojson:${Versions.mapboxGeoJson}"
}

object TestingDeps {
    object Versions {
        val assertj: String = "3.11.1"
        val junit5: String = "5.8.1"
        val mockk: String = "1.12.1"
        val roboelectric: String = "3.8"
        val junit4: String = "4.12"
        val coroutines: String = "1.3.2"
        val ktor: String = "1.2.5"
        val mockitoKotlin2: String = "2.2.0"
        val kotlinCoroutine: String = "1.3.9"
        val androidxTest: String = "1.1.0"
    }

    val androidxTestUnit = "androidx.test.ext:junit-ktx:${Versions.androidxTest}"
    val androidxTestCore = "androidx.test:core:${Versions.androidxTest}"
    val androidxTestRunner = "androidx.test:runner:${Versions.androidxTest}"

    val junit5_jupiter = "org.junit.jupiter:junit-jupiter:${Versions.junit5}"
    val junit5_jupiter_api = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5}"
    val junit5_jupiter_runtime = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit5}"
    val junit5_jupiter_params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit5}"
    val junit4 = "junit:junit:${Versions.junit4}"
    val junit5_vintage = "org.junit.vintage:junit-vintage-engine:${Versions.junit5}"
    val assertj = "org.assertj:assertj-core:${Versions.assertj}"
    val mockk_io = "io.mockk:mockk:${Versions.mockk}"
    val ktor_base = "io.ktor:ktor-client-mock:${Versions.ktor}"
    val ktor_jvm = "io.ktor:ktor-client-mock-jvm:${Versions.ktor}"
    val ktor_native = "io.ktor:ktor-client-mock-native:${Versions.ktor}"
    val mockitoKotlin2 = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin2}"
    val kotlinCoroutineTest =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutine}"
}
