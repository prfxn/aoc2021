package io.prfxn.aoc2021

import java.io.Reader
import java.lang.RuntimeException
import java.nio.charset.Charset

fun textResourceReader(name: String, charset: Charset = Charsets.UTF_8): Reader =
    requireNotNull(
            ::textResourceReader.javaClass.classLoader.getResourceAsStream(name))
        { "Resource not found: $name" }
        .reader(charset)

fun fail(msg: String = ""): Nothing =
    throw RuntimeException(msg)
