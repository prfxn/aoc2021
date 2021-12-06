#!/usr/bin/env python3

"""
https://adventofcode.com/2021/day/1

How many measurements are larger than the previous measurement?

"""

import sys

if __name__ == "__main__":
    prev = None
    count = 0
    for line in sys.stdin:
        curr = int(line)
        if prev is not None and curr > prev:
            count += 1
        prev = curr
    print(count)

