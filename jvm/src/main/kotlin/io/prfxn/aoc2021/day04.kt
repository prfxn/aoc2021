//  Giant Squid (https://adventofcode.com/2021/day/4)

package io.prfxn.aoc2021

fun main() {
    val maxRows = 5
    val maxCols = 5

    val (numbersToDraw, boards) =
        textResourceReader("input/04.txt").useLines { lineSeq ->
            val lineIter = lineSeq.iterator()

            val numbersToDraw = lineIter.next().splitToSequence(",").map { it.toInt() }.toList()

            val boards =
                generateSequence {
                    if (lineIter.hasNext()) {
                        lineIter.next()  //  blank line
                        val board = Array(maxRows) { IntArray(maxCols) { 0 } }
                        lineIter.asSequence()
                            .take(maxRows)
                            .forEachIndexed { row, line ->
                                line.trim().split(" +".toRegex()).forEachIndexed { col, s ->
                                    board[row][col] = s.toInt()
                                }
                            }
                        board
                    }
                    else null
                }
                    .toList()

            numbersToDraw to boards
        }

    fun boardScore(board: Array<IntArray>, nextDrawIndex: Int) =
        if (
            (0 until maxRows).any { row ->
                (0 until maxCols).all { col ->
                    board[row][col] in numbersToDraw.asSequence().take(nextDrawIndex)
                }
            } ||
            (0 until maxCols).any { col ->
                (0 until maxRows).all { row ->
                    board[row][col] in numbersToDraw.asSequence().take(nextDrawIndex)
                }
            }
        ) {
            (0 until maxRows).flatMap { row ->
                (0 until maxCols).map { col ->
                    row to col
                }
            }
                .filter { (row, col) ->
                    board[row][col] !in numbersToDraw.asSequence().take(nextDrawIndex)
                }
                .sumOf { (row, col) ->
                    board[row][col]
                } *
                    numbersToDraw[nextDrawIndex - 1]
        }
        else 0

    fun part1() {
        var nextDrawIndex = 0
        var topScore = 0

        while (topScore == 0 && nextDrawIndex < numbersToDraw.size) {
            nextDrawIndex++
            for (board in boards) {
                val score = boardScore(board, nextDrawIndex)
                if (score > topScore) {
                    topScore = score
                }
            }
        }

        println(topScore)
    }

    fun part2() {
        var nextDrawIndex = 0
        val wonBoards = mutableListOf<Array<IntArray>>()

        while (wonBoards.size < boards.size && nextDrawIndex < numbersToDraw.size) {
            nextDrawIndex++
            boards
                .filter { it !in wonBoards }
                .forEach{ board ->
                    val score = boardScore(board, nextDrawIndex)
                    if (score > 0) {
                        wonBoards.add(board)
                        if (wonBoards.size == boards.size) {
                            println(score)
                            return@forEach
                        }
                    }
                }
        }
    }

    part1()
    part2()
}

/** output
 * 41503
 * 3178
 */
