package year2020

import year2020.day01.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


private fun getTestInput(): String {
    return File("inputs/test01.txt").readText()
}

class Day01Test {
    @Test
    fun parseInput() {
        val rawInput = "1\n10\n100\n"
        val parsedInput = parseInput(rawInput)
        val expectedParsedInput = listOf(1, 10, 100)
        assertEquals(expectedParsedInput, parsedInput)
    }

    @Test
    fun part2_endToEnd() {
        val rawInput = getTestInput()
        val parsedInput = parseInput(rawInput)
        val result = part1(parsedInput)
        val expectedResult = 514579L
        assertEquals(expectedResult, result)
    }

    @Test
    fun part1_twoDistinctAddToTarget() {
        val input = listOf(1, 1000, 1020)
        val result = part1(input)
        val expectedResult = 1L * 1000 * 1020
        assertEquals(expectedResult, result)
    }

    @Test
    fun part1_cannotReachTarget() {
        val input = listOf(1, 1000, 2020)
        val result = part1(input)
        val expectedResult = -1L
        assertEquals(expectedResult, result)
    }

    @Test
    fun part1_twoSameAddToTarget() {
        val input = listOf(1, 1010, 1010)
        val result = part1(input)
        val expectedResult = 1L * 1010 * 1010
        assertEquals(expectedResult, result)
    }

    @Test
    fun part1_oneInstanceHalfTarget() {
        val input = listOf(1, 1010)
        val result = part1(input)
        val expectedResult = -1L
        assertEquals(expectedResult, result)
    }

    @Test
    fun part1_endToEnd() {
        val rawInput = getTestInput()
        val parsedInput = parseInput(rawInput)
        val result = part2(parsedInput)
        val expectedResult = 241861950L
        assertEquals(expectedResult, result)
    }

    @Test
    fun part2_threeAddToTarget() {
        val input = listOf(1, 2, 1000, 1018)
        val result = part2(input)
        val expectedResult = 1L * 2 * 1000 * 1018
        assertEquals(expectedResult, result)
    }

    @Test
    fun part2_cannotReachTarget() {
        val input = listOf(1, 2, 1000, 2000)
        val result = part2(input)
        val expectedResult = -1L
        assertEquals(expectedResult, result)
    }

    @Test
    fun part2_twoReachTarget() {
        val input = listOf(1, 2, 1000, 2018)
        val result = part2(input)
        val expectedResult = -1L
        assertEquals(expectedResult, result)
    }

    @Test
    fun part2_threeAddToTargetWithTwoDuplicate() {
        val input = listOf(1, 500, 500, 1020)
        val result = part2(input)
        val expectedResult = 1L * 500 * 500 * 1020
        assertEquals(expectedResult, result)
    }

    @Test
    fun part2_wouldReachTargetIfDuplicate() {
        val input = listOf(1, 500, 1020)
        val result = part2(input)
        val expectedResult = -1L
        assertEquals(expectedResult, result)
    }
}
