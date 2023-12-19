// --- Part Two ---
// Even with your help, the sorting process still isn't fast enough.
//
// One of the Elves comes up with a new plan: rather than sort parts
// individually through all of these workflows, maybe you can figure out in
// advance which combinations of ratings will be accepted or rejected.
//
// Each of the four ratings (x, m, a, s) can have an integer value ranging from
// a minimum of 1 to a maximum of 4000. Of all possible distinct combinations
// of ratings, your job is to figure out which ones will be accepted.
//
// In the above example, there are 167409079868000 distinct combinations of
// ratings that will be accepted.
//
// Consider only your list of workflows; the list of part ratings that the
// Elves wanted you to sort is no longer relevant. How many distinct
// combinations of ratings will be accepted by the Elves' workflows?

import java.io.*
import kotlin.math.max
import kotlin.math.min

fun IntRange.size() = (this.endInclusive - this.start + 1).toLong()

// Returns two ranges: the first is the result of applying the op + operand to
// the range, and the second is the remaining part of the range.
fun IntRange.splitOn(op: Char, operand: Int): Pair<IntRange, IntRange> {
  return when (op) {
    '<' -> min(this.start, operand - 1)..min(this.endInclusive, operand - 1) to max(this.start, operand)..max(this.endInclusive, operand)
    '>' -> max(this.start, operand + 1)..max(this.endInclusive, operand + 1) to min(this.start, operand)..min(this.endInclusive, operand)
    else -> throw Exception("Unknown op $op")
  }
}

interface Rule {
  val to: String
}

data class ConditionalRule(val op: Char, val operand1: Char, val operand2: Int, override val to: String) : Rule
data class UnconditionalRule(override val to: String) : Rule

val RULE_LINE_RGX = Regex("(?<name>\\w+)\\{(?<rules>.*)\\}")
val CONDITIONAL_RULE_RGX = Regex("(?<operand1>[xmas])(?<op>[<>])(?<operand2>\\d+):(?<to>\\w+)")

val lines = File("input.txt").readLines()
val blankLineIdx = lines.indexOfFirst { it.isBlank() }
val rules = lines.take(blankLineIdx).associate {
  val ruleLine = RULE_LINE_RGX.matchEntire(it)!!
  ruleLine.groups["name"]!!.value to ruleLine.groups["rules"]!!.value.split(',').map {
    val conditional = CONDITIONAL_RULE_RGX.matchEntire(it)
    if (conditional != null) {
      ConditionalRule(
        conditional.groups["op"]!!.value[0],
        conditional.groups["operand1"]!!.value[0],
        conditional.groups["operand2"]!!.value.toInt(),
        conditional.groups["to"]!!.value
      )
    } else {
      UnconditionalRule(it)
    }
  }
}

fun countAcceptable(to: String, x: IntRange, m: IntRange, a: IntRange, s: IntRange): Long {
  if (to == "A") {
    return x.size() * m.size() * a.size() * s.size()
  } else if (to == "R") {
    return 0L
  } else {
    return countAcceptable(rules[to]!!, x, m, a, s)
  }
}

fun countAcceptable(remainingRules: List<Rule>, x: IntRange, m: IntRange, a: IntRange, s: IntRange): Long {
  if (remainingRules.size == 0) {
    return 0
  }

  val thisRule = remainingRules[0]
  if (thisRule !is ConditionalRule) {
    return countAcceptable(remainingRules[0].to, x, m, a, s)
  }

  return when (thisRule.operand1) {
    'x' -> {
      val (x1, x2) = x.splitOn(thisRule.op, thisRule.operand2)
      val runRule = if (x1.size() == 0L) 0L else countAcceptable(thisRule.to, x1, m, a, s)
      val nextRules = if (x2.size() == 0L) 0L else countAcceptable(remainingRules.subList(1, remainingRules.size), x2, m, a, s)
      runRule + nextRules
    }
    'm' -> {
      val (m1, m2) = m.splitOn(thisRule.op, thisRule.operand2)
      val runRule = if (m1.size() == 0L) 0L else countAcceptable(thisRule.to, x, m1, a, s)
      val nextRules = if (m2.size() == 0L) 0L else countAcceptable(remainingRules.subList(1, remainingRules.size), x, m2, a, s)
      runRule + nextRules
    }
    'a' -> {
      val (a1, a2) = a.splitOn(thisRule.op, thisRule.operand2)
      val runRule = if (a1.size() == 0L) 0L else countAcceptable(thisRule.to, x, m, a1, s)
      val nextRules = if (a2.size() == 0L) 0L else countAcceptable(remainingRules.subList(1, remainingRules.size), x, m, a2, s)
      runRule + nextRules
    }
    's' -> {
      val (s1, s2) = s.splitOn(thisRule.op, thisRule.operand2)
      val runRule = if (s1.size() == 0L) 0L else countAcceptable(thisRule.to, x, m, a, s1)
      val nextRules = if (s2.size() == 0L) 0L else countAcceptable(remainingRules.subList(1, remainingRules.size), x, m, a, s2)
      runRule + nextRules
    }
    else -> throw Exception("Unknown operand ${thisRule.operand1}")
  }
}

println(countAcceptable(rules["in"]!!, 1..4000, 1..4000, 1..4000, 1..4000))
