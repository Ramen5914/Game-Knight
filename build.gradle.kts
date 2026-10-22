plugins {
    `java-library`
    `maven-publish`
    id("net.neoforged.moddev") version "2.0.148"
    id("idea")
}

val minecraftVersion = property("minecraftVersion") as String
val minecraftVersionRange = property("minecraftVersionRange") as String
val neoVersion = property("neoVersion") as String
val modId = property("modId") as String
val modName = property("modName") as String
val modLicense = property("modLicense") as String
val modVersion = property("modVersion") as String
val modGroupId = property("modGroupId") as String
val updateJsonUrl = property("updateJsonUrl") as String
val displayUrl = property("displayUrl") as String
val bannerFile = property("bannerFile") as String
val iconFile = property("iconFile") as String
val credits = property("credits") as String
val authors = property("authors") as String
val modDescription = property("modDescription") as String
val paneVersion = property("paneVersion") as String
val paneVersionRange = property("paneVersionRange") as String
val jeiVersion = property("jeiVersion") as String

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.BIN
}

tasks.matching { it.name == "createMinecraftArtifacts" }.configureEach {
    dependsOn("stonecutterGenerate")
}

version = "$modVersion+mc$minecraftVersion"
group = modGroupId

sourceSets.named("main") {
    resources {
        srcDir("src/generated/resources")
        exclude("**/*.bbmodel")
        exclude("**/*.aseprite")
        exclude("**/*.cache")
    }
}

repositories {
    exclusiveContent {
        forRepository { maven { url = uri("https://api.modrinth.com/maven") } }
        filter { includeGroup("maven.modrinth") }
    }

    maven {
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com")
    }

    maven {
        name = "ModMaven"
        url = uri("https://modmaven.dev")
    }

    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://cursemaven.com")
            }
        }
        filter {
            includeGroup("curse.maven")
        }
    }
}

base {
    archivesName.set(modId)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

neoForge {
    version = neoVersion

    validateAccessTransformers = true

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("clientAuth1") {
            client()

            devLogin = true
            systemProperty("devlogin.launch_profile", "Ramen5914")
            gameDirectory = file("runs/clientAuth1")
        }

        create("clientAuth2") {
            client()

            devLogin = true
            systemProperty("devlogin.launch_profile", "RamenBot")
            gameDirectory = file("runs/clientAuth2")
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("data") {
            clientData()
            programArguments.addAll(
                "--mod", modId, "--all", "--output",
                file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets["main"])
        }
    }

    unitTest {
        enable()
        testedMod = mods[modId]
    }
}

afterEvaluate {
    configurations.findByName("localRuntime")?.let {
        configurations["runtimeClasspath"].extendsFrom(it)
    }
}

dependencies {
    // Iris
    implementation("curse.maven:irisshaders-455508:8868972")
    // Sodium
    implementation("curse.maven:sodium-394468:8859536")

    // Credential Store
    implementation("com.microsoft:credential-secure-storage:1.0.3")

    // Pane
    compileOnly("maven.modrinth:pane:$paneVersion")
    runtimeOnly("maven.modrinth:bXdRPjye:mbdruwmV")

    compileOnly("mezz.jei:jei-$minecraftVersion-neoforge-api:$jeiVersion")
    runtimeOnly("mezz.jei:jei-$minecraftVersion-neoforge:$jeiVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    annotationProcessor("org.spongepowered:mixin:0.8.7:processor")
}

tasks.test {
    useJUnitPlatform()
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    dependsOn("stonecutterGenerate")

    val replaceProperties = mapOf(
        "minecraftVersion" to minecraftVersion,
        "minecraftVersionRange" to minecraftVersionRange,
        "neoVersion" to neoVersion,
        "modId" to modId,
        "modName" to modName,
        "modLicense" to modLicense,
        "modVersion" to modVersion,
        "updateJsonUrl" to updateJsonUrl,
        "displayUrl" to displayUrl,
        "bannerFile" to bannerFile,
        "iconFile" to iconFile,
        "credits" to credits,
        "authors" to authors,
        "modDescription" to modDescription,
        "paneVersionRange" to paneVersionRange
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(layout.buildDirectory.dir("generated/stonecutter/main/templates"))
    into("build/generated/sources/modMetadata")
}

sourceSets.named("main") {
    resources.srcDir(generateModMetadata)
}
neoForge.ideSyncTask(generateModMetadata)

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri(layout.projectDirectory.dir("repo"))
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
