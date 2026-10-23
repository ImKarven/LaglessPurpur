import io.papermc.paperweight.checkstyle.PaperCheckstyleExt
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.24"
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

paperweight {
    upstreams.register("purpur") {
        repo = github("PurpurMC", "Purpur")
        ref = providers.gradleProperty("purpurCommit")

        patchFile {
            path = "purpur-server/build.gradle.kts"
            outputFile = file("lagless-purpur-server/build.gradle.kts")
            patchFile = file("lagless-purpur-server/build.gradle.kts.patch")
        }
        patchFile {
            path = "purpur-api/build.gradle.kts"
            outputFile = file("lagless-purpur-api/build.gradle.kts")
            patchFile = file("lagless-purpur-api/build.gradle.kts.patch")
        }
        patchFile {
            path = "purpur-checkstyle/build.gradle.kts"
            outputFile = file("lagless-purpur-checkstyle/build.gradle.kts")
            patchFile = file("lagless-purpur-checkstyle/build.gradle.kts.patch")
        }
        patchRepo("paperApi") {
            upstreamPath = "paper-api"
            patchesDir = file("lagless-purpur-api/paper-patches")
            outputDir = file("paper-api")
        }
        patchDir("purpurApi") {
            upstreamPath = "purpur-api"
            excludes = listOf("build.gradle.kts", "build.gradle.kts.patch", "paper-patches")
            patchesDir = file("lagless-purpur-api/purpur-patches")
            outputDir = file("purpur-api")
        }
        patchRepo("paperCheckstyle") {
            upstreamPath = "paper-checkstyle"
            patchesDir = file("lagless-purpur-checkstyle/paper-patches")
            outputDir = file("paper-checkstyle")
        }
        patchDir("purpurCheckstyle") {
            upstreamPath = "purpur-checkstyle"
            excludes = setOf("build.gradle.kts", "build.gradle.kts.patch", "paper-patches")
            patchesDir = file("lagless-purpur-checkstyle/purpur-patches")
            outputDir = file("purpur-checkstyle")
        }
        patchRepo("paperCheckstyleConfig") {
            upstreamPath = ".checkstyle"
            patchesDir = file("lagless-purpur-checkstyle/config-patches")
            outputDir = file(".checkstyle")
        }
    }
}

subprojects {
    apply {
        plugin("java-library")
        plugin("maven-publish")
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    val tempDisabled = setOf("lagless-purpur-server", "purpur-server", "paper-server", "test-plugin")

    if (name !in tempDisabled) {
        apply { plugin("io.papermc.paperweight.paper-checkstyle") }
        extensions.configure<PaperCheckstyleExt> {
            typeUseAnnotationsFile.set(rootProject.layout.projectDirectory.file(".checkstyle/type_use_annotations.txt"))
        }

        /*tasks.withType<PaperCheckstyleTask>().configureEach {
            configDirectory = rootProject.layout.projectDirectory.dir(".checkstyle")
            // configFile = layout.projectDirectory.file(".checkstyle/checkstyle.xml").asFile // use the base file if not overwritten
            maxHeapSize = "2g"
            reports {
                xml.required = true
                html.required = true
            }
        }*/

        dependencies {
            "checkstyle"(project(":lagless-purpur-checkstyle"))
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
        options.isFork = true
        options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal"))
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    extensions.configure<PublishingExtension> {
        repositories {
            maven("https://repo.purpurmc.org/snapshots") {
                name = "lagless-purpur"
                credentials(PasswordCredentials::class)
            }
        }
    }
}

tasks.register("printMinecraftVersion") {
    doLast {
        println(providers.gradleProperty("mcVersion").get().trim())
    }
}

tasks.register("printLaglessPurpurVersion") {
    doLast {
        println(project.version)
    }
}
