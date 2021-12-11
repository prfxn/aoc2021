// The Treachery of Whales (https://adventofcode.com/2021/day/7)

package io.prfxn.aoc2021

import kotlin.math.abs

fun main() {
    val positions =
        textResourceReader("input/07.txt").readText().trim().split(",")
            .map { it.toInt() }

    fun getCost(targetPosition: Int) =
        positions.sumOf { abs(it - targetPosition)  }

    fun getCost2(targetPosition: Int) =
        positions.sumOf {
            val n  = abs(it - targetPosition)
            (n * (n + 1))/2
        }

    println((0..(positions.maxOrNull() ?: 0)).minOf(::getCost))
    println((0..(positions.maxOrNull() ?: 0)).minOf(::getCost2))
}

/** output
 * 337833
 * 96678050
 */
