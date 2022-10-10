package year2017.day13

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

fun parseInput(rawInput: String): List<Layer> {
    return rawInput.split("\n").map { line ->
        line.split(": ", limit = 2).let { (depth, range) ->
            Layer(depth.toInt(), range.toInt())
        }
    }
}

data class Layer(val depth: Int, val range: Int) {

    val severity = depth * range

    fun catchesAt(startTime: Int): Boolean {
        return (startTime + depth) % (2 * (range - 1)) == 0
    }
}

fun part1(layers: List<Layer>): Int {
    return layers.filter { it.catchesAt(0) }.sumOf { it.severity }
}

fun part2(layers: List<Layer>): Int {
    var startTime = 0
    while (layers.any { it.catchesAt(startTime) }) {
        startTime++
    }
    return startTime
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
