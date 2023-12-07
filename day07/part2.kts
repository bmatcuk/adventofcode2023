// --- Part Two ---
// To make things a little more interesting, the Elf introduces one additional
// rule. Now, J cards are jokers - wildcards that can act like whatever card
// would make the hand the strongest type possible.
//
// To balance this, J cards are now the weakest individual cards, weaker even
// than 2. The other cards stay in the same order: A, K, Q, T, 9, 8, 7, 6, 5,
// 4, 3, 2, J.
//
// J cards can pretend to be whatever card is best for the purpose of
// determining hand type; for example, QJJQ2 is now considered four of a kind.
// However, for the purpose of breaking ties between two hands of the same
// type, J is always treated as J, not the card it's pretending to be: JKKK2 is
// weaker than QQQQ2 because J is weaker than Q.
//
// Now, the above example goes very differently:
//
// 32T3K 765
// T55J5 684
// KK677 28
// KTJJT 220
// QQQJA 483
//
// - 32T3K is still the only one pair; it doesn't contain any jokers, so its
//   strength doesn't increase.
// - KK677 is now the only two pair, making it the second-weakest hand.
// - T55J5, KTJJT, and QQQJA are now all four of a kind! T55J5 gets rank 3,
//   QQQJA gets rank 4, and KTJJT gets rank 5.
//
// With the new joker rule, the total winnings in this example are 5905.
//
// Using the new joker rule, find the rank of every hand in your set. What are
// the new total winnings?

import java.io.*

fun cardToHash(card: Char): Int {
  return when (card) {
    'A' -> 13
    'K' -> 12
    'Q' -> 11
    'T' -> 10
    'J' -> 1
    else -> card - '0'
  }
}

class Hand(val hand: String, val bid: Int) : Comparable<Hand> {
  val handStrength: Int
  init {
    // Idea is simple: bottom 20 bits are for tie breaking: 2 bits per card, as
    // calculated by the cardToHash function above. Upper bits are for ranking
    // the hands.
    val cnts = hand.groupingBy { it }.eachCount().toMutableMap()
    if ('J' in cnts) {
      // add the number of jokers to whatever card has the highest count, not
      // including jokers themselves
      if (cnts.size == 1) {
        // hand is full of jokers
        cnts['A'] = 5
      } else {
        val (maxKey, maxCnt) = cnts.entries.filterNot { (k, _) -> k == 'J' }.maxBy { (_, v) -> v }
        cnts[maxKey] = maxCnt + cnts['J']!!
      }
      cnts.remove('J')
    }

    val tieBreakerBits = hand.withIndex().sumOf { (idx, card) -> cardToHash(card) shl ((4 - idx) * 4) }
    handStrength = when (cnts.size) {
      1 -> 7 shl 20   // five-of-a-kind
      2 -> when (cnts.values.max()) {
        4 -> 6 shl 20                  // four-of-a-kind
        3 -> 5 shl 20                  // full-house
        else -> throw Exception("err") // shouldn't happen
      }
      3 -> when (cnts.values.max()) {
        3 -> 4 shl 20                  // three-of-a-kind
        2 -> 3 shl 20                  // two-pair
        else -> throw Exception("err") // shouldn't happen
      }
      4 -> 2 shl 20                  // one-pair
      5 -> 1 shl 20                  // high-card
      else -> throw Exception("err") // shouldn't happen
    } + tieBreakerBits
  }

  override fun compareTo(other: Hand): Int {
    return this.handStrength - other.handStrength
  }

  override fun toString(): String {
    val rank = when (this.handStrength shr 20) {
      7 -> "five of a kind"
      6 -> "four of a kind"
      5 -> "full house"
      4 -> "three of kind"
      3 -> "two pair"
      2 -> "one pair"
      else -> "high card"
    }
    return "${this.hand} (%8x) $rank".format(this.handStrength)
  }
}

val hands = File("input.txt").readLines().map { it.split(' ').let { Hand(it[0], it[1].toInt()) } }.toMutableList()
hands.sort()
// hands.forEachIndexed { idx, hand -> println("%4d - %4d - $hand".format(idx + 1, hand.bid)) }

val result = hands.mapIndexed { idx, hand -> hand.bid.toLong() * (idx + 1) }.sum()
println(result)
