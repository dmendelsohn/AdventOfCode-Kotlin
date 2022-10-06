package year2017.day03

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt

private const val DEFAULT_INPUT = 277678

fun part1(input: Int): Any {
    // Step 1: Calculate side length of square
    // Note that "side length" means there are that many cells on the edge, including both corners
    val sideLength = ceil(sqrt(input.toFloat())).toInt().let { if (it % 2 == 0) it + 1 else it }

    // Calculate distance from point to next corner
    // sideLength ^ 2 is the bottom right corner of the square
    val distToEndOfLayer = (sideLength * sideLength - input)
    val distToNextCorner = distToEndOfLayer % (sideLength - 1)

    // The closest point on the square to the center is at the halfway point
    // That point is (sideLength - 1) / 2 away from the center
    // Every unit away from the halfway point increases the Manhattan distance by 1
    return (sideLength - 1) / 2 + abs((sideLength - 1) / 2 - distToNextCorner)
}

private data class Point(val row: Int, val col: Int) {
    // Note: rows increase upward, cols increase rightward

    fun getNeighbors(): Set<Point> {
        return (-1..1).flatMap { rowOffset ->
            (-1..1).map { colOffset ->
                Point(row + rowOffset, col + colOffset)
            }
        }.filter { it != this }.toSet()
    }

    /** Get next point in the spiral pattern */
    fun getNext(): Point {
        return when {
            // Right edge, moving up
            col > 0 && row in (-col + 1) until col -> Point(row + 1, col)
            // Top edge, moving left
            row > 0 && col in row downTo -row + 1 -> Point(row, col - 1)
            // Left edge, moving down
            col < 0 && row in -col downTo col + 1 -> Point(row - 1, col)
            // Bottom edge, moving right
            // Note that this handles the 0,0 case, and also includes the upper boundary (moving us to the next layer).
            row <= 0 && col in row..-row -> Point(row, col + 1)
            // The above should be exhaustive, but Kotlin doesn't know that
            else -> throw IllegalStateException("Could not determine successor for $this")
        }
    }
}

private class Grid {
    val values = mutableMapOf(Point(0, 0) to 1)
    var lastPoint = Point(0, 0)

    /** Fill next point, returning value placed */
    fun fillNextPoint() {
        val point = lastPoint.getNext()
        values[point] = point.getNeighbors().sumOf { values.getOrDefault(it, 0) }
        lastPoint = point
    }
}

fun part2(input: Int): Any {
    val grid = Grid()
    var lastValue = -1
    do {
        grid.fillNextPoint()
        lastValue = grid.values.getValue(grid.lastPoint)
    } while (lastValue <= input)
    return lastValue
}


fun main(args: Array<String>) {
    val input = args.firstOrNull()?.toInt() ?: DEFAULT_INPUT
    println("Running using input $input")
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
