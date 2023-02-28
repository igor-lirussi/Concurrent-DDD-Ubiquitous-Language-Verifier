plugins {
    java
    scala
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.scala-lang:scala-library:2.13.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}

application {
    mainClass.set("main.java.program.RunProgram")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "main.java.program.RunProgram"
        )
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

