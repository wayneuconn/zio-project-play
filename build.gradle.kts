buildscript {
    dependencies {
        classpath("com.github.alisiikh:gradle-scalastyle-plugin:3.3.0")
    }
    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}

plugins {
    scala
    idea
    application
    id("com.github.maiflai.scalatest") version "0.32"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    manifest {
        attributes(mapOf("Main-Class" to "com.wayne.ZIOHttpApp"))
    }
}

application {
    mainClass.set("com.wayne.ZIOHttpApp")
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

val scalaCompilerPlugin = configurations.create("scalaCompilerPlugin")
val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencies {
    // scala version
    val scalaVersion = "2.13.8"
    // scala major version: 2.13
    val scalaMajor = scalaVersion.split(".").take(2).joinToString(".")

    // scala standard libs
    implementation("org.scala-lang:scala-library:$scalaVersion")
    implementation("org.scala-lang.modules:scala-parallel-collections_$scalaMajor:1.0.4")

    // http
    implementation("io.d11:zhttp_$scalaMajor:2.0.0-RC10")

    // database
    implementation("org.scalikejdbc:scalikejdbc_$scalaMajor:4.0.0")
    implementation("org.scalikejdbc:scalikejdbc-config_$scalaMajor:4.0.0")
    implementation("com.microsoft.sqlserver:mssql-jdbc:10.2.1.jre17")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // logging
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.6.1")

    implementation("dev.zio:zio-logging_2.13:2.0.1")
    implementation("dev.zio:zio-logging-slf4j_2.13:2.0.1")

    // json
    implementation("io.circe:circe-jackson29_$scalaMajor:0.14.0")
    implementation("io.circe:circe-generic_$scalaMajor:0.14.1")
    implementation("io.circe:circe-core_$scalaMajor:0.14.1")

    // tapir - swagger/open-api docs generation
    implementation("com.softwaremill.sttp.tapir:tapir-core_$scalaMajor:1.0.3")
    implementation("com.softwaremill.sttp.tapir:tapir-zio-http-server_$scalaMajor:1.0.3")
    implementation("com.softwaremill.sttp.tapir:tapir-json-circe_$scalaMajor:1.0.3")
    implementation("com.softwaremill.sttp.tapir:tapir-swagger-ui-bundle_$scalaMajor:1.0.3")
    implementation("com.softwaremill.sttp.tapir:tapir-openapi-docs_$scalaMajor:1.0.3")
    implementation("com.softwaremill.sttp.tapir:tapir-openapi-circe-yaml_$scalaMajor:1.0.0-M7")

    // app config library
    implementation("com.github.pureconfig:pureconfig_$scalaMajor:0.17.1")

    // google oauth 2.0
    implementation("com.google.api-client:google-api-client:1.34.1")

    // testing
    testImplementation("org.scalatest:scalatest_$scalaMajor:3.2.11")
    testImplementation("com.h2database:h2:2.1.212")
    testImplementation("com.vladsch.flexmark:flexmark-all:0.64.0")

    // Google BIG Query Client Library
    implementation(platform("com.google.cloud:libraries-bom:25.4.0"))
    implementation("com.google.cloud:google-cloud-bigquery")
}
