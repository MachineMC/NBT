plugins {
    id("java-library-convention")
    `maven-publish`
}

dependencies {
    implementation(project(":nbt-core"))
}

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
            artifactId = "nbt-parser"
            version = project.version.toString()
            from(components["java"])
        }
    }
}