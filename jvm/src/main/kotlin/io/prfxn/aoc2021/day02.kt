//  Dive! (https://adventofcode.com/2021/day/2)

package io.prfxn.aoc2021

fun main() {

    val lines = textResourceReader("input/02.txt").useLines { ls ->
        ls.map { ln ->
            ln.split(" ").let {
                it[0] to it[1].toInt()
            }
        }.toList()
    }

    val answer1 = lines
        .fold(0 to 0) { (depth, distance), (key, value) ->
            when (key) {
                "forward" -> depth to (distance + value)
                "down" -> (depth + value) to distance
                "up" -> (depth - value) to distance
                else -> throw IllegalArgumentException()
            }
        }
        .let { (depth, distance) -> depth * distance }

    val answer2 = lines
        .fold(Triple(0, 0 ,0)) { (aim, depth, distance), (key, value) ->
            when (key) {
                "forward" -> Triple(aim, depth + (aim * value), distance + value)
                "down" -> Triple(aim + value, depth, distance)
                "up" -> Triple(aim - value, depth, distance)
                else -> throw IllegalArgumentException()
            }
        }
        .let { (_, depth, distance) -> depth * distance }

    sequenceOf(answer1, answer2).forEach(::println)
}

/** output
 * 1815044
 * 1739283308
 */
