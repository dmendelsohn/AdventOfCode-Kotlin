package year2020.day16

import java.io.File
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min

private val INPUT_PATH = "inputs/year2020/${object {}.javaClass.packageName.split(".").last()}.txt"

private fun getInput(): Triple<List<Rule>, Ticket, List<Ticket>> {
    val inputParts = File(INPUT_PATH).readText().trim().split("\n\n").map { it.split("\n") }

    // Input text validation
    if (inputParts.size != 3) {
        throw IllegalArgumentException("Got ${inputParts.size} input sections, expected 3 input sections.")
    }
    if (inputParts[1].size != 2 || !inputParts[1].first().contains("your ticket")) {
        throw IllegalArgumentException("Invalid second section: ${inputParts[1]}")
    }
    if (!inputParts[2].first().contains("nearby tickets")) {
        throw IllegalArgumentException("Invalid third section has first line ${inputParts[2].first()}")
    }

    val rules = inputParts[0].map { parseRule(it) }
    val myTicket = inputParts[1].last().let { Ticket.parse(it) }
    val otherTickets = inputParts[2].drop(1).map { Ticket.parse(it) }
    return Triple(rules, myTicket, otherTickets)
}


private data class Ticket(val values: List<Int>) {
    companion object {
        fun parse(text: String): Ticket {
            return Ticket(text.split(",").map { it.toInt() })
        }
    }
}

private typealias Rule = Pair<String, DisjointRangeSet>

private fun parseRule(text: String): Rule {
    val (field, rest) = text.split(": ", limit = 2)
    val ranges = rest.split(" or ").map { ValueRange.parse(it) }
    return Pair(field, DisjointRangeSet.build(ranges))
}

private data class ValueRange(val first: Int, val last: Int) {
    init {
        if (first > last) {
            throw IllegalArgumentException("Invalid range $first - $last")
        }
    }

    /**
     * Merged with other value range, or returns null if disjoint.
     * Note that ranges that are adjacent (e.g. 1-3 4-6) are not considered disjoint and can be merged.
     */
    fun merge(other: ValueRange): ValueRange? {
        return if (first <= other.last + 1 && last >= other.first - 1) {
            ValueRange(min(first, other.first), max(last, other.last))
        } else {
            null
        }
    }

    override fun toString(): String {
        return "$first-$last"
    }

    companion object {
        fun parse(text: String): ValueRange {
            return text.split("-").let { (first, last) -> ValueRange(first.toInt(), last.toInt()) }
        }
    }
}

private class DisjointRangeSet private constructor(val sortedRanges: List<ValueRange>) {

    /**
     * For now this is a naive implementation.
     * We can make it faster by binary searching for the relevant ValueRange
     * In practice, these sets have very few disjoint ranges so this isn't really necessary.
     */
    fun contains(value: Int): Boolean {
        return sortedRanges.any { value in it.first..it.last }
    }

    override fun toString(): String {
        return sortedRanges.toString()
    }

    companion object {
        /** Builds a disjoint range set by sorting the ranges then merging overlapping/adjacent ones */
        fun build(ranges: Iterable<ValueRange>): DisjointRangeSet {
            val disjointSortedRanges = mutableListOf<ValueRange>()
            val finalRange = ranges.sortedBy { it.first }.reduce { lastRange, nextRange ->
                val mergedRange = lastRange.merge(nextRange)
                if (mergedRange == null) {
                    // Ranges were not overlapping, commit lastRange and return nextRange
                    disjointSortedRanges.add(lastRange)
                    nextRange
                } else {
                    // Ranges were overlapping, commit nothing and return mergedRange
                    mergedRange
                }
            }
            disjointSortedRanges.add(finalRange) // Commit the final range
            return DisjointRangeSet(disjointSortedRanges.toList())
        }
    }
}

private fun part1(): Int {
    val (rules, _, tickets) = getInput()
    val combinedDisjointRangeSet = DisjointRangeSet.build(rules.flatMap { (_, rangeSet) -> rangeSet.sortedRanges })
    return tickets.flatMap { it.values }.filterNot { combinedDisjointRangeSet.contains(it) }.sum()
}

/** Determine the order of fields as they appear on each ticket */
private fun getFieldNames(rules: List<Rule>, tickets: List<Ticket>): List<String> {

    // Initially, every index can be associated with any field
    val fieldSet = rules.map { (field, _) -> field }.toSet()
    val possibilities = fieldSet.indices.associateWith { fieldSet.toMutableSet() }

    // Eliminate fields per-index by seeking rule violations in ticket numbers
    tickets.forEach { ticket ->
        ticket.values.withIndex().forEach { (index, value) ->
            rules.forEach { (field, rangeSet) ->
                if (!rangeSet.contains(value)) {
                    possibilities.getValue(index).remove(field)
                }
            }
        }
    }

    // Iteratively find indices that have only one possible field.
    // Remove that field entirely.
    // Eventually, all the values in our possibilities map should be empty sets
    val fieldOrder = MutableList(fieldSet.size) { "" }
    while (possibilities.values.any { it.size > 0 }) {
        // The call to `first()` will fail if the input is insufficiently constraining (i.e. no unique solution)
        val (index, fields) = possibilities.entries.first { (_, possibleFields) -> possibleFields.size == 1 }
        val fixedField = fields.single()
        fieldOrder[index] = fixedField
        possibilities.forEach { (_, remainingFields) -> remainingFields.remove(fixedField) }
    }

    // A bit of validation before returning
    if (fieldOrder.toSet() != fieldSet) {
        throw IllegalStateException("Invalid field order: $fieldOrder")
    }
    return fieldOrder
}

private fun part2(): BigInteger {
    val (rules, myTicket, otherTickets) = getInput()
    val combinedDisjointRangeSet = DisjointRangeSet.build(rules.flatMap { (_, rangeSet) -> rangeSet.sortedRanges })
    val validTickets = otherTickets.filter { it.values.all { value -> combinedDisjointRangeSet.contains(value) } }
    val fieldNames = getFieldNames(rules, validTickets)
    return fieldNames.zip(myTicket.values).filter { (field, _) -> field.startsWith("departure") }
        .map { (_, value) -> BigInteger.valueOf(value.toLong()) }.reduce { acc, i -> acc * i }
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
