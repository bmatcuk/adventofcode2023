// --- Day 23: A Long Walk ---
// The Elves resume water filtering operations! Clean water starts flowing over
// the edge of Island Island.
//
// They offer to help you go over the edge of Island Island, too! Just hold on
// tight to one end of this impossibly long rope and they'll lower you down a
// safe distance from the massive waterfall you just created.
//
// As you finally reach Snow Island, you see that the water isn't really
// reaching the ground: it's being absorbed by the air itself. It looks like
// you'll finally have a little downtime while the moisture builds up to
// snow-producing levels. Snow Island is pretty scenic, even without any snow;
// why not take a walk?
//
// There's a map of nearby hiking trails (your puzzle input) that indicates
// paths (.), forest (#), and steep slopes (^, >, v, and <).
//
// For example:
//
// #.#####################
// #.......#########...###
// #######.#########.#.###
// ###.....#.>.>.###.#.###
// ###v#####.#v#.###.#.###
// ###.>...#.#.#.....#...#
// ###v###.#.#.#########.#
// ###...#.#.#.......#...#
// #####.#.#.#######.#.###
// #.....#.#.#.......#...#
// #.#####.#.#.#########v#
// #.#...#...#...###...>.#
// #.#.#v#######v###.###v#
// #...#.>.#...>.>.#.###.#
// #####v#.#.###v#.#.###.#
// #.....#...#...#.#.#...#
// #.#########.###.#.#.###
// #...###...#...#...#.###
// ###.###.#.###v#####v###
// #...#...#.#.>.>.#.>.###
// #.###.###.#.###.#.#v###
// #.....###...###...#...#
// #####################.#
//
// You're currently on the single path tile in the top row; your goal is to
// reach the single path tile in the bottom row. Because of all the mist from
// the waterfall, the slopes are probably quite icy; if you step onto a slope
// tile, your next step must be downhill (in the direction the arrow is
// pointing). To make sure you have the most scenic hike possible, never step
// onto the same tile twice. What is the longest hike you can take?
//
// In the example above, the longest hike you can take is marked with O, and
// your starting position is marked S:
//
// #S#####################
// #OOOOOOO#########...###
// #######O#########.#.###
// ###OOOOO#OOO>.###.#.###
// ###O#####O#O#.###.#.###
// ###OOOOO#O#O#.....#...#
// ###v###O#O#O#########.#
// ###...#O#O#OOOOOOO#...#
// #####.#O#O#######O#.###
// #.....#O#O#OOOOOOO#...#
// #.#####O#O#O#########v#
// #.#...#OOO#OOO###OOOOO#
// #.#.#v#######O###O###O#
// #...#.>.#...>OOO#O###O#
// #####v#.#.###v#O#O###O#
// #.....#...#...#O#O#OOO#
// #.#########.###O#O#O###
// #...###...#...#OOO#O###
// ###.###.#.###v#####O###
// #...#...#.#.>.>.#.>O###
// #.###.###.#.###.#.#O###
// #.....###...###...#OOO#
// #####################O#
//
// This hike contains 94 steps. (The other possible hikes you could have taken
// were 90, 86, 82, 82, and 74 steps long.)
//
// Find the longest hike you can take through the hiking trails listed on your
// map. How many steps long is the longest hike?

import java.io.*

enum class Direction(val dx: Int, val dy: Int, val slope: Char) {
  UP(0, -1, '^'),
  DOWN(0, 1, 'v'),
  LEFT(-1, 0, '<'),
  RIGHT(1, 0, '>')
}

val map = File("input.txt").readLines()
val start = map[0].indexOf('.') to 0
val end = map.last().indexOf('.') to map.size - 1
val costs = map.map { IntArray(it.length) }

fun visit(xy: Pair<Int, Int>, cost: Int, visited: MutableSet<Pair<Int, Int>>) {
  val (x, y) = xy
  if (cost > costs[y][x] && visited.add(xy)) {
    costs[y][x] = cost
    if (xy != end) {
      Direction.entries.forEach {
        val nextx = x + it.dx
        val nexty = y + it.dy
        val nextxy = nextx to nexty
        if (nextxy !in visited) {
          val char = map[nexty][nextx]
          if (char == '.' || char == it.slope) {
            visit(nextxy, cost + 1, visited)
          }
        }
      }
    }
    visited.remove(xy)
  }
}

visit(start.first to 1, 1, mutableSetOf(start))
println(costs[end.second][end.first])
