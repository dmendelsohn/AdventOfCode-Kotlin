package year2020.day14

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.split(".").last()}.txt"
private const val NUM_BITS = 36

private typealias Mask = String
private typealias Address = Long
private typealias FuzzyAddress = String
private typealias Value = Long

private fun Long.applyValueMask(mask: Mask): Long {
    val andMask = mask.map { if (it == 'X') '1' else it }.joinToString("").toLong(2)
    val orMask = mask.map { if (it == 'X') '0' else it }.joinToString("").toLong(2)
    return this.and(andMask).or(orMask)
}

private fun Address.applyAddressMask(mask: Mask): FuzzyAddress {
    val bitString = this.toString(2).let { it.padStart(NUM_BITS, '0') }.takeLast(NUM_BITS)
    return bitString.zip(mask)
        .map { (addressChar, maskChar) -> if (maskChar in "1X") maskChar else addressChar }.joinToString("")
}

private fun FuzzyAddress.getAddresses(): List<Address> {
    val numAddresses = 1L shl count { it == 'X' }
    return (0 until numAddresses).map { resolveFuzziness(it) }
}

/** Replaces Xs with low order bits of resolutionIndex */
private fun FuzzyAddress.resolveFuzziness(resolutionIndex: Long): Address {
    var numSeenX = 0
    return this.reversed().map {
        if (it == 'X') {
            resolutionIndex.shr(numSeenX).toString(2).last().also { numSeenX += 1 }
        } else {
            it
        }
    }.reversed().joinToString("").toLong(2)
}

private data class State(var mask: Mask, val memory: MutableMap<Address, Value>)

private interface Instruction {
    /** Modifies state in-place. */
    fun applyPart1(state: State)

    /** Modifies state in-place. */
    fun applyPart2(state: State)
}

private class MaskInstruction(val mask: Mask) : Instruction {
    override fun applyPart1(state: State) {
        state.mask = mask
    }

    override fun applyPart2(state: State) {
        state.mask = mask
    }
}

private class MemoryInstruction(val address: Address, val unmaskedValue: Value) : Instruction {
    override fun applyPart1(state: State) {
        state.memory[address] = unmaskedValue.applyValueMask(state.mask)
    }

    override fun applyPart2(state: State) {
        address.applyAddressMask(state.mask).getAddresses().forEach { state.memory[it] = unmaskedValue }
    }
}

private fun parseLine(line: String): Instruction {
    return if (line.startsWith("mask")) {
        MaskInstruction(line.split(" = ", limit = 2)[1])
    } else {
        Regex("""mem\[(\d+)\] = (\d+)""").matchEntire(line)!!.destructured.let { (address, value) ->
            MemoryInstruction(
                address.toLong(),
                value.toLong()
            )
        }
    }
}

private fun getInput(): List<Instruction> {
    return File(INPUT_PATH).readLines().map { parseLine(it) }
}

private fun part1(): Value {
    val program = getInput()
    val state = State(mask = "", memory = mutableMapOf())
    for (instruction in program) {
        instruction.applyPart1(state)
    }
    return state.memory.values.sum()
}

private fun part2(): Any {
    val program = getInput()
    val state = State(mask = "", memory = mutableMapOf())
    for (instruction in program) {
        instruction.applyPart2(state)
    }
    return state.memory.values.sum()
}

fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
