package year2020.day08

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getInput(): Program {
    return File(INPUT_PATH).readLines().map { Instruction.parse(it) }
}

private enum class Operation(val value: String) {
    ACC("acc"),
    JMP("jmp"),
    NOP("nop"),
}

private data class Instruction(val operation: Operation, val value: Int) {
    companion object {
        fun parse(line: String): Instruction {
            val (operationStr, valueStr) = line.split(" ", limit = 2)
            return Instruction(Operation.values().single { it.value == operationStr }, valueStr.toInt())
        }
    }
}

private typealias Program = List<Instruction>

private data class State(val acc: Int, val eip: Int)

private fun doStep(program: Program, state: State): State {
    val instruction = program[state.eip]
    return when (instruction.operation) {
        Operation.ACC -> State(acc = state.acc + instruction.value, eip = state.eip + 1)
        Operation.JMP -> State(acc = state.acc, eip = state.eip + instruction.value)
        Operation.NOP -> State(acc = state.acc, eip = state.eip + 1)
    }
}

/** Execute until loop is detected or termination occurs normally */
private fun executeProgram(program: Program): State {
    val instructionsExecuted = mutableSetOf<Int>()  // Elements are instruction indices
    var state = State(acc = 0, eip = 0)
    while (!instructionsExecuted.contains(state.eip) && state.eip != program.size) {
        instructionsExecuted.add(state.eip)
        state = doStep(program, state)
    }
    return state
}

private fun part1(): Int {
    return executeProgram(getInput()).acc
}

/** Flip the instruction or return null if it cannot be flipped. */
private fun Instruction.flipInstruction(): Instruction? {
    return when (operation) {
        Operation.ACC -> null
        Operation.JMP -> Instruction(Operation.NOP, value)
        Operation.NOP -> Instruction(Operation.JMP, value)
    }
}

/** Using a sequence means we don't need to take up O(N**2) memory to store all the program versions */
private fun getMutations(program: Program): Sequence<Program> = sequence {
    program.indices.forEach { indexToModify ->
        val replacement = program[indexToModify].flipInstruction()
        if (replacement != null) {
            yield(program.mapIndexed { index, instruction -> if (index == indexToModify) replacement else instruction })
        }
    }
}

private fun part2(): Any {
    val corruptedProgram = getInput()
    val terminatingState =
        getMutations(corruptedProgram).map { executeProgram(it) }.single { it.eip == corruptedProgram.size }
    return terminatingState.acc
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
