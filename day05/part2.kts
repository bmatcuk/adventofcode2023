// --- Part Two ---
// Everyone will starve if you only plant such a small number of seeds.
// Re-reading the almanac, it looks like the seeds: line actually describes
// ranges of seed numbers.
//
// The values on the initial seeds: line come in pairs. Within each pair, the
// first value is the start of the range and the second value is the length of
// the range. So, in the first line of the example above:
//
// seeds: 79 14 55 13
//
// This line describes two ranges of seed numbers to be planted in the garden.
// The first range starts with seed number 79 and contains 14 values: 79, 80,
// ..., 91, 92. The second range starts with seed number 55 and contains 13
// values: 55, 56, ..., 66, 67.
//
// Now, rather than considering four seed numbers, you need to consider a total
// of 27 seed numbers.
//
// In the above example, the lowest location number can be obtained from seed
// number 82, which corresponds to soil 84, fertilizer 84, water 84, light 77,
// temperature 45, humidity 46, and location 46. So, the lowest location number
// is 46.
//
// Consider all of the initial seed numbers listed in the ranges on the first
// line of the almanac. What is the lowest location number that corresponds to
// any of the initial seed numbers?

import java.io.*
import kotlin.math.*

// The values of `maps` is a sorted list of pairs where the first pair element
// is a range of destination numbers (the list is sorted on this), and the
// second element of the pair is an "adjustment" - a number added to a
// destination number in the range to obtain a source number.
var seeds: List<Long> = listOf()
val mapsTo: MutableMap<String, String> = hashMapOf()
val maps: MutableMap<String, MutableList<Pair<LongRange, Long>>> = hashMapOf()
var currentMap: MutableList<Pair<LongRange, Long>> = mutableListOf()
var currentMapName = ""
val MAP_RGX = Regex("(\\w+)-to-(\\w+) map:")
File("input.txt").forEachLine {
  if (it.isBlank()) return@forEachLine

  if (it.startsWith("seeds:")) {
    seeds = it.drop(7).split(' ').map(String::toLong)
    return@forEachLine
  }

  val mapLine = MAP_RGX.find(it)
  if (mapLine != null) {
    currentMap = mutableListOf()
    currentMapName = mapLine.groupValues[2]
    mapsTo[currentMapName] = mapLine.groupValues[1]
    maps[currentMapName] = currentMap
    return@forEachLine
  }

  val (destinationStart, sourceStart, length) = it.split(' ').map(String::toLong)
  val destinationEnd = destinationStart + length - 1
  val adjustment = sourceStart - destinationStart
  var idx = currentMap.indexOfFirst { it.first.start > destinationStart }
  if (idx == -1) {
    idx = currentMap.size
  }
  currentMap.add(idx, (destinationStart..destinationEnd) to adjustment)
}

// Basic idea is to work backward... just try every possible location, starting
// with zero, and working up until we find a location that has a corresponding
// seed in the input. I spent hours on more complicated algos that always
// seemed to return zero - and I found other people complaining about the same
// issue. So, there must be some trick in the input that I didn't see. But
// working backward actually runs fairly quickly, so whatever.
fun locationToPossibleSeed(category: String, num: Long): Long {
  val pairs = maps[category]!!
  val pair = pairs.find { it.first.contains(num) }
  val newNum = if (pair == null) num else (num + pair.second)

  val nextCategory = mapsTo[category]!!
  if (nextCategory == "seed") {
    return newNum
  }
  return locationToPossibleSeed(nextCategory, newNum)
}

val seedRanges = seeds.chunked(2).map { (seed, length) -> seed..(seed + length - 1) }
val location = generateSequence(0L) { it + 1L }.find {
  val possibleSeed = locationToPossibleSeed("location", it)
  seedRanges.any { it.contains(possibleSeed) }
}
println(location)
