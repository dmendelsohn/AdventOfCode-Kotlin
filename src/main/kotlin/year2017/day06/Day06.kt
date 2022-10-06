package year2017.day06

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

fun parseInput(rawInput: String): List<Int> {
    return rawInput.split("\\s+".toRegex()).map { it.toInt() }
}

fun reallocate(banks: List<Int>): List<Int> {
    val numBlocks = banks.maxOrNull()!!
    val bankToReallocate = banks.indexOf(numBlocks)
    val mutableBanks = banks.toMutableList()
    mutableBanks[bankToReallocate] = 0
    (1..numBlocks).forEach { mutableBanks[(bankToReallocate + it) % banks.size] += 1 }
    return mutableBanks.toList()
}


private data class Result(val totalSteps: Int, val loopLength: Int)

private fun reallocateUntilLoop(banks: List<Int>): Result {
    // Maps states to the most recent move # after which that state was seen
    val statesSeen = mutableMapOf<List<Int>, Int>()
    var numMoves = 0
    var currentBanks = banks
    while (!statesSeen.keys.contains(currentBanks)) {
        statesSeen[currentBanks] = numMoves
        numMoves += 1
        currentBanks = reallocate(currentBanks)
    }
    return Result(numMoves, numMoves - statesSeen.getValue(currentBanks))
}

fun part1(input: List<Int>): Any {
    return reallocateUntilLoop(input).totalSteps
}

fun part2(input: List<Int>): Any {
    return reallocateUntilLoop(input).loopLength
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
