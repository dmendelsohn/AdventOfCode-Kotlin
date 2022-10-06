package year2020.day07

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getInput(): RuleSet {
    return File(INPUT_PATH).readLines().map { Rule.parse(it) }.associateBy { it.outerColor }
}

private typealias Color = String

private data class Rule(val outerColor: Color, val contains: Map<Color, Int>) {

    companion object {

        private fun innerParse(text: String): Pair<Color, Int> {
            val (num, innerColor) = Regex("""(\d+) ([\w\s]+) bags?""").matchEntire(text)?.destructured
                ?: throw IllegalArgumentException("Could not parse text: $text")
            return innerColor to num.toInt()
        }

        fun parse(text: String): Rule {
            val (outerColor, remainder) = Regex("""([\w\s]+) bags contain (.*)\.""").matchEntire(text)?.destructured
                ?: throw IllegalArgumentException("Could not parse line: $text")
            val contains: Map<Color, Int> = if (remainder == "no other bags") {
                emptyMap()
            } else {
                remainder.split(", ").associate { innerParse(it) }
            }
            return Rule(outerColor, contains)
        }
    }
}

private typealias RuleSet = Map<Color, Rule>

/** Expected part 1 answer = 4, expected part 2 answer = 32 */
private val exampleRuleText1 = """
    light red bags contain 1 bright white bag, 2 muted yellow bags.
    dark orange bags contain 3 bright white bags, 4 muted yellow bags.
    bright white bags contain 1 shiny gold bag.
    muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
    shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
    dark olive bags contain 3 faded blue bags, 4 dotted black bags.
    vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
    faded blue bags contain no other bags.
    dotted black bags contain no other bags.""".trimIndent()

/** Expected part 2 answer = 126 */
private val exampleRuleText2 = """
    shiny gold bags contain 2 dark red bags.
    dark red bags contain 2 dark orange bags.
    dark orange bags contain 2 dark yellow bags.
    dark yellow bags contain 2 dark green bags.
    dark green bags contain 2 dark blue bags.
    dark blue bags contain 2 dark violet bags.
    dark violet bags contain no other bags.
""".trimIndent()

/** Full DFS of the graph starting at [startColor], updating [canReachGoal] and [visited] sets. */
private fun canReachGoal(
    ruleSet: RuleSet,
    startColor: Color,
    goalColor: Color,
    knownGoalReachers: MutableSet<Color>,
    visited: MutableSet<Color>,
): Boolean {
    // Mark node as visited (or return early if already visited)
    if (visited.contains(startColor)) {
        return knownGoalReachers.contains(startColor)
    }
    visited.add(startColor)

    // Recursively determine if goal can be reached (or if current node is already the goal node)
    val nextColors = ruleSet.getValue(startColor).contains.keys
    val startColorCanReachGoal = (startColor == goalColor) || nextColors.any { nextColor ->
        canReachGoal(
            ruleSet,
            nextColor,
            goalColor,
            knownGoalReachers,
            visited
        )
    }

    // Update goal reachers and return
    if (startColorCanReachGoal) {
        knownGoalReachers.add(startColor)
    }
    return startColorCanReachGoal
}


fun part1(): Int {
    val ruleSet = getInput()
    val goalReachers = mutableSetOf<Color>()
    val visited = mutableSetOf<Color>()
    ruleSet.keys.forEach { canReachGoal(ruleSet, it, "shiny gold", goalReachers, visited) }
    return goalReachers.size - 1  // Don't count shiny gold itself
}

/** Memoized recursion to count number of bags contained. */
private fun numBagsContained(
    ruleSet: RuleSet,
    startColor: Color,
    memo: MutableMap<Color, Int>
): Int {
    memo[startColor]?.let { return it } // Return early if startColor in memo

    val result = ruleSet.getValue(startColor).contains.entries.sumOf { (nextColor, numRepeated) ->
        numRepeated * (1 + numBagsContained(ruleSet, nextColor, memo))
    }
    memo[startColor] = result
    return result
}

private fun part2(): Any {
    val ruleSet = getInput()
    val memo = mutableMapOf<Color, Int>()
    return numBagsContained(ruleSet, "shiny gold", memo)
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
