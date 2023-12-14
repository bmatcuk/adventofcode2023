// --- Part Two ---
// The parabolic reflector dish deforms, but not in a way that focuses the
// beam. To do that, you'll need to move the rocks to the edges of the
// platform. Fortunately, a button on the side of the control panel labeled
// "spin cycle" attempts to do just that!
//
// Each cycle tilts the platform four times so that the rounded rocks roll
// north, then west, then south, then east. After each tilt, the rounded rocks
// roll as far as they can before the platform tilts in the next direction.
// After one cycle, the platform will have finished rolling the rounded rocks
// in those four directions in that order.
//
// Here's what happens in the example above after each of the first few cycles:
//
// After 1 cycle:
// .....#....
// ....#...O#
// ...OO##...
// .OO#......
// .....OOO#.
// .O#...O#.#
// ....O#....
// ......OOOO
// #...O###..
// #..OO#....
//
// After 2 cycles:
// .....#....
// ....#...O#
// .....##...
// ..O#......
// .....OOO#.
// .O#...O#.#
// ....O#...O
// .......OOO
// #..OO###..
// #.OOO#...O
//
// After 3 cycles:
// .....#....
// ....#...O#
// .....##...
// ..O#......
// .....OOO#.
// .O#...O#.#
// ....O#...O
// .......OOO
// #...O###.O
// #.OOO#...O
//
// This process should work if you leave it running long enough, but you're
// still worried about the north support beams. To make sure they'll survive
// for a while, you need to calculate the total load on the north support beams
// after 1000000000 cycles.
//
// In the above example, after 1000000000 cycles, the total load on the north
// support beams is 64.
//
// Run the spin cycle for 1000000000 cycles. Afterward, what is the total load
// on the north support beams?

import java.io.*

val CYCLES = 1000000000

var platform = File("input.txt").readLines().map { it.toCharArray() }
val cache = mutableMapOf<String, Int>()
for (cycle in 0..<CYCLES) {
  val cacheKey = platform.joinToString("\n") { it.joinToString("") }
  val cycleStart = cache[cacheKey]
  if (cycleStart != null) {
    val cycleLength = cycle - cycleStart
    val remainingCycles = (CYCLES - cycle) % cycleLength
    val finalCycleValue = cycleStart + remainingCycles
    val platformState = cache.entries.find { it.value == finalCycleValue }!!.key
    platform = platformState.split('\n').map { it.toCharArray() }
    break
  }
  cache[cacheKey] = cycle

  // north
  var lastEmpty = IntArray(platform[0].size)
  (0..<platform.size).forEach { y ->
    (0..<platform[0].size).forEach { x ->
      when (platform[y][x]) {
        'O' -> {
          platform[y][x] = '.'
          platform[lastEmpty[x]][x] = 'O'
          lastEmpty[x]++
        }
        '#' -> lastEmpty[x] = y + 1
      }
    }
  }

  // west
  lastEmpty.fill(0)
  (0..<platform[0].size).forEach { x ->
    (0..<platform.size).forEach { y ->
      when (platform[y][x]) {
        'O' -> {
          platform[y][x] = '.'
          platform[y][lastEmpty[y]] = 'O'
          lastEmpty[y]++
        }
        '#' -> lastEmpty[y] = x + 1
      }
    }
  }

  // south
  lastEmpty.fill(platform.size - 1)
  (platform.size - 1 downTo 0).forEach { y ->
    (0..<platform[0].size).forEach { x ->
      when (platform[y][x]) {
        'O' -> {
          platform[y][x] = '.'
          platform[lastEmpty[x]][x] = 'O'
          lastEmpty[x]--
        }
        '#' -> lastEmpty[x] = y - 1
      }
    }
  }

  // east
  lastEmpty.fill(platform[0].size - 1)
  (platform[0].size - 1 downTo 0).forEach { x ->
    (0..<platform.size).forEach { y ->
      when (platform[y][x]) {
        'O' -> {
          platform[y][x] = '.'
          platform[y][lastEmpty[y]] = 'O'
          lastEmpty[y]--
        }
        '#' -> lastEmpty[y] = x - 1
      }
    }
  }
}

val result = platform.withIndex().sumOf { (idx, row) ->
  (platform.size - idx) * row.count { it == 'O' }
}
println(result)
