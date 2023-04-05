plugins {
    id("java")
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "dev.ryanseo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // YouTube Data API doesn't seem to support google-api-client version 2.0.0 and up
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-youtube:v3-rev20230123-2.0.0")
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("dev.ryanseo.Main")
}


