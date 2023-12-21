// --- Part Two ---
// The Elf seems confused by your answer until he realizes his mistake: he was
// reading from a list of his favorite numbers that are both perfect squares
// and perfect cubes, not his step counter.
// 
// The actual number of steps he needs to get today is exactly 26501365.
// 
// He also points out that the garden plots and rocks are set up so that the
// map repeats infinitely in every direction.
// 
// So, if you were to look one additional map-width or map-height out from the
// edge of the example map above, you would find that it keeps repeating:
// 
// .................................
// .....###.#......###.#......###.#.
// .###.##..#..###.##..#..###.##..#.
// ..#.#...#....#.#...#....#.#...#..
// ....#.#........#.#........#.#....
// .##...####..##...####..##...####.
// .##..#...#..##..#...#..##..#...#.
// .......##.........##.........##..
// .##.#.####..##.#.####..##.#.####.
// .##..##.##..##..##.##..##..##.##.
// .................................
// .................................
// .....###.#......###.#......###.#.
// .###.##..#..###.##..#..###.##..#.
// ..#.#...#....#.#...#....#.#...#..
// ....#.#........#.#........#.#....
// .##...####..##..S####..##...####.
// .##..#...#..##..#...#..##..#...#.
// .......##.........##.........##..
// .##.#.####..##.#.####..##.#.####.
// .##..##.##..##..##.##..##..##.##.
// .................................
// .................................
// .....###.#......###.#......###.#.
// .###.##..#..###.##..#..###.##..#.
// ..#.#...#....#.#...#....#.#...#..
// ....#.#........#.#........#.#....
// .##...####..##...####..##...####.
// .##..#...#..##..#...#..##..#...#.
// .......##.........##.........##..
// .##.#.####..##.#.####..##.#.####.
// .##..##.##..##..##.##..##..##.##.
// .................................
//
// This is just a tiny three-map-by-three-map slice of the
// inexplicably-infinite farm layout; garden plots and rocks repeat as far as
// you can see. The Elf still starts on the one middle tile marked S, though -
// every other repeated S is replaced with a normal garden plot (.).
// 
// Here are the number of reachable garden plots in this new infinite version
// of the example map for different numbers of steps:
// 
// In exactly 6 steps, he can still reach 16 garden plots.
// In exactly 10 steps, he can reach any of 50 garden plots.
// In exactly 50 steps, he can reach 1594 garden plots.
// In exactly 100 steps, he can reach 6536 garden plots.
// In exactly 500 steps, he can reach 167004 garden plots.
// In exactly 1000 steps, he can reach 668697 garden plots.
// In exactly 5000 steps, he can reach 16733044 garden plots.
//
// However, the step count the Elf needs is much larger! Starting from the
// garden plot marked S on your infinite map, how many garden plots could the
// Elf reach in exactly 26501365 steps?

import java.io.*

val STEPS = 26501365

enum class Direction(val dx: Int, val dy: Int) {
  UP(0, -1),
  DOWN(0, 1),
  LEFT(-1, 0),
  RIGHT(1, 0)
}

val map = File("input.txt").readLines()
val start = map.withIndex().map { (y, line) -> line.indexOf('S') to y }.find { it.first >= 0 }!!
val width = map[0].length
val height = map.size

fun isGarden(point: Pair<Int, Int>): Boolean {
  val (x, y) = point
  val actualx = when {
    x < 0 -> ((x % width) + width) % width
    x >= width -> x % width
    else -> x
  }
  val actualy = when {
    y < 0 -> ((y % height) + height) % height
    y >= height -> y % height
    else -> y
  }
  return map[actualy][actualx] != '#'
}

// Because there are no stones in the row or column with the starting position,
// the steps grow into a diamond shape. The starting position is dead-center of
// a perfectly square map, so the diamond repeats every width (or height)
// steps. That means we can solve this as a quadratic using some math that I
// frankly don't understand. We need at least three points to solve the
// quadratic, so we take a count at n/2 steps, 3n/2, and 5n/2 (where n is the
// width or height) - basically, at the point when we reach the end of the map
// 3 times.
val steps = (1..2).runningFold(width / 2) { acc, _ -> acc + width }
var positions = setOf(start)
var counts = mutableListOf(0, 0, 0)
for (step in 1..steps.last()) {
  positions = positions.flatMap { (x, y) ->
    Direction.entries.map { x + it.dx to y + it.dy }.filter(::isGarden)
  }.toSet()
  if (step == steps[0]) {
    counts[0] = positions.size
  } else if (step == steps[1]) {
    counts[1] = positions.size
  } else if (step == steps[2]) {
    counts[2] = positions.size
  }
}
println(counts)

// Calculate the Lagrange coefficients for the quadratic interpolation
val a = (counts[0] / 2 - counts[1] + counts[2] / 2).toLong()
val b = (-3 * (counts[0] / 2) + 2 * counts[1] - counts[2] / 2).toLong()
val c = counts[0].toLong()
println(a)
println(b)
println(c)

// Calculate quadratic
val x = (STEPS / width).toLong()
val result = a * x * x + b * x + c
println(result)
