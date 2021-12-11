//  Binary Diagnostic (https://adventofcode.com/2021/day/3)

package io.prfxn.aoc2021

fun main() {

    fun part1() {
        val bitCounts = IntArray(12) { 0 }
        var numLines = 0

        textResourceReader("input/03.txt").useLines { lineSeq ->
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
    }

    fun part2() {
        val lines = textResourceReader("input/03.txt").readLines()

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
    }

    part1()
    part2()
}

/** output
 * 4174964
 * 4474944
 */