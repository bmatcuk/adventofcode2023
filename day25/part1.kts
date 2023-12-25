// --- Day 25: Snowverload ---
// Still somehow without snow, you go to the last place you haven't checked:
// the center of Snow Island, directly below the waterfall.
// 
// Here, someone has clearly been trying to fix the problem. Scattered
// everywhere are hundreds of weather machines, almanacs, communication
// modules, hoof prints, machine parts, mirrors, lenses, and so on.
// 
// Somehow, everything has been wired together into a massive snow-producing
// apparatus, but nothing seems to be running. You check a tiny screen on one
// of the communication modules: Error 2023. It doesn't say what Error 2023
// means, but it does have the phone number for a support line printed on it.
// 
// "Hi, you've reached Weather Machines And So On, Inc. How can I help you?"
// You explain the situation.
// 
// "Error 2023, you say? Why, that's a power overload error, of course! It
// means you have too many components plugged in. Try unplugging some
// components and--" You explain that there are hundreds of components here and
// you're in a bit of a hurry.
// 
// "Well, let's see how bad it is; do you see a big red reset button somewhere?
// It should be on its own module. If you push it, it probably won't fix
// anything, but it'll report how overloaded things are." After a minute or
// two, you find the reset button; it's so big that it takes two hands just to
// get enough leverage to push it. Its screen then displays:
// 
// SYSTEM OVERLOAD!
// 
// Connected components would require
// power equal to at least 100 stars!
//
// "Wait, how many components did you say are plugged in? With that much
// equipment, you could produce snow for an entire--" You disconnect the call.
// 
// You have nowhere near that many stars - you need to find a way to disconnect
// at least half of the equipment here, but it's already Christmas! You only
// have time to disconnect three wires.
// 
// Fortunately, someone left a wiring diagram (your puzzle input) that shows
// how the components are connected. For example:
// 
// jqt: rhn xhk nvd
// rsh: frs pzl lsr
// xhk: hfx
// cmg: qnr nvd lhk bvb
// rhn: xhk bvb hfx
// bvb: xhk hfx
// pzl: lsr hfx nvd
// qnr: nvd
// ntq: jqt hfx bvb xhk
// nvd: lhk
// lsr: lhk
// rzs: qnr cmg lsr rsh
// frs: qnr lhk lsr
//
// Each line shows the name of a component, a colon, and then a list of other
// components to which that component is connected. Connections aren't
// directional; abc: xyz and xyz: abc both represent the same configuration.
// Each connection between two components is represented only once, so some
// components might only ever appear on the left or right side of a colon.
// 
// In this example, if you disconnect the wire between hfx/pzl, the wire
// between bvb/cmg, and the wire between nvd/jqt, you will divide the
// components into two separate, disconnected groups:
// 
// 9 components: cmg, frs, lhk, lsr, nvd, pzl, qnr, rsh, and rzs.
// 6 components: bvb, hfx, jqt, ntq, rhn, and xhk.
//
// Multiplying the sizes of these groups together produces 54.
// 
// Find the three wires you need to disconnect in order to divide the
// components into two separate groups. What do you get if you multiply the
// sizes of these two groups together?

import java.io.*
import kotlin.math.ceil

// This *cannot* be a data class, even though it seems like such an obvious
// candidate. The reason is that I want edges with identical a's and b's to be
// treated as completely different objects. Otherwise, bad things happen when
// trying to copy the Graph.
class Edge(var a: String, var b: String) {
  operator fun component1() = this.a
  operator fun component2() = this.b

  fun copy() = Edge(this.a, this.b)

  override fun toString() = "${this.a} <-> ${this.b}"
}

class Graph(components: Map<String, List<String>>) {
  val nodes: MutableMap<String, MutableList<Edge>> = mutableMapOf()
  val edges: MutableList<Edge> = mutableListOf()
  val nodeSizes: MutableMap<String, Int> = mutableMapOf()

  init {
    components.entries.forEach { (node, neighbors) ->
      this.nodeSizes[node] = 1
      if (node !in nodes) {
        this.nodes[node] = mutableListOf()
      }
      neighbors.forEach {
        if (it !in nodes) {
          this.nodes[it] = mutableListOf()
        }

        val edge = Edge(node, it)
        this.edges.add(edge)
        this.nodes[node]!!.add(edge)
        this.nodes[it]!!.add(edge)
        this.nodeSizes[it] = 1
      }
    }
    this.edges.shuffle()
  }

  // Pick a random edge and combine those two nodes into a single node
  fun contract(): Unit {
    val edge = this.edges.removeLast()
    val (a, b) = edge
    if (a != b) {
      val aEdges = this.nodes[a]!!
      this.nodes.remove(b)!!.forEach {
        if (it.a == b) {
          it.a = a
        } else {
          it.b = a
        }
        aEdges.add(it)
      }
      aEdges.removeAll { it.a == it.b }
      this.nodeSizes[a] = this.nodeSizes[a]!! + this.nodeSizes.remove(b)!!
    }
  }

  // create a copy of the graph
  fun copy(): Graph {
    val newGraph = Graph(mapOf())
    val newEdges = this.edges.associate { it to it.copy() }
    this.nodes.entries.forEach { (k, v) -> newGraph.nodes[k] = v.map { newEdges[it]!! }.toMutableList() }
    this.nodeSizes.entries.forEach { (k, v) -> newGraph.nodeSizes[k] = v }
    newGraph.edges.addAll(newEdges.values)
    newGraph.edges.shuffle()
    return newGraph
  }
}

val components = File("input.txt").readLines().associate { it.take(3) to it.drop(5).split(' ') }

// Karger algo to find the min-cut (Karger algo, t = 2).
fun contract(graph: Graph, t: Int): Unit {
  while (graph.nodes.size > t) {
    graph.contract()
  }
}

// Karger-Stein algo to find the min-cut.
fun fastmincut(graph: Graph): Graph {
  if (graph.nodes.size <= 6) {
    contract(graph, 2)
    return graph
  } else {
    // t = 1 + ceil(|V| / sqrt(2))
    val t = 1 + ceil(graph.nodes.size.toDouble() / 1.41421356237).toInt()
    val graph2 = graph.copy()
    contract(graph, t)
    contract(graph2, t)

    val resultGraph1 = fastmincut(graph)
    val resultGraph2 = fastmincut(graph2)
    if (resultGraph1.nodes.values.first().size < resultGraph2.nodes.values.first().size) {
      return resultGraph1
    } else {
      return resultGraph2
    }
  }
}

// Run the algo until we get what we want
// Ultimately, the Karger-Stein algo failed me... running just the Karger algo
// proved to be _much_ faster: ~6s vs ~66s (times include kotlin compilation,
// which accounts for probably ~3s). I suspect the copy step is killing my
// Karger-Stein performance, but I don't particularly care to improve it at
// this point. However, it is worth noting that the Karger-Stein algo gets the
// correct answer in a single iteration, whereas the Karger algo by itself took
// 20 iterations.
for (i in 1u..UInt.MAX_VALUE) {
  // val graph = Graph(components)
  // val resultGraph = fastmincut(graph)
  var resultGraph = Graph(components)
  contract(resultGraph, 2)
  if (resultGraph.nodes.values.first().size <= 3) {
    println("After $i iterations: ${resultGraph.nodeSizes.values.reduce(Int::times)}")
    break
  }
}
