// Smoke Basin (https://adventofcode.com/2021/day/9)

package io.prfxn.aoc2021

object Day09 {
    val lines =
        textResourceReader("input/09.txt").useLines { seq ->
            seq.map { it.map(Char::digitToInt) }.toList()
        }

    fun isValidCoords(row: Int, col: Int) =
        row in lines.indices && col in lines[0].indices

    fun adjCoordsSeq(row: Int, col: Int) =
        sequenceOf(
            row - 1 to col,
            row + 1 to col,
            row to col - 1,
            row to col + 1
        )
            .filter { (r, c) -> isValidCoords(r, c) }

    fun isLow(row: Int, col: Int) =
        lines[row][col].let { value ->
            adjCoordsSeq(row, col)
                .all { (r, c) -> lines[r][c] > value }
        }

    val lowPoints =
        lines.indices
            .flatMap { row ->
                lines[0].indices.map { col ->
                    row to col
                }
            }
            .filter { (r, c) -> isLow(r, c) }

    fun crawlBasin(row: Int, col: Int,
                   visitedCoords: MutableSet<Pair<Int, Int>> = mutableSetOf()): Set<Pair<Int, Int>> {

        visitedCoords.add(row to col)
        adjCoordsSeq(row, col)
            .filter { (r, c) ->
                val h = lines[r][c]
                lines[row][col] < h && h < 9
                        && r to c !in visitedCoords
            }
            .forEach { (r, c) -> crawlBasin(r, c, visitedCoords) }
        return visitedCoords
    }

    fun getBasinSize(row: Int, col: Int) =
        crawlBasin(row, col).count()
}

fun main() {
    Day09.apply {
        fun part1() =
            lowPoints.sumOf { (r, c) -> Day09.lines[r][c] + 1 }

        fun part2() =
            lowPoints
                .map { (r, c) -> getBasinSize(r, c) }
                .sorted()
                .takeLast(3)
                .reduce { p, bs -> p * bs }

        println(part1())
        println(part2())
    }
}

/** output
 * 532
 * 1110780
 */
