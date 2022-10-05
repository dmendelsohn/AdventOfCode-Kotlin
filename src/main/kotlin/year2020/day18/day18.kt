package year2020.day18

import java.io.File

private val INPUT_PATH = "inputs/${object {}.javaClass.packageName.split(".").last()}.txt"

private fun getInput(): List<Expression> {
    return File(INPUT_PATH).readLines().map { parseTokens(tokenize(it)) }
}

private enum class Operator(val symbol: String) { ADD("+"), MULTIPLY("*") }

private fun String.toOperator(): Operator {
    return Operator.values().firstOrNull { this == it.symbol }
        ?: throw IllegalArgumentException("Illegal operator: $this")
}


private interface Expression {
    fun evaluate1(): Long

    fun evaluate2(): Long
}

private class ComputedExpression(val children: List<Expression>, val operators: List<Operator>) : Expression {
    init {
        if (children.size != operators.size + 1) {
            throw IllegalArgumentException("Must have exactly one more child than operator")
        }
    }

    override fun evaluate1(): Long {
        val initialValue = children.first().evaluate1()
        val expressionsAndOperators = operators.zip(children.drop(1))
        return expressionsAndOperators.fold(initialValue) { acc, (operator, child) ->
            when (operator) {
                Operator.ADD -> acc + child.evaluate1()
                Operator.MULTIPLY -> acc * child.evaluate1()
            }
        }
    }

    override fun evaluate2(): Long {
        // Here a "group" is one or more consecutive expressions with only addition in between
        var result = 1L
        val initialValue = children.first().evaluate2()
        val expressionsAndOperators = operators.zip(children.drop(1))
        expressionsAndOperators.fold(initialValue) { latestGroupSum, (operator, child) ->
            when (operator) {
                Operator.ADD -> latestGroupSum + child.evaluate2()// Continue existing group
                Operator.MULTIPLY -> {
                    result *= latestGroupSum
                    child.evaluate2() // Start a new group
                }
            }
        }.let { finalGroupSum -> result *= finalGroupSum }
        return result
    }
}

private class ValueExpression(val value: Long) : Expression {
    override fun evaluate1(): Long {
        return value
    }

    override fun evaluate2(): Long {
        return value
    }
}

private enum class TokenType { NUMBER, OPERATOR, OPEN_PAREN, CLOSE_PAREN }

private data class Token(val tokenType: TokenType, val value: String)

/** Note that in our input, all tokens are a single character, which makes this really simple. */
private fun tokenize(text: String): List<Token> {
    return text.filterNot { it.isWhitespace() }.map { char ->
        val tokenType = when (char) {
            in "+*" -> TokenType.OPERATOR
            '(' -> TokenType.OPEN_PAREN
            ')' -> TokenType.CLOSE_PAREN
            in '0'..'9' -> TokenType.NUMBER
            else -> throw IllegalArgumentException("Could not tokenize character: $char")
        }
        Token(tokenType, char.toString())
    }
}


private fun parseTokens(tokens: List<Token>): Expression {
    if (tokens.size == 1) {
        tokens.single().let { token ->
            if (token.tokenType == TokenType.NUMBER) {
                return ValueExpression(token.value.toLong())
            } else {
                throw IllegalArgumentException("Illegal singleton token: $token")
            }
        }
    }

    // At this point, we're building a ComputedExpression
    val children = mutableListOf<Expression>()
    val operators = mutableListOf<Operator>()
    var idx = 0
    while (idx in tokens.indices) {
        val token = tokens[idx]
        when (token.tokenType) {
            TokenType.NUMBER -> children.add(ValueExpression(token.value.toLong())).also { idx++ }
            TokenType.OPERATOR -> operators.add(token.value.toOperator()).also { idx++ }
            TokenType.OPEN_PAREN -> {
                val matchingParenIdx = findMatchingParen(tokens, idx + 1)
                if (matchingParenIdx == idx + 1) {
                    throw UnsupportedOperationException("Empty parens not currently supported")
                }
                children.add(parseTokens(tokens.subList(idx + 1, matchingParenIdx)))
                idx = matchingParenIdx + 1
            }
            TokenType.CLOSE_PAREN -> throw IllegalArgumentException("Malformed statement, unexpected CLOSE_PAREN")
        }
    }
    return ComputedExpression(children.toList(), operators.toList())
}

/** Returns the index of a matching close paren */
private fun findMatchingParen(tokens: List<Token>, idxAfterOpen: Int): Int {
    var netParenCount = 1
    var idx = idxAfterOpen
    while (netParenCount > 0 && idx in tokens.indices) {
        val token = tokens[idx]
        when (token.tokenType) {
            TokenType.OPEN_PAREN -> netParenCount++
            TokenType.CLOSE_PAREN -> netParenCount--
            else -> Unit
        }
        idx++
    }
    if (netParenCount > 0) {
        throw IllegalArgumentException("Expression does not have matching closing paren")
    }
    return idx - 1
}

private fun part1(): Long {
    return getInput().sumOf { it.evaluate1() }
}

private fun part2(): Any {
    return getInput().sumOf { it.evaluate2() }
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
