import io.gitlab.arturbosch.detekt.getSupportedKotlinVersion
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
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

val integrationTest: SourceSet by sourceSets.creating

val testIntegrationImplementation: Configuration = configurations[integrationTest.implementationConfigurationName].extendsFrom(
  configurations.testImplementation.get(),
)
val testIntegrationRuntimeOnly: Configuration = configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(
  configurations.testRuntimeOnly.get(),
)
val mockitoAgent: Configuration = configurations.create("mockitoAgent")

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
  implementation("org.redisson:redisson-spring-boot-starter:${libs.versions.redisson.get()}")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${libs.versions.springDoc.get()}")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${libs.versions.springDoc.get()}")
  implementation("io.micrometer:micrometer-core")

  implementation("org.jetbrains.kotlin:kotlin-reflect")

  implementation("io.github.resilience4j:resilience4j-circuitbreaker:${libs.versions.resilience4j.get()}")
  implementation("io.github.resilience4j:resilience4j-retry:${libs.versions.resilience4j.get()}")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.amshove.kluent:kluent:${testLibs.versions.kluent.get()}")
  testImplementation("org.mockito:mockito-core:${dependencyManagement.importedProperties["mockito.version"]}")
  testImplementation("org.mockito.kotlin:mockito-kotlin:${testLibs.versions.mockitoKotlin.get()}")
  testImplementation("org.awaitility:awaitility:${testLibs.versions.awaitility.get()}")
  testImplementation("org.awaitility:awaitility-kotlin:${testLibs.versions.awaitility.get()}")
  testImplementation("org.skyscreamer:jsonassert:${testLibs.versions.jsonassert.get()}")

  testIntegrationImplementation("org.springframework.boot:spring-boot-testcontainers")
  testIntegrationImplementation("org.testcontainers:junit-jupiter")
  testIntegrationImplementation("org.testcontainers:toxiproxy")
  testIntegrationImplementation(
    "org.springframework.cloud:spring-cloud-contract-wiremock:${testLibs.versions.springCloudContractWiremock.get()}",
  )

  mockitoAgent("org.mockito:mockito-core:${dependencyManagement.importedProperties["mockito.version"]}") { isTransitive = false }
}

tasks.test {
  useJUnitPlatform()
  defaultCharacterEncoding = "UTF-8"
  jvmArgs("-XX:+EnableDynamicAgentLoading", "-javaagent:${mockitoAgent.asPath}")

  testLogging {
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
    events(
      TestLogEvent.FAILED,
      TestLogEvent.PASSED,
      TestLogEvent.SKIPPED,
      TestLogEvent.STANDARD_OUT,
    )
  }
}
val testIntegration = tasks.register<Test>("testIntegration") {
  description = "Runs integration tests."
  group = "verification"
  defaultCharacterEncoding = "UTF-8"

  useJUnitPlatform()
  jvmArgs("-XX:+EnableDynamicAgentLoading", "-javaagent:${mockitoAgent.asPath}")

  testLogging {
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
    events(
      TestLogEvent.FAILED,
      TestLogEvent.PASSED,
      TestLogEvent.SKIPPED,
      TestLogEvent.STANDARD_OUT,
    )
  }

  testClassesDirs = integrationTest.output.classesDirs
  classpath = configurations[integrationTest.runtimeClasspathConfigurationName] + integrationTest.output
  shouldRunAfter(tasks.test)

  finalizedBy(tasks.jacocoTestReport)
}

// customize if needed: https://docs.gradle.org/current/userguide/jacoco_plugin.html
// reports are in build/reports/jacoco as index.html
tasks.jacocoTestReport {
  dependsOn(testIntegration) // all tests are required to run before generating the report
}

tasks.check { dependsOn(testIntegration) }

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

project.afterEvaluate {
  // https://detekt.dev/docs/gettingstarted/gradle/#dependencies
  configurations["detekt"].resolutionStrategy.eachDependency {
    if (requested.group == "org.jetbrains.kotlin") {
      useVersion(getSupportedKotlinVersion())
    }
  }
}