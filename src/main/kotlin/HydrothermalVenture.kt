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

object HydrothermalVenture: Day {
  override val name: String = "Hydrothermal Venture"

  @Composable
  override fun ColumnScope.content() {
    p("""
      You come across a field of hydrothermal vents on the ocean floor! These vents constantly produce large, opaque clouds, so it would be best to avoid them if possible.

      They tend to form in lines; the submarine helpfully produces a list of nearby lines of vents (your puzzle input) for you to review.
    """.trimIndent())

    h4("Puzzle Input")
    val lines by answering {
      buildList {
        forEachVent { x1, y1, x2, y2 ->
          add(Offset(x1.toFloat(), y1.toFloat()) to Offset(x2.toFloat(), y2.toFloat()))
        }
      }
    }

    lines {
      Canvas(Modifier.width(1000.dp).height(1000.dp)) {
        it.forEach { (start, end) ->
          drawLine(start = start, end = end, color = Color.Black, alpha = 0.125f)
        }
      }
    }

    h4("Part 1")
    p("""
     For now, only consider horizontal and vertical lines: lines where either x1 = x2 or y1 = y2. 
     
     To avoid the most dangerous areas, you need to determine the number of points where at least two lines overlap.
     
     Consider only horizontal and vertical lines. At how many points do at least two lines overlap?
    """.trimIndent())

    Answer {
      val map = IntArray(1000 * 1000)
      forEachVent { x1, y1, x2, y2 ->
        if (x1 != x2 && y1 != y2) return@forEachVent
        for ((x, y) in Coordinate(x1, y1)..Coordinate(x2, y2)) {
          map[x * 1000 + y] += 1
        }
      }
      map.count { it >= 2 }
    }

    h4("Part 2")
    p("""
      Unfortunately, considering only horizontal and vertical lines doesn't give you the full picture; you need to also consider diagonal lines.

      Because of the limits of the hydrothermal vent mapping system, the lines in your list will only ever be horizontal, vertical, or a diagonal line at exactly 45 degrees.
      
      You still need to determine the number of points where at least two lines overlap.
    """.trimIndent())

    Answer {
      val map = IntArray(1000 * 1000)
      forEachVent { x1, y1, x2, y2 ->
        for ((x, y) in Coordinate(x1, y1)..Coordinate(x2, y2)) {
          map[x * 1000 + y] += 1
        }
      }
      map.count { it >= 2 }
    }
  }

  private inline fun forEachVent(action: (x1: Int, y1: Int, x2: Int, y2: Int) -> Unit) {
    inputStream().reader().useLines { lines ->
      val linePattern = Regex("""(\d+),(\d+) -> (\d+),(\d+)""")
      lines.map { line ->
        linePattern.matchEntire(line)!!.destructured
      }.forEach { (x1 ,y1, x2, y2) ->
        action(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
      }
    }
  }

  private data class Coordinate(val x: Int, val y: Int): Comparable<Coordinate> {
    override fun compareTo(other: Coordinate): Int = when {
      x == other.x -> y.compareTo(other.y)
      else -> x.compareTo(other.x)
    }

    override fun toString() = "($x, $y)"
  }

  private operator fun ClosedRange<Coordinate>.iterator() = iterator {
    var (x, y) = start
    val (s, t) = endInclusive
    val dx = (s - x).let { it / maxOf(1, it.absoluteValue) }
    val dy = (t - y).let { it / maxOf( 1, it.absoluteValue) }

    while (x != s || y != t) {
      yield(Coordinate(x, y))
      x += dx
      y += dy
    }
    yield(endInclusive)
  }
}