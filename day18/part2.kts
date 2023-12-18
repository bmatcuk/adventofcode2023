// --- Part Two ---
// The Elves were right to be concerned; the planned lagoon would be much too
// small.
//
// After a few minutes, someone realizes what happened; someone swapped the
// color and instruction parameters when producing the dig plan. They don't
// have time to fix the bug; one of them asks if you can extract the correct
// instructions from the hexadecimal codes.
//
// Each hexadecimal code is six hexadecimal digits long. The first five
// hexadecimal digits encode the distance in meters as a five-digit hexadecimal
// number. The last hexadecimal digit encodes the direction to dig: 0 means R,
// 1 means D, 2 means L, and 3 means U.
//
// So, in the above example, the hexadecimal codes can be converted into the
// true instructions:
//
// #70c710 = R 461937
// #0dc571 = D 56407
// #5713f0 = R 356671
// #d2c081 = D 863240
// #59c680 = R 367720
// #411b91 = D 266681
// #8ceee2 = L 577262
// #caa173 = U 829975
// #1b58a2 = L 112010
// #caa171 = D 829975
// #7807d2 = L 491645
// #a77fa3 = U 686074
// #015232 = L 5411
// #7a21e3 = U 500254
//
// Digging out this loop and its interior produces a lagoon that can hold an
// impressive 952408144115 cubic meters of lava.
//
// Convert the hexadecimal color codes into the correct instructions; if the
// Elves follow this new dig plan, how many cubic meters of lava could the
// lagoon hold?

import java.io.*
import kotlin.math.abs;

val RGX = Regex("([UDLR]) (\\d+) \\(#([0-9a-f]{6})\\)")
val outline = File("input.txt").readLines().runningFold(0L to 0L) { (x, y), line ->
  val (_, _, _, color) = RGX.matchEntire(line)!!.groupValues
  val meters = color.take(5).toLong(radix = 16)
  when (color[5]) {
    '3' -> x to y - meters.toInt()
    '1' -> x to y + meters.toInt()
    '2' -> x - meters.toInt() to y
    '0' -> x + meters.toInt() to y
    else -> throw Exception("unknown direction")
  }
}

// First, calculate the internal area with the shoelace formula:
//   1/2 * abs(sum((y(i) + y(i+1)) * (x(i) - x(i+1))))
//
// Next, calculate the boundary. Since each pair of points represents a line
// in a cardinal direction (ie, either horizontal or vertical), the length can
// just be calculated as the absolute value of the sum of the difference of
// coordinates.
//
// Finally, use Pick's theorem to calculate the total area:
//   A = i + b / 2 - 1
//
// Since calculating the internal area involves a division by 2, and we're
// going to divide the boundary by 2 in Pick's theorem, we can combine those
// steps into a single step.
//
// We add 1 instead of subtract because... reasons?
val area = 1L + (abs(outline.zipWithNext { (x1, y1), (x2, y2) ->
  // |-area-----------|   |-boundary-----------|
  (y1 + y2) * (x1 - x2) + abs(x2 - x1 + y2 - y1)
}.sum()) shr 1)
println(area)
