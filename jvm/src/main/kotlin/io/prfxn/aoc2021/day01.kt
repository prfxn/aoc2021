//  Sonar Sweep (https://adventofcode.com/2021/day/1)

package io.prfxn.aoc2021

fun main() {

    val lines = textResourceReader("input/01.txt").readLines().map { it.toInt() }

    val answer1 =
        lines
            .windowed(2)
            .count { (prev, next) -> next > prev }

    val answer2 =
        lines
            .windowed(3)
            .map { it.sum() }
            .windowed(2)
            .count { (prev, next) -> next > prev }

    sequenceOf(answer1, answer2).forEach(::println)
}

/** output
 * 1228
 * 1257
 */
