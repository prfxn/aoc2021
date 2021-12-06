#!/usr/bin/env kscript

/**
 *  https://adventofcode.com/2021/day/2
 *
 *  What do you get if you multiply your final horizontal position by your final depth?
 *
 */

System.`in`.reader().useLines { lineSeq ->
    lineSeq
        .map { line ->
            line.split(" ").let {
                it[0] to it[1].toInt()
            }
        }
        .fold(0 to 0) { (depth, distance), (key, value) ->
            when (key) {
                "forward" -> depth to (distance + value)
                "down" -> (depth + value) to distance
                "up" -> (depth - value) to distance
                else -> throw IllegalArgumentException()
            }
        }
        .let { (depth, distance) ->
            println(depth * distance)
        }
}
