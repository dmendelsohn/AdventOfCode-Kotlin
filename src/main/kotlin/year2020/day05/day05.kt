package year2020.day05

import java.io.File

private const val INPUT_PATH = "inputs/day05.txt"
private const val MAX_ROW = 127
private const val MAX_COL = 7

private fun getInput(): List<String> {
    return File(INPUT_PATH).readLines()
}

private data class Seat(val row: Int, val col: Int) {

    init {
        require(row in 0..MAX_ROW) { "row value $row is out of range" }
        require(col in 0..MAX_COL) { "col value $col is out of range" }
    }

    val seatId = row * (MAX_COL + 1) + col

    companion object {
        fun fromBoardingPass(boardingPass: String): Seat {
            val row = boardingPass.filter { it in "FB" }.replace('F', '0').replace('B', '1').toInt(2)
            val col = boardingPass.filter { it in "LR" }.replace('L', '0').replace('R', '1').toInt(2)
            return Seat(row, col)
        }
    }
}


private fun part1(): Int {
    return getInput().map { Seat.fromBoardingPass(it) }.maxOf { it.seatId }
}

private fun part2(): Int? {
    val seatIds = getInput().map { Seat.fromBoardingPass(it) }.map { it.seatId }.toSet()
    for (seatId in seatIds) {
        if (!seatIds.contains(seatId + 1) && seatIds.contains(seatId + 2)) {
            return seatId + 1
        }
    }
    return null
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
