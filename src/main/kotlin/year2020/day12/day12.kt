package year2020.day12

import java.io.File
import kotlin.math.abs

private val INPUT_PATH = "inputs/year2020/${object {}.javaClass.packageName.split(".").last()}.txt"

private fun getInput(): List<Instruction> {
    return File(INPUT_PATH).readLines().map { Instruction.parse(it) }
}

private data class Instruction(val operation: Char, val magnitude: Int) {

    init {
        if (operation in "LR" && !listOf(0, 90, 180, 270).contains(magnitude)) {
            throw IllegalArgumentException(
                "Turn magnitude must be a reduced non-negative multiple of 90, not $magnitude"
            )
        }
    }

    companion object {
        fun parse(line: String): Instruction {
            return Instruction(line.first(), line.drop(1).toInt())
        }
    }
}

private data class State1(val lat: Int, val lon: Int, private val rawHeading: Int) {
    val heading = rawHeading.mod(360) // Handles negative nicely, % does not
}

private fun doStepPart1(state: State1, instruction: Instruction): State1 {
    // Convert F to a cardinal direction
    val effectiveOperation = when {
        instruction.operation != 'F' -> instruction.operation
        state.heading == 0 -> 'E'
        state.heading == 90 -> 'N'
        state.heading == 180 -> 'W'
        state.heading == 270 -> 'S'
        else -> throw UnsupportedOperationException("Cannot move forward with a heading of ${state.heading}")
    }
    return when (effectiveOperation) {
        'N' -> state.copy(lat = state.lat + instruction.magnitude)
        'S' -> state.copy(lat = state.lat - instruction.magnitude)
        'E' -> state.copy(lon = state.lon + instruction.magnitude)
        'W' -> state.copy(lon = state.lon - instruction.magnitude)
        'L' -> state.copy(rawHeading = state.heading + instruction.magnitude)
        'R' -> state.copy(rawHeading = state.heading - instruction.magnitude)
        else -> throw UnsupportedOperationException("Cannot execute $instruction from state $state")
    }
}

private fun part1(): Int {
    var state = State1(0, 0, 0)
    getInput().forEach {
        state = doStepPart1(state, it)
    }
    return abs(state.lat) + abs(state.lon)
}

private data class State2(val lat: Int, val lon: Int, val wayLatOffset: Int, val wayLonOffset: Int) {
    fun rotateWaypoint(degrees: Int): State2 {
        return when (degrees) {
            0 -> copy()
            90 -> copy(wayLatOffset = wayLonOffset, wayLonOffset = -wayLatOffset)
            180 -> copy(wayLatOffset = -wayLatOffset, wayLonOffset = -wayLonOffset)
            270 -> copy(wayLatOffset = -wayLonOffset, wayLonOffset = wayLatOffset)
            else -> throw UnsupportedOperationException("Cannot rotate by $degrees")
        }
    }
}


private fun doStepPart2(state: State2, instruction: Instruction): State2 {
    return when (instruction.operation) {
        'N' -> state.copy(wayLatOffset = state.wayLatOffset + instruction.magnitude)
        'S' -> state.copy(wayLatOffset = state.wayLatOffset - instruction.magnitude)
        'E' -> state.copy(wayLonOffset = state.wayLonOffset + instruction.magnitude)
        'W' -> state.copy(wayLonOffset = state.wayLonOffset - instruction.magnitude)
        'F' -> state.copy(
            lat = state.lat + instruction.magnitude * state.wayLatOffset,
            lon = state.lon + instruction.magnitude * state.wayLonOffset
        )
        'L' -> state.rotateWaypoint(instruction.magnitude)
        'R' -> state.rotateWaypoint((360 - instruction.magnitude) % 360)
        else -> throw UnsupportedOperationException("Could not execute $instruction from $state")
    }
}

private fun part2(): Any {
    var state = State2(lat = 0, lon = 0, wayLatOffset = 1, wayLonOffset = 10)
    getInput().forEach {
        state = doStepPart2(state, it)
    }
    return abs(state.lat) + abs(state.lon)
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
