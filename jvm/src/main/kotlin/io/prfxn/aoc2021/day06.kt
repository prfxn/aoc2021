//  Lanternfish (https://adventofcode.com/2021/day/6)

package io.prfxn.aoc2021

fun main() {

    val initialCountsByDaysUntilRepro =
        textResourceReader("input/06.txt").readText().trim().split(",")
            .map { it.toInt() }
            .groupBy { it }
            .map { (daysUntilRepro, instances) ->
                daysUntilRepro to instances.size.toLong()
            }
            .toMap()

    fun processDay(countsByDaysUntilRepro: MutableMap<Int, Long>) {
        val count0 = countsByDaysUntilRepro[0] ?: 0
        for (daysUntilRepro in (1..8))
            countsByDaysUntilRepro[daysUntilRepro - 1] = countsByDaysUntilRepro[daysUntilRepro] ?: 0
        countsByDaysUntilRepro[8] = count0
        countsByDaysUntilRepro[6] = (countsByDaysUntilRepro[6] ?: 0) + count0
    }

    val answer1 = initialCountsByDaysUntilRepro.toMutableMap().let {  countsByDaysUntilRepro ->
        repeat(80) { processDay(countsByDaysUntilRepro) }
        countsByDaysUntilRepro.values.sum()
    }

    val answer2 = initialCountsByDaysUntilRepro.toMutableMap().let {  countsByDaysUntilRepro ->
        repeat(256) { processDay(countsByDaysUntilRepro) }
        countsByDaysUntilRepro.values.sum()
    }

    sequenceOf(answer1, answer2).forEach(::println)
}

/** output
 * 388739
 * 1741362314973
 */
