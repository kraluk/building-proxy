import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
  idea
  jacoco

  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.spring)

  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependencyManagement)

  alias(libs.plugins.versions)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.detekt)
}

group = "io.kraluk"
version = "0.0.1-SNAPSHOT"

val integrationTestImplementation: Configuration = configurations.create("integrationTestImplementation")
  .extendsFrom(configurations.testImplementation.get())
val integrationTestRuntimeOnly: Configuration = configurations.create("integrationTestRuntimeOnly")
  .extendsFrom(configurations.testRuntimeOnly.get())

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(jvm.versions.java.get().toInt())
  }
}

kotlin {
  jvmToolchain(jvm.versions.java.get().toInt())
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

repositories {
  mavenCentral()
}

dependencies {
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-logging")
  implementation("org.springframework.boot:spring-boot-starter-aop")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${libs.versions.springDoc.get()}")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${libs.versions.springDoc.get()}")
  implementation("io.micrometer:micrometer-core")

  implementation("org.jetbrains.kotlin:kotlin-reflect")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.amshove.kluent:kluent:${testLibs.versions.kluent.get()}")
  testImplementation("org.mockito.kotlin:mockito-kotlin:${testLibs.versions.mockitoKotlin.get()}")
  testImplementation("org.awaitility:awaitility:${testLibs.versions.awaitility.get()}")
  testImplementation("org.awaitility:awaitility-kotlin:${testLibs.versions.awaitility.get()}")
  testImplementation("org.skyscreamer:jsonassert:${testLibs.versions.jsonassert.get()}")
}

tasks.test {
  useJUnitPlatform()
  defaultCharacterEncoding = "UTF-8"
  jvmArgs("-XX:+EnableDynamicAgentLoading")

  testLogging {
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
    events(
      org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
    )
  }
}

sourceSets {
  create("integrationTest") {
    compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
  }
}

val integrationTest = tasks.register<Test>("integrationTest") {
  description = "Runs integration tests."
  group = "verification"
  defaultCharacterEncoding = "UTF-8"

  useJUnitPlatform()
  jvmArgs("-XX:+EnableDynamicAgentLoading")

  testLogging {
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
    events(
      org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
    )
  }

  testClassesDirs = sourceSets["integrationTest"].output.classesDirs
  classpath = sourceSets["integrationTest"].runtimeClasspath
  shouldRunAfter(tasks.test)

  finalizedBy(tasks.jacocoTestReport)
}

// customize if needed: https://docs.gradle.org/current/userguide/jacoco_plugin.html
// reports are in build/reports/jacoco as index.html
tasks.jacocoTestReport {
  dependsOn(integrationTest) // all tests are required to run before generating the report
}

tasks.check { dependsOn(integrationTest) }

ktlint {
  version.set(rootProject.toolLibs.versions.ktlint)
  android.set(false)
  verbose.set(true)
  outputToConsole.set(true)
  coloredOutput.set(true)

  filter {
    exclude { entry ->
      entry.file.toString().contains("generated")
    }
  }
  reporters {
    reporter(ReporterType.HTML)
  }
}

detekt {
  buildUponDefaultConfig = true
  allRules = false
  config.setFrom("$projectDir/detekt.yml")
}

project.afterEvaluate { // https://detekt.dev/docs/gettingstarted/gradle/#dependencies
  configurations["detekt"].resolutionStrategy.eachDependency {
    if (requested.group == "org.jetbrains.kotlin") {
      useVersion(io.gitlab.arturbosch.detekt.getSupportedKotlinVersion())
    }
  }
}