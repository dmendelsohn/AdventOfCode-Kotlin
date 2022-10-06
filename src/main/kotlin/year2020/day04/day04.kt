package year2020.day04

import java.io.File

private const val INPUT_PATH = "inputs/year2020/day04.txt"

typealias Passport = Map<String, String>

private fun parseEntry(entry: String): Pair<String, String> {
    return entry.split(":", limit = 2).let { Pair(it[0], it[1]) }
}

private fun parsePassport(text: String): Passport {
    return text.split("\n", " ").associate { parseEntry(it) }
}

private fun getPassports(): List<Passport> {
    return File(INPUT_PATH).readText().trim().split("\n\n").map { parsePassport(it) }
}

private fun isValidPart1(passport: Passport): Boolean {
    val requiredFields = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
    return passport.keys.containsAll(requiredFields)
}

private fun isValidPart2(passport: Passport): Boolean {
    return listOf(
        ValidationRule("byr", Regex("""(\d{4})""")) { it.destructured.toList().single().toInt() in 1920..2002 },
        ValidationRule("iyr", Regex("""(\d{4})""")) { it.destructured.toList().single().toInt() in 2010..2020 },
        ValidationRule("eyr", Regex("""(\d{4})""")) { it.destructured.toList().single().toInt() in 2020..2030 },
        ValidationRule("hgt", Regex("""(\d+)(cm|in)""")) {
            it.destructured.let { (num, unit) ->
                when (unit) {
                    "cm" -> num.toInt() in 150..193
                    "in" -> num.toInt() in 59..76
                    else -> throw IllegalStateException("Unexpected unit $unit")
                }
            }
        },
        ValidationRule("hcl", Regex("""#(\d|[a-f]){6}""")),
        ValidationRule("ecl", Regex("""(amb|blu|brn|gry|grn|hzl|oth)""")),
        ValidationRule("pid", Regex("""(\d{9})""")),
    ).all { it.apply(passport) }
}

private data class ValidationRule(
    val field: String,
    val regex: Regex,
    val validateMatch: ((MatchResult) -> Boolean)? = null
) {
    fun apply(passport: Passport): Boolean {
        val value = passport[field] ?: return false
        val match = regex.matchEntire(value) ?: return false
        return validateMatch == null || validateMatch.invoke(match)
    }
}


private fun part1(): Any {
    return getPassports().count { isValidPart1(it) }
}

private fun part2(): Any {
    return getPassports().count { isValidPart2(it) }
}

fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
