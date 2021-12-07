#!/usr/bin/env kscript

import kotlin.math.abs

/**
 *  https://adventofcode.com/2021/day/7
 *
 *  Determine the horizontal position that the crabs can align to using the least fuel possible.
 *  How much fuel must they spend to align to that position?
 *
 */

val positions =
    System.`in`.reader().readText().trim().split(",")
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
