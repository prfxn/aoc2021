#!/usr/bin/env kscript

/**
 *  https://adventofcode.com/2021/day/6
 *
 *  Find a way to simulate lanternfish. How many lanternfish would there be after 80 days?
 *
 */

val countsByDaysUntilRepro =
    System.`in`.reader().readText().trim().split(",")
        .map { it.toInt() }
        .groupBy { it }
        .map { (daysUntilRepro, instances) ->
            daysUntilRepro to instances.size.toLong()
        }
        .toMap()
        .toMutableMap()


fun processDay() {
    val count0 = countsByDaysUntilRepro[0] ?: 0
    for (daysUntilRepro in (1..8))
        countsByDaysUntilRepro[daysUntilRepro - 1] = countsByDaysUntilRepro[daysUntilRepro] ?: 0
    countsByDaysUntilRepro[8] = count0
    countsByDaysUntilRepro[6] = (countsByDaysUntilRepro[6] ?: 0) + count0
}

repeat(80) { processDay() }

println(countsByDaysUntilRepro.values.sum())
