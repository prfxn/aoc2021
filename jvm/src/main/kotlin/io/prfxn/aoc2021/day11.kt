//  Dumbo Octopus (https://adventofcode.com/2021/day/11)

package io.prfxn.aoc2021

fun main() {

    val lines = textResourceReader("input/11.txt").readLines().map { it.map(Char::digitToInt) }

    fun step(grid: MutableList<MutableList<Int>>): MutableSet<Pair<Int, Int>> {
        // increment
        grid.indices.forEach { r ->
            grid[0].indices.forEach { c ->
                grid[r][c] = grid[r][c] + 1
            }
        }

        // process flashes
        val flashed = mutableSetOf<Pair<Int, Int>>()
        fun processFlash(row: Int, col: Int) {
            if (row to col in flashed) return
            flashed.add(row to col)
            grid[row][col] = 0

            for (r in (row-1)..(row+1)) {
                for (c in (col-1)..(col+1)) {
                    if (r !in grid.indices || c !in grid[0].indices || r to c in flashed) continue
                    grid[r][c] = grid[r][c] + 1
                }
            }

            for (r in (row-1)..(row+1)) {
                for (c in (col-1)..(col+1)) {
                    if (r !in grid.indices || c !in grid[0].indices || r to c in flashed) continue
                    if (grid[r][c] > 9) processFlash(r, c)
                }
            }

        }
        grid.indices.flatMap { r -> grid[0].indices.map { c -> r to c } }
            .filter { (r, c) -> grid[r][c] > 9 }
            .toList()
            .forEach { (r, c) ->
                if (r to c !in flashed) processFlash(r, c)
            }

        return flashed
    }

    val answer1 = lines.map { it.toMutableList() }.toMutableList().let { grid ->
        generateSequence { step(grid) }
            .take(100)
            .fold(0) { sum, flashed ->
                sum + flashed.size
            }
    }

    val answer2 = lines.map { it.toMutableList() }.toMutableList().let { grid ->
        generateSequence(0 to mutableSetOf<Pair<Int, Int>>()) { (i, _) -> (i + 1) to step(grid) }
            .find { (i, flashed) ->
                flashed.size == 100
            }!!
            .first
    }

    sequenceOf(answer1, answer2).forEach(::println)
}

/** output
 * 1655
 * 337
 */
