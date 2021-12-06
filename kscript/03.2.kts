#!/usr/bin/env kscript

/**
 *  https://adventofcode.com/2021/day/3#part2
 *
 *  What is the life support rating of the submarine?
 *
 */


val lines = System.`in`.reader().readLines()

fun getFilteredLineIndices(isFiltered: (Int) -> Boolean): Sequence<Int> =
    lines.indices.asSequence().filter { isFiltered(it) }

fun getRating(getExpectedBit: (Int, Int) -> Int ): Int {
    val filterFlags = BooleanArray(lines.size) { true }

    var remaining = lines.size
    var bitIndex = 0

    while (remaining > 1) {
        val num1s = getFilteredLineIndices(filterFlags::get).sumOf { lines[it][bitIndex].digitToInt() }
        val num0s = remaining - num1s
        val expectedBit = getExpectedBit(num0s, num1s)
        getFilteredLineIndices(filterFlags::get).forEach { lineIndex ->
            if (lines[lineIndex][bitIndex].digitToInt() != expectedBit) {
                filterFlags[lineIndex] = false
                remaining -= 1
            }
        }
        bitIndex += 1
    }

    if (remaining != 1) throw IllegalArgumentException()

    return Integer.parseInt(lines[getFilteredLineIndices(filterFlags::get).first()], 2)
}

val oxygenGeneratorBits = getRating { num0s, num1s -> if (num1s >= num0s) 1 else 0 }
val co2ScrubberBits = getRating { num0s, num1s -> if (num1s < num0s) 1 else 0 }

println(oxygenGeneratorBits * co2ScrubberBits)
