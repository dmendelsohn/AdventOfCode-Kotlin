package year2020.day13

import java.io.File
import java.math.BigInteger

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"


private fun getInput(): Pair<Int, List<String>> {
    val lines = File(INPUT_PATH).readLines()
    return Pair(lines[0].toInt(), lines[1].split(","))
}

private fun getTimeToWait(startTime: Int, period: Int): Int {
    // This can be done as a fancy one-liner, but I'd rather go for readability
    val timeSinceLastBus = startTime.mod(period)
    return if (timeSinceLastBus == 0) 0 else period - timeSinceLastBus
}

private fun part1(): Int {
    val (startTime, unrestrictedBusIds) = getInput()
    val busIds = unrestrictedBusIds.filter { it != "x" }.map { it.toInt() }
    val (bestBus, bestTimeToWait) = busIds.map { it to getTimeToWait(startTime, it) }
        .minByOrNull { (_, timeToWait) -> timeToWait }!!
    return bestBus * bestTimeToWait
}

private data class ModRequirement(val remainder: BigInteger, val modulus: BigInteger) {
    companion object {
        fun fromScheduleItem(index: Int, item: String): ModRequirement? {
            val modulus = if (item != "x") item.toLong() else return null
            val remainder = (modulus - index).mod(modulus)
            return ModRequirement(BigInteger.valueOf(remainder), BigInteger.valueOf(modulus))
        }
    }

    fun merge(other: ModRequirement): ModRequirement {
        // See https://shainer.github.io/crypto/math/2017/10/22/chinese-remainder-theorem.html
        // Note the assumption that the moduli are co-prime.
        // In practice, this is true for our input (in fact, all the moduli are unique primes).
        val thisInverseOther = modulus.modInverse(other.modulus)
        val otherInverseThis = other.modulus.modInverse(modulus)
        val newModulus = modulus * other.modulus
        val newRemainder =
            (this.remainder * otherInverseThis * other.modulus + other.remainder * thisInverseOther * this.modulus).mod(
                newModulus
            )
        return ModRequirement(newRemainder, newModulus)
    }
}

private fun part2(): Long {
    val (_, requiredSchedule) = getInput()
    val requirements = requiredSchedule.mapIndexedNotNull { index, s -> ModRequirement.fromScheduleItem(index, s) }
    val mergedRequirement = requirements.reduce { acc, modRequirement -> println(acc); acc.merge(modRequirement) }
    return mergedRequirement.remainder.toLong()
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
