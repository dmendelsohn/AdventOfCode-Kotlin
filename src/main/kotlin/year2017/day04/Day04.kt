package year2017.day04

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

fun parseInput(rawInput: String): List<List<String>> {
    return rawInput.split("\n").map { line -> line.split(" ") }
}

fun part1(phrases: List<List<String>>): Any {
    return phrases.count { it.size == it.toSet().size }
}

fun part2(phrases: List<List<String>>): Any {
    return phrases.count { phrase ->
        // Normalize words by sorting the characters within them.
        // We could normalize words by getting a frequency distribution (which is asymptotically better).
        // In practice, we have short words, so sorting is likely faster.
        // Also, our total input is so small this the difference is hard to detect.
        val phraseWithNormalizedWords = phrase.map { word -> word.toCharArray().sorted().joinToString("") }
        phraseWithNormalizedWords.size == phraseWithNormalizedWords.toSet().size
    }
}

fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
