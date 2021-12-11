// Syntax Scoring (https://adventofcode.com/2021/day/10)

package io.prfxn.aoc2021

fun main() {

    val lines = textResourceReader("input/10.txt").readLines()

    val o2c = "()[]{}<>".chunked(2).associate { it.first() to it.last() }
    val c2o = o2c.entries.associate { it.value to it.key }
    val seS = ")]}>".asSequence().zip(sequenceOf(3, 57, 1197, 25137)).toMap()
    val acS = "([{<".mapIndexed { i, c -> c to  i + 1 }.toMap()

    fun seScore(s: String): Int {
        val stack = ArrayDeque<Char>()
        for (c in s)
            if (c in o2c.keys)
                stack.add(c)
            else if (c in c2o.keys)
                if (stack.last() != c2o[c])
                    return seS[c]!!
                else
                    stack.removeLast()
        return 0
    }

    val answer1 = lines.sumOf(::seScore)


    fun acScore(s: String): Long {
        val stack = ArrayDeque<Char>()
        for (c in s)
            if (c in o2c.keys)
                stack.add(c)
            else if (c in c2o.keys)
                if (stack.last() == c2o[c])
                    stack.removeLast()
                else
                    fail("syntax error")

        return stack.foldRight(0L) { c, acScore -> (acScore * 5) + acS[c]!! }
    }

    val answer2 =
        lines
            .filter { seScore(it) == 0 }
            .map(::acScore)
            .sorted()
            .let { it[it.size / 2] }

    sequenceOf(answer1, answer2).forEach(::println)
}

/** output
 * 318099
 * 2389738699
 */
