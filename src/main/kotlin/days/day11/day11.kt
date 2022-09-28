package days.day11

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.split(".").last()}.txt"

private typealias Grid = List<List<Cell>>
private typealias MutableGrid = MutableList<MutableList<Cell>>

private enum class Cell(val symbol: Char) {
    FLOOR('.'),
    OCCUPIED('#'),
    UNOCCUPIED('L'),
}

private fun getInput(): Grid {
    return File(INPUT_PATH).readLines().map { line ->
        line.map { symbol -> Cell.values().single { it.symbol == symbol } }
    }
}

private fun Grid.toPrettyString(): String {
    return joinToString("\n", prefix = "\n") { row -> row.map { cell -> cell.symbol }.joinToString("") }
}

private fun Grid.mutableCopy(): MutableGrid {
    return map { row -> row.toMutableList() }.toMutableList()
}

private fun Grid.safeGetCell(row: Int, col: Int): Cell? {
    if (row < 0 || row > this.lastIndex || col < 0 || col > this[0].lastIndex) {
        return null
    }
    return this[row][col]
}

private fun Grid.countOccupiedNeighbors(row: Int, col: Int): Int {
    var num = 0
    for (newRow in (row - 1)..(row + 1)) {
        for (newCol in (col - 1)..(col + 1)) {
            if (safeGetCell(newRow, newCol) == Cell.OCCUPIED && (newRow != row || newCol != col)) {
                num++
            }
        }
    }
    return num
}

/** Return the grid and the number of changes that were made */
private fun getNextStepPart1(grid: Grid): Pair<Grid, Int> {
    val nextGrid = grid.mutableCopy()
    var numChanges = 0
    for (row in grid.indices) {
        for (col in grid[0].indices) {
            val cell = grid[row][col]
            val numOccupiedNeighbors = grid.countOccupiedNeighbors(row, col)
            val newCell = when {
                cell == Cell.OCCUPIED && numOccupiedNeighbors >= 4 -> Cell.UNOCCUPIED
                cell == Cell.UNOCCUPIED && numOccupiedNeighbors == 0 -> Cell.OCCUPIED
                else -> cell
            }
            if (cell != newCell) {
                nextGrid[row][col] = newCell
                numChanges++
            }
        }
    }
    return Pair(nextGrid, numChanges)
}

private typealias Direction = Pair<Int, Int>  // Row offset followed by col offset

private fun Grid.canSeeOccupiedSeat(row: Int, col: Int, direction: Direction): Boolean {
    var newRow = row
    var newCol = col
    var cell: Cell?
    do {
        newRow += direction.first
        newCol += direction.second
        cell = safeGetCell(newRow, newCol)
    } while (cell == Cell.FLOOR)
    return cell == Cell.OCCUPIED
}

private fun Grid.countOccupiedVisible(row: Int, col: Int): Int {
    var num = 0
    for (rowOffset in -1..1) {
        for (colOffset in -1..1) {
            val direction = Pair(rowOffset, colOffset)
            if (direction != Pair(0, 0) && canSeeOccupiedSeat(row, col, direction)) {
                num++
            }
        }
    }
    return num
}

/** Return the grid and the number of changes that were made */
private fun getNextStepPart2(grid: Grid): Pair<Grid, Int> {
    val nextGrid = grid.mutableCopy()
    var numChanges = 0
    for (row in grid.indices) {
        for (col in grid[0].indices) {
            val cell = grid[row][col]
            val numOccupiedVisible = grid.countOccupiedVisible(row, col)
            val newCell = when {
                cell == Cell.OCCUPIED && numOccupiedVisible >= 5 -> Cell.UNOCCUPIED
                cell == Cell.UNOCCUPIED && numOccupiedVisible == 0 -> Cell.OCCUPIED
                else -> cell
            }
            if (cell != newCell) {
                nextGrid[row][col] = newCell
                numChanges++
            }
        }
    }
    return Pair(nextGrid, numChanges)
}


private fun part1(): Any {
    var grid = getInput()
    do {
        val nextStep = getNextStepPart1(grid)
        grid = nextStep.first
        val numChanges = nextStep.second
    } while (numChanges > 0)
    return grid.sumOf { row -> row.count { cell -> cell == Cell.OCCUPIED } }
}

private fun part2(): Any {
    var grid = getInput()
    do {
        val nextStep = getNextStepPart2(grid)
        grid = nextStep.first
        val numChanges = nextStep.second
    } while (numChanges > 0)
    return grid.sumOf { row -> row.count { cell -> cell == Cell.OCCUPIED } }
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
