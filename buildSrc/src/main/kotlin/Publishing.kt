import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

object Maven {
    var localProperties = Properties().apply {
        try {
            this.load(FileInputStream("buildSrc/local.properties"))
        } catch (ex: FileNotFoundException) {
            // Set some defaults
            this.setProperty("gpr.user", "unspecified")
            this.setProperty("gpr.key", "unspecified")
            this.setProperty("version.build", "-1")
        }

        // Load the version from version.txt
        try {
            if (this.getProperty("version.version").trim().isNullOrBlank()) {
                val fileVersion: String? = File("version.txt").useLines { it.firstOrNull() }
                if (!fileVersion.isNullOrBlank()) this.setProperty("version.version", fileVersion)
            }
        } catch (e: Exception) {
            println("Exception read version.txt $e")
        }
    }

    var group: String = "me.safer.apilib"
    var artifactId: String = "saferme-kotlin"
    var gprBaseUrl = "https://maven.pkg.github.com"
    var gprRepoOwner = "SaferMe"
    var gprRepoId = "saferme-api-client-android"

    var gprUser = if (System.getenv().containsKey("GPR_USER")) {
        System.getenv("GPR_USER")
    } else if (localProperties.containsKey("gpr.user")) {
        localProperties.getProperty("gpr.user").trim()
    } else {
        "unspecified"
    }

    var gprKey = when {
        System.getenv().containsKey("GPR_KEY") -> System.getenv("GPR_KEY")
        localProperties.containsKey("gpr.key") -> localProperties.getProperty("gpr.key").trim()
        else -> "unspecified"
    }

    var build: Int = when {
        System.getenv().containsKey("BUILD") -> System.getenv("BUILD").toInt()
        localProperties.containsKey("version.build") -> localProperties.getProperty("version.build").trim().toInt()
        else -> -1
    }

    var version = when {
        System.getenv().containsKey("VERSION") -> System.getenv("VERSION")
        localProperties.containsKey("version.version")
                -> localProperties.getProperty("version.version").trim().filter { it.isDigit() || it == '.' }
        else -> "3.0.0"
    }
}
