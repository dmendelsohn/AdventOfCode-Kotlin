package year2017.day11

import java.io.File
import kotlin.math.abs
import kotlin.math.sign

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

fun parseInput(rawInput: String): List<Direction> {
    return rawInput.split(",").map { symbol -> Direction.values().first { it.symbol == symbol } }
}

enum class Direction(val symbol: String, val offset: Vector) {
    NORTH("n", Vector(0, 1)),
    NORTHEAST("ne", Vector(1, 1)),
    NORTHWEST("nw", Vector(-1, 0)),
    SOUTH("s", Vector(0, -1)),
    SOUTHEAST("se", Vector(1, 0)),
    SOUTHWEST("sw", Vector(-1, -1)),
}

private typealias Vector = Pair<Int, Int>

private typealias Point = Pair<Int, Int>

private fun Point.manhattanDistance(): Int {
    return if (first.sign == second.sign) {
        // If same sign, can step toward both at the same time (via northeast or southwest steps)
        maxOf(abs(first), abs(second))
    } else {
        // If opposite sign, we have to make progress in only one dimension at a time
        abs(first) + abs(second)
    }
}

private fun Point.add(other: Vector): Point {
    return Pair(first + other.first, second + other.second)
}

/** Return all points reached by following the directions */
private fun getWaypoints(path: List<Direction>): List<Point> {
    val waypoints = mutableListOf(Point(0, 0))
    for (direction in path) {
        waypoints.add(waypoints.last().add(direction.offset))
    }
    return waypoints.toList()
}

fun part1(input: List<Direction>): Int {
    return getWaypoints(input).last().manhattanDistance()
}

fun part2(input: List<Direction>): Int {
    return getWaypoints(input).maxOf { it.manhattanDistance() }
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
