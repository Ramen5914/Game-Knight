plugins {
    `java-library`
    `maven-publish`
    id("net.neoforged.moddev") version "2.0.148"
    id("idea")
}

val minecraft_version: String by project
val minecraft_version_range: String by project
val neo_version: String by project
val mod_id: String by project
val mod_name: String by project
val mod_license: String by project
val mod_version: String by project
val mod_group_id: String by project
val update_json_url: String by project
val display_url: String by project
val banner_file: String by project
val icon_file: String by project
val credits: String by project
val authors: String by project
val mod_description: String by project
val pane_version: String by project
val pane_version_range: String by project
val jei_version: String by project

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.BIN
}

version = "$mod_version+mc$minecraft_version"
group = mod_group_id

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
    archivesName.set(mod_id)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

neoForge {
    version = neo_version

    validateAccessTransformers = true

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
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
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("data") {
            clientData()
            programArguments.addAll(
                "--mod", mod_id, "--all", "--output",
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
        create(mod_id) {
            sourceSet(sourceSets["main"])
        }
    }

    unitTest {
        enable()
        testedMod = mods[mod_id]
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
    compileOnly("maven.modrinth:pane:$pane_version")
    runtimeOnly("maven.modrinth:bXdRPjye:mbdruwmV")

    compileOnly("mezz.jei:jei-$minecraft_version-neoforge-api:$jei_version")
    runtimeOnly("mezz.jei:jei-$minecraft_version-neoforge:$jei_version")

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    annotationProcessor("org.spongepowered:mixin:0.8.7:processor")
}

tasks.test {
    useJUnitPlatform()
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraft_version,
        "minecraft_version_range" to minecraft_version_range,
        "neo_version" to neo_version,
        "mod_id" to mod_id,
        "mod_name" to mod_name,
        "mod_license" to mod_license,
        "mod_version" to mod_version,
        "update_json_url" to update_json_url,
        "display_url" to display_url,
        "banner_file" to banner_file,
        "icon_file" to icon_file,
        "credits" to credits,
        "authors" to authors,
        "mod_description" to mod_description,
        "pane_version_range" to pane_version_range
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
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
