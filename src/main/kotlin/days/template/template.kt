package days.template

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.split(".").last()}.txt"

private fun getInput(): List<String> {
    return File(INPUT_PATH).readLines()
}

private fun part1(): Any {
    return "Not implemented"
}

private fun part2(): Any {
    return "Not implemented"
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
