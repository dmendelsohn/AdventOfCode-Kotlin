package year2017.day09

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

private sealed class Element {
    abstract fun getGarbageQuantity(): Int
}

private class Group(val elements: List<Element>) : Element() {

    /**
     * Recursively get score of this group.
     *
     * @param depth: number of groups that encompass this group.
     */
    fun getScore(depth: Int = 0): Int {
        return depth + 1 + elements.filterIsInstance<Group>().sumOf { it.getScore(depth + 1) }
    }

    override fun getGarbageQuantity(): Int {
        return elements.sumOf { it.getGarbageQuantity() }
    }

    companion object {
        /** Returns group and index at which to continue parsing next element */
        fun parse(text: String, startIdx: Int): Pair<Group, Int> {
            if (text[startIdx] != '{') {
                throw IllegalArgumentException("Group literals must start with '{', not '${text[startIdx]}'")
            }
            var currentIdx = startIdx + 1
            val elements = mutableListOf<Element>()
            while (text.getOrNull(currentIdx) != '}') {
                if (currentIdx > text.lastIndex) {
                    throw IllegalArgumentException("Reached end of string while parsing: $text")
                }
                when (text[currentIdx]) {
                    '{' -> parse(text, currentIdx)
                    '<' -> Garbage.parse(text, currentIdx)
                    ',' -> Pair(null, currentIdx + 1) // Continue past commas
                    else -> throw IllegalArgumentException("Unexpected first character of element: ${text[currentIdx]}")
                }.let { (element, nextIdx) ->
                    if (element != null) elements.add(element)
                    currentIdx = nextIdx
                }
            }
            return Pair(Group(elements.toList()), currentIdx + 1)
        }

    }
}

private class Garbage(val contents: String) : Element() {

    override fun getGarbageQuantity(): Int {
        var garbageQuantity = 0
        var currentIdx = 0
        while (currentIdx in contents.indices) {
            if (contents[currentIdx] == '!') {
                // Don't count the escape char as garbage and skip the next char
                currentIdx += 2
            } else {
                garbageQuantity++
                currentIdx++
            }
        }
        return garbageQuantity
    }

    companion object {
        /** Returns garbage and index at which to continue parsing next element */
        fun parse(text: String, startIdx: Int): Pair<Garbage, Int> {
            if (text[startIdx] != '<') {
                throw IllegalArgumentException("Garbage literals must start with '<', not '${text[startIdx]}'")
            }
            var currentIdx = startIdx + 1
            while (text.getOrNull(currentIdx) != '>') {
                if (currentIdx > text.lastIndex) {
                    throw IllegalArgumentException("Reached end of string while parsing: $text")
                }
                currentIdx += when (text[currentIdx]) {
                    '!' -> 2  // Skip next character, which is cancelled
                    else -> 1
                }
            }
            val contents = text.substring((startIdx + 1) until currentIdx)
            return Pair(Garbage(contents), currentIdx + 1)
        }
    }
}

fun part1(input: String): Int {
    val (outerGroup, nextIdx) = Group.parse(input, 0)
    check(nextIdx == input.lastIndex + 1) { "Dangling text not part of outer group: ${input.drop(nextIdx)}" }
    return outerGroup.getScore()
}

fun part2(input: String): Int {
    val (outerGroup, nextIdx) = Group.parse(input, 0)
    check(nextIdx == input.lastIndex + 1) { "Dangling text not part of outer group: ${input.drop(nextIdx)}" }
    return outerGroup.getGarbageQuantity()
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = getRawInput(inputPath)
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
