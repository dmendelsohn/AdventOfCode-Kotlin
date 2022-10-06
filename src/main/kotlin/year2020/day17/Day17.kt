package year2020.day17

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getInput(): List<List<Boolean>> {
    return File(INPUT_PATH).readLines().map { line -> line.map { it == '#' } }
}

private data class Point(val x: Int, val y: Int, val z: Int, val w: Int = 0) {
    fun getNeighbors(): Set<Point> {
        return (-1..1).map { xOffset ->
            (-1..1).map { yOffset ->
                (-1..1).map { zOffset ->
                    (-1..1).map { wOffset ->
                        Point(x + xOffset, y + yOffset, z + zOffset, w + wOffset)
                    }
                }.flatten()
            }.flatten()
        }.flatten().filterNot { it == this }.toSet()
    }
}

private fun getStartState(): Set<Point> {
    val state = mutableSetOf<Point>()
    val startGrid = getInput()
    for (idx in startGrid.indices) {
        for (idy in startGrid[idx].indices) {
            if (startGrid[idx][idy]) {
                state.add(Point(idx, idy, 0))
            }
        }
    }
    return state.toSet()
}

/**
 * Returns the low and high corners of the bounding box with a 1 unit margin
 * This also serves as our mechanism for keeping part 1 resticted to 3 dimensions
 **/
private fun getBoundingBox(activePoints: Set<Point>, includeW: Boolean): Pair<Point, Point> {
    val lowPoint = Point(
        activePoints.minOf { it.x } - 1,
        activePoints.minOf { it.y } - 1,
        activePoints.minOf { it.z } - 1,
        if (includeW) activePoints.minOf { it.w } - 1 else 0,
    )
    val highPoint = Point(
        activePoints.maxOf { it.x } + 1,
        activePoints.maxOf { it.y } + 1,
        activePoints.maxOf { it.z } + 1,
        if (includeW) activePoints.maxOf { it.w } + 1 else 0,
    )
    return Pair(lowPoint, highPoint)
}

private fun getNextActivePoints(activePoints: Set<Point>, includeW: Boolean): Set<Point> {
    val nextActivePoints = mutableSetOf<Point>()
    val (lowPoint, highPoint) = getBoundingBox(activePoints, includeW)
    for (idx in lowPoint.x..highPoint.x) {
        for (idy in lowPoint.y..highPoint.y) {
            for (idz in lowPoint.z..highPoint.z) {
                for (idw in lowPoint.w..highPoint.w) {
                    val point = Point(idx, idy, idz, idw)
                    val activeNeighbors = point.getNeighbors().count { it in activePoints }
                    if (activeNeighbors == 3 || (activeNeighbors == 2 && point in activePoints)) {
                        nextActivePoints.add(point)
                    }
                }
            }
        }
    }
    return nextActivePoints.toSet()
}


private fun part1(): Int {
    var activePoints = getStartState()
    repeat(6) { activePoints = getNextActivePoints(activePoints, includeW = false) }
    return activePoints.size
}

private fun part2(): Any {
    var activePoints = getStartState()
    repeat(6) { activePoints = getNextActivePoints(activePoints, includeW = true) }
    return activePoints.size
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
