import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

object Chiton : Day {
  override val name = "Chiton"

  @Composable
  override fun ColumnScope.content() {
    p("""
      You've almost reached the exit of the cave, but the walls are getting closer together. Your submarine can barely still fit, though; the main problem is that the walls of the cave are covered in chitons, and it would be best not to bump any of them.

      The cavern is large, but has a very low ceiling, restricting your motion to two dimensions. The shape of the cavern resembles a square; a quick scan of chiton density produces a map of risk level throughout the cave (your puzzle input).
    """.trimIndent())

    h4("Risk Map")
    val input by answering { readRiskMap() }
    input { it.render() }

    h4("Part 1")
    p("""
      You start in the top left position, your destination is the bottom right position, and you cannot move diagonally. The number at each position is its risk level; to determine the total risk of an entire path, add up the risk levels of each position you enter (that is, don't count the risk level of your starting position unless you enter it; leaving it adds no risk to your total).

      Your goal is to find a path with the lowest total risk.
      
      What is the lowest total risk of any path from the top left to the bottom right?
    """.trimIndent())

    val part1 by input.transform { risk -> risk to cost(risk) }

    part1 { (risk, cost) ->
      val maxCost = cost.maxOf {
        if (it == Int.MAX_VALUE) Int.MIN_VALUE else it
      }
      val alphaStep = 1f/maxCost
      risk.render {
        cost.forEachIndexed { index, c ->
          drawRect(
            color = Color.Black,
            alpha = minOf(1f, alphaStep * c),
            topLeft = risk.offset(index),
            size = Size(1f, 1f)
          )
        }
      }

      Answer(cost.last())
    }

    h4("Part 2")
    p("""
      Now that you know how to find low-risk paths in the cave, you can try to find your way out.

      The entire cave is actually five times larger in both dimensions than you thought; the area you originally scanned is just one tile in a 5x5 tile area that forms the full map. Your original map tile repeats to the right and downward; each time the tile repeats to the right or downward, all of its risk levels are 1 higher than the tile immediately up or left of it. However, risk levels above 9 wrap back around to 1.
      
      Using the full map, what is the lowest total risk of any path from the top left to the bottom right?
    """.trimIndent())

    val part2Input by answering { readLargeRiskMap() }

    part2Input {
      p("Input:")
      it.render(scale = 2f)
      p("")
    }

    val part2 by part2Input.transform { risk -> risk to cost(risk) }

    part2 { (risk, cost) ->
      val maxCost = cost.maxOf {
        if (it == Int.MAX_VALUE) Int.MIN_VALUE else it
      }
      val alphaStep = 1f/maxCost
      risk.render(scale = 2f) {
        cost.forEachIndexed { index, c ->
          drawRect(
            color = Color.Black,
            alpha = minOf(1f, alphaStep * c),
            topLeft = risk.offset(index),
            size = Size(1f, 1f)
          )
        }
      }

      Answer(cost.last())
    }
  }

  private fun cost(risk: RiskMap): IntArray {
    val cost = IntArray(risk.size) { Int.MAX_VALUE }
    cost[0] = 0
    val toExplore = mutableSetOf(0)
    while (toExplore.isNotEmpty()) {
      val next = toExplore.minByOrNull { cost[it] }!!
      toExplore.remove(next)
      risk.neighbors(next).forEach { neighbor ->
        val moveCost = cost[next] + risk[neighbor]
        if (moveCost < cost[neighbor]) {
          toExplore.add(neighbor)
          cost[neighbor] = moveCost
        }
      }
    }
    return cost
  }

  fun readRiskMap() = inputStream().reader().useLines { lines ->
    val iterator = lines.iterator()
    val first = iterator.next()
    val width = first.length
    val riskValues = (sequenceOf(first) + iterator.asSequence())
      .flatMap { it.asSequence() }
      .map { (it - '0').toByte() }
      .toList().toByteArray()
    RiskMap(width, riskValues)
  }

  fun readLargeRiskMap() = inputStream().reader().useLines { lines ->
    val iterator = lines.iterator()
    val first = iterator.next()
    val width = first.length * 5
    val riskValues = (sequenceOf(first) + iterator.asSequence())
      .flatMap { line ->
        val template = line.map { it - '0' }
        (0 until 5).flatMap { i ->
          template.map { it + i }
        }
      }
      .toList()
      .let { template ->
        (0 until 5).flatMap { i ->
          template.map { it + i }
        }
      }
      .map { (if (it > 9) it - 9 else it).toByte() }
      .toByteArray()

    RiskMap(width, riskValues)
  }

  class RiskMap(val width: Int, val values: ByteArray) {
    val height = values.size / width
    val size get() = values.size

    operator fun get(index: Int) = values[index]

    @Composable
    fun render(scale: Float = 10f, content: DrawScope.() -> Unit = {
      values.forEachIndexed { index, risk ->
        drawRect(
          color = Color.Black,
          alpha = 0.1f * risk,
          topLeft = offset(index),
          size = Size(1f, 1f)
        )
      }
    }) {
      Canvas(Modifier.width(width * scale.dp).height(height * scale.dp)) {
        scale(scale, Offset.Zero, content)
      }
    }

    fun offset(index: Int): Offset = index.let { (x, y) ->
      Offset(x.toFloat(), y.toFloat())
    }

    fun neighbors(index: Int) = sequence {
      val (x, y) = index
      if (y < width - 1) yield(index + 1)
      if (x < height - 1) yield(index + width)
      if (y > 0) yield(index - 1)
      if (x > 0) yield(index - width)
    }

    operator fun Int.component1() = this / width
    operator fun Int.component2() = this % width
  }
}
