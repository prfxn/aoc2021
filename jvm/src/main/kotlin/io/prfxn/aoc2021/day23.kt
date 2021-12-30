//  Amphipod (https://adventofcode.com/2021/day/23)

package io.prfxn.aoc2021


fun main() {

    val pods = ('A'..'D').toList()
    val energiesByPod = pods.asSequence().zip(generateSequence(1) { it * 10 }).toMap()

    fun readInput(lines: Sequence<String>): Map<CP, Char> {
        val coi = pods.toSet() + '.'
        return lines
            .flatMapIndexed { r, line ->
                line.mapIndexedNotNull { c, char ->
                    if (char in coi)
                        (r to c) to char
                    else null
                }
            }
            .toMap()
    }

    val startState = textResourceReader("input/23.txt").useLines { lines -> readInput(lines) }

    val roomCellsByPod =
        pods.asSequence().zip(
            startState.keys
                .groupBy { (_, c) -> c }
                .asSequence()
                .mapNotNull { (_, cells) ->
                    if (cells.size == 3)
                        cells.sortedBy { it.first }.drop(1)
                    else null
                }
        ).toMap()

    val roomCells = roomCellsByPod.values.flatten().toSet()
    val entrywayCells = startState.keys.filter { (r, c) -> (r to c) !in roomCells &&  ((r + 1) to c) in roomCells }.toSet()
    val hallwayCells = startState.keys.filter { it !in roomCells && it !in entrywayCells }.toSet()

    fun printState(state: Map<CP, Char> ) {
        println("#############")
        println("#${(1..11).map { state[1 to it] }.joinToString("")}#")
        println("###" + pods.map { state[roomCellsByPod[it]!![0]] }.joinToString("#") + "###")
        println("  #" + pods.map { state[roomCellsByPod[it]!![1]] }.joinToString("#") + "#")
        println("  #########")
    }

    fun getPath(state: Map<CP, Char>, start: CP, end: CP): List<CP>? {
        val (r1, c1) = start
        val (r2, c2) = end
        val rr = if (r1 > r2) r1.downTo(r2) else (r1..r2)
        val cr = if (c1 > c2) c1.downTo(c2) else (c1..c2)
        return sequenceOf(
            rr.filter { it != r2 }.map { r -> r to c1 } + cr.map { c -> r2 to c },
            cr.filter { it != c2 }.map { c -> r1 to c } + rr.map { r -> r to c2 }
        ).find { p -> p.drop(1).all { cell -> state[cell] == '.' } }
    }

    fun getLeastEnergy(startState: Map<CP, Char>, endState: Map<CP, Char>): Int {

        fun allowRoomExit(state: Map<CP, Char>, cell: CP): Boolean {
            val pod = state[cell]!!
            val podRoomCells = roomCellsByPod[pod]!!
            return cell !in podRoomCells ||
                    cell == podRoomCells[0] && state[podRoomCells[1]] != pod
        }

        fun allowRoomEntry(state: Map<CP, Char>, cell: CP, pod: Char): Boolean {
            val podRoomCells = roomCellsByPod[pod]!!
            return if (cell == podRoomCells[0])
                state[podRoomCells[1]] == pod
            else
                cell == podRoomCells[1]
        }

        val energyAndPrevOf =
            PriorityMap(sequenceOf(startState to (0 to startState))) { (a, _), (b, _) -> a - b}
        val explored = mutableSetOf<Map<CP, Char>>()

        // get unexplored next states and corresponding transition energies
        fun getNextFrom(state: Map<CP, Char>): Sequence<Pair<Map<CP, Char>, Int>> =
            (
                // region room to room
                roomCells.asSequence()
                    .filter { state[it] in pods && allowRoomExit(state, it) }
                    .flatMap { start ->
                        val pod = state[start]!!
                        roomCells.asSequence().filter { state[it] == '.' && allowRoomEntry(state, it, pod) }
                            .map { end -> Triple(pod, start, end) }
                    } /* endregion */ +
                // region hallway to room
                hallwayCells.asSequence()
                    .filter { state[it] in pods }
                    .flatMap { start ->
                        val pod = state[start]!!
                        roomCells.asSequence()
                            .filter { state[it] == '.' && allowRoomEntry(state, it, pod) }
                            .map { end -> Triple(pod, start, end) }
                    } /* endregion */ +
                // region room to hallway
                roomCells.asSequence()
                    .filter { state[it] in pods && allowRoomExit(state, it) }
                    .flatMap { start ->
                        val pod = state[start]!!
                        hallwayCells.asSequence()
                            .filter { state[it] == '.' }
                            .map { end -> Triple(pod, start, end) }
                    } /* endregion */
            )
                .mapNotNull { (pod, start, end) ->
                    getPath(state, start, end)?.let { path ->
                        (state + mapOf(start to '.', end to pod))
                            .takeIf { it !in explored }
                            ?.let { nextState ->
                                nextState to (path.size - 1) * energiesByPod[pod]!!
                            }
                    }
                }

        return generateSequence {
            if (endState in explored) null
            else {
                val (here, energyAndPrev) = energyAndPrevOf.extract()
                val (energy, _) = energyAndPrev
                getNextFrom(here).forEach { (nextState, transitionEnergy) ->
                    val nextEnergy = energy + transitionEnergy
                    if (here == startState) {
                        println("$energy + $transitionEnergy = $nextEnergy")
                        printState(nextState)
                    }
                    if (nextState !in energyAndPrevOf || nextEnergy < energyAndPrevOf[nextState]!!.first)
                        energyAndPrevOf[nextState] = nextEnergy to here
                }
                explored.add(here)
                energy
            }
        }/*.onEach(::println)*/.last()
    }

    val testState =
        readInput(
            """
            #############
            #...........#
            ###A#D#A#B###
              #B#C#D#C#
              #########
            """.trimIndent().splitToSequence("\n")
        )

    val endState = (
            (hallwayCells.asSequence() + entrywayCells.asSequence()).map { it to '.' } +
                    roomCellsByPod.asSequence().flatMap { (pod, cells) ->
                        cells.asSequence().map { cell -> cell to pod }
                    }
        ).toMap()

    println(getLeastEnergy(startState, endState))
}
