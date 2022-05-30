object GradlePlugins {
    data class Versions(
        val gradle: String = "7.0.3",
        val kotlin: String = "1.5.31",
        val junit5: String = "1.8.0.0"
    )

    val versions = Versions()
    val gradle = "com.android.tools.build:gradle:${versions.gradle}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"
    val junit5 = "de.mannodermaus.gradle.plugins:android-junit5:${versions.junit5}"
}

object Versions {
    const val compileSdk = 30
    const val targetSdk = 30
    const val minSdk = 23
    const val jacoco = "0.8.7"
}


object Deps {
    data class Versions(
        val arch_comp: String = "2.0.0",
        val design: String = "1.0.0",
        val gson: String = "2.8.5",
        val dagger2: String = "2.25.2",
        val junit5: String = "5.2.0",
        val crayon: String = "0.1.0",
        val ktor: String = "1.2.5",
        val slf4j: String = "1.6.1",
        val geojson: String = "0.2.0",
        val awsAndroid: String = "2.44.0",
        val moshi: String = "1.13.0"
    )

    val versions = Versions()

    val kotlin_stdlib_jdk8 =
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${GradlePlugins.versions.kotlin}"
    val gson = "com.google.code.gson:gson:${versions.gson}"
    val dagger2 = "com.google.dagger:dagger:${versions.dagger2}"
    val dagger2_annotation = "com.google.dagger:dagger-compiler:${versions.dagger2}"
    val geojson = "io.github.dellisd.spatialk:geojson:${versions.geojson}"
    val awsAndroidS3 = "com.amazonaws:aws-android-sdk-s3:${versions.awsAndroid}"
    val moshi = "com.squareup.moshi:moshi:${versions.moshi}"
    val moshiKotlin = "com.squareup.moshi:moshi-kotlin:${versions.moshi}"
    val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${versions.moshi}"
}

object TestingDeps {
    data class Versions(
        val assertj: String = "3.11.1",
        val junit5: String = "5.8.1",
        val mockk: String = "1.12.1",
        val roboelectric: String = "3.8",
        val junit4: String = "4.12",
        val coroutines: String = "1.3.2",
        val ktor: String = "1.2.5",
        val mockitoKotlin2: String = "2.2.0",
        val kotlinCoroutine: String = "1.3.9",
        val androidxTest: String = "1.1.0"
    )

    val versions = Versions()

    val androidxTestUnit = "androidx.test.ext:junit-ktx:${versions.androidxTest}"
    val androidxTestCore = "androidx.test:core:${versions.androidxTest}"
    val androidxTestRunner = "androidx.test:runner:${versions.androidxTest}"

    val junit5_jupiter = "org.junit.jupiter:junit-jupiter:${versions.junit5}"
    val junit5_jupiter_api = "org.junit.jupiter:junit-jupiter-api:${versions.junit5}"
    val junit5_jupiter_runtime = "org.junit.jupiter:junit-jupiter-engine:${versions.junit5}"
    val junit5_jupiter_params = "org.junit.jupiter:junit-jupiter-params:${versions.junit5}"
    val junit4_legacy = "junit:junit:${versions.junit4}"
    val junit5_vintage = "org.junit.vintage:junit-vintage-engine:${versions.junit5}"
    val assertj = "org.assertj:assertj-core:${versions.assertj}"
    val mockk = "io.mockk:mockk:${versions.mockk}"
    val ktor_base = "io.ktor:ktor-client-mock:${versions.ktor}"
    val ktor_jvm = "io.ktor:ktor-client-mock-jvm:${versions.ktor}"
    val ktor_native = "io.ktor:ktor-client-mock-native:${versions.ktor}"
    val mockitoKotlin2 = "com.nhaarman.mockitokotlin2:mockito-kotlin:${versions.mockitoKotlin2}"
    val kotlinCoroutineTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${versions.kotlinCoroutine}"
}
