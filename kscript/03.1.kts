#!/usr/bin/env kscript

/**
 *  https://adventofcode.com/2021/day/3
 *
 *  What is the power consumption of the submarine?
 *
 */

val bitCounts = IntArray(12) { 0 }
var numLines = 0

System.`in`.reader().useLines { lineSeq ->
    for (line in lineSeq) {
        for (i in line.indices) {
            bitCounts[i] +=
                when (line[i]) {
                    '0' -> 0
                    '1' -> 1
                    else -> throw IllegalArgumentException()
                }
        }
        numLines += 1
    }
}

val gammaRateBits =
    bitCounts
        .map { num1s ->
            val num0s = numLines - num1s
            if (num1s > num0s) '1'
            else if (num1s < num0s) '0'
            else throw IllegalArgumentException()
        }
        .joinToString("")

val epsilonRateBits =
    gammaRateBits
        .map {
            when (it) {
                '0' -> '1'
                '1' -> '0'
                else -> throw IllegalArgumentException()
            }
        }
        .joinToString("")

println(
    Integer.parseInt(gammaRateBits, 2) *
        Integer.parseInt(epsilonRateBits, 2)
)

