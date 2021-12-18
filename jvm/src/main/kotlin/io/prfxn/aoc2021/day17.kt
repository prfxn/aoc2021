package io.prfxn.aoc2021

import kotlin.math.max

fun main() {

    val (targetXRange, targetYRange) =
        "-?\\d+".toRegex().findAll(textResourceReader("input/17.txt").readText())
            .map { it.value.toInt() }
            .let {
                val (x1, x2, y1, y2) = it.toList()
                (x1..x2) to (y1..y2)
            }

    fun bullseye(x: Int, y: Int): Boolean = x in targetXRange && y in targetYRange
    fun missed(x: Int, y: Int) = x > targetXRange.last || y < targetYRange.first

    fun stepSequence(xv0: Int, yv0: Int) =
        generateSequence((0 to 0) to (xv0 to yv0)) { (pos, vel) ->
            val (x, y) = pos
            val (xv, yv) = vel
            (x + xv to y + yv) to
                (max(xv - 1, 0) to yv - 1)
        }

    fun getMaxY(xv0: Int, yv0: Int): Int? =
        with (stepSequence(xv0, yv0)) {
            var maxY = 0
            find { (pos, vel) ->
                val (x, y) = pos
                maxY = max(maxY, y)
                if (missed(x, y)) return@with null
                bullseye(x, y)
            }
            maxY
        }

    // judgement call on those ranges
    
    println(  // answer 1
        (0..1000).flatMap { x -> (-1000..1000).map { y -> x to y } }
            .mapNotNull { (x, y) -> getMaxY(x, y) }
            .maxOf { it }
    )

    println(  // answer 2
        (0..1000).flatMap { x -> (-1000..1000).map { y -> x to y } }
            .mapNotNull { (x, y) -> getMaxY(x, y) }
            .count()
    )
}
