import com.bmuschko.gradle.nexus.NexusPluginExtension

buildscript {
    dependencies {
        classpath("gradle.plugin.com.github.sherter.google-java-format:google-java-format-gradle-plugin:0.8")
        classpath ("com.bmuschko:gradle-nexus-plugin:2.3.1")
    }
}

allprojects {
    group = "com.newrelic.telemetry"
    version = project.findProperty("releaseVersion") as String
    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

plugins {
    id("com.github.sherter.google-java-format") version "0.8"
    `java-library`
}
apply(plugin = "com.bmuschko.nexus")

googleJavaFormat {
    exclude(".**")
}

dependencies {
    api("io.micrometer:micrometer-core:1.2.0")
    api("com.newrelic.telemetry:telemetry:0.2.0-SNAPSHOT")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.mockito:mockito-core:3.0.0")
    testImplementation("org.mockito:mockito-junit-jupiter:3.0.0")
    testImplementation("org.mock-server:mockserver-netty:5.6.0")
}

val jar: Jar by tasks
jar.apply {
    manifest.attributes["Implementation-Version"] = project.version
    manifest.attributes["Implementation-Vendor"] = "New Relic, Inc"
}

val sonatype by configurations.creating {
    extendsFrom(configurations["archives"])
}

val uploadSonatype by tasks.registering(org.gradle.api.tasks.Upload::class) {
    configuration = configurations["sonatype"]

    isUploadDescriptor = true
}

configure<NexusPluginExtension> {
    if (project.properties["useLocalSonatype"] == "true") {
        sign = false
        snapshotRepositoryUrl = "http://admin:admin123@localhost:8081/repository/maven-snapshots/"
        repositoryUrl = "http://admin:admin123@localhost:8081/repository/maven-releases/"
    }
    setConfiguration(sonatype)
}

val build by tasks.named("build")
build.dependsOn("javadoc")
