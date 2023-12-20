// --- Part Two ---
// The final machine responsible for moving the sand down to Island Island has
// a module attached named rx. The machine turns on when a single low pulse is
// sent to rx.
//
// Reset all modules to their default states. Waiting for all pulses to be
// fully handled after each button press, what is the fewest number of button
// presses required to deliver a single low pulse to the module named rx?

import java.io.*

data class Counts(val lowPulses: Int, val highPulses: Int) {
  operator fun plus(other: Counts): Counts {
    return Counts(this.lowPulses + other.lowPulses, this.highPulses + other.highPulses)
  }

  operator fun times(num: Int): Counts {
    return Counts(this.lowPulses * num, this.highPulses * num)
  }
}

data class Pulse(val from: String, val to: String, val value: Boolean) {
  override fun toString() = "${this.from} ${this.value} -> ${this.to}"
}

interface Module {
  val name: String
  val output: List<String>
  var value: Boolean
  fun pulse(pulse: Pulse): List<Pulse>
}

class Broadcast(override val name: String, override val output: List<String>) : Module {
  override var value = false

  override fun pulse(pulse: Pulse): List<Pulse> {
    this.value = pulse.value
    return this.output.map { Pulse(this.name, it, this.value) }
  }

  override fun toString() = "${this.name} -> ${this.output.joinToString(", ")}"
}

class FlipFlop(override val name: String, override val output: List<String>) : Module {
  override var value = false

  override fun pulse(pulse: Pulse): List<Pulse> {
    if (pulse.value) {
      return listOf()
    }

    this.value = !this.value
    return this.output.map { Pulse(this.name, it, this.value) }
  }

  override fun toString() = "%${this.name} -> ${this.output.joinToString(", ")}"
}

class Conjunction(override val name: String, override val output: List<String>) : Module {
  override var value = false
  val inputs = mutableMapOf<String, Boolean>()

  fun initializeInput(input: String): Unit {
    this.inputs[input] = false
  }

  override fun pulse(pulse: Pulse): List<Pulse> {
    this.inputs[pulse.from] = pulse.value
    val value = !this.inputs.values.all { it }
    return this.output.map { Pulse(this.name, it, value) }
  }

  override fun toString() = "&${this.name} -> ${this.output.joinToString(", ")}"
}

// read input
val RGX = Regex("([%&]?\\w+) -> ([\\w, ]+)")
val modules = File("input.txt").readLines().associate {
  val (_, name, output) = RGX.matchEntire(it)!!.groupValues
  val sym = name.drop(1)
  when (name[0]) {
    '%' -> sym to FlipFlop(sym, output.split(", "))
    '&' -> sym to Conjunction(sym, output.split(", "))
    else -> name to Broadcast(name, output.split(", "))
  }
}
// modules.forEach { println(it) }

// initialize conjunction modules
val conjunctions = modules.values.filterIsInstance<Conjunction>().associate { it.name to it }
modules.forEach { (name, module) ->
  module.output.forEach {
    conjunctions[it]?.let {
      it.initializeInput(name)
    }
  }
}

// rx is singaled by a single conjunction - find what that is
val rxConjunction = conjunctions.values.find { "rx" in it.output }!!

// The rx conjunction will pulse low when all of the inputs to the conjunction
// are high. So, find how many button presses it takes to signal each high.
var pulsedHigh = mutableMapOf<String, Long>()
var buttonPresses = 0L
outer@ while (true) {
  val pulses = mutableListOf(Pulse("button", "broadcaster", false))
  buttonPresses++
  while (pulses.isNotEmpty()) {
    val pulse = pulses.removeFirst()
    if (pulse.value && pulse.to == rxConjunction.name && pulse.from !in pulsedHigh) {
      pulsedHigh[pulse.from] = buttonPresses
      if (pulsedHigh.size == rxConjunction.inputs.size) {
        break@outer
      }
    }
    pulses.addAll(modules[pulse.to]?.pulse(pulse) ?: listOf())
  }
}

// The lcm of when each input goes high will tell us when they're all high.
fun gcd(a: Long, b: Long): Long {
  if (a == 0L) return b
  return gcd(b % a, a)
}

fun lcm(a: Long, b: Long): Long {
  return a * b / gcd(a, b)
}

val result = pulsedHigh.values.reduce(::lcm)
println(result)
