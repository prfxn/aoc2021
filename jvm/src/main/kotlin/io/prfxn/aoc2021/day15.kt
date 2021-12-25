//  Chiton (https://adventofcode.com/2021/day/15)

package io.prfxn.aoc2021

import java.util.PriorityQueue


fun main() {

    fun getMinRisk(rlm: List<List<Int>>, start: CP, end: CP): Int {

        val riskAndPrevOf = mutableMapOf(start to (0 to start))
        val nextPq =
            PriorityQueue<CP> { a, b -> riskAndPrevOf[a]!!.first - riskAndPrevOf[b]!!.first }.apply { add(start) }
        val visited = mutableSetOf<CP>()

        fun getUnvisitedNeighbors(cp: CP) =
            cp.let { (r, c) ->
                sequenceOf(r - 1 to c, r + 1 to c, r to c - 1, r to c + 1)
                    .filter { (r, c) -> r in rlm.indices && c in rlm[0].indices }
                    .filterNot(visited::contains)
            }

        while (end !in visited) {
            val here = nextPq.remove()
            val (riskToHere, _) = riskAndPrevOf[here]!!
            getUnvisitedNeighbors(here).forEach { next ->
                val (nr, nc) = next
                val riskToNextViaHere = riskToHere + rlm[nr][nc]
                val seen = next in riskAndPrevOf
                if (!seen || riskToNextViaHere < riskAndPrevOf[next]!!.first)
                    riskAndPrevOf[next] = riskToNextViaHere to here
                if (!seen) nextPq.add(next)
            }
            visited.add(here)
        }

        return riskAndPrevOf[end]!!.first
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
