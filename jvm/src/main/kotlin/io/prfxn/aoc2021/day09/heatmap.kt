package io.prfxn.aoc2021.day09

fun main() {

    val basinCoords =
        lowPoints
            .fold(mutableSetOf<Pair<Int, Int>>()) { bc, (r, c) ->
                bc.apply { addAll(crawlBasin(r, c)) }
            }

    for (r in lines.indices) {
        for (c in lines[0].indices)
            if (r to c in basinCoords)
                print("<span class='h${lines[r][c]}'>${lines[r][c]}</span>")
            else
                print(lines[r][c])
        println("<br/>")
    }
}
