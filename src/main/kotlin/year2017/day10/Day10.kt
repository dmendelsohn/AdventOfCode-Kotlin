package year2017.day10

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"
private const val DEFAULT_NUM_MARKS = 256

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

fun parseInputAsIntList(rawInput: String): List<Int> {
    return rawInput.split(",").map { it.toInt() }
}

fun parseInputAsBytes(rawInput: String): List<Int> {
    return rawInput.map { it.code }
}

private data class State(val marks: List<Int>, val currentPosition: Int, val skipSize: Int)

private fun doKnotHashRound(startState: State, lengthSequence: List<Int>): State {
    var state = startState
    for (length in lengthSequence) {
        state = doKnotHashStep(state, length)
    }
    return state
}

private fun doKnotHashStep(state: State, length: Int): State {
    return State(
        marks = state.marks.rotateLeft(state.currentPosition).twistFirst(length).rotateRight(state.currentPosition),
        currentPosition = (state.currentPosition + length + state.skipSize) % state.marks.size,
        skipSize = state.skipSize + 1
    )
}

private fun <T> List<T>.rotateLeft(amount: Int): List<T> {
    return drop(amount) + take(amount)
}

private fun <T> List<T>.rotateRight(amount: Int): List<T> {
    return takeLast(amount) + dropLast(amount)
}

private fun <T> List<T>.twistFirst(amount: Int): List<T> {
    return take(amount).reversed() + drop(amount)
}

fun part1(input: String, numMarks: Int): Int {
    val lengthSequence = parseInputAsIntList(input)
    val startState = State(marks = (0 until numMarks).toList(), currentPosition = 0, skipSize = 0)
    val finalState = doKnotHashRound(startState, lengthSequence)
    return finalState.marks[0] * finalState.marks[1]
}


@OptIn(ExperimentalUnsignedTypes::class)
private fun UByteArray.denseHash(): UByteArray {
    require(size % 16 == 0) { "Can only get dense hash for an array whose length is a multiple of 16" }
    return (0 until size / 16).map {
        sliceArray(it * 16 until (it + 1) * 16).reduce { acc, uByte -> acc.xor(uByte) }
    }.toUByteArray()
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun UByteArray.hexDigest(): String {
    return joinToString("") { it.toString(16).padStart(2, '0') }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun part2(input: String): String {
    val lengthSequence = parseInputAsBytes(input) + listOf(17, 31, 73, 47, 23)
    var state = State(marks = (0 until 256).toList(), currentPosition = 0, skipSize = 0)
    repeat(64) { state = doKnotHashRound(state, lengthSequence) }
    return state.marks.map { it.toUByte() }.toUByteArray().denseHash().hexDigest()
}


fun main(args: Array<String>) {
    println("Running with args: ${args.joinToString(" ")}")
    val inputPath = args.getOrNull(0) ?: DEFAULT_INPUT_PATH
    val numMarks = args.getOrNull(1)?.toInt() ?: DEFAULT_NUM_MARKS
    val input = getRawInput(inputPath)
    println("Solution to part 1: ${part1(input, numMarks)}")
    println("Solution to part 2: ${part2(input)}")
}
