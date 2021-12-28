import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

object PassagePathing : Day {
  override val name = "Passage Pathing"

  @Composable
  override fun ColumnScope.content() {
    p("""
      With your submarine's subterranean subsystems subsisting suboptimally, the only way you're getting out of this cave anytime soon is by finding a path yourself. Not just a path - the only way to know if you've found the best path is to find all of them.

      Fortunately, the sensors are still mostly working, and so you build a rough map of the remaining caves (your puzzle input).
    """.trimIndent())

    h4("Input")

    val graph by answering {
      readGraph()
    }

    graph {
      p(it.entries.joinToString("\n") { (k, vs) -> "$k: $vs" })
    }

    h4("Part 1")
    p("""
      Your goal is to find the number of distinct paths that start at start, end at end, and don't visit small caves more than once. There are two types of caves: big caves (written in uppercase, like A) and small caves (written in lowercase, like b). It would be a waste of time to visit any small cave more than once, but big caves are large enough that it might be worth visiting them multiple times. So, all paths you find should visit small caves at most once, and can visit big caves any number of times.
      
      How many paths through this cave system are there that visit small caves at most once?
    """.trimIndent())

    val part1 by graph.transform {
      it.navigate("start", "end") { markers -> markers.keys }
    }

    part1.invoke()

    h4("Part 2")
    p("""
      After reviewing the available paths, you realize you might have time to visit a single small cave twice. Specifically, big caves can be visited any number of times, a single small cave can be visited at most twice, and the remaining small caves can be visited at most once. However, the caves named start and end can only be visited exactly once each: once you leave the start cave, you may not return to it, and once you reach the end cave, the path must end immediately.
      
      Given these new rules, how many paths through this cave system are there?
    """.trimIndent())

    val part2 by graph.transform {
      it.navigate("start", "end") { markers ->
        if (markers.any { (_, visits) -> visits > 1 }) {
          markers.keys
        } else {
          setOf()
        }
      }
    }

    part2.invoke()
  }

  fun readGraph(): Map<String, List<String>> = buildMap<String, MutableList<String>> {
    inputStream().reader().forEachLine { line ->
      val (a, b) = line.split('-')
      if (a != "end" && b != "start") {
        getOrPut(a, ::mutableListOf).add(b)
      }
      if (b != "end" && a != "start") {
        getOrPut(b, ::mutableListOf).add(a)
      }
    }
  }

  fun Map<String, List<String>>.navigate(
    from: String, to: String,
    markers: MutableMap<String, Int> = mutableMapOf(),
    blacklist: (markers: Map<String, Int>) -> Set<String>,
  ): Long {
    if (from == to) return 1L
    return markers.with(from.takeIf { it.all(Char::isLowerCase) }) {
      getOrElse(from, ::listOf).minus(blacklist(markers)).sumOf {
        navigate(it, to, markers, blacklist)
      }
    }
  }

  inline fun <T> MutableMap<String, Int>.with(element: String?, action: () -> T): T {
    element?.let {
      compute(it) { _, count -> (count ?: 0) + 1 }
    }
    return action().also {
      element?.let {
        computeIfPresent(it) { _, count ->
          (count - 1).takeIf { it > 0 }
        }
      }
    }
  }
}
