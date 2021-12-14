//  Extended Polymerization (https://adventofcode.com/2021/day/14)

package io.prfxn.aoc2021

fun main() {

    val lines = textResourceReader("input/14.txt").readLines()

    val template = lines.first()
    val rules = lines.drop(2).map {
        with (it.split(" -> ")) { first() to last() }
    }

    val spc = template.windowed(2).groupBy { it }.map { (k, v) -> k to v.size.toLong() }.toMap()  // starting pair counts
    val sec = template.groupBy(Char::toString).map { (k, v) -> k to v.size.toLong() }.toMap()  // starting element counts

    fun maxMinCountsDiffAfterSteps(steps: Int) =
        with (
            generateSequence(spc to sec) { (pc, ec) ->
                rules.fold(pc to ec) { (npc, nec), (p, e) ->
                    pc[p]?.takeIf { it > 0 }
                        ?.let { n ->
                            val (p1, p2) = (p[0] + e + p[1]).windowed(2)
                            sequenceOf(
                                p to -n,
                                p1 to n,
                                p2 to n
                            ).fold(npc) { npc, (p, d) ->
                                npc + mapOf(p to (npc[p] ?: 0) + d)
                            }  to
                                    nec + mapOf(e to (nec[e] ?: 0) + n)
                        }
                        ?: (npc to nec)
                }
            }.drop(1).take(steps).last().second.values
        ) {
            maxOf { it } - minOf { it }
        }

    val answer1 = maxMinCountsDiffAfterSteps(10)

    val answer2 = maxMinCountsDiffAfterSteps(40)

    sequenceOf(answer1, answer2).forEach(::println)
}

/** output
2703
2984946368465
*/
