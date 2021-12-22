import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

object WhaleTreachery: Day {
  override val name = "The Treachery of Whales"

  @Composable
  override fun ColumnScope.content() {
    p(
      """
      A giant whale has decided your submarine is its next meal, and it's much faster than you are. There's nowhere to run!

      Suddenly, a swarm of crabs (each in its own tiny submarine - it's too deep for them otherwise) zooms in to rescue you! They seem to be preparing to blast a hole in the ocean floor; sensors indicate a massive underground cave system just beyond where they're aiming!

      The crab submarines all need to be aligned before they'll have enough power to blast a large enough hole for your submarine to get through. However, it doesn't look like they'll be aligned before the whale catches you! Maybe you can help?

      There's one major catch - crab submarines can only move horizontally.

      You quickly make a list of the horizontal position of each crab (your puzzle input). Crab submarines have limited fuel, so you need to find a way to make all of their horizontal positions match while requiring them to spend as little fuel as possible.
    """.trimIndent()
    )

    h4("Part 1")
    p(
      """
      Each change of 1 step in horizontal position of a single crab costs 1 fuel.
      
      Determine the horizontal position that the crabs can align to using the least fuel possible. How much fuel must they spend to align to that position?
    """.trimIndent()
    )

    val answer1 by answering {
      readCrabs().fuelSequenceLinear().toList().toIntArray()
    }

    answer1 {
      it.lineGraph(1024, 512)
      Answer(it.minOf { fuel -> fuel })
    }

    h4("Part 2")
    p(
      """
      The crabs don't seem interested in your proposed solution. Perhaps you misunderstand crab engineering?

      As it turns out, crab submarine engines don't burn fuel at a constant rate. Instead, each change of 1 step in horizontal position costs 1 more unit of fuel than the last: the first step costs 1, the second step costs 2, the third step costs 3, and so on.

      As each crab moves, moving further becomes more expensive. This changes the best horizontal position to align them all on.
      
      Determine the horizontal position that the crabs can align to using the least fuel possible so they can make you an escape route! How much fuel must they spend to align to that position?
    """.trimIndent()
    )

    val answer2 by answering {
      readCrabs().fuelSequenceQuadratic().toList().toIntArray()
    }

    answer2 {
      it.lineGraph(1024, 512)
      Answer(it.minOf { fuel -> fuel })
    }
  }

  @Composable
  private fun IntArray.lineGraph(width: Int, height: Int) {
    Canvas(Modifier.width(width.dp).height(height.dp)) {
      val h = height.toFloat()
      val dx = width.toFloat() / this@lineGraph.size
      val ys = normalized().map { it * height }.iterator()
      var offset = Offset(0f, h - ys.next())
      while (ys.hasNext()) {
        val end = Offset(offset.x + dx, h - ys.next())
        drawLine(
          color = Color.Black,
          start = offset,
          end = end
        )
        offset = end
      }
    }
  }

  private fun IntArray.normalized(): Sequence<Float> {
    var min = Int.MAX_VALUE
    var max = Int.MIN_VALUE
    forEach {
      min = minOf(min, it)
      max = maxOf(max, it)
    }
    val norm = (max - min).toFloat()
    return asSequence().map { (it - min) / norm }
  }

  private fun readCrabs() = inputStream().reader().useLines { lines ->
    lines.flatMap { it.splitToSequence(',') }
      .map { it.toInt() }
      .toList()
      .toIntArray()
  }

  private fun IntArray.fuelSequenceLinear() = sequence {
    sort()
    var index = 0
    var fuel = sum()
    for (position in first()..last()) {
      index = indexOfFirst(index) { it > position }
      val tail = size - index
      fuel += index - tail
      yield(fuel)
    }
  }

  private fun IntArray.fuelSequenceQuadratic() = sequence {
    sort()
    for (position in first()..last()) {
      yield(sumOf {
        val distance = (it - position).absoluteValue
        distance * (distance + 1) / 2
      })
    }
  }

  private inline fun IntArray.indexOfFirst(start: Int, predicate: (Int) -> Boolean): Int {
    for (index in start until size) {
      if (predicate(this[index])) {
        return index
      }
    }
    return -1
  }
}