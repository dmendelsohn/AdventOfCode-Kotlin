package year2017.day12

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

fun parseInput(rawInput: String): Graph {
    val graph = mutableMapOf<Int, Set<Int>>()
    rawInput.split("\n").forEach { line ->
        val (left, right) = line.split(" <-> ", limit = 2)
        graph[left.toInt()] = right.split(", ").map { it.toInt() }.toSet()
    }

    // Validate that all edge's reverse appears in graph
    graph.entries.forEach { (node, neighbors) ->
        neighbors.forEach { neighbor ->
            require(graph.getValue(neighbor).contains(node)) {
                "Found edge $node -> $neighbor that is not bidirectional."
            }
        }
    }

    return graph.toMap()
}

private typealias Graph = Map<Int, Set<Int>>

private fun Graph.connectedComponents(): Set<Set<Int>> {
    val visited = mutableSetOf<Int>()
    val components = mutableSetOf<Set<Int>>()
    for (node in keys) {
        if (node in visited) continue
        val component = connectedComponentOf(node)
        visited.addAll(component)
        components.add(component)
    }
    return components.toSet()
}

private fun Graph.connectedComponentOf(node: Int): Set<Int> {
    val queue = ArrayDeque(listOf(node))
    val component = mutableSetOf<Int>()
    while (!queue.isEmpty()) {
        val node = queue.removeFirst()
        if (node in component) continue
        component.add(node)
        queue.addAll(getValue(node))
    }
    return component.toSet()
}

fun part1(graph: Graph): Int {
    return graph.connectedComponents().first { it.contains(0) }.size
}

fun part2(graph: Graph): Any {
    return graph.connectedComponents().size
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
