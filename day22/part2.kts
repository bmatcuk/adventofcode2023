// --- Part Two ---
// Disintegrating bricks one at a time isn't going to be fast enough. While it
// might sound dangerous, what you really need is a chain reaction.
//
// You'll need to figure out the best brick to disintegrate. For each brick,
// determine how many other bricks would fall if that brick were disintegrated.
//
// Using the same example as above:
//
// - Disintegrating brick A would cause all 6 other bricks to fall.
// - Disintegrating brick F would cause only 1 other brick, G, to fall.
// - Disintegrating any other brick would cause no other bricks to fall. So, in
//   this example, the sum of the number of other bricks that would fall as a
//   result of disintegrating each brick is 7.
//
// For each brick, determine how many other bricks would fall if that brick
// were disintegrated. What is the sum of the number of other bricks that would
// fall?

import java.io.*
import kotlin.math.max
import kotlin.math.min

operator fun <T> List<T>.component6() = this[5]
fun IntRange.intersects(other: IntRange) = max(this.start, other.start) <= min(this.endInclusive, other.endInclusive)

class Brick(coords: String) : Comparable<Brick> {
  val supports = mutableListOf<Brick>()
  val supportedBy = mutableListOf<Brick>()
  var x: IntRange
  var y: IntRange
  var z: IntRange

  init {
    val (x1, y1, z1, x2, y2, z2) = coords.split(',', '~').map(String::toInt)
    this.x = min(x1, x2)..max(x1, x2)
    this.y = min(y1, y2)..max(y1, y2)
    this.z = min(z1, z2)..max(z1, z2)
  }

  fun moveDownTo(z: Int): Unit {
    val dz = this.z.first - z
    this.z = z..(this.z.endInclusive - dz)
  }

  fun intersectsXY(other: Brick): Boolean {
    return this.x.intersects(other.x) && this.y.intersects(other.y)
  }

  override operator fun compareTo(other: Brick): Int {
    return this.z.start - other.z.start
  }

  override fun toString() = "${this.x.first},${this.y.first},${this.z.first}~${this.x.endInclusive},${this.y.endInclusive},${this.z.endInclusive}"
}

// .map(::Brick) threw a _really_ weird compiler error, despite documentation
// saying it should have worked ¯\_(ツ)_/¯
val bricks = File("input.txt").readLines().map { Brick(it) }.toMutableList()
bricks.sort()

for (i in bricks.indices) {
  val brick = bricks[i]
  if (brick.z.first == 1) {
    continue
  }

  var supportedBy = bricks.subList(0, i).filter { it.intersectsXY(brick) }
  if (supportedBy.size > 0) {
    val maxz = supportedBy.maxOf { it.z.endInclusive }
    supportedBy = supportedBy.filter { it.z.endInclusive == maxz }
    brick.moveDownTo(maxz + 1)
    brick.supportedBy.addAll(supportedBy)
    supportedBy.forEach { it.supports.add(brick) }
  } else {
    brick.moveDownTo(1)
  }
}

// val counts = mutableMapOf<Brick, Int>()
// fun countSupportedBricks(brick: Brick): Int {
//   return counts[brick] ?: brick.supports
//     .filter { it.supportedBy.size == 1 }
//     .let { it.size + it.sumOf(::countSupportedBricks) }
//     .also { counts[brick] = it }
// }

fun countSupportedBricks(brick: Brick, falling: MutableSet<Brick> = mutableSetOf(brick)): Int {
  return brick.supports
    .filter { it !in falling && it.supportedBy.all { it in falling } }
    .let {
      falling.addAll(it)
      it.size + it.sumOf { countSupportedBricks(it, falling) }
    }
}

val result = bricks.sumOf(::countSupportedBricks)
println(result)
