// --- Day 18: Lavaduct Lagoon ---
// Thanks to your efforts, the machine parts factory is one of the first
// factories up and running since the lavafall came back. However, to catch up
// with the large backlog of parts requests, the factory will also need a large
// supply of lava for a while; the Elves have already started creating a large
// lagoon nearby for this purpose.
//
// However, they aren't sure the lagoon will be big enough; they've asked you
// to take a look at the dig plan (your puzzle input). For example:
//
// R 6 (#70c710)
// D 5 (#0dc571)
// L 2 (#5713f0)
// D 2 (#d2c081)
// R 2 (#59c680)
// D 2 (#411b91)
// L 5 (#8ceee2)
// U 2 (#caa173)
// L 1 (#1b58a2)
// U 2 (#caa171)
// R 2 (#7807d2)
// U 3 (#a77fa3)
// L 2 (#015232)
// U 2 (#7a21e3)
//
// The digger starts in a 1 meter cube hole in the ground. They then dig the
// specified number of meters up (U), down (D), left (L), or right (R),
// clearing full 1 meter cubes as they go. The directions are given as seen
// from above, so if "up" were north, then "right" would be east, and so on.
// Each trench is also listed with the color that the edge of the trench should
// be painted as an RGB hexadecimal color code.
//
// When viewed from above, the above example dig plan would result in the
// following loop of trench (#) having been dug out from otherwise ground-level
// terrain (.):
//
// #######
// #.....#
// ###...#
// ..#...#
// ..#...#
// ###.###
// #...#..
// ##..###
// .#....#
// .######
//
// At this point, the trench could contain 38 cubic meters of lava. However,
// this is just the edge of the lagoon; the next step is to dig out the
// interior so that it is one meter deep as well:
//
// #######
// #######
// #######
// ..#####
// ..#####
// #######
// #####..
// #######
// .######
// .######
//
// Now, the lagoon can contain a much more respectable 62 cubic meters of lava.
// While the interior is dug out, the edges are also painted according to the
// color codes in the dig plan.
//
// The Elves are concerned the lagoon won't be large enough; if they follow
// their dig plan, how many cubic meters of lava could it hold?

import java.io.*
import kotlin.math.abs;

val RGX = Regex("([UDLR]) (\\d+) \\(#([0-9a-f]{6})\\)")
val outline = File("input.txt").readLines().runningFold(0 to 0) { (x, y), line ->
  val (_, dir, meters, color) = RGX.matchEntire(line)!!.groupValues
  when (dir[0]) {
    'U' -> x to y - meters.toInt()
    'D' -> x to y + meters.toInt()
    'L' -> x - meters.toInt() to y
    'R' -> x + meters.toInt() to y
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
val area = 1 + (abs(outline.zipWithNext { (x1, y1), (x2, y2) ->
  // |-area-----------|   |-boundary-----------|
  (y1 + y2) * (x1 - x2) + abs(x2 - x1 + y2 - y1)
}.sum()) shr 1)
println(area)
