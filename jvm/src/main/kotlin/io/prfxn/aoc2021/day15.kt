//  Chiton (https://adventofcode.com/2021/day/15)

package io.prfxn.aoc2021

fun main() {

    fun getMinRisk(rlm: List<List<Int>>, start: CP, end: CP): Int {

        val riskAndPrevOf = PriorityMap(sequenceOf(start to (0 to start))) { (a, _), (b, _) -> a - b }

        val visited = mutableSetOf<CP>()

        fun getUnvisitedNeighbors(cp: CP) =
            cp.let { (r, c) ->
                sequenceOf(r - 1 to c, r + 1 to c, r to c - 1, r to c + 1)
                    .filter { (r, c) -> r in rlm.indices && c in rlm[0].indices }
                    .filterNot(visited::contains)
            }

        return generateSequence {
            if (end in visited) null
            else {
                val (here, riskAndVia) = riskAndPrevOf.extract()
                val (riskToHere, _) = riskAndVia
                getUnvisitedNeighbors(here).forEach { next ->
                    val (nr, nc) = next
                    val riskToNextViaHere = riskToHere + rlm[nr][nc]
                    if (next !in riskAndPrevOf || riskToNextViaHere < riskAndPrevOf[next]!!.first)
                        riskAndPrevOf[next] = riskToNextViaHere to here
                }
                visited.add(here)
                riskToHere
            }
        }.last()
    }

    fun getMinRisk(rlm: List<List<Int>>) = getMinRisk(rlm, 0 to 0, (rlm.size - 1) to (rlm[0].size - 1))

    val rlm1 = textResourceReader("input/15.txt").readLines()
        .map { ln -> ln.map { it.digitToInt() } }

    val answer1 = getMinRisk(rlm1)

    val rlm2  =
        (0 until rlm1.size * 5).map { r2 ->
            val tr = r2 / rlm1.size
            val r = r2 % rlm1.size
            (0 until rlm1[0].size * 5).map { c2 ->
                val tc = c2 / rlm1[0].size
                val c = c2 % rlm1[0].size
                ((rlm1[r][c] - 1 + tr + tc) % 9) + 1
            }
        }

    val answer2 = getMinRisk(rlm2)

    sequenceOf(answer1, answer2).forEach(::println)
}

/** output
595
2914
*/
