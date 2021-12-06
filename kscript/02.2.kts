#!/usr/bin/env kscript

/**
 *  https://adventofcode.com/2021/day/2#part2
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
        .fold(Triple(0, 0 ,0)) { (aim, depth, distance), (key, value) ->
            when (key) {
                "forward" -> Triple(aim, depth + (aim * value), distance + value)
                "down" -> Triple(aim + value, depth, distance)
                "up" -> Triple(aim - value, depth, distance)
                else -> throw IllegalArgumentException()
            }
        }
        .let { (_, depth, distance) ->
            println(depth * distance)
        }
}
