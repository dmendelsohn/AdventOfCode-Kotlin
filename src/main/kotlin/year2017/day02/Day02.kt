package year2017.day02

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

fun parseInput(rawInput: String): List<List<Int>> {
    return rawInput.split("\n").map { line -> line.trim().split("\\s+".toRegex()).map { it.toInt() } }
}

fun part1(input: List<List<Int>>): Any {
    return input.sumOf { row -> row.maxOrNull()!! - row.minOrNull()!! }
}

fun getWholeDividend(row: List<Int>): Int {
    for (i in row.indices) {
        for (j in (i + 1)..row.lastIndex) {
            val (lower, higher) = listOf(row[i], row[j]).sorted()
            if (higher % lower == 0) {
                return higher / lower
            }
        }
    }
    throw IllegalStateException("No number evenly divided another number in this row: $row")
}

fun part2(input: List<List<Int>>): Any {
    return input.sumOf { row -> getWholeDividend(row) }
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
