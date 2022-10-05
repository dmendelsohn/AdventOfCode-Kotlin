package year2020.day23

private val TEST_INPUT = listOf(3, 8, 9, 1, 2, 5, 4, 6, 7)
private val REAL_INPUT = listOf(6, 8, 5, 9, 7, 4, 2, 1, 3)

private fun getInput(): List<Int> {
    return REAL_INPUT.also { validateCups(it) }
}

/** Validate that elements are unique and continuous starting with 1 */
private fun validateCups(cups: List<Int>) {
    require(cups.toSet().let { cupSet -> cupSet == (1..cups.size).toSet() && cupSet.size == cups.size }) {
        "Invalid cup list of size ${cups.size}"
    }
}

private fun List<Int>.toPretty(): String {
    return joinToString("") { it.toString() }
}

/** Use a map to implement a linked list, offering O(1) performance on most operations. */
private class Circle(cups: List<Int>) {
    /** Maps cup numbers to their successor **/
    private var nextCupMap = (cups.zipWithNext() + (cups.last() to cups.first())).associate { it }.toMutableMap()
    private var currentCup = cups.first()
    private val maxCupNum = cups.maxOrNull()!!

    private fun removeNext(cup: Int): Int {
        return nextCupMap.getValue(cup).also { nextCup ->
            nextCupMap[cup] = nextCupMap.getValue(nextCup)  // Point to cup to next.next
            nextCupMap.remove(nextCup)
        }
    }

    private fun addNext(cup: Int, nextCup: Int) {
        nextCupMap[nextCup] = nextCupMap.getValue(cup)
        nextCupMap[cup] = nextCup
    }

    private fun Int.wrap(): Int {
        return (this - 1).mod(maxCupNum) + 1
    }

    private fun getDestinationCup(): Int {
        return (1..4).map { offset ->
            (currentCup - offset).wrap()
        }.first { nextCupMap.contains(it) }
    }

    fun doMove() {
        val removedCups = mutableListOf<Int>()
        repeat(3) { removedCups.add(removeNext(currentCup)) }

        val destinationCup = getDestinationCup()

        // Insert removed cups in reverse order to they retain their original order
        removedCups.reversed().forEach { addNext(destinationCup, it) }

        currentCup = nextCupMap.getValue(currentCup)
    }

    /** Return as list with given cup at the front */
    fun toList(startCup: Int = currentCup, limit: Int = nextCupMap.size): List<Int> {
        var cup = startCup
        val cupList = mutableListOf<Int>()
        do {
            cupList.add(cup)
            cup = nextCupMap.getValue(cup)
        } while (cup != startCup && cupList.size < limit)
        return cupList.toList()
    }
}

private fun part1(): String {
    val cups = getInput()
    val circle = Circle(cups)
    repeat(100) { circle.doMove() }
    return circle.toList(1).drop(1).toPretty()
}

private const val ONE_MILLION = 1000 * 1000

private fun part2(): Any {
    val explicitCups = getInput()
    val cups = explicitCups + (1..ONE_MILLION).drop(explicitCups.size)
    validateCups(cups)
    val circle = Circle(cups)
    repeat(10 * ONE_MILLION) { circle.doMove() }
    return circle.toList(1, limit = 3).drop(1).fold(1L) { acc, i -> acc * i }
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
