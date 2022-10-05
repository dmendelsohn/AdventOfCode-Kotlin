package year2020.day03

import java.io.File

private const val INPUT_PATH = "inputs/day03.txt"

typealias Grid = Array<BooleanArray>
typealias Vector = Pair<Int, Int>  // down, right

private fun getInput(): Grid {
    return File(INPUT_PATH).readLines().map { line -> line.map { it == '#' }.toBooleanArray() }.toTypedArray()
}

private fun countTreesOnVector(grid: Grid, vector: Vector): Int {
    var position = Pair(0, 0)  // row num, col num
    var numTrees = 0
    while (position.first < grid.size) {
        if (grid[position.first][position.second % grid[0].size]) {
            numTrees++
        }
        position = Pair(position.first + vector.first, position.second + vector.second)
    }
    return numTrees
}

private fun part1(): Int {
    val grid = getInput()
    return countTreesOnVector(grid, Pair(1, 3))
}

private fun part2(): Long {
    val grid = getInput()
    return listOf(
        Pair(1, 1),
        Pair(1, 3),
        Pair(1, 5),
        Pair(1, 7),
        Pair(2, 1),
    ).foldRight(1L) { vector, acc -> acc * countTreesOnVector(grid, vector) }
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
