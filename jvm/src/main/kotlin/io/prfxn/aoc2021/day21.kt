//  Dirac Dice (https://adventofcode.com/2021/day/21)

package io.prfxn.aoc2021

import kotlin.math.max
import kotlin.math.min

private typealias PositionAndScore = Pair<Int, Int>

fun main() {
    val (p1sp, p2sp) = textResourceReader("input/21.txt").readLines().map { it.split(":").last().trim().toInt() }
    val rollDice = generateSequence(0) { (it + 1) % 100 }.map(Int::inc).iterator()::next
    fun rollDiceThrice() = generateSequence { rollDice() }.take(3).sum()
    fun addToPosition(pos: Int, amt: Int) = 1 + ((pos - 1) + amt) % 10

    // answer 1
    val (turnCount, p1s, p2s) =
        generateSequence(0 to listOf((p1sp to 0), (p2sp to 0))) { (currentPlayerIndex, players) ->
            (currentPlayerIndex + 1) % players.size to
                    players.mapIndexed { i, player ->
                        if (i == currentPlayerIndex) {
                            val (pos, score) = player
                            val newPos = addToPosition(pos, rollDiceThrice())
                            (newPos to score + newPos)
                        }
                        else player
                    }
        }
            .mapIndexed { i, (_, players) -> Triple(i, players.first().second, players.last().second) }
            .find { (_, s1, s2) -> s1 >= 1000 || s2 >= 1000 }!!

    println(3 * turnCount * min(p1s, p2s))

    // answer 2
    val diracDiceThreeRollOutcomes =
        (1..3).flatMap { one -> (1..3).flatMap { two -> (1..3).map { three -> one + two + three } } }

    fun countWins(currentPlayerIndex: Int,
                  players: List<PositionAndScore>,
                  resultCache: MutableMap<Pair<Int, List<PositionAndScore>>, Pair<Long, Long>> = mutableMapOf()
    ): Pair<Long, Long> =
        resultCache.getOrPut(currentPlayerIndex to players) {
            when {
                players[0].second >= 21 -> 1L to 0L
                players[1].second >= 21 -> 0L to 1L
                else ->
                    diracDiceThreeRollOutcomes
                        .map { amt ->
                            countWins(
                                (currentPlayerIndex + 1) % players.size,
                                players.mapIndexed { i, player ->
                                    if (i == currentPlayerIndex) {
                                        val (pos, score) = player
                                        val newPos = addToPosition(pos, amt)
                                        (newPos to score + newPos)
                                    }
                                    else player
                                },
                                resultCache
                            )
                        }
                        .reduce { (totalWins1, totalWins2), (wins1, wins2) ->
                            (totalWins1 + wins1) to (totalWins2 + wins2)
                        }
            }
        }

    fun countWins(p1sp: Int, p2sp: Int) = countWins(0, listOf(p1sp to 0, p2sp to 0))

    println(countWins(p1sp, p2sp).let { (wins1, wins2) -> max(wins1, wins2) })
}
