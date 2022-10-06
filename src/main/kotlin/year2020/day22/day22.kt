package year2020.day22

import java.io.File
import java.util.LinkedList

private val INPUT_PATH = "inputs/year2020/${object {}.javaClass.packageName.split(".").last()}.txt"

private typealias Deck = LinkedList<Int>
private typealias DeckSnapshot = List<Int>

private fun Deck.toSnapshot() = toList()

private fun parseDeck(text: String): Deck {
    val lines = text.split("\n")
    require(lines.first().contains("Player"))
    return Deck(lines.drop(1).map { it.toInt() })
}

private fun getInput(): Pair<Deck, Deck> {
    val (section1, section2) = File(INPUT_PATH).readText().trim().split("\n\n", limit = 2)
    return Pair(parseDeck(section1), parseDeck(section2))
}

private fun playCombat(deck1: Deck, deck2: Deck): Deck {
    var numRounds = 0
    while (deck1.isNotEmpty() && deck2.isNotEmpty()) {
        numRounds++

        val card1 = deck1.remove()
        val card2 = deck2.remove()
        if (card1 > card2) {
            deck1.addAll(listOf(card1, card2))
        } else {
            deck2.addAll(listOf(card2, card1))
        }
    }
    return listOf(deck1, deck2).first { it.isNotEmpty() }
}

/** Boolean is true if winning player was player 1, false if winning player was player 2 */
private fun playRecursiveCombat(deck1: Deck, deck2: Deck): Pair<Deck, Boolean> {
    val statesSeen = mutableSetOf<Pair<DeckSnapshot, DeckSnapshot>>()
    while (true) {
        // Check for regular game completion
        if (deck1.isEmpty()) {
            return Pair(deck2, false)
        }
        if (deck2.isEmpty()) {
            return Pair(deck1, true)
        }

        // Check for loop-triggered completion
        val state = Pair(deck1.toSnapshot(), deck2.toSnapshot())
        if (state in statesSeen) {
            return Pair(deck1, true)
        } else {
            statesSeen.add(state)
        }

        val card1 = deck1.remove()
        val card2 = deck2.remove()
        val isPlayer1Win = if (deck1.size >= card1 && deck2.size >= card2) {
            // Play a recursive game to resolve the round
            val subdeck1 = Deck(deck1.take(card1))
            val subdeck2 = Deck(deck2.take(card2))
            playRecursiveCombat(subdeck1, subdeck2).second

        } else {
            // Can't recurse, do the regular way of resolving the round
            card1 > card2
        }

        if (isPlayer1Win) {
            deck1.addAll(listOf(card1, card2))
        } else {
            deck2.addAll(listOf(card2, card1))
        }
    }
}

private fun getScore(deck: Deck): Long {
    return deck.reversed().withIndex().sumOf { (1L + it.index) * it.value }
}

private fun part1(): Long {
    val (deck1, deck2) = getInput()
    val winningDeck = playCombat(deck1, deck2)
    return getScore(winningDeck)
}

private fun part2(): Long {
    val (deck1, deck2) = getInput()
    val (winningDeck, _) = playRecursiveCombat(deck1, deck2)
    return getScore(winningDeck)
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
