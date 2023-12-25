# Advent of Code, 2023 :christmas_tree:
My solutions to the [Advent of Code 2023] in [Kotlin].

I like to use the Advent of Code as a learning opportunity. This year, I
decided on learning [Kotlin]. I have zero experience with the language.

## Retrospective :santa:
I must admit, my opinion of [Kotlin] was kind of sour after the first day or
two. I found the language to be... adequate? That is to say: perfectly usable,
but without any stand-out features. It started to grow on me somewhere around
day 3 or 4, I think. I appreciate the vast built-in functionally-inspired
niceties, shorthands (such as `it`, data classes, and the concise syntax for
primary constructors), and familiar syntax (it was easy to learn).

But, it's not without its problems.
* I find the regex engine to be severely lacking. I never did figure out how to
  make it honor start and end anchors (ie, `^` and `$`). And, there's no
  syntactical sugar for the regex language so you end up with a lot of `\\`.
* No implicit conversions? In 2023? If I never see another compiler _error_
  about comparing or performing math on an Int and a Long again, it'll be too
  damn soon. Come on!
* (Almost) everything is an expression is cool. I kind of like when languages
  do that. But I'd argue that other languages do a better job of it. For
  starters, the "almost" qualifier - why can't an assignment be an expression?
  Then there was the error I ran into on [day 15, part 2]. The compiler reasons
  that the return value of the `RGX.matchEntire(it)?.let { ... }` will be
  `Unit` because it is the last statement in the enclosing `forEach`, which
  expects a `Unit` return value. However, if you uncomment the lines below the
  `matchEntire`, the compiler now throws an error inside the `let` block - code
  that is unchanged by adding code below it! The reason is that the last
  statement in the `let` is an `if` and not all paths return the same type
  (there's no `else` for starters, and the three paths through the `if` return
  `Unit`, `Boolean`, and a `Lens`, respectively). So now the compiler can't
  figure out the return value of the `let`... the return value that literally
  doesn't matter because it's not assigned to anything. Sure, since I don't
  care about the return value, I probably should have used `also` instead of
  `let`. But I think the compiler could do a better job reasoning the intention
  here. Since the return value is unused, why not just assume it is `Unit` like
  before?
* Documentation. The docs are pretty nice. But in the stdlib api docs, I would
  _kill_ for a scrollable side bar that had a list of all of the symbols. When
  I knew exactly what I was looking for, it was somewhat easy to just Cmd+F
  search for it (but even that was painful if the function was a common english
  word, or, for example, something like MutableList on the collections page -
  you have to step through a dozen matches before you get to the _actual_
  MutableList class). When I vaguely knew what I was looking for (like, "hmm, I
  bet there's a function that does X - but what would it be called?"), I had no
  option but to scroll through everything.
* I made the compiler very mad one day by trying to do a `.map(::SomeClass)`.
  It's my understanding that should have worked, and I found examples of it on
  stackoverflow. But, for me, the compiler threw an exception and died. Had to
  change it to `.map { SomeClass(it) }` :shrug:

But, despite my complaints above, I did enjoy [Kotlin]. It wasn't particularly
well suited for solving AoC puzzles, such as some of the languages I chose in
previous years. But, as I became more comfortable with it, I found writing
[Kotlin] to be pleasurable.

:snowman_with_snow:

[Advent of Code 2023]: https://adventofcode.com/2023
[Kotlin]: https://kotlinlang.org/
[day 15, part 2]: https://github.com/bmatcuk/adventofcode2023/blob/c7d201d505a257792e0ba32bbdf66e2e6b1f119e/day15/part2.kts#L136-L156
