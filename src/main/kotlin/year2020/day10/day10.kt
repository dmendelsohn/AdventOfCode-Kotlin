package year2020.day10

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.split(".").last()}.txt"

private fun getInput(): List<Int> {
    return File(INPUT_PATH).readLines().map { it.toInt() }
}

private fun part1(): Int {
    val joltages = getInput().let { listOf(0) + it.sorted() + (it.maxOrNull()!! + 3) }
    val jumps = joltages.zipWithNext { a, b -> b - a }
    return jumps.count { it == 1 } * jumps.count { it == 3 }
}

private fun part2(): Any {
    // Element at idx N is the number of arrangements that end with the Nth adapter
    val numWaysToReach = mutableListOf(1L) // By definition, one way to reach start adapter
    val joltages = getInput().let { listOf(0) + it.sorted() + (it.maxOrNull()!! + 3) }

    joltages.indices.drop(1).forEach { index ->
        // It may be possible to reach this joltage from any of the previous 3
        val previousIndices = (index - 3).coerceAtLeast(0) until index
        // For the indices whose adapter is close enough to current adapter, sum num ways to reach that adapter
        previousIndices.filter { joltages[it] >= joltages[index] - 3 }.sumOf { numWaysToReach[it] }
            .let { numWaysToReach.add(it) }
    }

    return numWaysToReach.last()
}


fun main(args: Array<String>) {
    println(INPUT_PATH)
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
