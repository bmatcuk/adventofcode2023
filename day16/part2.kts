// --- Part Two ---
// As you try to work out what might be wrong, the reindeer tugs on your shirt
// and leads you to a nearby control panel. There, a collection of buttons lets
// you align the contraption so that the beam enters from any edge tile and
// heading away from that edge. (You can choose either of two directions for
// the beam if it starts on a corner; for instance, if the beam starts in the
// bottom-right corner, it can start heading either left or upward.)
//
// So, the beam could start on any tile in the top row (heading downward), any
// tile in the bottom row (heading upward), any tile in the leftmost column
// (heading right), or any tile in the rightmost column (heading left). To
// produce lava, you need to find the configuration that energizes as many
// tiles as possible.
//
// In the above example, this can be achieved by starting the beam in the fourth tile from the left in the top row:
//
// .|<2<\....
// |v-v\^....
// .v.v.|->>>
// .v.v.v^.|.
// .v.v.v^...
// .v.v.v^..\
// .v.v/2\\..
// <-2-/vv|..
// .|<<<2-|.\
// .v//.|.v..
//
// Using this configuration, 51 tiles are energized:
//
// .#####....
// .#.#.#....
// .#.#.#####
// .#.#.##...
// .#.#.##...
// .#.#.##...
// .#.#####..
// ########..
// .#######..
// .#...#.#..
//
// Find the initial beam configuration that energizes the largest number of
// tiles; how many tiles are energized in that configuration?

import java.io.*

enum class Direction(val dx: Int, val dy: Int) {
  DOWN(0, 1),
  LEFT(-1, 0),
  RIGHT(1, 0),
  UP(0, -1)
}

val contraption = File("input.txt").readLines()
val energized = contraption.map { BooleanArray(it.length) }
val processed = mutableSetOf<Triple<Int, Int, Direction>>()

fun followBeam(x: Int, y: Int, dir: Direction) {
  if (y !in 0..<contraption.size || x !in 0..<contraption[y].length) {
    return
  }
  if (!processed.add(Triple(x, y, dir))) {
    return
  }

  energized[y][x] = true

  when (contraption[y][x]) {
    '/' -> when (dir) {
      Direction.DOWN -> followBeam(x - 1, y, Direction.LEFT)
      Direction.LEFT -> followBeam(x, y + 1, Direction.DOWN)
      Direction.RIGHT -> followBeam(x, y - 1, Direction.UP)
      Direction.UP -> followBeam(x + 1, y, Direction.RIGHT)
    }
    '\\' -> when (dir) {
      Direction.DOWN -> followBeam(x + 1, y, Direction.RIGHT)
      Direction.LEFT -> followBeam(x, y - 1, Direction.UP)
      Direction.RIGHT -> followBeam(x, y + 1, Direction.DOWN)
      Direction.UP -> followBeam(x - 1, y, Direction.LEFT)
    }
    '-' -> when (dir) {
      Direction.UP, Direction.DOWN -> {
        followBeam(x - 1, y, Direction.LEFT)
        followBeam(x + 1, y, Direction.RIGHT)
      }
      else -> followBeam(x + dir.dx, y + dir.dy, dir)
    }
    '|' -> when (dir) {
      Direction.LEFT, Direction.RIGHT -> {
        followBeam(x, y - 1, Direction.UP)
        followBeam(x, y + 1, Direction.DOWN)
      }
      else -> followBeam(x + dir.dx, y + dir.dy, dir)
    }
    else -> followBeam(x + dir.dx, y + dir.dy, dir)
  }
}

fun runSimulation(x: Int, y: Int, dir: Direction): Int {
  energized.forEach { it.fill(false) }
  processed.clear()
  followBeam(x, y, dir)
  return energized.sumOf { it.count { it } }
}

// This naive algo was _WAY_ faster than I expected
val result = Direction.entries.maxOf { dir ->
  when (dir) {
    Direction.DOWN -> (0..<contraption[0].length).maxOf { runSimulation(it, 0, dir) }
    Direction.LEFT -> (0..<contraption.size).maxOf { runSimulation(contraption[0].length - 1, it, dir) }
    Direction.RIGHT -> (0..<contraption.size).maxOf { runSimulation(0, it, dir) }
    Direction.UP -> (0..<contraption[0].length).maxOf { runSimulation(it, contraption.size - 1, dir) }
  }
}
println(result)
