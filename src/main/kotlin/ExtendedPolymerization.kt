import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable

object ExtendedPolymerization : Day {
  override val name: String = "Extended Polymerization"

  @Composable
  override fun ColumnScope.content() {
    p("""
      The incredible pressures at this depth are starting to put a strain on your submarine. The submarine has polymerization equipment that would produce suitable materials to reinforce the submarine, and the nearby volcanically-active caves should even have the necessary input elements in sufficient quantities.

      The submarine manual contains instructions for finding the optimal polymer formula; specifically, it offers a polymer template and a list of pair insertion rules (your puzzle input). You just need to work out what polymer would result after repeating the pair insertion process a few times.
      
      The first line is the polymer template - this is the starting point of the process.

      The following section defines the pair insertion rules. A rule like AB -> C means that when elements A and B are immediately adjacent, element C should be inserted between them. These insertions all happen simultaneously.
      
      Note that these pairs overlap: the second element of one pair is the first element of the next pair. Also, because all pairs are considered simultaneously, inserted elements are not considered to be part of a pair until the next step.
    """.trimIndent())

    h4("Part 1")
    p("""
      Apply 10 steps of pair insertion to the polymer template and find the most and least common elements in the result. What do you get if you take the quantity of the most common element and subtract the quantity of the least common element?
    """.trimIndent())

    Answer {
      generatePolymer(10)
    }

    h4("Part 2")
    p("""
      The resulting polymer isn't nearly strong enough to reinforce the submarine. You'll need to run more steps of the pair insertion process; a total of 40 steps should do it.
      
      Apply 40 steps of pair insertion to the polymer template and find the most and least common elements in the result. What do you get if you take the quantity of the most common element and subtract the quantity of the least common element?
    """.trimIndent())

    Answer {
      generatePolymer(40)
    }
  }

  private fun generatePolymer(steps: Int): Long {
    val (template, rules) = readPolymerTemplate()
    var polymer = buildMap {
      template.zipWithNext { a, b -> "$a$b" }.forEach(::increment)
    }

    repeat(steps) {
      polymer = buildMap {
        polymer.forEach { (key, count) ->
          (rules[key] ?: listOf(key)).forEach { newKey ->
            changeBy(newKey, count)
          }
        }
      }
    }

    val histogram = buildMap {
      polymer.forEach { (key, count) ->
        changeBy(key[0], count)
      }
      increment(template.last())
    }

    return histogram.values.run {
      maxOf { it } - minOf { it }
    }
  }

  fun readPolymerTemplate(): Pair<String, Map<String, List<String>>> = inputStream().reader().useLines {
    val lines = it.iterator()
    val template = lines.next()
    lines.next() // empty line
    val rules = lines.asSequence().map { line ->
      val m0 = line[0]
      val m1 = line[1]
      val insert = line[6]
      "$m0$m1" to listOf("$m0$insert", "$insert$m1")
    }.toMap()
    return template to rules
  }
}

fun <K> MutableMap<K, Long>.changeBy(key: K, delta: Long) = compute(key) { _, count ->
  ((count ?: 0L) + delta).takeIf { it != 0L }
}

fun <K> MutableMap<K, Long>.increment(key: K) = changeBy(key, 1)
