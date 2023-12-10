// --- Day 10: Pipe Maze ---
// You use the hang glider to ride the hot air from Desert Island all the way
// up to the floating metal island. This island is surprisingly cold and there
// definitely aren't any thermals to glide on, so you leave your hang glider
// behind.
//
// You wander around for a while, but you don't find any people or animals.
// However, you do occasionally find signposts labeled "Hot Springs" pointing
// in a seemingly consistent direction; maybe you can find someone at the hot
// springs and ask them where the desert-machine parts are made.
//
// The landscape here is alien; even the flowers and trees are made of metal.
// As you stop to admire some metal grass, you notice something metallic scurry
// away in your peripheral vision and jump into a big pipe! It didn't look like
// any animal you've ever seen; if you want a better look, you'll need to get
// ahead of it.
//
// Scanning the area, you discover that the entire field you're standing on is
// densely packed with pipes; it was hard to tell at first because they're the
// same metallic silver color as the "ground". You make a quick sketch of all
// of the surface pipes you can see (your puzzle input).
//
// The pipes are arranged in a two-dimensional grid of tiles:
//
// | is a vertical pipe connecting north and south.
// - is a horizontal pipe connecting east and west.
// L is a 90-degree bend connecting north and east.
// J is a 90-degree bend connecting north and west.
// 7 is a 90-degree bend connecting south and west.
// F is a 90-degree bend connecting south and east.
// . is ground; there is no pipe in this tile.
// S is the starting position of the animal; there is a pipe on this tile, but
//   your sketch doesn't show what shape the pipe has.
//
// Based on the acoustics of the animal's scurrying, you're confident the pipe
// that contains the animal is one large, continuous loop.
//
// For example, here is a square loop of pipe:
//
// .....
// .F-7.
// .|.|.
// .L-J.
// .....
//
// If the animal had entered this loop in the northwest corner, the sketch
// would instead look like this:
//
// .....
// .S-7.
// .|.|.
// .L-J.
// .....
//
// In the above diagram, the S tile is still a 90-degree F bend: you can tell
// because of how the adjacent pipes connect to it.
//
// Unfortunately, there are also many pipes that aren't connected to the loop!
// This sketch shows the same loop as above:
//
// -L|F7
// 7S-7|
// L|7||
// -L-J|
// L|-JF
//
// In the above diagram, you can still figure out which pipes form the main
// loop: they're the ones connected to S, pipes those pipes connect to, pipes
// those pipes connect to, and so on. Every pipe in the main loop connects to
// its two neighbors (including S, which will have exactly two pipes connecting
// to it, and which is assumed to connect back to those two pipes).
//
// Here is a sketch that contains a slightly more complex main loop:
//
// ..F7.
// .FJ|.
// SJ.L7
// |F--J
// LJ...
//
// Here's the same example sketch with the extra, non-main-loop pipe tiles also
// shown:
//
// 7-F7-
// .FJ|7
// SJLL7
// |F--J
// LJ.LJ
//
// If you want to get out ahead of the animal, you should find the tile in the
// loop that is farthest from the starting position. Because the animal is in
// the pipe, it doesn't make sense to measure this by direct distance. Instead,
// you need to find the tile that would take the longest number of steps along
// the loop to reach from the starting point - regardless of which way around
// the loop the animal went.
//
// In the first example with the square loop:
//
// .....
// .S-7.
// .|.|.
// .L-J.
// .....
//
// You can count the distance each tile in the loop is from the starting point
// like this:
//
// .....
// .012.
// .1.3.
// .234.
// .....
//
// In this example, the farthest point from the start is 4 steps away.
//
// Here's the more complex loop again:
//
// ..F7.
// .FJ|.
// SJ.L7
// |F--J
// LJ...
//
// Here are the distances for each tile on that loop:
//
// ..45.
// .236.
// 01.78
// 14567
// 23...
//
// Find the single giant loop starting at S. How many steps along the loop does
// it take to get from the starting position to the point farthest from the
// starting position?

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

// Find the two pipes that connect to the start
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

// Find how many steps it takes for the two paths to meet
val firstDir = generateSequence((startx to starty) to next1!!, ::nextStep)
val secondDir = generateSequence((startx to starty) to next2!!, ::nextStep)
val result = firstDir.zip(secondDir).takeWhile { (first, second) -> first.first != second.second && first.second != second.second }.count() + 1
println(result)
