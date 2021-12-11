//  Hydrothermal Venture (https://adventofcode.com/2021/day/5)

package io.prfxn.aoc2021

import kotlin.math.min
import kotlin.math.max
import kotlin.math.abs

fun main() {

    val lines = textResourceReader("input/05.txt").readLines()

    fun part1() {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val visitedMulti = mutableSetOf<Pair<Int, Int>>()

        fun process(x: Int, y: Int,) {
            val xy = x to y
            if (xy !in visitedMulti)
                if (xy in visited)
                    visitedMulti.add(xy)
                else
                    visited.add(xy)
        }

        lines
            .forEach { line ->
                val (x1, y1, x2, y2) =
                    line.split(" -> ").map { it.split(",") }.flatten().map { it.toInt() }

                when  {
                    (x1 == x2) ->
                        (min(y1, y2)..max(y1, y2)).forEach { y -> process(x1, y) }

                    (y1 == y2) ->
                        (min(x1, x2)..max(x1, x2)).forEach { x -> process(x, y1) }
                }
            }
        println(visitedMulti.size)
    }

    fun part2() {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val visitedMulti = mutableSetOf<Pair<Int, Int>>()

        fun process(x: Int, y: Int) {
            val xy = x to y
            if (xy !in visitedMulti)
                if (xy in visited)
                    visitedMulti.add(xy)
                else
                    visited.add(xy)
        }


        lines
            .forEach { line ->
                    val (x1, y1, x2, y2) =
                        line.split(" -> ").map { it.split(",") }.flatten().map { it.toInt() }

                    when  {
                        (x1 == x2) -> {
                            (min(y1, y2)..max(y1, y2)).forEach { y -> process(x1, y)  }
                        }

                        (y1 == y2) -> {
                            (min(x1, x2)..max(x1, x2)).forEach { x -> process(x, y1) }
                        }

                        (abs(y2 - y1) == abs(x2 -x1)) -> {
                            val xStep = if (x2 > x1) 1 else -1
                            val yStep = if (y2 > y1) 1 else -1
                            var x = x1
                            var y = y1
                            while (true) {
                                process(x, y)
                                if (x == x2) break
                                x += xStep
                                y += yStep
                            }
                        }
                    }
                }

        println(visitedMulti.size)
    }

    part1()
    part2()
}

/** output
 * 4421
 * 18674
 */
