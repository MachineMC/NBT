plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
}