/**
 * Implementation notes:
 * - this solution handles the possibility of duplicate input values.
 * - in practice, this doesn't happen in our input, but it was fun to write.
 */
package year2020.day01

import java.io.File

private const val TARGET = 2020
private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText()
}

fun parseInput(rawInput: String): List<Int> {
    return rawInput.trim().split("\n").map { it.toInt() }
}

// Invariant: all values are >= 1
private typealias FrequencyCount = Map<Int, Int>

private fun FrequencyCount.decrement(decrementKey: Int): FrequencyCount {
    return this.mapValues { (key, value) -> if (key == decrementKey) value - 1 else value }.filterValues { it > 0 }
}

fun part1(input: List<Int>): Long {
    val numberCounts = input.groupingBy { it }.eachCount()
    for (number in numberCounts.keys) {
        val remainingNumberCounts = numberCounts.decrement(number)
        val complement = TARGET - number
        if (remainingNumberCounts.contains(complement)) {
            return 1L * number * complement

        }
    }
    return -1
}

fun part2(input: List<Int>): Long {
    val numberCounts = input.groupingBy { it }.eachCount()
    for (first in numberCounts.keys) {
        val remainingCountsAfterFirst = numberCounts.decrement(first)
        for (second in remainingCountsAfterFirst.keys) {
            val remainingCountsAfterSecond = remainingCountsAfterFirst.decrement(second)
            val complement = TARGET - first - second
            if (remainingCountsAfterSecond.contains(complement)) {
                return 1L * first * second * complement
            }
        }
    }
    return -1
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
