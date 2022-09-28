/**
 * For fun, I'm implementing this as if it were an online algorithm with arbitrarily long input.
 * E.g. not allowed to read entire input into memory.
 */
package days.day09

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.split(".").last()}.txt"
private const val BUFFER_SIZE = 25

private fun getInput(): Sequence<Long> =
    File(INPUT_PATH).bufferedReader().lineSequence().map { it.toLong() }


private class RingBuffer(val size: Int) {

    var elements = LongArray(size)
    var nextIndex = 0

    fun add(elt: Long) {
        elements[nextIndex] = elt
        nextIndex = (nextIndex + 1) % size
    }
}

private fun LongArray.hasPairSum(total: Long): Boolean {
    val freqCount = groupBy { it }.mapValues { (_, values) -> values.size }
    return any {
        val complement = total - it
        val complementCount = freqCount.getOrDefault(total - it, 0)
        if (it == complement) {
            // Need two of these values in this case
            complementCount >= 2
        } else {
            complementCount >= 1
        }
    }
}

private fun part1(): Long {
    // Process preamble
    val input = getInput().iterator()
    val buffer = RingBuffer(BUFFER_SIZE)
    repeat(BUFFER_SIZE) { buffer.add(input.next()) }

    // Process the rest
    // For each element, evaluate the validity condition and then add the new value
    // When we find the validity condition fails, stop
    return input.asSequence().first { value ->
        !buffer.elements.hasPairSum(value).also { buffer.add(value) }
    }
}

/** Using window instead of a circular buffer */
private fun part1Window(): Long {
    val input = getInput().toList()
    return input.windowed(BUFFER_SIZE + 1).first { window ->
        !window.dropLast(1).toLongArray().hasPairSum(window.last())
    }.last()
}

private fun part2(targetSum: Long): Long? {
    val input = getInput().toList()
    // Element at index N is sum of elements up through N (exclusive)
    val cumulativeSums = mutableListOf(0L)
    // Maps elements of cumulativeSums to first index where it appeared
    // Lets us determine if a prefix has a certain sum (and if so, how long the prefix is)
    val prefixLengthBySums = mutableMapOf(0L to 0)

    for (value in input) {
        val sumSoFar = value + (cumulativeSums.lastOrNull() ?: 0)
        cumulativeSums.add(sumSoFar)

        // Don't overwrite values in the prefix-sum-lookup, it can't help and be problematic in edge cases.
        // If multiple prefixes have the same length, prefer the earlier one.
        // Because of the complement logic below, this means we prefer longer ranges to reach the target sum.
        if (!prefixLengthBySums.containsKey(sumSoFar)) {
            prefixLengthBySums[sumSoFar] = cumulativeSums.lastIndex
        }

        val excessSum = sumSoFar - targetSum
        prefixLengthBySums[excessSum]?.let { prefixLength ->
            val inputSliceWithSum = input.slice(prefixLength..cumulativeSums.lastIndex)
            if (inputSliceWithSum.size >= 2) {
                return inputSliceWithSum.maxOrNull()!! + inputSliceWithSum.minOrNull()!!
            }
        }
    }
    return null
}


fun main(args: Array<String>) {
    val part1Answer = part1()
    println("Solution to part 1: $part1Answer")
    println("Solution to part 2: ${part2(part1Answer)}")
}
