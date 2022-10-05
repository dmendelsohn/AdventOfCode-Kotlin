package year2020.day25

private const val SUBJECT = 7L
private const val MODULO = 20201227L

private val TEST_PUBLIC_KEYS = Pair(5764801L, 17807724L)
private val PUBLIC_KEYS = Pair(9033205L, 9281649L)

private fun getInput(): Pair<Long, Long> {
    return PUBLIC_KEYS
}

/** Return value N such that (base^N) % modulo = this */
private fun Long.moduloLog(base: Long, modulo: Long): Long {
    // Iteratively try every number (this is linear in the size of modulo, which is okay for this problem)
    var product = 1L
    var logValue = 0L
    while (this != product) {
        product = (product * base).mod(modulo)
        logValue++
    }
    return logValue
}

/** Raise value to exponent mod modulo */
private fun Long.exp(exponent: Long, modulo: Long): Long {
    // This linear time algorithm is fine because our discrete log algorithm is linear anyway.
    // The log time algorithm isn't too crazy, but linear time is dead simple.
    var product = 1L
    repeat(exponent.toInt()) { product = (product * this).mod(modulo) }
    return product
}

private fun part1(): Long {
    // Note: I say "private key" where the problem says "loop number"
    val (publicKey1, publicKey2) = getInput()
    val privateKey1 = publicKey1.moduloLog(base = SUBJECT, modulo = MODULO)
    val privateKey2 = publicKey2.moduloLog(base = SUBJECT, modulo = MODULO)

    val encryptedValue1 = publicKey1.exp(exponent = privateKey2, modulo = MODULO)
    val encryptedValue2 = publicKey2.exp(exponent = privateKey1, modulo = MODULO)
    check(encryptedValue1 == encryptedValue2)
    return encryptedValue1
}

private fun part2(): Any {
    return "N/A - freebie"
}


fun main(args: Array<String>) {
    println("Solution to part 1: ${part1()}")
    println("Solution to part 2: ${part2()}")
}
