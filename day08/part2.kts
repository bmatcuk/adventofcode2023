// --- Part Two ---
// The sandstorm is upon you and you aren't any closer to escaping the
// wasteland. You had the camel follow the instructions, but you've barely left
// your starting position. It's going to take significantly more steps to
// escape!
//
// What if the map isn't for people - what if the map is for ghosts? Are ghosts
// even bound by the laws of spacetime? Only one way to find out.
//
// After examining the maps a bit longer, your attention is drawn to a curious
// fact: the number of nodes with names ending in A is equal to the number
// ending in Z! If you were a ghost, you'd probably just start at every node
// that ends with A and follow all of the paths at the same time until they all
// simultaneously end up at nodes that end with Z.
//
// For example:
//
// LR
//
// 11A = (11B, XXX)
// 11B = (XXX, 11Z)
// 11Z = (11B, XXX)
// 22A = (22B, XXX)
// 22B = (22C, 22C)
// 22C = (22Z, 22Z)
// 22Z = (22B, 22B)
// XXX = (XXX, XXX)
//
// Here, there are two starting nodes, 11A and 22A (because they both end with
// A). As you follow each left/right instruction, use that instruction to
// simultaneously navigate away from both nodes you're currently on. Repeat
// this process until all of the nodes you're currently on end with Z. (If only
// some of the nodes you're on end with Z, they act like any other node and you
// continue as normal.) In this example, you would proceed as follows:
//
// Step 0: You are at 11A and 22A.
// Step 1: You choose all of the left paths, leading you to 11B and 22B.
// Step 2: You choose all of the right paths, leading you to 11Z and 22C.
// Step 3: You choose all of the left paths, leading you to 11B and 22Z.
// Step 4: You choose all of the right paths, leading you to 11Z and 22B.
// Step 5: You choose all of the left paths, leading you to 11B and 22C.
// Step 6: You choose all of the right paths, leading you to 11Z and 22Z.
//
// So, in this example, you end up entirely on nodes that end in Z after 6
// steps.
//
// Simultaneously start on every node that ends with A. How many steps does it
// take before you're only on nodes that end with Z?

import java.io.*

fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }

data class Ending(val steps: Int, val thenEvery: Int)

class Fork(val left: String, val right: String) {
  fun next(turn: Char): String {
    if (turn == 'L') return this.left
    return this.right
  }
}

val lines = File("input.txt").readLines()
val directions = lines[0]
val map = lines.drop(2).associate {
  it.substring(0..2) to Fork(it.substring(7..9), it.substring(12..14))
}

// While iterating on my code, I noticed that each starting position only runs
// into a single ending position before reaching a cycle, so I don't need to
// both considering cases where a starting postition can reach multiple ending
// positions.
val cnts = map.keys.filter { it.endsWith('A') }.map { startingPos ->
  // Map of a position to a map of direction idx to the step that the position
  // was reached at that direction index.
  val cache: MutableMap<String, MutableMap<Int, Int>> = mutableMapOf()
  var position = startingPos
  var ending = 0
  for ((step, dirWithIndex) in directions.withIndex().asSequence().repeat().withIndex()) {
    val (dirIdx, dir) = dirWithIndex
    if (position in cache && dirIdx in cache[position]!!) {
      // found a cycle
      return@map Ending(ending, step - cache[position]!![dirIdx]!!)
      break
    }
    if (position.endsWith('Z')) {
      ending = step
    }

    cache.getOrPut(position) { mutableMapOf<Int, Int>() }[dirIdx] = step
    position = map[position]!!.next(dir)
  }
  null
}

// Generously, at least in my data, the cycles happen x steps after finding an
// ending position, and they cycle back to a position that was reached x steps
// after the start. For example, DRA reaches an end position in 20777 steps,
// and then reaches a cycle 3 steps later, repeating back to the third step.
// Meaning, the end position will be reached every 20777 steps. This didn't
// _need_ to be the case. The cycle 3 steps later could have gone to the 42nd
// step, meaning the end step would be reached in 20777 steps and then every
// 20738 (20780-42) steps after that.
//
// This knowledge reduces the problem to LCM:
fun gcd(a: Long, b: Long): Long {
  if (a == 0L) return b
  return gcd(b % a, a)
}

fun lcm(a: Long, b: Long): Long {
  return a * b / gcd(a, b)
}

val result = cnts.map { it!!.steps.toLong() }.reduce(::lcm)
println(result)
