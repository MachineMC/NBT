plugins {
    java
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")
    testImplementation("junit:junit:4.13.2")
}

group = "org.machinemc"
version = "2.0.0"
description = "NBT"

publishing {
    repositories {
        maven {
            name = "machine"
            url = uri("https://repo.machinemc.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.machinemc"
            artifactId = "nbt"
            version = project.version.toString()
            from(components["java"])
        }
    }
}

tasks {
    java {
        withSourcesJar()
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
