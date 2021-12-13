//  Transparent Origami (https://adventofcode.com/2021/day/13)

package io.prfxn.aoc2021

fun main() {

    val lines = textResourceReader("input/13.txt").readLines()
    val points = lines.takeWhile(String::isNotBlank).map {
        with(it.split(",").map(String::toInt)) { first() to last() }
    }
    val folds = lines.drop(points.size + 1)

    fun processFold(points: List<Pair<Int, Int>>, fold: String): List<Pair<Int, Int>> {
        val axis = fold.split("=").last().toInt()
        val foldAlongX = fold.startsWith("fold along x")

        val lowerHalfPoints = points.filter { (x, y) -> if (foldAlongX) x < axis else y < axis }

        return lowerHalfPoints +
                points
                    .filter { (x, y) -> if (foldAlongX) x > axis else y > axis }
                    .map { (x, y) ->
                        if (foldAlongX)
                             axis - (x - axis) to y
                        else
                            x to axis - (y - axis)
                    }
                    .filter { it !in lowerHalfPoints }
    }

    println(processFold(points, folds.first()).size)            // answer 1

    val finalPoints = folds.fold(points) { p, f -> processFold(p, f) }
    for (y in (0..finalPoints.maxOf { it.second })) {           // answer 2
        for (x in (0..finalPoints.maxOf { it.first }))
            print(if (x to y in finalPoints) "##" else "  ")
        println()
    }
}

/** output
842
######    ########  ##    ##  ######      ####        ####  ########  ##    ##
##    ##  ##        ##  ##    ##    ##  ##    ##        ##        ##  ##    ##
######    ######    ####      ##    ##  ##              ##      ##    ##    ##
##    ##  ##        ##  ##    ######    ##              ##    ##      ##    ##
##    ##  ##        ##  ##    ##  ##    ##    ##  ##    ##  ##        ##    ##
######    ##        ##    ##  ##    ##    ####      ####    ########    ####
*/
// B F K R C J Z U
