plugins {
    kotlin("jvm") version "2.0.21"
    id("application")
}

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.6"

val lwjglNatives = when (org.gradle.internal.os.OperatingSystem.current()) {
    org.gradle.internal.os.OperatingSystem.WINDOWS -> "natives-windows"
    org.gradle.internal.os.OperatingSystem.LINUX -> "natives-linux"
    org.gradle.internal.os.OperatingSystem.MAC_OS -> "natives-macos"
    else -> throw Error("Unsupported OS")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")

    implementation("de.javagl:obj:0.4.0") // TODO: Replace with own obj parser

    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:$lwjglNatives")

    implementation("org.joml:joml:1.10.7")
}

application {
    mainClass.set("io.github.notnekodev.apfel.Main")
}
