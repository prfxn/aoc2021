//  Seven Segment Search (https://adventofcode.com/2021/day/8)

package io.prfxn.aoc2021

fun main() {
    val lines =
        requireNotNull(object {}.javaClass.classLoader.getResourceAsStream("input/08.txt"))
            .reader()
            .useLines { lineSeq ->
                lineSeq
                    .map { line ->
                        val (patterns, output) =
                            line.split(" | ").map { it.split(" ").map { p -> p.toSet() } }
                        object {
                            val patterns = patterns
                            val output = output
                        }
                    }
                    .toList()
            }

    fun part1() =
        lines
            .flatMap { line ->
                line.output.filter { it.size in setOf(2, 4, 3, 7) }
            }
            .count()


    fun part2(): Int {

        fun <T> isValid235(digitToPattern: Map<Int, Set<T>>, p2: Set<T>, p3: Set<T>, p5: Set<T>): Boolean =
            p2 union p5 == digitToPattern[8] &&
                    p2 union digitToPattern[4]!! == digitToPattern[8]

        fun <T> isValid069(digitToPattern: Map<Int, Set<T>>, p0: Set<T>, p6: Set<T>, p9: Set<T>): Boolean =
            p6 union digitToPattern[1]!! == digitToPattern[8] &&
                    p9 union digitToPattern[4]!! == p9

        return lines
            .sumOf { line ->
                val patternsBySize = line.patterns.groupBy { it.size }

                val patternToDigit =
                    patternsBySize
                        .map { (patternSize, patterns) ->
                            when (patternSize) {
                                2 -> {
                                    require(patterns.size == 1)
                                    patterns.first() to 1
                                }
                                3 -> {
                                    require(patterns.size == 1)
                                    patterns.first() to 7
                                }
                                4 -> {
                                    require(patterns.size == 1)
                                    patterns.first() to 4
                                }
                                7 -> {
                                    require(patterns.size == 1)
                                    patterns.first() to 8
                                }
                                else -> null
                            }
                        }
                        .filterNotNull()
                        .toMap()
                        .toMutableMap()

                val digitToPattern = patternToDigit.map { (k, v) -> v to k }.toMap()

                patternsBySize
                    .flatMap { (patternSize, patterns) ->
                        when (patternSize) {
                            5 -> {
                                val (p2, p3, p5) = requireNotNull(
                                    patterns
                                        .permutations()
                                        .find { (p2, p3, p5) -> isValid235(digitToPattern, p2, p3, p5) }
                                )
                                listOf(p2 to 2, p3 to 3, p5 to 5)
                            }
                            6 -> {
                                val (p0, p6, p9) = requireNotNull(
                                    patterns
                                        .permutations()
                                        .find { (p0, p6, p9) -> isValid069(digitToPattern, p0, p6, p9) }
                                )
                                listOf(p0 to 0, p6 to 6, p9 to 9)
                            }
                            else -> listOf()
                        }
                    }
                    .toMap(patternToDigit)

                line.output
                    .map(patternToDigit::get)
                    .joinToString("")
                    .toInt()
            }
    }

    sequenceOf(part1(), part2()).forEach(::println)
}

/** output
 * 349
 * 1070957
 */
