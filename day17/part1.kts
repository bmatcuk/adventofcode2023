// --- Day 17: Clumsy Crucible ---
// The lava starts flowing rapidly once the Lava Production Facility is
// operational. As you leave, the reindeer offers you a parachute, allowing you
// to quickly reach Gear Island.
//
// As you descend, your bird's-eye view of Gear Island reveals why you had
// trouble finding anyone on your way up: half of Gear Island is empty, but the
// half below you is a giant factory city!
//
// You land near the gradually-filling pool of lava at the base of your new
// lavafall. Lavaducts will eventually carry the lava throughout the city, but
// to make use of it immediately, Elves are loading it into large crucibles on
// wheels.
//
// The crucibles are top-heavy and pushed by hand. Unfortunately, the crucibles
// become very difficult to steer at high speeds, and so it can be hard to go
// in a straight line for very long.
//
// To get Desert Island the machine parts it needs as soon as possible, you'll
// need to find the best way to get the crucible from the lava pool to the
// machine parts factory. To do this, you need to minimize heat loss while
// choosing a route that doesn't require the crucible to go in a straight line
// for too long.
//
// Fortunately, the Elves here have a map (your puzzle input) that uses traffic
// patterns, ambient temperature, and hundreds of other parameters to calculate
// exactly how much heat loss can be expected for a crucible entering any
// particular city block.
//
// For example:
//
// 2413432311323
// 3215453535623
// 3255245654254
// 3446585845452
// 4546657867536
// 1438598798454
// 4457876987766
// 3637877979653
// 4654967986887
// 4564679986453
// 1224686865563
// 2546548887735
// 4322674655533
//
// Each city block is marked by a single digit that represents the amount of
// heat loss if the crucible enters that block. The starting point, the lava
// pool, is the top-left city block; the destination, the machine parts
// factory, is the bottom-right city block. (Because you already start in the
// top-left block, you don't incur that block's heat loss unless you leave that
// block and then return to it.)
//
// Because it is difficult to keep the top-heavy crucible going in a straight
// line for very long, it can move at most three blocks in a single direction
// before it must turn 90 degrees left or right. The crucible also can't
// reverse direction; after entering each city block, it may only turn left,
// continue straight, or turn right.
//
// One way to minimize heat loss is this path:
//
// 2>>34^>>>1323
// 32v>>>35v5623
// 32552456v>>54
// 3446585845v52
// 4546657867v>6
// 14385987984v4
// 44578769877v6
// 36378779796v>
// 465496798688v
// 456467998645v
// 12246868655<v
// 25465488877v5
// 43226746555v>
//
// This path never moves more than three consecutive blocks in the same
// direction and incurs a heat loss of only 102.
//
// Directing the crucible from the lava pool to the machine parts factory, but
// not moving more than three consecutive blocks in the same direction, what is
// the least heat loss it can incur?

import java.io.*

enum class Direction(val dx: Int, val dy: Int) {
  UP(0, -1),
  DOWN(0, 1),
  LEFT(-1, 0),
  RIGHT(1, 0);

  fun isOpposite(dir: Direction): Boolean {
    return when (this) {
      Direction.UP -> dir == Direction.DOWN
      Direction.DOWN -> dir == Direction.UP
      Direction.LEFT -> dir == Direction.RIGHT
      Direction.RIGHT -> dir == Direction.LEFT
    }
  }
}

data class Node(val x: Int, val y: Int, val dir: Direction, val straight: Int) {
  var heatloss: Int = 0
  var prev: Node? = null
}

// to avoid a lot of special case logic for the start node, I'm just going to
// manually process the start node. Instructions are unclear about whether or
// not to treat the first move as having moved "in the same direction".
val map = File("input.txt").readLines().map { it.map { it - '0' } }
val factory = map.last().size - 1 to map.size - 1
val visited = mutableSetOf<Node>()
val unvisited = mutableListOf<Node>()
if (map[0][1] < map[1][0]) {
  unvisited.add(Node(1, 0, Direction.RIGHT, 0).also { it.heatloss = map[0][1] })
  unvisited.add(Node(0, 1, Direction.DOWN, 0).also { it.heatloss = map[1][0] })
} else {
  unvisited.add(Node(0, 1, Direction.DOWN, 0).also { it.heatloss = map[1][0] })
  unvisited.add(Node(1, 0, Direction.RIGHT, 0).also { it.heatloss = map[0][1] })
}
while (unvisited.isNotEmpty()) {
  var current = unvisited.removeFirst()
  if ((current.x to current.y) == factory) {
    println(current.heatloss)

    // val marked = map.map { BooleanArray(it.size) }.also { it[0][0] = true }
    // while (current != null) {
    //   marked[current.y][current.x] = true
    //   current = current.prev
    // }
    // map.zip(marked).forEach { (maprow, markedrow) ->
    //   println(maprow.zip(markedrow.asIterable()).joinToString("") { (num, mark) -> if (mark) "\u001b[7m$num\u001b[0m" else num.toString() })
    // }

    break
  }

  if (!visited.add(current)) {
    continue
  }
  Direction.entries.filterNot { it.isOpposite(current.dir) }.forEach { dir ->
    val nextx = current.x + dir.dx
    val nexty = current.y + dir.dy
    if (nexty in 0..<map.size && nextx in 0..<map[nexty].size) {
      var nextStraight = if (dir == current.dir) current.straight + 1 else 0
      if (nextStraight < 3) {
        var nextHeatloss = current.heatloss + map[nexty][nextx]
        var next = Node(nextx, nexty, dir, nextStraight).also {
          it.heatloss = nextHeatloss
          it.prev = current
        }

        if (next !in visited) {
          var insertIdx = unvisited.indexOfFirst { it.heatloss > nextHeatloss }
          if (insertIdx == -1) {
            unvisited.add(next)
          } else {
            unvisited.add(insertIdx, next)
          }
        }
      }
    }
  }
}
