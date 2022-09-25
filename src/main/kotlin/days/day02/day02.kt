package days.day02

import java.io.File

private const val INPUT_PATH = "inputs/day02.txt"
private val regex = Regex("""(\d+)-(\d+) ([a-z]): ([a-z]+)""")

private data class Policy(val char: Char, val range: IntRange) {
    fun isValid(password: String): Boolean {
        return range.contains(password.count { it == char })
    }
}

// 1-3 a: abcde
private fun parseLine(line: String): Pair<Policy, String> {
    val regexMatch = regex.matchEntire(line)
    regexMatch ?: throw IllegalArgumentException("Could not parse input line: $line")
    return regexMatch.destructured.let { (lo, hi, letter, password) -> Pair(Policy(letter.first(), IntRange(lo.toInt(), hi.toInt())), password) }
}


private fun getInput(): List<String> {
    return File(INPUT_PATH).readLines()
}

private fun part1(): Any {
    return getInput().map { parseLine(it) }.count { (policy, password) -> policy.isValid(password) }
}

private fun part2(): Any {
    return "Not implemented"
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
