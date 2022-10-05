package year2020.day15

import kotlin.properties.Delegates

private fun getInput(): List<Int> {
    return listOf(8, 13, 1, 0, 18, 9)
}


private class State(private val startingNumbers: List<Int>) {
    /** Maps values to last turn they were spoken (not including most recently spoken number) */
    private val memory = mutableMapOf<Int, Int>()
    var numTurns = 0
        private set
    var lastSpoken by Delegates.notNull<Int>()
        private set

    init {
        memory.putAll(startingNumbers.dropLast(1).withIndex().associate { (index, value) -> value to index })
        numTurns = startingNumbers.size
        lastSpoken = startingNumbers.last()
    }

    fun speakNext() = getValueToSpeak().also { updateState(it) }

    private fun getValueToSpeak() = memory[lastSpoken]?.let { numTurns - 1 - it } ?: 0

    private fun updateState(newSpokenValue: Int) {
        memory[lastSpoken] = numTurns - 1
        numTurns++
        lastSpoken = newSpokenValue
    }

    override fun toString(): String {
        return "State(numTurns=$numTurns, lastSpoken=$lastSpoken, memory=$memory)"
    }
}

private fun part1(): Int {
    val state = State(getInput())
    while (state.numTurns < 2020) {
        state.speakNext()
    }
    return state.lastSpoken
}

private fun part2(): Any {
    val state = State(getInput())
    while (state.numTurns < 30 * 1000 * 1000) {
        state.speakNext()
    }
    return state.lastSpoken
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
