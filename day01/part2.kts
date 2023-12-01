// --- Part Two ---
// Your calculation isn't quite right. It looks like some of the digits are
// actually spelled out with letters: one, two, three, four, five, six, seven,
// eight, and nine also count as valid "digits".
//
// Equipped with this new information, you now need to find the real first and
// last digit on each line. For example:
//
// two1nine
// eightwothree
// abcone2threexyz
// xtwone3four
// 4nineeightseven2
// zoneight234
// 7pqrstsixteen
//
// In this example, the calibration values are 29, 83, 13, 24, 42, 14, and 76.
// Adding these together produces 281.
//
// What is the sum of all of the calibration values?

import java.io.*
import kotlin.io.*

fun strToInt(s: String): Int {
  if (s[0].isDigit()) {
    return s.toInt()
  }
  return when (s) {
    "one" -> 1
    "two" -> 2
    "three" -> 3
    "four" -> 4
    "five" -> 5
    "six" -> 6
    "seven" -> 7
    "eight" -> 8
    "nine" -> 9
    else -> 0
  }
}

val RGX = Regex("(one|two|three|four|five|six|seven|eight|nine|\\d)")
val result = File("input.txt").readLines().sumBy {
  val matches = RGX.findAll(it)
  val firstDigit = matches.first().groupValues[1]
  var lastDigit = matches.last()
  val maybeLastDigit = RGX.find(it, lastDigit.range.start + 1)
  if (maybeLastDigit != null) {
    lastDigit = maybeLastDigit
  }
  strToInt(firstDigit) * 10 + strToInt(lastDigit.groupValues[1])
}
println(result)
