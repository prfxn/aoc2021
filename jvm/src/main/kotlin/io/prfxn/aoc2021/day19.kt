//  Beacon Scanner (https://adventofcode.com/2021/day/19)

package io.prfxn.aoc2021

import kotlin.math.abs

private typealias CT = Triple<Int, Int, Int>

fun main() {

    val orientationMappers = // region sequenceOf..
        sequenceOf(
            { t: CT -> t.let { (x, y, z) -> Triple(x, y, z) } },   // (default)
            { t: CT -> t.let { (x, y, z) -> Triple(-x, y, -z) } }, // z -> -z (via x)
            { t: CT -> t.let { (x, y, z) -> Triple(z, y, -x) } },  // z -> x
            { t: CT -> t.let { (x, y, z) -> Triple(-z, y, x) } },  // z -> -x
            { t: CT -> t.let { (x, y, z) -> Triple(x, z, -y) } },  // z -> y
            { t: CT -> t.let { (x, y, z) -> Triple(x, -z, y) } }   // z -> -y
        )
            .flatMap { fn ->
                sequenceOf(
                    { t: CT -> fn(t).let { (x, y, z) -> Triple(x, y, z) } },   // 0 (default)
                    { t: CT -> fn(t).let { (x, y, z) -> Triple(y, -x, z) } },  // 90
                    { t: CT -> fn(t).let { (x, y, z) -> Triple(-x, -y, z) } }, // 180
                    { t: CT -> fn(t).let { (x, y, z) -> Triple(-y, x, z) } }   // 270
                )
            } // endregion

    fun findOriginAndOrientationMapper(wrtS: Set<CT>, ofS: Set<CT>) =
        orientationMappers
            .flatMap { orient ->
                wrtS.flatMap { (x1, y1, z1) ->
                    ofS.map {
                        val (x2, y2, z2) = orient(it)
                        Triple(x1 - x2, y1 - y2, z1 - z2) to orient
                    }
                }
            }
            .find { (s2o, orient) ->
                val (x2o, y2o, z2o) = s2o
                ofS.map(orient)
                    .filter { (x2, y2, z2) -> Triple(x2 + x2o, y2 + y2o, z2 + z2o) in wrtS }
                    .take(12)
                    .count() == 12
            }

    val scanners: MutableList<MutableSet<CT>> =
        textResourceReader("input/19.txt").useLines { lines ->
            mutableListOf<MutableSet<CT>>().apply {
                lines.filter(String::isNotBlank).forEach { line ->
                    if (line.startsWith("--- scanner"))
                        add(mutableSetOf())
                    else
                        last().add(line.split(",").map(String::toInt).let { (a, b, c) -> Triple(a, b, c) })
                }
            }
        }

    val scannerOrigins = mutableMapOf<Int, MutableList<Pair<Int, CT>>>()

    val unmergedScannerIndices = scanners.indices.toMutableSet()
    while (unmergedScannerIndices.size > 1) {
        for (s2i in (scanners.size - 1 downTo 1).filter(unmergedScannerIndices::contains)) {
            ((s2i - 1 downTo 0).filter(unmergedScannerIndices::contains))
                .find { s1i ->
                    val s1 = scanners[s1i]
                    val s2 = scanners[s2i]
                    findOriginAndOrientationMapper(s1, s2)
                        ?.let { (s2o, orient) ->
                            val (x2o, y2o, z2o) = s2o
                            scannerOrigins.getOrPut(s1i) { mutableListOf(s1i to Triple(0, 0, 0)) }
                                .apply {
                                    add(s2i to s2o)
                                    scannerOrigins[s2i]
                                        ?.map { (s3i, s3o) ->
                                            val (x3o, y3o, z3o) = orient(s3o)
                                            s3i to Triple(x3o + x2o, y3o + y2o, z3o + z2o)
                                        }
                                        ?.let {
                                            addAll(it)
                                            scannerOrigins.remove(s2i)
                                        }
                                }
                            s2.forEach {
                                val (x2, y2, z2) = orient(it)
                                s1.add(Triple(x2 + x2o, y2 + y2o, z2 + z2o))
                            }
                            unmergedScannerIndices.remove(s2i)
                            println("scanner $s2i -> scanner $s1i")
                            true
                        }
                        ?: false
                }
        }
    }
    println(scanners.first().size)  // answer 1

    fun manhattanDistance(ct1: CT, ct2: CT) =
        ct1.let { (x1, y1, z1) ->
            ct2.let { (x2, y2, z2) ->
                abs(x1 - x2) + abs(y1 - y2) + abs(z1 - z2)
            }
        }

    val scannerOrigins0 = scannerOrigins[0]!!.map { it.second }
    (0 until scannerOrigins0.size - 1)
        .flatMap { i1 ->
            (i1 + 1 until scannerOrigins0.size)
                .map { i2 -> manhattanDistance(scannerOrigins0[i1], scannerOrigins0[i2]) }
        }
        .maxOf { it }
        .let(::println)  // answer 2
}
