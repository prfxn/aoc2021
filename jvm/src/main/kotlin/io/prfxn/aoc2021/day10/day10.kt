package io.prfxn.aoc2021.day10

import io.prfxn.aoc2021.textResourceReader

// region common
val lines =
    textResourceReader("input/10.txt").readLines()


fun score(s: String): Int {
    val stack = mutableListOf<Char>()

    for (c in s) {
        if (c in "[{(<") {
            stack.add(c)
        } else if (c in ">)}]") {
            val oc =
                when (c) {
                    ']' -> '['
                    '}' -> '{'
                    ')' -> '('
                    '>' -> '<'
                    else -> throw IllegalArgumentException(c.toString())
                }
            if (stack.last() != oc) {
                return when (c) {
                        ']' -> 57
                        '}' -> 1197
                        ')' -> 3
                        '>' -> 25137
                        else -> throw IllegalArgumentException()
                    }
            } else {
                stack.removeLast()
            }
        }
    }

    return 0
}

fun closingScore(s: String): Long {
    val stack = mutableListOf<Char>()
    for (c in s) {
        if (c in "[{(<") {
            stack.add(c)
        } else if (c in ">)}]") {
            val oc =
                when (c) {
                    ']' -> '['
                    '}' -> '{'
                    ')' -> '('
                    '>' -> '<'
                    else -> throw IllegalArgumentException(c.toString())
                }
            if (stack.last() != oc) {
                throw IllegalArgumentException()
            } else {
                stack.removeLast()
            }
        }
    }

    println(stack)

    return stack.reversed()
        .fold(0L) { sc, c ->
            val v: Long = when(c) {
                '[' -> 2L
                '{' -> 3L
                '(' -> 1L
                '<' -> 4L
                else -> throw IllegalArgumentException(c.toString())
            }
            (sc * 5) + v
        }
}

fun part1() =
    lines.sumOf(::score)

fun part2() =
    lines
        .filter { score(it) == 0 }
        .map(::closingScore)
        .sorted()
        .let {
            val mid = ((it.size +1) / 2) -1
            it[mid]
        }


fun main() {
    println(part1())
    println(part2())
}
