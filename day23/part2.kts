// --- Part Two ---
// As you reach the trailhead, you realize that the ground isn't as slippery as
// you expected; you'll have no problem climbing up the steep slopes.
//
// Now, treat all slopes as if they were normal paths (.). You still want to
// make sure you have the most scenic hike possible, so continue to ensure that
// you never step onto the same tile twice. What is the longest hike you can
// take?
//
// In the example above, this increases the longest hike to 154 steps:
//
// #S#####################
// #OOOOOOO#########OOO###
// #######O#########O#O###
// ###OOOOO#.>OOO###O#O###
// ###O#####.#O#O###O#O###
// ###O>...#.#O#OOOOO#OOO#
// ###O###.#.#O#########O#
// ###OOO#.#.#OOOOOOO#OOO#
// #####O#.#.#######O#O###
// #OOOOO#.#.#OOOOOOO#OOO#
// #O#####.#.#O#########O#
// #O#OOO#...#OOO###...>O#
// #O#O#O#######O###.###O#
// #OOO#O>.#...>O>.#.###O#
// #####O#.#.###O#.#.###O#
// #OOOOO#...#OOO#.#.#OOO#
// #O#########O###.#.#O###
// #OOO###OOO#OOO#...#O###
// ###O###O#O###O#####O###
// #OOO#OOO#O#OOO>.#.>O###
// #O###O###O#O###.#.#O###
// #OOOOO###OOO###...#OOO#
// #####################O#
//
// Find the longest hike you can take through the surprisingly dry hiking
// trails listed on your map. How many steps long is the longest hike?

import java.io.*

enum class Direction(val dx: Int, val dy: Int) {
  UP(0, -1),
  DOWN(0, 1),
  LEFT(-1, 0),
  RIGHT(1, 0)
}

data class Node(val x: Int, val y: Int) {
  val links: MutableList<Link> = mutableListOf()

  constructor(pos: Pair<Int, Int>) : this(pos.first, pos.second)
}

data class Link(val to: Node, val steps: Int)

data class Crossroad(val at: Pair<Int, Int>, val steps: Int, val paths: List<Pair<Int, Int>>)

val map = File("input.txt").readLines()
val start = map[0].indexOf('.') to 0
val end = map.last().indexOf('.') to map.size - 1

fun findCrossroad(start: Pair<Int, Int>, to: Pair<Int, Int>): Crossroad {
  var from = start
  var next = listOf(to)
  var steps = 0
  while (next.size == 1) {
    steps++
    if (next[0] == end) {
      return Crossroad(end, steps, listOf())
    }

    var (x, y) = next[0]
    next = Direction.entries
      .map { x + it.dx to y + it.dy }
      .filter { it != from && map[it.second][it.first] != '#' }
    from = x to y
  }
  return Crossroad(from, steps, next)
}

// Reduce the map to a graph where nodes are forks in the path with edge
// weights equal to the number of steps to reach that fork.
val startNode = Node(start.first, start.second)
val nodes = mutableMapOf(start to startNode)
val remainingCrossroads = mutableListOf(Crossroad(start, 0, listOf(start.first to 1)))
while (remainingCrossroads.isNotEmpty()) {
  val (at, _, paths) = remainingCrossroads.removeFirst()
  paths.forEach {
    val node = nodes[at]!!
    val crossroad = findCrossroad(at, it)
    var crossroadNode = nodes[crossroad.at]
    if (crossroadNode != null) {
      if (node.links.all { it.to != crossroadNode }) {
        node.links.add(Link(crossroadNode, crossroad.steps))
        crossroadNode.links.add(Link(node, crossroad.steps))
      }
    } else {
      crossroadNode = Node(crossroad.at)
      nodes[crossroad.at] = crossroadNode
      node.links.add(Link(crossroadNode, crossroad.steps))
      crossroadNode.links.add(Link(node, crossroad.steps))
      remainingCrossroads.add(crossroad)
    }
  }
}

// Now a DFS to find the longest path - I'm pretty happy with this algo, taking
// only roughly twice as long as my part 1 solution. Lots of people on reddit
// seemed to have runtimes way longer.
val endNode = nodes[end]!!

fun findLongestPath(node: Node, visited: MutableSet<Node> = mutableSetOf()): Int {
  var longest = Int.MIN_VALUE
  if (node == endNode) {
    return 0
  }
  if (node.links.isNotEmpty() && visited.add(node)) {
    longest = node.links.maxOf { it.steps + findLongestPath(it.to, visited) }
    visited.remove(node)
  }
  return longest
}

println(findLongestPath(startNode))
