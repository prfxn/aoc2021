//  Trench Map (https://adventofcode.com/2021/day/20)

package io.prfxn.aoc2021

fun main() {
    val lines = textResourceReader("input/20.txt").readLines()
    val ieAlgo = lines.take(1).first()
    val litMap =
        lines.drop(2).flatMapIndexed { r, line ->
            line.mapIndexed { c, char ->
                Triple(0, r, c) to (char == '#')
            }
        }.toMap().toMutableMap()

    fun isLit(step: Int, r: Int, c: Int): Boolean =
        Triple(step, r, c).let { key ->
            if (step == 0) litMap[key] ?: false
            else
                litMap.getOrPut(Triple(step, r, c)) {
                    ieAlgo[
                        Integer.parseInt(
                            (r - 1..r + 1).flatMap { r1 ->
                                (c - 1..c + 1).map { c1 ->
                                    if (isLit(step - 1, r1, c1)) "1"
                                    else "0"
                                }
                            }.joinToString(""),
                            2
                        )
                    ] == '#'
                }
        }

    fun count(step: Int, rows: IntRange, cols: IntRange) =
        rows.flatMap { r -> cols.map { c -> isLit(step, r, c) } }.count { it }

    fun Int.toRange() = (this..this)

    fun countLitPixels(numEnhancements: Int): Int {
        fun count(rows: IntRange, cols: IntRange) = count(numEnhancements, rows, cols)
        val rows0 = (-numEnhancements until lines.drop(2).size + numEnhancements)
        val cols0 = (-numEnhancements until lines.drop(2).first().length + numEnhancements)
        return generateSequence(Triple(rows0, cols0, count(rows0, cols0))) { (rows, cols, count) ->
            val rows1 = (rows.first - 1 .. rows.last + 1)
            val cols1 = (cols.first - 1 .. cols.last + 1)
            Triple(rows1, cols1, count +
                    count(rows1.first.toRange(), cols1) + // top row
                    count(rows1.last.toRange(), cols1) +  // bottom row
                    count(rows, cols1.first.toRange()) +  // left col (minus top & bottom rows)
                    count(rows, cols1.last.toRange()))    // right col (minus top & bottom rows)
        }.map { it.third }.windowed(2).find { (c1, c2) -> c1 == c2 }!!.first()
    }

    println(countLitPixels(2))  // answer1
    println(countLitPixels(50)) // answer2
}
