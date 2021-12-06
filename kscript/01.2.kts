#!/usr/bin/env kscript

/**
 *  https://adventofcode.com/2021/day/1#part2
 *
 *  How many sums are larger than the previous sum?
 *
 */

System.`in`.reader().useLines { lineSeq ->
    println(
        lineSeq
            .map { it.toInt() }
            .windowed(3)
            .map { it.sum() }
            .windowed(2)
            .count { (prev, next) -> next > prev }
    )
}
