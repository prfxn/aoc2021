//  Passage Pathing (https://adventofcode.com/2021/day/12)

package io.prfxn.aoc2021

fun main() {

    val connections = textResourceReader("input/12.txt").readLines().map { ln ->
        with (ln.split("-")) {
            first() to last()
        }
    }

    fun getNumPaths(pathSoFar: MutableList<String>, destination: String, nextFilter: (List<String>, String) -> Boolean): Int {

        val last = pathSoFar.last()
        if (last == destination) return 1

        return connections.asSequence()
            .map { c ->
                when (last) {
                    c.first -> c.second
                    c.second -> c.first
                    else -> null
                }
            }
            .filterNotNull()
            .filter { next -> nextFilter(pathSoFar, next) }
            .sumOf { next ->
                pathSoFar.add(next)
                getNumPaths(pathSoFar, destination, nextFilter)
                    .also { pathSoFar.removeLast() }
            }
    }

    fun getNumPaths(start: String, end: String, nextFilter: (List<String>, String) -> Boolean) =
        getNumPaths(mutableListOf(start), end, nextFilter)

    val answer1 =
        getNumPaths("start", "end") { pathSoFar, next ->
            when (next) {
                next.lowercase() -> next !in pathSoFar
                else -> true
            }
        }

    val answer2 =
        getNumPaths("start", "end") { pathSoFar, next ->
            when (next) {
                "start" -> false
                next.lowercase() -> {
                    next !in pathSoFar ||
                        pathSoFar.filter { it.lowercase() == it }.groupBy { it }.all { it.value.size < 2 }
                }
                else -> true
            }
        }

    sequenceOf(answer1, answer2).forEach(::println)
}

/** output
 * 4304
 * 118242
 */
