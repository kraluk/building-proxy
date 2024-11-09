rootProject.name = "building-proxy"

dependencyResolutionManagement {
  versionCatalogs {
    dependencyResolutionManagement {
      versionCatalogs {
        create("jvm") {
          version("java", "21")
        }
        create("libs") {
          plugin("kotlin.jvm", "org.jetbrains.kotlin.jvm").version("1.9.25")
          plugin("kotlin.spring", "org.jetbrains.kotlin.plugin.spring").version("1.9.25")

          plugin("spring.boot", "org.springframework.boot").version("3.3.4")
          plugin("spring.dependencyManagement", "io.spring.dependency-management").version("1.1.6")

          plugin("ktlint", "org.jlleitschuh.gradle.ktlint").version("12.1.1")
          plugin("detekt", "io.gitlab.arturbosch.detekt").version("1.23.7")

          plugin("versions", "com.github.ben-manes.versions").version("0.51.0")

          version("springDoc", "2.6.0")
        }
        create("testLibs") {
          version("awaitility", "4.2.2")
          version("kluent", "1.73")
          version("mockitoKotlin", "5.4.0")
          version("jsonassert", "1.5.3")
        }
        create("toolLibs") {
          version("ktlint", "0.50.0")
        }
      }
    }
  }
}