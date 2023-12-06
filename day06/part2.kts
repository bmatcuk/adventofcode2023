// --- Part Two ---
// As the race is about to start, you realize the piece of paper with race
// times and record distances you got earlier actually just has very bad
// kerning. There's really only one race - ignore the spaces between the
// numbers on each line.
//
// So, the example from before:
//
// Time:      7  15   30
// Distance:  9  40  200
//
// ...now instead means this:
//
// Time:      71530
// Distance:  940200
//
// Now, you have to figure out how many ways there are to win this single race.
// In this example, the race lasts for 71530 milliseconds and the record
// distance you need to beat is 940200 millimeters. You could hold the button
// anywhere from 14 to 71516 milliseconds and beat the record, a total of 71503
// ways!
//
// How many ways can you beat the record in this one much longer race?

import java.io.*
import kotlin.math.*

val (time, distance) = File("input.txt").readLines().map {
  it.dropWhile { !it.isDigit() }.filterNot { it.isWhitespace() }.toDouble()
}

// If total time is `T`, winning distance is `D`, and time spent holding the
// button is `x`, `(T - x)` would be the remaining time where the boat is
// moving, and `(T - x) * x` would be the distance moved. Therefore, we want to
// solve the equation `(T - x) * x > D`. This can be done with the quadratic
// formula, giving us: `(-T ± √(T² - 4D)) / -2`. Or, basically, the ceiling in
// the plus case, and the floor in the minus case. If the results of the
// calculation are an integer (meaning ceiling or floor doesn't change the
// answer), then nudge the result one higher for ceiling, or one lower for
// floor using a tiny fudge factor.
val sqrtTerm = sqrt(time.pow(2) - 4.0 * distance)
val minimum = ceil((-time + sqrtTerm) / -2.0 + 0.00001)
val maximum = floor((-time - sqrtTerm) / -2.0 - 0.00001)
val result = maximum - minimum + 1.0
println(result.toLong())
