package days.day20

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.split(".").last()}.txt"
private const val EDGE_LENGTH = 10
private const val TILE_GRID_LENGTH = 12


private typealias Edge = List<Boolean>

private fun Edge.toInt(): Int {
    return map { if (it) '1' else '0' }.joinToString("").toInt(2)
}

private fun minOf(first: Edge, second: Edge): Edge {
    return if (first.toInt() <= second.toInt()) first else second
}

private typealias TileId = Int

private data class Tile(val id: TileId, val grid: List<List<Boolean>>) {

    init {
        require(grid.size == EDGE_LENGTH) { "Grid must have $EDGE_LENGTH rows, not ${grid.size}" }
        grid.forEach { require(it.size == EDGE_LENGTH) { "Row must have $EDGE_LENGTH cells, not ${it.size}" } }
    }

    /** Returns edges (top, right, bottom, left), reading values in the clockwise direction */
    fun getEdges(): List<Edge> {
        return listOf(
            grid[0],
            grid.map { row -> row.last() },
            grid.last().reversed(),
            grid.map { row -> row.first() }.reversed(),
        )
    }

    override fun toString(): String {
        val renderedGrid = grid.joinToString("\n") { row -> row.map { if (it) '#' else '.' }.joinToString("") }
        return "Tile: $id:\n$renderedGrid"
    }

    companion object {
        fun parse(text: String): Tile {
            var lines = text.split("\n")
            val id = Regex("""Tile (\d+):""").matchEntire(lines.first())!!.destructured.let { (value) -> value.toInt() }
            val grid = lines.drop(1).map { line -> line.map { char -> char == '#' } }
            return Tile(id, grid)
        }
    }
}

private fun getInput(): List<Tile> {
    return File(INPUT_PATH).readText().trim().split("\n\n").map { Tile.parse(it) }
}

private fun Edge.matches(other: Edge): Boolean {
    return this == other || this == other.reversed()
}

private fun Tile.matches(other: Tile): Boolean {
    return getEdges().any { thisEdge ->
        other.getEdges().any { otherEdge ->
            thisEdge.matches(otherEdge)
        }
    }
}

private fun getMatches(tiles: Map<TileId, Tile>): Map<TileId, Set<TileId>> {
    return tiles.mapValues { (_, tile) ->
        tiles.values.filter { other ->
            tile.id != other.id && tile.matches(other)
        }.map { it.id }.toSet()
    }
}

private fun isBoundaryTile(index: Int): Boolean {
    // We don't need a separate method for row and cols because this is a square.
    return index == 0 || index == TILE_GRID_LENGTH - 1
}

private fun selectTile(
    row: Int,
    col: Int,
    knownNeighborsIds: Set<TileId>,
    matches: Map<TileId, Set<TileId>>,
    unusedTileIds: Set<TileId>,
): TileId {
    // We can find the relevant tile using just the match data
    // This works thanks to the assumptions [confirmAssumptions]...there are no extra possible matches.
    // Sometimes there are a few options (e.g. the initial choice of the corner tile)
    // In practice this will not matter, and should just yield rotations/flips of the "true" solution.
    val numNeighbors = 2 + listOf(isBoundaryTile(row), isBoundaryTile(col)).filterNot { it }.size
    return matches.filter { (tileId, neighbors) ->
        tileId in unusedTileIds && neighbors.size == numNeighbors && neighbors.containsAll(knownNeighborsIds)
    }.keys.first()
}

/** Rotate 90 degrees clockwise */
private fun Tile.rotate90(): Tile {
    val rotatedGrid = mutableListOf<MutableList<Boolean>>()
    for (row in 0 until EDGE_LENGTH) {
        rotatedGrid.add(mutableListOf())
        for (col in 0 until EDGE_LENGTH) {
            rotatedGrid.last().add(this.grid[EDGE_LENGTH - 1 - col][row])
        }
    }
    return Tile(this.id, rotatedGrid.map { row -> row.toList() })
}

/** Flip about horizontal axis */
private fun Tile.flip(): Tile {
    return Tile(id, grid.reversed())
}

private fun Tile.isValidFit(above: Tile?, left: Tile?): Boolean {
    val edges = this.getEdges()
    return when {
        above != null -> edges[0] == above.grid.last()
        left != null -> edges[3] == left.grid.map { row -> row.last() }.reversed()
        else -> throw IllegalArgumentException("Cannot check fit without a left or above tile")
    }
}

private fun orientTile(tile: Tile, above: Tile?, left: Tile?): Tile {
    // Just try all orientations
    val allRotations = mutableListOf(tile)
    repeat(3) { allRotations.add(allRotations.last().rotate90()) }
    val allOrientations = allRotations.flatMap { listOf(it, it.flip()) }
    return allOrientations.first { it.isValidFit(above, left) }
}

private fun orientTopLeftTile(tile: Tile, neighbors: Collection<Tile>): Tile {
    require(neighbors.size == 2) { "Top left corner must have exactly two neighbors" }
    val edgesWithoutMatch =
        tile.getEdges().withIndex().filterNot { (_, edge) ->
            val neighborEdges = neighbors.flatMap { it.getEdges() }
            neighborEdges.any { it.matches(edge) }
        }.map { it.index }.toSet()

    return when (edgesWithoutMatch) {
        setOf(3, 0) -> tile
        setOf(2, 3) -> tile.rotate90()
        setOf(1, 2) -> tile.rotate90().rotate90()
        setOf(0, 1) -> tile.rotate90().rotate90().rotate90()
        else -> throw IllegalStateException("Unexpected non-matching edges: $edgesWithoutMatch")
    }
}


private fun arrangeTiles(tiles: Map<TileId, Tile>): List<List<Tile>> {
    // Initialize state (including grid full of empty rows)
    val grid = (0 until TILE_GRID_LENGTH).map { mutableListOf<Tile>() }.toMutableList()
    val unusedTileIds = tiles.keys.toMutableSet()

    // Precompute which tiles match with other tiles
    val matches = getMatches(tiles)

    // Step 2: Find a corner tile to be our top-left tile and place it
    val cornerTileId = matches.filterValues { it.size == 2 }.keys.first()
    val cornerTileNeighbors = matches.getValue(cornerTileId).map { tiles.getValue(it) }
    val orientedCornerTile = orientTopLeftTile(tiles.getValue(cornerTileId), cornerTileNeighbors)
    grid[0].add(orientedCornerTile)
    unusedTileIds.remove(cornerTileId)

    // Build grid row by row
    for (row in 0 until TILE_GRID_LENGTH) {
        for (col in 0 until TILE_GRID_LENGTH) {
            if (row == 0 && col == 0) continue  // Already placed corner

            // Determine the tile to place here and its orientation
            val aboveNeighbor = if (row > 0) grid[row - 1][col] else null
            val leftNeighbor = if (col > 0) grid[row][col - 1] else null
            val knownNeighborIds = setOfNotNull(leftNeighbor?.id, aboveNeighbor?.id)
            val tileId = selectTile(row, col, knownNeighborIds, matches, unusedTileIds)
            val orientedTile = orientTile(tiles.getValue(tileId), above = aboveNeighbor, left = leftNeighbor)

            // Update state
            grid[row].add(orientedTile)
            unusedTileIds.remove(tileId)
        }
    }

    // Return immutable copy
    return grid.map { row -> row.toList() }
}

private fun part1(): Long {
    val tiles = getInput().associateBy { it.id }
    return arrangeTiles(tiles).let {
        1L * it.first().first().id * it.first().last().id * it.last().first().id * it.last().last().id
    }
}

private fun part2(): Any {
    return "Not implemented"
}


/**
 * Helper to confirm some simplifying assumptions I have about our input
 */
private fun confirmAssumptions() {
    val tiles = getInput()

    /*
    * A {K, V} entry in the output means there are V unique edges that appear precisely K times
    * Note that two edges are considered equivalent if one is a reversal of the other.
    * Our puzzle input, this function prints {2=264, 1=48}.
    * There are 264 unique "edge locations" in our 12x12 grid of tiles.
    * This confirms my hypothesis that there are no extraneous matches: if two edges can match they must be aligne
    */
    val appearancesPerEdge =
        tiles.flatMap { it.getEdges() }.groupBy { minOf(it, it.reversed()) }.values.map { it.size }
    val numEdgesPerFreqCount = appearancesPerEdge.groupingBy { it }.eachCount()
    println("Number of unique edges with given frequency count: $numEdgesPerFreqCount")

    /*
    * Palindromes are confusing because it is ambiguous whether a matching edge should be flipped
    * This check confirms there are no palindromic edges (thanks Advent of Code!)
    */
    val numPalindromes = tiles.flatMap { it.getEdges() }.count { it == it.reversed() }
    println("Number of edges that are palindromes $numPalindromes")

}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
