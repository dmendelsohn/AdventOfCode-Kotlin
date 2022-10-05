package year2020.day19

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.split(".").last()}.txt"

/*
A few notes on nomenclature so that this matches CFG terminology better:
- Where the problem talks about "rules", I call those "non-terminal symbols"
- To be consistent with terminal symbols (e.g. "a"), I'm keeping these non-terminal symbols as strings (e.g. "25")
- Each non-terminal symbol is associated with one or two production rules.
- Happily, the production rules in the input are already in Chomsky Normal Form.
 */
private typealias Symbol = String

private fun Symbol.isNonTerminal(): Boolean {
    return this.any { it.isDigit() }
}

private const val START_SYMBOL = "0"

/**
 * Represents a production rule in "nearly" Chomsky normal form
 * Only exception is A -> B rules where right hand is non-terminal symbol is non-terminal
 */
private data class ProductionRule(val fromSymbol: Symbol, val toSymbols: Pair<Symbol, Symbol?>) {

    // Only non-CNF rules in our input have a lone non-terminal symbol
    val isCnf = !(toSymbols.first.isNonTerminal() && toSymbols.second == null)

    companion object {
        /**
         * Possible formats
         * - 112: 35 72
         * - 8: 42
         * - 2: 35 21 | 72 80
         * - 35: "a"
         */
        fun parseLine(line: String): List<ProductionRule> {
            val (fromSymbol, rest) = line.split(": ", limit = 2)
            return rest.split(" | ").map { rightClause ->
                // Drop quote chars from terminal symbols (no effect on non-terminal symbols)
                rightClause.split(" ").map { symbol -> symbol.replace("\"", "") }
            }.map { rightSymbols ->
                if (rightSymbols.size > 2) {
                    throw UnsupportedOperationException("We don't support production rules with > 2 right hand parts")
                }
                ProductionRule(fromSymbol, Pair(rightSymbols.first(), rightSymbols.getOrNull(1)))
            }
        }
    }
}

private fun getInput(): Pair<List<ProductionRule>, List<String>> {
//    return Pair(
//        listOf(
//            ProductionRule("0", Pair("4", "6")),
//            ProductionRule("6", Pair("1", "5")),
//            ProductionRule("1", Pair("2", "3")),
//            ProductionRule("1", Pair("3", "2")),
//            ProductionRule("2", Pair("4", "4")),
//            ProductionRule("2", Pair("5", "5")),
//            ProductionRule("3", Pair("4", "5")),
//            ProductionRule("3", Pair("5", "4")),
//            ProductionRule("4", Pair("a", null)),
//            ProductionRule("5", Pair("b", null)),
//        ),
//        listOf(
//            "ababbb",
//            "bababa",
//            "abbbab",
//            "aaabbb",
//            "aaaabbb",
//        ),
//    )
    val (ruleLines, words) = File(INPUT_PATH).readText().trim().split("\n\n", limit = 2).map { it.split("\n") }
    return Pair(ruleLines.flatMap { ProductionRule.parseLine(it) }, words)
}

/** Implementation of https://en.wikipedia.org/wiki/CYK_algorithm */
private fun isValidWord(rules: List<ProductionRule>, word: String): Boolean {
    // 3D array
    // - first dimension means "substring of length {index}"
    // - second dimension means "substring starting at position {index}"
    // - third dimension means "starting symbol, when cast to int, equals idx"
    val maxRuleNum = rules.maxOf { it.fromSymbol.toInt() }
    val memo = Array(word.length + 1) { Array(word.length) { BooleanArray(maxRuleNum + 1) } }

    // Initially note which terminal production rules satisfy the length-1 substrings
    for (charIdx in word.indices) {
        for (rule in rules) {
            if (rule.toSymbols.first == word[charIdx].toString()) {
                memo[1][charIdx][rule.fromSymbol.toInt()] = true
            }
        }
    }

    // Dynamic programming to build up sub solutions
    // - outermost: from shorter to longer substrings
    // - next: from left to right starting positions
    // - next: from left to right "partition points" withing the substring
    // - innermost: for each non-terminal rule
    for (substringLength in 2..word.length) {
        for (substringStartIdx in 0..word.length - substringLength) {
            for (firstHalfLength in 1 until substringLength) {
                for (rule in rules) {
                    val (fromSymbol, toSymbols) = rule
                    val (firstToSymbol, secondToSymbol) = toSymbols
                    secondToSymbol ?: continue // Continue past non-terminal rules

                    val secondHalfLength = substringLength - firstHalfLength
                    val secondHalfStartIdx = substringStartIdx + firstHalfLength
                    if (
                        memo[firstHalfLength][substringStartIdx][firstToSymbol.toInt()] &&
                        memo[secondHalfLength][secondHalfStartIdx][secondToSymbol.toInt()]
                    ) {
                        memo[substringLength][substringStartIdx][fromSymbol.toInt()] = true
                    }
                }
            }
        }
    }

    // Finally check the spot we care about (full length substring with correct starting symbol)
    return memo[word.length][0][START_SYMBOL.toInt()]
}

/**
 * Replace A -> (non-terminal) B rules with proper CNF rules
 */
private fun normalizeUnitRules(rules: List<ProductionRule>): List<ProductionRule> {
    val rulesByFromSymbol = rules.groupBy { it.fromSymbol }
    val normalizedRules = mutableListOf<ProductionRule>()
    rules.forEach { rule ->
        if (rule.isCnf) {
            normalizedRules.add(rule)
        } else {
            // Expand a unit rule
            val expandedRules = rulesByFromSymbol.getOrDefault(rule.toSymbols.first, emptyList()).map { nextRule ->
                ProductionRule(rule.fromSymbol, nextRule.toSymbols)
            }
            // In theory, we could iteratively expand non CNF rules.
            // In practice, this isn't necessary for our input.
            expandedRules.forEach {
                if (!it.isCnf) throw UnsupportedOperationException("Expansion to non-CNF rule not supported")
            }
            normalizedRules.addAll(expandedRules)
        }
    }

    return normalizedRules.toList()
}

private fun part1(): Int {
    val (rules, words) = getInput()
    val normalizedRules = normalizeUnitRules(rules)
    return words.count { isValidWord(normalizedRules, it) }
}

private fun part2(): Any {
    val (rules, words) = getInput()
    val nextUnusedSymbol =
        (rules.map { it.fromSymbol }.filter { it.isNonTerminal() }.maxOf { it.toInt() } + 1).toString()
    val normalizedRules = normalizeUnitRules(rules) + listOf(
        ProductionRule("8", Pair("42", "8")),
        ProductionRule("11", Pair("42", nextUnusedSymbol)),
        ProductionRule(nextUnusedSymbol, Pair("11", "31")),
    )
    return words.count { isValidWord(normalizedRules, it) }
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
