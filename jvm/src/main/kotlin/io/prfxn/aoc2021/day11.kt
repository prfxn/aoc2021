//  Dumbo Octopus (https://adventofcode.com/2021/day/11)

package io.prfxn.aoc2021

fun main() {

    val lines = textResourceReader("input/11.txt").readLines().map { it.map(Char::digitToInt) }

    fun process(
        grid: Array<IntArray>,
        cells: List<Pair<Int, Int>>,
        flashed: MutableSet<Pair<Int, Int>> = mutableSetOf()
    ): MutableSet<Pair<Int, Int>> {

        cells.onEach { (r, c) -> grid[r][c]++ }
            .asSequence()
            .filter { (r, c) -> grid[r][c] > 9 && r to c !in flashed }
            .forEach { (r, c) ->
                grid[r][c] = 0
                flashed.add(r to c)
                val affectedCells =
                    (r-1..r+1).flatMap { nr -> (c-1..c+1).map { nc -> nr to nc } }
                        .filter { (nr, nc) ->
                            nr in grid.indices && nc in grid[0].indices
                                && !(nr == r && nc == c)
                                && nr to nc !in flashed
                        }
                if (affectedCells.isNotEmpty())
                    process(grid, affectedCells, flashed)
            }

        return flashed
    }

    fun doStep(grid: Array<IntArray>) =
        process(grid, grid.indices.flatMap { r -> grid[0].indices.map { c -> r to c } })

    fun getGrid() = lines.map { it.toIntArray() }.toTypedArray()


    val answer1 = getGrid().let { grid ->
        generateSequence { doStep(grid) }
            .take(100)
            .fold(0) { sum, flashed ->
                sum + flashed.size
            }
    }


    val answer2 = getGrid().let { grid ->
        val gridSize = grid.size * grid[0].size
        generateSequence { doStep(grid) }
            .withIndex()
            .find { it.value.size == gridSize }!!
            .index + 1
    }

    sequenceOf(answer1, answer2).forEach(::println)
}

/** output
 * 1655
 * 337
 */
