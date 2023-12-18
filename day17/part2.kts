// --- Part Two ---
// The crucibles of lava simply aren't large enough to provide an adequate
// supply of lava to the machine parts factory. Instead, the Elves are going to
// upgrade to ultra crucibles.
//
// Ultra crucibles are even more difficult to steer than normal crucibles. Not
// only do they have trouble going in a straight line, but they also have
// trouble turning!
//
// Once an ultra crucible starts moving in a direction, it needs to move a
// minimum of four blocks in that direction before it can turn (or even before
// it can stop at the end). However, it will eventually start to get wobbly: an
// ultra crucible can move a maximum of ten consecutive blocks without turning.
//
// In the above example, an ultra crucible could follow this path to minimize
// heat loss:
//
// 2>>>>>>>>1323
// 32154535v5623
// 32552456v4254
// 34465858v5452
// 45466578v>>>>
// 143859879845v
// 445787698776v
// 363787797965v
// 465496798688v
// 456467998645v
// 122468686556v
// 254654888773v
// 432267465553v
//
// In the above example, an ultra crucible would incur the minimum possible
// heat loss of 94.
//
// Here's another example:
//
// 111111111111
// 999999999991
// 999999999991
// 999999999991
// 999999999991
//
// Sadly, an ultra crucible would need to take an unfortunate path like this
// one:
//
// 1>>>>>>>1111
// 9999999v9991
// 9999999v9991
// 9999999v9991
// 9999999v>>>>
//
// This route causes the ultra crucible to incur the minimum possible heat loss
// of 71.
//
// Directing the ultra crucible from the lava pool to the machine parts
// factory, what is the least heat loss it can incur?

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
    if (current.straight < 3) {
      continue
    }
    println(current.heatloss)

    val marked = map.map { BooleanArray(it.size) }.also { it[0][0] = true }
    while (current != null) {
      marked[current.y][current.x] = true
      current = current.prev
    }
    map.zip(marked).forEach { (maprow, markedrow) ->
      println(maprow.zip(markedrow.asIterable()).joinToString("") { (num, mark) -> if (mark) "\u001b[7m$num\u001b[0m" else num.toString() })
    }

    break
  }

  if (!visited.add(current)) {
    continue
  }
  Direction.entries.filterNot { it.isOpposite(current.dir) }.forEach { dir ->
    if (dir == current.dir || current.straight >= 3) {
      val nextx = current.x + dir.dx
      val nexty = current.y + dir.dy
      if (nexty in 0..<map.size && nextx in 0..<map[nexty].size) {
        var nextStraight = if (dir == current.dir) current.straight + 1 else 0
        if (nextStraight < 10) {
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
}
