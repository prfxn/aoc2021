#!/usr/bin/env kscript

/**
 *  https://adventofcode.com/2021/day/1
 *
 *  How many measurements are larger than the previous measurement?
 *
 */

System.`in`.reader().useLines { lineSeq ->
    println(
        lineSeq
            .map { it.toInt() }
            .windowed(2)
            .count { (prev, next) -> next > prev }
    )
}