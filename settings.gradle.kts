rootProject.name = "building-proxy"

dependencyResolutionManagement {
  versionCatalogs {
    create("jvm") {
      version("java", "21")
    }
    create("libs") {
      plugin("kotlin.jvm", "org.jetbrains.kotlin.jvm").version("2.2.20")
      plugin("kotlin.spring", "org.jetbrains.kotlin.plugin.spring").version("2.2.20")

      plugin("spring.boot", "org.springframework.boot").version("3.5.6")
      plugin("spring.dependencyManagement", "io.spring.dependency-management").version("1.1.7")

      plugin("ktlint", "org.jlleitschuh.gradle.ktlint").version("13.1.0")
      plugin("detekt", "io.gitlab.arturbosch.detekt").version("1.23.8")

      plugin("versions", "com.github.ben-manes.versions").version("0.53.0")

      version("springDoc", "2.8.13")
      version("resilience4j", "2.3.0")
      version("redisson", "3.52.0")
    }
    create("testLibs") {
      version("awaitility", "4.3.0")
      version("kluent", "1.73")
      version("mockitoKotlin", "6.1.0")
      version("jsonassert", "2.0-rc1")
      version("springCloudContractWiremock", "4.3.0")
    }
    create("toolLibs") {
      version("ktlint", "1.7.1")
    }
  }
}