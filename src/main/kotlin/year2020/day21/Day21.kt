package year2020.day21

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.replace(".", "/")}.txt"
// private const val INPUT_PATH = "inputs/year2020/test21.txt"

private typealias Ingredient = String
private typealias Allergen = String

private data class Food(val ingredients: Set<Ingredient>, val allergens: Set<Allergen>) {
    companion object {
        fun parse(line: String): Food {
            val (ingredientPart, allergenPart) = line.dropLast(1).split(" (contains ")
            return Food(ingredientPart.split(" ").toSet(), allergenPart.split(", ").toSet())
        }
    }
}

private fun getInput(): List<Food> {
    return File(INPUT_PATH).readLines().map { Food.parse(it) }
}

/** Get map of allergens to ingredient known to contain it */
private fun getSafeIngredients(foods: List<Food>): Map<Allergen, Ingredient> {
    // Initially, all ingredients are possible location for each allergen
    val allIngredients = foods.flatMap { it.ingredients }.toSet()
    val allAllergens = foods.flatMap { it.allergens }.toSet()
    val possibleLocations = allAllergens.associateWith { allIngredients.toMutableSet() }.toMutableMap()

    // Iterate over foods and allergens, reducing possible ingredients via set intersection
    for (food in foods) {
        for (allergen in food.allergens) {
            possibleLocations.getValue(allergen).retainAll(food.ingredients)
        }
    }

    // Iteratively find singleton possibilities and remove them as possibilities for other allergens
    val knownAllergenLocations = mutableMapOf<Allergen, Ingredient>()
    while (possibleLocations.isNotEmpty()) {
        // Find a singleton or break if there are no more singletons
        val singletonEntry = possibleLocations.entries.firstOrNull { it.value.size == 1 } ?: break
        val (allergen, ingredient) = singletonEntry.let { it.key to it.value.single() }

        // Update state
        knownAllergenLocations[allergen] = ingredient
        possibleLocations.remove(allergen)
        possibleLocations.values.forEach { it.remove(ingredient) }
    }

    return knownAllergenLocations.toMap()
}

/** An ingredient is safe if it only appears in foods where all allergens are accounted for. */
private fun getSafeIngredients(foods: List<Food>, knownAllergenLocations: Map<Allergen, Ingredient>): Set<Ingredient> {
    // Start with all ingredients that aren't known to be allergenic already
    val possiblySafeIngredients =
        foods.flatMap { it.ingredients }.subtract(knownAllergenLocations.values.toSet()).toMutableSet()
    for (food in foods) {
        // If this food has an unaccounted for allergen, none of its ingredients are safe
        if (!food.allergens.all { it in knownAllergenLocations.keys }) {
            possiblySafeIngredients.removeAll(food.ingredients)
        }
    }
    return possiblySafeIngredients.toSet()
}

private fun part1(): Int {
    val foods = getInput()
    val knownAllergenLocations = getSafeIngredients(foods)
    val safeIngredients = getSafeIngredients(foods, knownAllergenLocations)
    return foods.sumOf { it.ingredients.intersect(safeIngredients).size }
}

private fun part2(): String {
    val foods = getInput()
    val knownAllergenLocations = getSafeIngredients(foods)
    return knownAllergenLocations.entries.sortedBy { it.key }.map { it.value }.joinToString(",")
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
