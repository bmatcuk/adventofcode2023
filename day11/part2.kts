// --- Part Two ---
// The galaxies are much older (and thus much farther apart) than the
// researcher initially estimated.
//
// Now, instead of the expansion you did before, make each empty row or column
// one million times larger. That is, each empty row should be replaced with
// 1000000 empty rows, and each empty column should be replaced with 1000000
// empty columns.
//
// (In the example above, if each empty row or column were merely 10 times
// larger, the sum of the shortest paths between every pair of galaxies would
// be 1030. If each empty row or column were merely 100 times larger, the sum
// of the shortest paths between every pair of galaxies would be 8410. However,
// your universe will need to expand far beyond these values.)
//
// Starting with the same initial image, expand the universe according to these
// new rules, then find the length of the shortest path between every pair of
// galaxies. What is the sum of these lengths?

import java.io.*
import kotlin.math.absoluteValue

// Instead of actually expanding the photo, just calculate a map of indexes for
// each row and column, as if the photo had been expanded.
val photo = File("input.txt").readLines()
val emptyRows = photo.foldIndexed(mutableListOf<Int>(0)) { idx, acc, row ->
  acc.also {
    if (!row.contains('#')) {
      it.add(idx)
    }
  }
}
emptyRows.add(photo.size)
val emptyCols = (0..<photo[0].length).fold(mutableListOf<Int>(0)) { acc, idx ->
  acc.also {
    if (!photo.any { it[idx] == '#' }) {
      it.add(idx)
    }
  }
}
emptyCols.add(photo[0].length)

// build list of galaxies
val rowIdxs = emptyRows.zipWithNext().withIndex().flatMap { (adj, pair) -> (pair.first + adj * 999999L)..<(pair.second + adj * 999999L) }
val colIdxs = emptyCols.zipWithNext().withIndex().flatMap { (adj, pair) -> (pair.first + adj * 999999L)..<(pair.second + adj * 999999L) }
val galaxies = photo.zip(rowIdxs).flatMap { (row, y) ->
  row.toList().zip(colIdxs).filter { (pixel, _) -> pixel == '#' }.map { (_, x) -> x to y }
}

// calculate shortest paths
val result = galaxies.withIndex().sumOf { (i, a) ->
  galaxies.subList(i + 1, galaxies.size).sumOf { b -> (b.first - a.first).absoluteValue + (b.second - a.second).absoluteValue }
}
println(result)
