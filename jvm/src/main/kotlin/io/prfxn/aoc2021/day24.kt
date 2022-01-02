//  Arithmetic Logic Unit (https://adventofcode.com/2021/day/24)

package io.prfxn.aoc2021

import kotlin.math.absoluteValue
import kotlin.math.log

fun main() {
    fun parseInstruction(str: String) =
        str.split(" ").let {
            if (it.size == 3) Triple(it[0], it[1], it[2])
            else Triple(it[0], it[1], "")
        }

    fun processInstruction(state: Map<String, Int>,
                           instruction: Triple<String, String, String>,
                           read: () -> Int): Map<String, Int> =
        instruction.let { (cmd, dest, op2) ->
            val a = state[dest] ?: 0
            val b = op2.toIntOrNull() ?: state[op2] ?: 0
            state + (
                dest to
                    when (cmd) {
                        "inp" -> read()
                        "add" -> a + b
                        "mul" -> a * b
                        "div" -> a / b
                        "mod" -> a % b
                        "eql" -> if (a == b) 1 else 0
                        else -> fail()
                    }
            )
        }

    fun processInstructions(state: Map<String, Int>,
                            instructions: List<Triple<String, String, String>>,
                            read: () -> Int): Map<String, Int> =
            instructions.fold(state) { s, i -> processInstruction(s, i, read) }


    fun numBase26Digits(decimal: Int) =
        decimal.absoluteValue.takeIf { it != 0 }?.let { log(it.toFloat(), 26F).toCeilInt() } ?: 0

    // see explanation below
    fun nextValidStateOrNull(state: Map<String, Int>, chunk: List<Triple<String, String, String>>, input: Int): Map<String, Int>? =
        processInstructions(state, chunk) { input }.takeIf { newState ->
            numBase26Digits(newState["z"]!!) - numBase26Digits(state["z"]!!) ==
                    if (chunk[4].third == "1") 1 else -1
        }

    fun findModelNumber(instructions: List<Triple<String, String, String>>, findMax: Boolean = true): String =
        instructions.chunked(instructions.size / 14).let { chunks ->
            var last: Int? = -1
            val stack = mutableListOf<Pair<Int, Map<String, Int>>>()
            var state = mapOf("z" to 0)
            while (stack.size < 14) {
                last =
                    if (last == null && stack.isNotEmpty()) {
                        stack.removeLast().let { (last, lastState) ->
                            val chunk = chunks[stack.size]
                            (if (findMax) (last - 1 downTo 1) else (last + 1 .. 9))
                                .find { i ->
                                    nextValidStateOrNull(lastState, chunk, i)?.also { newState ->
                                        stack.add(i to lastState)
                                        state = newState
                                    } != null
                                }
                        }
                    } else {
                        val chunk = chunks[stack.size]
                        (if (findMax) (9 downTo 1) else (1 .. 9))
                            .find { i ->
                                nextValidStateOrNull(state, chunk, i)?.also { newState ->
                                    stack.add(i to state.toMap())
                                    state = newState
                                } != null
                            }
                    }
            }
            return stack.map { it.first }.joinToString("")
        }

    val instructions =
        textResourceReader("input/24.txt").useLines { it.map(::parseInstruction).toList() }
            .apply { dump(this); println() }

    // answer 1
    println(findModelNumber(instructions))

    // answer 2
    println(findModelNumber(instructions, findMax = false))
}
/** output
 1: inp  w
 2: mul  x  0
 3: add  x  z
 4: mod  x  26
 5: div  z   1,   1,   1,  26,   1,  26,   1,   1,   1,  26,  26,  26,  26,  26
 6: add  x  13,  11,  15, -11,  14,   0,  12,  12,  14,  -6, -10, -12,  -3,  -5
 7: eql  x  w
 8: eql  x  0
 9: mul  y  0
10: add  y  25
11: mul  y  x
12: add  y  1
13: mul  z  y
14: mul  y  0
15: add  y  w
16: add  y  13,  10,   5,  14,   5,  15,   4,  11,   1,  15,  12,   8,  14,   9
17: mul  y  x
18: add  z  y

12934998949199
11711691612189
*/

fun dump(instructions: List<Triple<String, String, String>>) {
    val chunkSize = instructions.size / 14
    instructions
        .mapIndexed { i, ins -> i % chunkSize to ins }
        .groupBy({ (pos, _) -> pos }) { (_, ins) -> ins }
        .forEach { (i, posList) ->
            val (a, b) = posList.first()
            val c = posList.map { it.third }.let {
                val s = it.toSet()
                when {
                    s.isEmpty() -> ""
                    s.size == 1 -> " ${it.first()}"
                    else -> it.map { n -> "%3s".format(n) }.joinToString(", ")
                }
            }
            println("%2d: $a  $b $c".format(i + 1))
        }
}
/*
- The instructions can be split into 14 similar chunks, each made up of 18 instructions
- Most instructions are identical across each chunk, except instructions in positions 5, 6 and 16, where the values
  of the second operand differ across chunks. Let's call these parameters a, b and c respectively
- Upon studying the instructions and working out the state of vars as below,
  1. x, y & w reset for every new chunk processing. x & y are set to 0 while w holds the input for the current chunk.
     The value of z from one chunk's processing feeds into the processing for the next
  2. Every chunk processing results in a new value of z, related to the previous one as -
     new_z =  (z % 26 + b == w) ? z/a : (26 * z/a + w + c)
- The condition (z % 26 + b == w) cannot be satisfied when a = 1, as the corresponding values of b are all above 9 (the
  max possible value of w). For chunks where a = 26, it's possible to statisfy this condition though, by choosing the
  right inputs leading upto such chunks. When the condition is satisfied, z gets divided by 26, but when it's not, z
  gets multiplied by 26 and a value less than 26 (based on values in input and c) is added to it. This is equivalent to
  shifting base26 digits of z left or right respectively
- There's an even split of 1s and 26s for "a" across the chunks. If we ensure shifting z right for all chunks with
  a = 26 we'd have shifted it left and right an equal number of times bringing it back to 0, if it started with 0

 1: inp  w

 2: mul  x  0
 3: add  x  z
x = z

 4: mod  x  26
x = z % 26

 5: div  z   1,   1,   1,  26,   1,  26,   1,   1,   1,  26,  26,  26,  26,  26
z = z/a

 6: add  x  13,  11,  15, -11,  14,   0,  12,  12,  14,  -6, -10, -12,  -3,  -5
x = z % 26 + b

 7: eql  x  w
 8: eql  x  0
x = (z % 26 + b == w) ? 0 : 1

 9: mul  y  0
10: add  y  25
y = 25

11: mul  y  x
12: add  y  1
y = (z % 26 + b == w) ? 1 : 26

13: mul  z  y
z = (z % 26 + b == w) ? z/a : 26 * z/a

14: mul  y  0
15: add  y  w
16: add  y  13,  10,   5,  14,   5,  15,   4,  11,   1,  15,  12,   8,  14,   9
y = w + c

17: mul  y  x
y = (z % 26 + b == w) ? 0 : w + c

18: add  z  y
z = ((z % 26 + b == w) ? z/a : 26 * z/a) + ((z % 26 + b == w) ? 0 : (w + c))
z = (z % 26 + b == w) ? z/a : (26 * z/a + w + c)
*/
