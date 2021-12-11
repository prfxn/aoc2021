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

fun <T> List<T>.permutations(): List<List<T>> = permutations(size)

fun <T> List<T>.permutations(r: Int): List<List<T>> =
    if (r <= size) {
        generateSequence((0 until r).toMutableList()) { indices ->
            val (ptr, nextIdx) =
                (r - 1 downTo 0)
                    .map { ptr ->
                        ptr to
                                (indices[ptr] + 1 until size)
                                    .find { it !in indices.asSequence().take(ptr) }
                    }
                    .find { (_, nextIdx) -> nextIdx != null }
                    ?: return@generateSequence null

            (sequenceOf(nextIdx!!) +
                    this.indices.asSequence().filter { it !in indices.asSequence().take(ptr + 1) })
                .take(r - ptr)
                .forEachIndexed { i, idx ->
                    indices[ptr + i] = idx
                }

            indices
        }
            .map { indices -> indices.map(::get).toList() }
            .toList()
    } else {
        emptyList()
    }
