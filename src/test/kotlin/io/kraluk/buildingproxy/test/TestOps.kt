package io.kraluk.buildingproxy.test

import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader

fun contentOf(resourceName: String): String =
  InputStreamReader(ClassPathResource(resourceName).inputStream, Charsets.UTF_8)
    .use { FileCopyUtils.copyToString(it) }

fun cleanContentOf(resourceName: String): String =
  InputStreamReader(ClassPathResource(resourceName).inputStream, Charsets.UTF_8)
    .use { FileCopyUtils.copyToString(it) }.filterNot { it.isWhitespace() }