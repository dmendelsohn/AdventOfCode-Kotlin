package year2020.day06

import java.io.File

private const val INPUT_PATH = "inputs/year2020/day06.txt"

typealias Group = List<Set<Char>>

private fun getInput(): List<Group> {
    return File(INPUT_PATH).readText().trim().split("\n\n").map { it.toGroup() }
}

fun String.toGroup(): Group {
    return split("\n").map { it.toSet() }
}

fun Group.getUnion(): Set<Char> {
    return reduce { acc, chars -> acc.union(chars) }
}

fun Group.getIntersection(): Set<Char> {
    return reduce { acc, chars -> acc.intersect(chars) }
}

private fun part1(): Any {
    return getInput().sumOf { it.getUnion().size }
}

private fun part2(): Any {
    return getInput().sumOf { it.getIntersection().size }
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
