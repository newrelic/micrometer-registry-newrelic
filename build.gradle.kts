repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

plugins {
    id("com.github.sherter.google-java-format") version "0.9"
    `java-library`
    `maven-publish`
    signing
}

group = "com.newrelic.telemetry"

// -Prelease=true will render a non-snapshot version
// All other values (including unset) will render a snapshot version.
val release: String? by project
version = project.findProperty("releaseVersion") as String + if("true" == release) "" else "-SNAPSHOT"

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

googleJavaFormat {
    exclude(".**")
}

dependencies {
    api("io.micrometer:micrometer-core:1.6.3")
    api("com.newrelic.telemetry:telemetry:0.10.0")
    implementation("org.slf4j:slf4j-api:1.7.30")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testRuntimeOnly("org.slf4j:slf4j-simple:1.7.30")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation("org.mockito:mockito-core:3.2.4")
    testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
    testImplementation("org.mock-server:mockserver-netty:5.11.2")
    constraints {
        testImplementation("org.apache.httpcomponents:httpclient:4.5.13") {
            because("previous versions trigger Snyk security warnings")
        }
    }

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    from ("LICENSE.md")
    manifest.attributes["Implementation-Version"] = project.version
    manifest.attributes["Implementation-Vendor"] = "New Relic, Inc"
}

val useLocalSonatype = project.properties["useLocalSonatype"] == "true"

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
        // customize all publications here
        withType(MavenPublication::class) {
            pom {
                name.set(project.name)
                description.set("Micrometer registry implementation that sends data to New Relic as dimensional metrics")
                url.set("https://github.com/newrelic/micrometer-registry-newrelic")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("newrelic")
                        name.set("New Relic")
                        email.set("opensource@newrelic.com")
                    }
                }
                scm {
                    url.set("git@github.com:newrelic/micrometer-registry-newrelic.git")
                    connection.set("scm:git@github.com:newrelic/micrometer-registry-newrelic.git")
                }
            }
        }
    }
    repositories {
        maven {
            if (useLocalSonatype) {
                val releasesRepoUrl = uri("http://localhost:8081/repository/maven-releases/")
                val snapshotsRepoUrl = uri("http://localhost:8081/repository/maven-snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            }
            else {
                val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            }
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

// This makes it difficult to use modern Java and produce usable output.
tasks.withType<GenerateModuleMetadata> {
    enabled = false
}
