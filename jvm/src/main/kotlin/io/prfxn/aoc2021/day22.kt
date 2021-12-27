//  Reactor Reboot (https://adventofcode.com/2021/day/22)

package io.prfxn.aoc2021

import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Cuboid(val xr: IntRange, val yr: IntRange, val zr: IntRange) {
        private infix fun IntRange.overlap(other: IntRange) = max(first, other.first)..min(last, other.last)
        infix fun intersect(other: Cuboid): Cuboid? =
            listOf(xr overlap other.xr, yr overlap other.yr, zr overlap other.zr)
                .takeIf { it.none(IntRange::isEmpty) }
                ?.let { (nxr, nyr, nzr) -> Cuboid(nxr, nyr, nzr)}
        fun volume() = sequenceOf(xr, yr, zr).map { (it.last - it.first + 1).toLong() }.reduce { p, s -> p * s }
    }

    fun getOnCubesCount(rebootSteps: List<Pair<Boolean, Cuboid>>): Long =
        rebootSteps
            .fold(listOf<Pair<Boolean, Cuboid>>()) { stateAndCuboids, (turnOn, cuboid) ->
                stateAndCuboids +
                        stateAndCuboids.mapNotNull { (s, c) -> c.intersect(cuboid)?.let { !s to it }  } +
                        (if (turnOn) listOf(true to cuboid) else listOf())
            }
            .sumOf { (turnedOn, cuboid) -> cuboid.volume() * if(turnedOn) 1 else -1 }

    val rebootSteps =
        textResourceReader("input/22.txt").useLines { lines ->
            val rgx = "-?\\d+".toRegex()
            lines.map { line ->
                rgx.findAll(line).map { it.value.toInt() }.chunked(2).map { (a, b) -> (min(a, b)..max(a,b)) }
                    .toList().let { (xr, yr, zr) ->
                        line.startsWith("on ") to Cuboid(xr, yr, zr)
                    }
            }.toList()
        }

    // answer 1
    val limitCuboid = Cuboid(-50..50, -50..50, -50..50)
    println(getOnCubesCount(rebootSteps.mapNotNull { (b, c) -> c.intersect(limitCuboid)?.let { b to it } }))

    // answer 2
    println(getOnCubesCount(rebootSteps))
}
