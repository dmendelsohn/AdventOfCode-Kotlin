package year2017.day08

import java.io.File
import kotlin.math.max

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

enum class ModifyOperator(val value: String, val func: (Int, Int) -> Int) {
    INC("inc", { first, second -> first + second }),
    DEC("dec", { first, second -> first - second }),
}

enum class CompareOperator(val value: String, val func: (Int, Int) -> Boolean) {
    EQ("==", { first, second -> first == second }),
    NE("!=", { first, second -> first != second }),
    LT("<", { first, second -> first < second }),
    LE("<=", { first, second -> first <= second }),
    GE(">", { first, second -> first > second }),
    GT(">=", { first, second -> first >= second }),
}

data class Modification(val register: String, val modifyOperator: ModifyOperator, val constant: Int)

data class Predicate(val register: String, val compareOperator: CompareOperator, val constant: Int)

data class Instruction(val modification: Modification, val predicate: Predicate) {
    companion object {

        fun parse(line: String): Instruction {
            val register = """(\w+)"""
            val int = """(-?\d+)"""
            val modifier = ModifyOperator.values().joinToString("|", prefix = "(", postfix = ")") { it.value }
            val comparator = CompareOperator.values().joinToString("|", prefix = "(", postfix = ")") { it.value }
            val lineRegex = """$register $modifier $int if $register $comparator $int""".toRegex()
            val match = lineRegex.matchEntire(line) ?: throw IllegalArgumentException("Could not parse line $line")
            return match.groupValues.let {
                Instruction(
                    Modification(it[1], ModifyOperator.values().single { op -> op.value == it[2] }, it[3].toInt()),
                    Predicate(it[4], CompareOperator.values().single { op -> op.value == it[5] }, it[6].toInt()),
                )
            }
        }
    }
}

// Example line: "b inc 5 if a > 1"
fun parseInput(rawInput: String): List<Instruction> {
    return rawInput.split("\n").map { Instruction.parse(it) }
}

/** Runs program and returns resulting register values */
private fun runProgram(program: List<Instruction>): Pair<Map<String, Int>, Int> {
    val registers = mutableMapOf<String, Int>()
    var maxValueEver = 0
    program.forEach {
        if (evaluatePredicate(it.predicate, registers)) {
            performModification(it.modification, registers)
            maxValueEver = max(maxValueEver, registers.getValue(it.modification.register))
        }
    }
    return Pair(registers.toMap(), maxValueEver)
}


private fun evaluatePredicate(predicate: Predicate, registers: Map<String, Int>): Boolean {
    return predicate.compareOperator.func(registers.getOrDefault(predicate.register, 0), predicate.constant)
}

private fun performModification(modification: Modification, registers: MutableMap<String, Int>) {
    val originalValue = registers.getOrDefault(modification.register, 0)
    registers[modification.register] = modification.modifyOperator.func(originalValue, modification.constant)
}

fun part1(input: List<Instruction>): Int {
    val registers = runProgram(input)
    return registers.first.values.maxOrNull()!!
}

fun part2(input: List<Instruction>): Int {
    val registers = runProgram(input)
    return registers.second
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
