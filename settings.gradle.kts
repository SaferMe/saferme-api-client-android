object Modules {
    const val app = ":app"
}

include(
    Modules.app
)

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.51.0"
}

refreshVersions {
    featureFlags {
        enable(de.fayard.refreshVersions.core.FeatureFlag.LIBS)
        disable(de.fayard.refreshVersions.core.FeatureFlag.GRADLE_UPDATES)
    }
    enableBuildSrcLibs()
}
