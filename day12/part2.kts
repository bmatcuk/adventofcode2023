// --- Part Two ---
// As you look out at the field of springs, you feel like there are way more
// springs than the condition records list. When you examine the records, you
// discover that they were actually folded up this whole time!
//
// To unfold the records, on each row, replace the list of spring conditions
// with five copies of itself (separated by ?) and replace the list of
// contiguous groups of damaged springs with five copies of itself (separated
// by ,).
//
// So, this row:
//
// .# 1
//
// Would become:
//
// .#?.#?.#?.#?.# 1,1,1,1,1
//
// The first line of the above example would become:
//
// ???.###????.###????.###????.###????.### 1,1,3,1,1,3,1,1,3,1,1,3,1,1,3
//
// In the above example, after unfolding, the number of possible arrangements
// for some rows is now much larger:
//
// ???.### 1,1,3 - 1 arrangement
// .??..??...?##. 1,1,3 - 16384 arrangements
// ?#?#?#?#?#?#?#? 1,3,1,6 - 1 arrangement
// ????.#...#... 4,1,1 - 16 arrangements
// ????.######..#####. 1,6,5 - 2500 arrangements
// ?###???????? 3,2,1 - 506250 arrangements
//
// After unfolding, adding all of the possible arrangement counts together
// produces 525152.
//
// Unfold your condition records; what is the new sum of possible arrangement
// counts?

import java.io.*

val cache = mutableMapOf<Pair<String,List<Int>>,Long>()

fun countArrangements(record: String, groups: List<Int>): Long {
  if (record.isEmpty()) {
    return if (groups.isEmpty()) 1 else 0
  }

  if (groups.isEmpty()) {
    return if ('#' in record) 0 else 1
  }

  val cacheKey = record to groups
  if (cacheKey in cache) {
    return cache[cacheKey]!!
  }

  var result = 0L

  // if next position in the record is a dot, or possibly a dot...
  if (record[0] in ".?") {
    result += countArrangements(record.drop(1), groups)
  }

  // if next position in the record is a spring, or possibly a spring...
  if (record[0] in "#?") {
    // if the next group can fit at the beginning of the record, and there are
    // no dots in that substring, and if the character after the group is not a
    // spring (since there'd need to be a dot between groups), then recurse...
    if (
      record.length >= groups[0] &&
      '.' !in record.take(groups[0]) &&
      (record.length == groups[0] || record[groups[0]] != '#')
    ) {
      result += countArrangements(record.drop(groups[0] + 1), groups.drop(1))
    }
  }

  cache[cacheKey] = result
  return result
}

// Fairly naive approach - just generate every possible combination given the
// number of groups, and compare that to the input to see which ones match.
val result = File("input.txt").readLines().sumOf {
  val (records, groups) = it.split(' ').let { (r, g) ->
    "?$r".repeat(5).drop(1) to ",$g".repeat(5).drop(1).split(',').map(String::toInt)
  }
  countArrangements(records, groups)
}
println(result)
