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

object SmokeBasin: Day {
  override val name = "Smoke Basin"

  @Composable
  override fun ColumnScope.content() {
    p(
      """
      These caves seem to be lava tubes. Parts are even still volcanically active; small hydrothermal vents release smoke into the caves that slowly settles like rain.

      If you can model how the smoke flows through the caves, you might be able to avoid it and be that much safer. The submarine generates a heightmap of the floor of the nearby caves for you (your puzzle input).

      Smoke flows to the lowest point of the area it's in. Each number corresponds to the height of a particular location, where 9 is the highest and 0 is the lowest a location can be.
    """.trimIndent()
    )

    h4("Height Map")
    val input by answering { readHeightMap() }

    input { heightMap ->
      with(heightMap) {
        render {
          forEachIndexed { index, height ->
            drawRect(
              color = Color.Black,
              alpha = 1f - 0.1f * height,
              topLeft = offset(index),
              size = Size(1f, 1f),
            )
          }
        }
      }
    }

    h4("Part 1")
    p(
      """
      Your first goal is to find the low points - the locations that are lower than any of its adjacent locations. Most locations have four adjacent locations (up, down, left, and right); locations on the edge or corner of the map have three or two adjacent locations, respectively. (Diagonal locations do not count as adjacent.)
      
      The risk level of a low point is 1 plus its height. In the above example, the risk levels of the low points are 2, 1, 6, and 6. The sum of the risk levels of all low points in the heightmap is therefore 15.

      Find all of the low points on your heightmap. What is the sum of the risk levels of all low points on your heightmap?
    """.trimIndent()
    )

    val part1 by input.transform { heightMap ->
      val flows = IntArray(heightMap.size) { position -> position } // To start: every position flows to itself
      while (true) {
        var changed = false
        flows.indices.forEach {
          val height = heightMap[it]
          val (newFlow, _) = heightMap.neighborhood(it)
            .filter { neighbor -> heightMap[neighbor] <= height }
            .map { neighbor ->
              val f = flows[neighbor]
              f to heightMap[f]
            }
            .minByOrNull { (_, height) -> height }
            ?: return@forEach

          if (flows[it] != newFlow) {
            flows[it] = newFlow
            changed = true
          }
        }

        if (!changed) break
      }
      heightMap to flows
    }

    part1 { (heightMap, flows) ->
      with(heightMap) {
        render {
          flows.forEachIndexed { from, to ->
            drawLine(
              color = Color.Black,
              alpha = 0.125f,
              start = offset(from),
              end = offset(to),
            )
          }
        }
      }
      Answer(flows.toHashSet().sumOf { heightMap[it] + 1 })
    }

    h4("Part 2")
    p("""
      Next, you need to find the largest basins so you know what areas are most important to avoid.

      A basin is all locations that eventually flow downward to a single low point. Therefore, every low point has a basin, although some basins are very small. Locations of height 9 do not count as being in any basin, and all other locations will always be part of exactly one basin.

      The size of a basin is the number of locations within the basin, including the low point.
      
      Find the three largest basins and multiply their sizes together. What do you get if you multiply together the sizes of the three largest basins?
    """.trimIndent())

    val part2 by part1.transform { (heightMap, flows) ->
      heightMap to flows.asSequence()
        .mapIndexed { index, flow -> index to flow }
        .filter { (index, _) -> heightMap[index] < 9  }
        .groupBy { (_, flow) -> flow }
        .mapValues { (_, locs) -> locs.map { (index, _) -> index } }
        .values
        .sortedByDescending { it.size }
    }

    part2 { (heightMap, basins) ->
      with(heightMap) {
        render {
          var alpha = 1f
          basins.forEach { basin ->
            basin.forEach {
              drawRect(
                color = Color.Black,
                topLeft = offset(it),
                size = Size(1f, 1f),
                alpha = alpha,
              )
            }
            alpha = maxOf(alpha / 2, 1f / 32)
          }
        }
      }
      Answer(basins.take(3).fold(1) { product, basin -> product * basin.size })
    }
  }

  private fun readHeightMap() = inputStream().reader().useLines { lines ->
    val iterator = lines.iterator()
    val firstLine = iterator.next()
    val width = firstLine.length
    val values = (sequenceOf(firstLine) + iterator.asSequence())
      .flatMap { line -> line.map { (it - '0').toByte() } }
      .toList().toByteArray()
    HeightMap(width, values)
  }

  private class HeightMap(val width: Int, private val values: ByteArray) {
    val size get() = values.size
    val length = values.size / width

    operator fun get(index: Int): Byte = values[index]

    inline fun forEachIndexed(action: (Int, Byte) -> Unit) = values.forEachIndexed(action)

    fun neighborhood(index: Int): List<Int> = buildList {
      val x = index / width
      val y = index % width
      if (y > 0) add(index - 1)
      if (x > 0) add(index - width)
      if (y < width - 1) add(index + 1)
      if (x < length - 1) add(index + width)
    }

    override fun toString(): String {
      return values.asSequence().chunked(width) { it.joinToString("") }
        .joinToString("\n")
    }

    @Composable
    inline fun render(scale: Float = 10f, crossinline render: DrawScope.() -> Unit) {
      Canvas(Modifier.width(width * scale.dp).height(length * scale.dp)) {
        scale(scale, pivot = Offset(0f, 0f), block = render)
      }
    }

    fun offset(index: Int) = Offset((index % width).toFloat(), (index / width).toFloat())
  }
}