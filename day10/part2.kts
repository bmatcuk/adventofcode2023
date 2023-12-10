// --- Part Two ---
// You quickly reach the farthest point of the loop, but the animal never
// emerges. Maybe its nest is within the area enclosed by the loop?
//
// To determine whether it's even worth taking the time to search for such a
// nest, you should calculate how many tiles are contained within the loop. For
// example:
//
// ...........
// .S-------7.
// .|F-----7|.
// .||.....||.
// .||.....||.
// .|L-7.F-J|.
// .|..|.|..|.
// .L--J.L--J.
// ...........
//
// The above loop encloses merely four tiles - the two pairs of . in the
// southwest and southeast (marked I below). The middle . tiles (marked O
// below) are not in the loop. Here is the same loop again with those regions
// marked:
//
// ...........
// .S-------7.
// .|F-----7|.
// .||OOOOO||.
// .||OOOOO||.
// .|L-7OF-J|.
// .|II|O|II|.
// .L--JOL--J.
// .....O.....
//
// In fact, there doesn't even need to be a full tile path to the outside for
// tiles to count as outside the loop - squeezing between pipes is also
// allowed! Here, I is still within the loop and O is still outside the loop:
//
// ..........
// .S------7.
// .|F----7|.
// .||OOOO||.
// .||OOOO||.
// .|L-7F-J|.
// .|II||II|.
// .L--JL--J.
// ..........
//
// In both of the above examples, 4 tiles are enclosed by the loop.
//
// Here's a larger example:
//
// .F----7F7F7F7F-7....
// .|F--7||||||||FJ....
// .||.FJ||||||||L7....
// FJL7L7LJLJ||LJ.L-7..
// L--J.L7...LJS7F-7L7.
// ....F-J..F7FJ|L7L7L7
// ....L7.F7||L7|.L7L7|
// .....|FJLJ|FJ|F7|.LJ
// ....FJL-7.||.||||...
// ....L---J.LJ.LJLJ...
//
// The above sketch has many random bits of ground, some of which are in the
// loop (I) and some of which are outside it (O):
//
// OF----7F7F7F7F-7OOOO
// O|F--7||||||||FJOOOO
// O||OFJ||||||||L7OOOO
// FJL7L7LJLJ||LJIL-7OO
// L--JOL7IIILJS7F-7L7O
// OOOOF-JIIF7FJ|L7L7L7
// OOOOL7IF7||L7|IL7L7|
// OOOOO|FJLJ|FJ|F7|OLJ
// OOOOFJL-7O||O||||OOO
// OOOOL---JOLJOLJLJOOO
//
// In this larger example, 8 tiles are enclosed by the loop.
//
// Any tile that isn't part of the main loop can count as being enclosed by the
// loop. Here's another example with many bits of junk pipe lying around that
// aren't connected to the main loop at all:
//
// FF7FSF7F7F7F7F7F---7
// L|LJ||||||||||||F--J
// FL-7LJLJ||||||LJL-77
// F--JF--7||LJLJ7F7FJ-
// L---JF-JLJ.||-FJLJJ7
// |F|F-JF---7F7-L7L|7|
// |FFJF7L7F-JF7|JL---7
// 7-L-JL7||F7|L7F-7F7|
// L.L7LFJ|||||FJL7||LJ
// L7JLJL-JLJLJL--JLJ.L
//
// Here are just the tiles that are enclosed by the loop marked with I:
//
// FF7FSF7F7F7F7F7F---7
// L|LJ||||||||||||F--J
// FL-7LJLJ||||||LJL-77
// F--JF--7||LJLJIF7FJ-
// L---JF-JLJIIIIFJLJJ7
// |F|F-JF---7IIIL7L|7|
// |FFJF7L7F-JF7IIL---7
// 7-L-JL7||F7|L7F-7F7|
// L.L7LFJ|||||FJL7||LJ
// L7JLJL-JLJLJL--JLJ.L
//
// In this last example, 10 tiles are enclosed by the loop.
//
// Figure out whether you have time to search for the nest by calculating the
// area within the loop. How many tiles are enclosed by the loop?

import java.io.*

// load maze
val maze = File("input.txt").readLines()
val (starty, startx) = maze.asSequence().map { it.indexOf('S') }.withIndex().first { (_, i) -> i != -1 }

// function to calculate the next move, given a current position and the
// previous position
fun nextStep(currentPair: Pair<Pair<Int, Int>, Pair<Int, Int>>): Pair<Pair<Int, Int>, Pair<Int, Int>> {
  val (prev, cur) = currentPair
  val (prevx, prevy) = prev
  val (curx, cury) = cur
  val pipe = maze[cury][curx]
  if (prevx < curx) {
    if (pipe == '-') {
      return cur to (curx + 1 to cury)
    } else if (pipe == 'J') {
      return cur to (curx to cury - 1)
    } else { // if (pipe == '7')
      return cur to (curx to cury + 1)
    }
  } else if (prevx > curx) {
    if (pipe == '-') {
      return cur to (curx - 1 to cury)
    } else if (pipe == 'L') {
      return cur to (curx to cury - 1)
    } else { // if (pipe == 'F')
      return cur to (curx to cury + 1)
    }
  } else if (prevy < cury) {
    if (pipe == '|') {
      return cur to (curx to cury + 1)
    } else if (pipe == 'L') {
      return cur to (curx + 1 to cury)
    } else { // if (pipe == 'J')
      return cur to (curx - 1 to cury)
    }
  } else { // if (prevy > cury)
    if (pipe == '|') {
      return cur to (curx to cury - 1)
    } else if (pipe == '7') {
      return cur to (curx - 1 to cury)
    } else { // if (pipe == 'F')
      return cur to (curx + 1 to cury)
    }
  }
}

// Find the two pipes that connect to the start - we still need both some that
// we know what kind of pipe the start tile is.
var next1: Pair<Int, Int>? = null
var next2: Pair<Int, Int>? = null
if (starty > 0 && "|7F".contains(maze[starty - 1][startx])) {
  next1 = startx to starty - 1
}
if (startx + 1 < maze[0].length && "-J7".contains(maze[starty][startx + 1])) {
  next2 = next1
  next1 = startx + 1 to starty
}
if (starty + 1 < maze.size && "|JL".contains(maze[starty + 1][startx])) {
  next2 = next1
  next1 = startx to starty + 1
}
if (startx > 0 && "-FL".contains(maze[starty][startx - 1])) {
  next2 = next1
  next1 = startx - 1 to starty
}

// calculate the type of pipe that the start tile is
val startPipe = if (next1!!.first == next2!!.first) {
  '|'
} else if (next1!!.second == next2!!.second) {
  '-'
} else if (next1!!.first > next2!!.first) {
  if (next1!!.second > next2!!.second) {
    '7'
  } else {
    'J'
  }
} else {
  if (next1!!.second > next2!!.second) {
    'F'
  } else {
    'L'
  }
}

// Mark visited spaces
val visited = Array(maze.size) { BooleanArray(maze[0].length) }
val start = startx to starty
visited[starty][startx] = true
generateSequence(start to next1!!, ::nextStep)
  .takeWhile { (_, cur) -> cur != start }
  .forEach { (_, cur) -> visited[cur.second][cur.first] = true }

// apply even-odd rule
val result = visited.withIndex().sumOf { (y, row) ->
  val inside = row.asSequence()
    .zip(maze[y].asSequence())
    .runningReduce { (odd, entry), (vstd, pipe) ->
      if (!vstd) return@runningReduce odd to entry
      val thisPipe = if (pipe == 'S') startPipe else pipe
      when (thisPipe) {
        'F', 'L' -> !odd to thisPipe
        '7' -> (odd xor (entry == 'F')) to 'X'
        'J' -> (odd xor (entry == 'L')) to 'X'
        '|' -> !odd to 'X'
        else -> odd to entry
      }
    }
    .zip(row.asSequence())
    .map { (oddEndTurn, vstd) -> oddEndTurn.first && !vstd }
    .toList()
  val visitedMaze = row.zip(maze[y].asIterable()).map { (v, m) -> if (v) m else ' ' }
  println("%3d: ".format(y) + inside.zip(visitedMaze).joinToString("") { (i, m) -> if (i) "\u001b[7mI\u001b[0m" else m.toString() })
  inside.count { it }
}
println(result)
