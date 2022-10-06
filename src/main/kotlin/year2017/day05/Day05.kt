package year2017.day05

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

fun parseInput(rawInput: String): List<Int> {
    return rawInput.split("\n").map { it.toInt() }
}

/** Run the program with the given modifier function, returning number of steps to terminate */
fun runProgram(program: List<Int>, modifier: (Int) -> Int): Int {
    var currentIdx = 0
    var numSteps = 0
    val mutableProgram = program.toMutableList()
    while (currentIdx in mutableProgram.indices) {
        val jumpValue = mutableProgram[currentIdx]
        mutableProgram[currentIdx] = modifier(jumpValue)
        currentIdx += jumpValue
        numSteps++
    }
    return numSteps
}

fun part1(input: List<Int>): Int {
    return runProgram(input) { it + 1 }
}

fun part2(input: List<Int>): Any {
    return runProgram(input) { if (it >= 3) it - 1 else it + 1 }
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
