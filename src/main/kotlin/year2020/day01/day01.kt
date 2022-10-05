package year2020.day01

import java.io.File

private fun getInput(): List<Int> {
    return File("inputs/day01.txt").readLines().map { it.toInt() }
}

private fun part1(): Any {
    val numbers = getInput().toSet()
    for (number in numbers) {
        if (numbers.contains(2020 - number)) {
            return number * (2020 - number)
        }
    }
    return "No solution"
}

private fun part2(): Any {
    val numbers = getInput().toSet()
    for (first in numbers) {
        for (second in numbers) {
            if (numbers.contains(2020 - first - second)) {
                return first * second * (2020 - first - second)
            }
        }
    }
    return "No solution"
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
