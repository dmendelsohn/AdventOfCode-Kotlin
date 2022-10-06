package year2020.day24

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private val directionSymbols = listOf("e", "se", "sw", "w", "ne", "nw")

private enum class Direction(val symbol: String, val offset: Vector) {
    EAST("e", Vector(1, 0)),
    SOUTHEAST("se", Vector(0, -1)),
    SOUTHWEST("sw", Vector(-1, -1)),
    WEST("w", Vector(-1, 0)),
    NORTHWEST("nw", Vector(0, 1)),
    NORTHEAST("ne", Vector(1, 1)),
}

private typealias Path = List<Direction>

private typealias Point = Pair<Int, Int>
private typealias Vector = Pair<Int, Int>


private fun Point.add(other: Vector): Point {
    return Pair(first + other.first, second + other.second)
}

private fun getInput(): List<Path> {
    return File(INPUT_PATH).readLines().map { line ->
        var remainingLine = line
        val directions = mutableListOf<Direction>()
        while (remainingLine.isNotEmpty()) {
            val nextDirection = Direction.values().first { remainingLine.startsWith(it.symbol) }
            directions.add(nextDirection)
            remainingLine = remainingLine.drop(nextDirection.symbol.length)
        }
        directions.toList()
    }
}

private fun Path.getEndpoint(): Point {
    var point = Point(0, 0)
    this.forEach { direction -> point = point.add(direction.offset) }
    return point
}

private class Grid {
    // Terminology note: "black" in the problem = "on" in my code
    private var onPoints = mutableSetOf<Point>()

    fun flipPoint(point: Point) {
        if (onPoints.contains(point)) {
            onPoints.remove(point)
        } else {
            onPoints.add(point)
        }
    }

    fun getAllOnPoints(): Set<Point> {
        return onPoints.toSet()
    }

    private fun Point.getNeighbors(): Set<Point> {
        return Direction.values().map { direction -> this.add(direction.offset) }.toSet()
    }

    fun doStep() {
        val newOnPoints = mutableSetOf<Point>()

        // Consider a bounding box around our current on points with margin 1
        val rowRange = (onPoints.minOf { it.first } - 1)..(onPoints.maxOf { it.first } + 1)
        val colRange = (onPoints.minOf { it.second } - 1)..(onPoints.maxOf { it.second } + 1)

        for (row in rowRange) {
            for (col in colRange) {
                val point = Point(row, col)
                val numOnNeighbors = point.getNeighbors().count { neighbor -> onPoints.contains(neighbor) }
                if (onPoints.contains(point) && numOnNeighbors in 1..2) {
                    newOnPoints.add(point)
                }
                if (!onPoints.contains(point) && numOnNeighbors == 2) {
                    newOnPoints.add(point)
                }
            }
        }

        onPoints = newOnPoints
    }
}

private fun getStartingGrid(): Grid {
    val paths = getInput()
    val grid = Grid()
    paths.forEach { path -> grid.flipPoint(path.getEndpoint()) }
    return grid
}

private fun part1(): Int {
    val grid = getStartingGrid()
    return grid.getAllOnPoints().size
}

private fun part2(): Int {
    val grid = getStartingGrid()
    repeat(100) { grid.doStep() }
    return grid.getAllOnPoints().size
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
