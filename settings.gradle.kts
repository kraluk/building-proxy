rootProject.name = "building-proxy"

dependencyResolutionManagement {
  versionCatalogs {
    create("jvm") {
      version("java", "21")
    }
    create("libs") {
      plugin("kotlin.jvm", "org.jetbrains.kotlin.jvm").version("2.3.0")
      plugin("kotlin.spring", "org.jetbrains.kotlin.plugin.spring").version("2.3.0")

      plugin("spring.boot", "org.springframework.boot").version("4.0.3")
      plugin("spring.dependencyManagement", "io.spring.dependency-management").version("1.1.7")

      plugin("ktlint", "org.jlleitschuh.gradle.ktlint").version("14.1.0")
      plugin("detekt", "dev.detekt").version("2.0.0-alpha.2")

      plugin("versions", "com.github.ben-manes.versions").version("0.53.0")

      version("springDoc", "3.0.2")
      version("resilience4j", "2.3.0")
      version("redisson", "4.3.0")
    }
    create("testLibs") {
      version("awaitility", "4.3.0")
      version("kluent", "1.73")
      version("mockitoKotlin", "6.1.0")
      version("jsonassert", "2.0-rc1")
      version("springWiremock", "4.0.9")
    }
    create("toolLibs") {
      version("ktlint", "1.8.0")
    }
  }
}