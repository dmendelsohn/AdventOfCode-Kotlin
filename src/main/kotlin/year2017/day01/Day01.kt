package year2017.day01

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

fun parseInput(rawInput: String): List<Int> {
    return rawInput.map { it.toString().toInt() }
}

fun part1(input: List<Int>): Int {
    return (input + input.first()).zipWithNext().sumOf { if (it.first == it.second) it.first else 0 }
}

fun part2(input: List<Int>): Int {
    val firstHalf = input.take(input.size / 2)
    val secondHalf = input.drop(input.size / 2)
    check(firstHalf.size == secondHalf.size)
    return 2 * firstHalf.zip(secondHalf).sumOf { if (it.first == it.second) it.first else 0 }
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
