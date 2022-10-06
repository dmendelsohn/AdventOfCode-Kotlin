package year2017.day07

import java.io.File

private val DEFAULT_INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"

private fun getRawInput(inputPath: String): String {
    return File(inputPath).readText().trim()
}

data class Statement(val name: String, val weight: Int, val children: List<String>) {
    companion object {
        // Example input (with children): ugml (68) -> gyxo, ebii, jptl
        // Example input (no children): gyxo (61)
        fun parse(line: String): Statement {
            val match = Regex("""(\w+) \((\d+)\)(?: -> (.*))?""").matchEntire(line)
            checkNotNull(match) { "No regex match for input line: $line" }
            return match.destructured.let { (name, weight, children) ->
                Statement(name, weight.toInt(), children.split(", ").filterNot { it == "" })
            }
        }
    }
}

fun parseInput(rawInput: String): List<Statement> {
    return rawInput.split("\n").map { Statement.parse(it) }
}

fun getRoot(statements: List<Statement>): String {
    val parents = statements.map { it.name }.toSet()
    val children = statements.flatMap { it.children }.toSet()
    return (parents - children).single()
}

fun part1(input: List<Statement>): String {
    return getRoot(input)
}

/** Similar to statement, but with actual pointers to child nodes */
private data class Node(val name: String, val weight: Int, val children: Set<Node>) {

    val towerWeight: Int by lazy { weight + children.sumOf { it.towerWeight } }
    val isBalanced = children.map { it.towerWeight }.toSet().size <= 1

    /**
     * Gets all tower weights shared by all children (or all children except one).
     * The only case in which there can be two such weights is when there are two children with different tower weights.
     * Otherwise, there will be 0 or 1 such weights.
     */
    val potentialBalanceWeights by lazy {
        children.groupingBy { it.towerWeight }.eachCount()
            .filterValues { freq -> freq == children.size - 1 }.keys.toSet()
    }

//    /** Returns the shared tower weight of the child's siblings or null if the siblings don't have one unique weight. */
//    fun getSiblingTowerWeight(childName: String): Int? {
//        require(childName in children.map { it.name })
//        return children.filterNot { it.name == childName }.map { it.towerWeight }.singleOrNull()
//    }

    companion object {
        fun buildTree(rootName: String, statementsByName: Map<String, Statement>): Node {
            val rootStatement =
                statementsByName[rootName] ?: throw IllegalArgumentException("No statement with name $rootName")
            return Node(
                rootStatement.name,
                rootStatement.weight,
                rootStatement.children.map { buildTree(it, statementsByName) }.toSet()
            )
        }
    }
}

private data class Correction(val name: String, val oldWeight: Int, val newWeight: Int) {
    init {
        require(oldWeight > 0) { "oldWeight must be positive, not $oldWeight" }
        require(newWeight > 0) { "newWeight must be positive, not $newWeight" }
        require(newWeight != oldWeight) { "newWeight must be different than oldWeight (both are $newWeight)" }
    }

    val netChange = newWeight - oldWeight
}

/**
 * Return all corrections that (on their own) would balance the tower.
 * Throws [IllegalStateException] if there are no corrections that would balance the tower.
 * Throws [IllegalArgumentException] if the tower is already balanced.
 */
private fun fixImbalance(root: Node): Set<Correction> {
    if (root.isBalanced) {
        throw IllegalArgumentException("Cannot balance ${root.name} because it is already balanced.")
    }

    // At this point, we can be certain there are at least 2 children (otherwise this tower would be balanced).
    val imbalancedChildren = root.children.filterNot { it.isBalanced }
    when (imbalancedChildren.size) {
        0 -> {
            // Directly fix imbalance by changing a child so that its tower weight reaches a potential balance weight.
            // Making this (non-zero) change would ensure that all children are now equal at that balance weight.
            val corrections = root.potentialBalanceWeights.flatMap { targetTowerWeight ->
                root.children.mapNotNull { child ->
                    val requiredWeightChange = targetTowerWeight - child.towerWeight
                    val newWeight = child.weight + requiredWeightChange
                    if (requiredWeightChange != 0 && newWeight > 0) {
                        Correction(child.name, child.weight, newWeight)
                    } else {
                        null
                    }
                }
            }.toSet()
            if (corrections.isEmpty()) {
                "Cannot balance ${root.name} because no child can change to reach a balancing tower weight."
            }
            return corrections
        }
        1 -> {
            // Fix the one imbalanced child, and check that the new tower weight matches the sibling tower weights
            val imbalancedChild = imbalancedChildren.single()
            val corrections = fixImbalance(imbalancedChild).filter { correction ->
                root.potentialBalanceWeights.contains(imbalancedChild.towerWeight + correction.netChange)
            }.toSet()
            if (corrections.isEmpty()) {
                throw IllegalStateException(
                    "Cannot balance ${root.name} because no correction to imbalanced child" +
                            " ${imbalancedChild.name} results in balance with its siblings."
                )
            }
            return corrections

        }
        else -> throw IllegalStateException(
            "Cannot balance ${root.name} because it has multiple imbalanced children: " +
                    imbalancedChildren.joinToString { it.name }
        )
    }
}


fun part2(input: List<Statement>): Int {
    val rootName = getRoot(input)
    val statementsByName = input.associateBy { it.name }
    val root = Node.buildTree(rootName, statementsByName)
    return fixImbalance(root).single().newWeight
}


fun main(args: Array<String>) {
    val inputPath = args.firstOrNull() ?: DEFAULT_INPUT_PATH
    println("Running using input from $inputPath")
    val input = parseInput(getRawInput(inputPath))
    println("Solution to part 1: ${part1(input)}")
    println("Solution to part 2: ${part2(input)}")
}
