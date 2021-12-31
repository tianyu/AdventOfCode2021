import androidx.compose.foundation.Canvas
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

object DumboOctopus: Day {
  override val name = "Dumbo Octopus"

  @Composable
  override fun ColumnScope.content() {
    p("""
      You enter a large cavern full of rare bioluminescent dumbo octopuses! They seem to not like the Christmas lights on your submarine, so you turn them off for now.

      There are 100 octopuses arranged neatly in a 10 by 10 grid. Each octopus slowly gains energy over time and flashes brightly for a moment when its energy is full. Although your lights are off, maybe you could navigate through the cave without disturbing the octopuses if you could predict when the flashes of light will happen.

      Each octopus has an energy level - your submarine can remotely measure the energy level of each octopus (your puzzle input).
      
      The energy level of each octopus is a value between 0 and 9. Here, the top-left octopus has an energy level of 5, the bottom-right one has an energy level of 6, and so on.

      You can model the energy levels and flashes of light in steps. During a single step, the following occurs:

         - First, the energy level of each octopus increases by 1.
         - Then, any octopus with an energy level greater than 9 flashes. This increases the energy level of all adjacent octopuses by 1, including octopuses that are diagonally adjacent. If this causes an octopus to have an energy level greater than 9, it also flashes. This process continues as long as new octopuses keep having their energy level increased beyond 9. (An octopus can only flash at most once per step.)
         - Finally, any octopus that flashed during this step has its energy level set to 0, as it used all of its energy to flash.

      Adjacent flashes can cause an octopus to flash on a step even if it begins that step with very little energy.
    """.trimIndent())

    h4("Part 1")
    p("""
      Given the starting energy levels of the dumbo octopuses in your cavern, simulate 100 steps. How many total flashes are there after 100 steps?
    """.trimIndent())

    val part1 by answering {
      octopuses().steps().take(100).toList()
    }

    part1 {
      it.render()
      Answer(it.sumOf { formation ->
        formation.count { octopus ->
          octopus == 0.toByte()
        }
      })
    }

    h4("Part 2")
    p("""
      It seems like the individual flashes aren't bright enough to navigate. However, you might have a better option: the flashes seem to be synchronizing!
      
     If you can calculate the exact moments when the octopuses will all flash simultaneously, you should be able to navigate through the cavern. What is the first step during which all octopuses flash? 
    """.trimIndent())

    val part2 by answering {
      var stop = false
      octopuses().steps().takeWhile { formation ->
        val prevStop = stop
        stop = formation.all { octopus ->
          octopus == 0.toByte()
        }
        !prevStop
      }.toList()
    }

    part2 {
      it.render()
      Answer(it.size)
    }
  }

  fun octopuses() = inputStream().reader().useLines { lines ->
    val octopuses = lines.flatMap { line -> line.map { (it - '0').toByte() } }.iterator()
    ByteArray(100) { octopuses.next() }
  }

  fun ByteArray.energize(index: Int) {
    this[index]++
    if (this[index] == 10.toByte()) {
      neighbors(index).forEach { neighbor -> energize(neighbor) }
    }
  }

  fun neighbors(index: Int) = sequence {
    val (x, y) = index
    if (x > 0) {
      if (y > 0) yield(index - 11)
      yield(index - 10)
      if (y < 9) yield(index - 9)
    }
    if (y > 0) yield(index - 1)
    if (y < 9) yield(index + 1)
    if (x < 9) {
      if (y > 0) yield(index + 9)
      yield(index + 10)
      if (y < 9) yield(index + 11)
    }
  }

  fun ByteArray.steps() = sequence {
    while (true) {
      indices.forEach {
        energize(it)
      }

      forEachIndexed { id, octopus ->
        if (octopus > 9) {
          this@steps[id] = 0
        }
      }

      yield(this@steps.clone())
    }
  }

  @Composable
  fun ByteArray.render(scale: Float = 10f, size: Dp = 12 * scale.dp) = Canvas(Modifier.width(size).height(size)) {
    scale(scale, Offset(0f, 0f)) {
      forEachIndexed { (x, y), octopus ->
        drawRect(
          color = Color.Black,
          alpha = maxOf(0f, 1f - octopus * 0.1f),
          topLeft = Offset(x.toFloat() + 1, y.toFloat() + 1),
          size = Size(1f, 1f)
        )
      }
    }
  }

  @Composable
  fun List<ByteArray>.render(scale: Float = 10f) = Box(
    modifier = Modifier.fillMaxWidth().height(500.dp),
  ) {
    val lazyListState = rememberLazyListState()
    LazyVerticalGrid(
      cells = GridCells.Fixed(10),
      state = lazyListState,
    ) {
      items(this@render) {
        it.render(scale)
      }
    }
    VerticalScrollbar(
      modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
      adapter = rememberScrollbarAdapter(lazyListState),
    )
  }

  operator fun Int.component1() = this / 10
  operator fun Int.component2() = this % 10
}