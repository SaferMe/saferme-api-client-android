object GradlePlugins {
    data class Versions(val gradle: String = "3.3.0",
                        val kotlin: String = "1.3.20",
                        val junit5: String = "1.2.0.0")

    val versions = Versions()

    val gradle = "com.android.tools.build:gradle:${versions.gradle}"

    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"

    val junit5 = "de.mannodermaus.gradle.plugins:android-junit5:${versions.junit5}"
}

object Versions {
    val compile_sdk = 28
    val target_sdk = 26
    val min_sdk = 26
}

object Deps {
    data class Versions(val arch_comp: String = "2.0.0",
                        val design: String = "1.0.0",
                        val gson: String = "2.8.5",
                        val dagger2: String = "2.17",
                        val junit5: String = "5.2.0",
                        val crayon: String = "0.1.0",
                        val ktor: String = "1.2.5",
                        val slf4j: String = "1.6.1")

    val versions = Versions()

    val kotlin_stdlib_jdk8 =
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${GradlePlugins.versions.kotlin}"
    val arch_comp = "androidx.lifecycle:lifecycle-extensions:${versions.arch_comp}"
    val material_design = "com.google.android.material:material:${versions.design}"
    val vector_drawable = "androidx.vectordrawable:vectordrawable:${versions.design}"
    val recycler_view = "androidx.recyclerview:recyclerview:${versions.design}"
    val gson = "com.google.code.gson:gson:${versions.gson}"
    val dagger2 = "com.google.dagger:dagger:${versions.dagger2}"
    val dagger2_annotation = "com.google.dagger:dagger-compiler:${versions.dagger2}"

}

object TestingDeps {
    data class Versions(val assertj: String = "3.11.1",
                        val junit5: String = "5.2.0",
                        val mockk: String = "1.9.3",
                        val roboelectric: String = "3.8",
                        val junit4: String = "4.12",
                        val coroutines: String = "1.3.2",
                        val ktor_version: String = "1.2.5")

    val versions = Versions()

    val junit5_jupiter = "org.junit.jupiter:junit-jupiter-api:${versions.junit5}"
    val junit5_jupiter_runtime = "org.junit.jupiter:junit-jupiter-engine:${versions.junit5}"
    val junit5_jupiter_params = "org.junit.jupiter:junit-jupiter-params:${versions.junit5}"
    val junit4_legacy = "junit:junit:${versions.junit4}"
    val junit5_vintage = "org.junit.vintage:junit-vintage-engine:${versions.junit5}"
    val assertj = "org.assertj:assertj-core:${versions.assertj}"
    val mockk = "io.mockk:mockk:${versions.mockk}"
    val roboelectric = "org.robolectric:robolectric:${versions.roboelectric}"
    val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.coroutines}"
    val ktor_base = "io.ktor:ktor-client-mock:${versions.ktor_version}"
    val ktor_jvm = "io.ktor:ktor-client-mock-jvm:${versions.ktor_version}"
    val ktor_native = "io.ktor:ktor-client-mock-native:${versions.ktor_version}"
}
