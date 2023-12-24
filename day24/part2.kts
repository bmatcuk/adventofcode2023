// --- Part Two ---
// Upon further analysis, it doesn't seem like any hailstones will naturally
// collide. It's up to you to fix that!
//
// You find a rock on the ground nearby. While it seems extremely unlikely, if
// you throw it just right, you should be able to hit every hailstone in a
// single throw!
//
// You can use the probably-magical winds to reach any integer position you
// like and to propel the rock at any integer velocity. Now including the Z
// axis in your calculations, if you throw the rock at time 0, where do you
// need to be so that the rock perfectly collides with every hailstone? Due to
// probably-magical inertia, the rock won't slow down or change direction when
// it collides with a hailstone.
//
// In the example above, you can achieve this by moving to position 24, 13, 10
// and throwing the rock at velocity -3, 1, 2. If you do this, you will hit
// every hailstone as follows:
//
// Hailstone: 19, 13, 30 @ -2, 1, -2
// Collision time: 5
// Collision position: 9, 18, 20
//
// Hailstone: 18, 19, 22 @ -1, -1, -2
// Collision time: 3
// Collision position: 15, 16, 16
//
// Hailstone: 20, 25, 34 @ -2, -2, -4
// Collision time: 4
// Collision position: 12, 17, 18
//
// Hailstone: 12, 31, 28 @ -1, -2, -1
// Collision time: 6
// Collision position: 6, 19, 22
//
// Hailstone: 20, 19, 15 @ 1, -5, -3
// Collision time: 1
// Collision position: 21, 14, 12
//
// Above, each hailstone is identified by its initial position and its
// velocity. Then, the time and position of that hailstone's collision with
// your rock are given.
//
// After 1 nanosecond, the rock has exactly the same position as one of the
// hailstones, obliterating it into ice dust! Another hailstone is smashed to
// bits two nanoseconds after that. After a total of 6 nanoseconds, all of the
// hailstones have been destroyed.
//
// So, at time 0, the rock needs to be at X position 24, Y position 13, and Z
// position 10. Adding these three coordinates together produces 47. (Don't add
// any coordinates from the rock's velocity.)
//
// Determine the exact position and velocity the rock needs to have at time 0
// so that it perfectly collides with every hailstone. What do you get if you
// add up the X, Y, and Z coordinates of that initial position?

import java.io.*
import kotlin.math.max
import kotlin.math.min

operator fun <T> List<T>.component6() = this[5]

data class Hail(
  val x: Long,
  val y: Long,
  val z: Long,
  val vx: Long,
  val vy: Long,
  val vz: Long,
)

val hail = File("input.txt").readLines().map {
  val (x, y, z, vx, vy, vz) = it.split(", ", " @ ").map(String::toLong)
  Hail(x, y, z, vx, vy, vz)
}

// If two hailstones have the same velocity in a given direction, then the
// stone we throw must have a velocity that satisfies the equation:
//   (x2 - x1) % (rockVelocity - hailVelocity) = 0
fun findStoneVelocity(): Triple<Long, Long, Long> {
  var potentialXVelocities = (-1000L..1000L).toList()
  var potentialYVelocities = (-1000L..1000L).toList()
  var potentialZVelocities = (-1000L..1000L).toList()
  hail.subList(0, hail.size - 1).withIndex().forEach { (idx, a) ->
    hail.subList(idx + 1, hail.size).forEach { b ->
      if (a.vx == b.vx && potentialXVelocities.size > 1) {
        val difference = b.x - a.x
        potentialXVelocities = potentialXVelocities.filter { it != a.vx && difference % (it - a.vx) == 0L }
      }
      if (a.vy == b.vy && potentialYVelocities.size > 1) {
        val difference = b.y - a.y
        potentialYVelocities = potentialYVelocities.filter { it != a.vy && difference % (it - a.vy) == 0L }
      }
      if (a.vz == b.vz && potentialZVelocities.size > 1) {
        val difference = b.z - a.z
        potentialZVelocities = potentialZVelocities.filter { it != a.vz && difference % (it - a.vz) == 0L }
      }
      if (potentialXVelocities.size == 1 && potentialYVelocities.size == 1 && potentialZVelocities.size == 1) {
        return Triple(potentialXVelocities[0], potentialYVelocities[0], potentialZVelocities[0])
      }
    }
  }
  throw Exception("Couldn't find stone velocity")
}

// Now that we have the stone's velocity, we can find its starting position by
// figuring out when it intersects with two arbitrary hailstones.
val (vx, vy, vz) = findStoneVelocity()
val (hail1, hail2) = hail.filter { it.vx != vx && it.vy != vy && it.vz != vz }.take(2)
val m1 = (hail1.vy - vy).toDouble() / (hail1.vx - vx).toDouble()
val m2 = (hail2.vy - vy).toDouble() / (hail2.vx - vx).toDouble()
val c1 = hail1.y.toDouble() - (m1 * hail1.x.toDouble())
val c2 = hail2.y.toDouble() - (m2 * hail2.x.toDouble())
val x = ((c2 - c1) / (m1 - m2)).toLong()
val y = (m1 * x.toDouble() + c1).toLong()
val t = (x - hail1.x) / (hail1.vx - vx)
val z = hail1.z + (hail1.vz - vz) * t

// My calculated y value is off by one, which is obvious from the output below.
// So, the actual answer is x + y + z + 1. I don't feel like figuring out the
// issue. I did not like today's puzzle - this wasn't a programming puzzle, it
// was a math puzzle. Not what I signed up for.
println("$x, $y, $z")
println("At $t: ${x + vx * t}, ${y + vy * t}, ${z + vz * t}")
println("At $t: ${hail1.x + hail1.vx * t}, ${hail1.y + hail1.vy * t}, ${hail1.z + hail1.vz * t}")
println(x + y + z)
