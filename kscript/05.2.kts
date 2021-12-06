#!/usr/bin/env kscript

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 *  https://adventofcode.com/2021/day/5#part2
 *
 *  Consider all of the lines. At how many points do at least two lines overlap?
 *
 */

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

System.`in`.reader().useLines { lineSeq ->
    lineSeq
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
}

println(visitedMulti.size)
