package io.prfxn.aoc2021

private object Day18 {

    interface SFNum {

        operator fun plus(other: SFNum): SFNum = SFNumPair(this, other)

        fun traverseInPostOrder(depth: Int = 0, parent: SFNumPair? = null): Sequence<Triple<SFNum, Int, SFNumPair?>> =
            if (this is SFNumPair)
                this.left.traverseInPostOrder(depth + 1, this) +
                    this.right.traverseInPostOrder(depth + 1, this) +
                    Triple(this, depth, parent)
            else sequenceOf(Triple(this, depth, parent))

        fun explode(): Boolean {
            var lastRegular = SFNumRegular(0)
            var rightResidue = -1
            for ((sfn, depth, parent) in traverseInPostOrder().toList()) {
                if (rightResidue != -1)
                    if (sfn is SFNumRegular) {
                        sfn.value += rightResidue
                        return true
                    } else continue
                if (depth > 4) continue
                if (sfn is SFNumRegular) lastRegular = sfn
                if (sfn is SFNumPair && depth == 4) {
                    lastRegular.value += (sfn.left as SFNumRegular).value
                    rightResidue = (sfn.right as SFNumRegular).value
                    parent?.apply {
                        SFNumRegular(0).let {
                            if (left == sfn) left = it
                            else right = it
                        }
                    }
                }
            }
            return false
        }

        fun split(): Boolean {
            traverseInPostOrder()
                .toList()
                .forEach { (sfn, _, parent) ->
                    if (sfn is SFNumRegular && sfn.value >= 10) {
                        parent?.apply {
                            val l = SFNumRegular(sfn.value / 2)
                            SFNumPair(l, SFNumRegular(sfn.value - l.value)).let {
                                if (left == sfn) left = it
                                else right = it
                            }
                        }
                        return true
                    }
                }
            return false
        }

        fun reduce() {
            while (true) {
                if (explode()) continue
                if (split()) continue
                break
            }
        }

        fun magnitude(): Int =
            when (this) {
                is SFNumRegular -> value
                is SFNumPair -> 3 * left.magnitude() + 2 * right.magnitude()
                else -> fail()
            }
    }

    data class SFNumRegular(var value: Int): SFNum { override fun toString() = value.toString() }
    data class SFNumPair(var left: SFNum, var right: SFNum): SFNum { override fun toString() = "[$left,$right]" }

    fun parseSFNum(s: String, start: Int, end: Int): SFNum =
        if (s[start] == '[') {   // parse pair
            val mid = with ((start..end)) {
                var depth = 0
                find { i ->
                    when (s[i]) {
                        ',' -> depth == 1
                        '[' -> { depth++; false }
                        ']' -> { depth--; false}
                        else -> false
                    }
                }!!
            }
            SFNumPair(
                parseSFNum(s, start + 1, mid - 1),
                parseSFNum(s, mid + 1, end - 1)
            )
        } else {  // parse regular
            SFNumRegular((start..end).map(s::get).joinToString("").toInt())
        }

    fun parseSFNum(s: String) = parseSFNum(s, 0, s.length - 1)

}

fun main() {

        val lines = textResourceReader("input/18.txt").readLines()

        var totalSfn: Day18.SFNum? = null
        lines.map(Day18::parseSFNum).forEach { sfn ->
            totalSfn = totalSfn?.let { (it + sfn).apply { reduce() } } ?: sfn.apply { reduce() }
        }
        println(totalSfn?.magnitude())

        val maxSfnMagnitude =
            lines.permutations(2).map {
                val (a, b) = it.map(Day18::parseSFNum)
                (a + b).apply { reduce() }.magnitude()
            }.maxOf { it }
        println(maxSfnMagnitude)
}
