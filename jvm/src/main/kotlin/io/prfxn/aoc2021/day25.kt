//  Sea Cucumber (https://adventofcode.com/2021/day/25)

package io.prfxn.aoc2021

fun main() {

    val v = 'v'
    val gt = '>'

    val lines = textResourceReader("input/25.txt").readLines()
    val numRows = lines.size
    val numCols = lines.first().length
    val (gts, vs) =
        lines.indices
            .flatMap {  r ->
                lines[r].indices.mapNotNull { c ->
                    val char = lines[r][c]
                    if (char == v || char == gt)
                        char to (r to c)
                    else null
                }
            }
            .groupBy({ it.first }) { it.second }
            .let { it[gt]!!.toSet() to it[v]!!.toSet() }

    fun step(gts: MutableSet<CP>, vs: MutableSet<CP>): Int =
        sequenceOf(gts, vs).sumOf { cps ->
            cps.asSequence()
                .map { (r, c) ->
                    (r to c) to
                        when (cps) {
                            gts -> (r to  (c + 1) % numCols)
                            else -> ((r + 1) % numRows to c)
                        }
                }
                .filter { (_, to) -> to !in gts && to !in vs }
                .toList()
                .map { (from, to) ->
                    cps.remove(from)
                    cps.add(to)
                }
                .count()
        }

    val gtms = gts.toMutableSet()
    val vms = vs.toMutableSet()

    println(
        generateSequence(1) {
            if (step(gtms, vms) > 0) it + 1
            else null
        }.last()
    )
}
