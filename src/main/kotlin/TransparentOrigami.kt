import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp

object TransparentOrigami : Day {
  override val name = "Transparent Origami"

  @Composable
  override fun ColumnScope.content() {
    p("""
      You reach another volcanically active part of the cave. It would be nice if you could do some kind of thermal imaging so you could tell ahead of time which caves are too hot to safely enter.

      Fortunately, the submarine seems to be equipped with a thermal camera! When you activate it, you are greeted with:

      Congratulations on your purchase! To activate this infrared thermal imaging
      camera system, please enter the code found on page 1 of the manual.

      Apparently, the Elves have never used this feature. To your surprise, you manage to find the manual; as you go to open it, page 1 falls out. It's a large sheet of transparent paper! The transparent paper is marked with random dots and includes instructions on how to fold it up (your puzzle input).
      
      The first section is a list of dots on the transparent paper. 0,0 represents the top-left coordinate. The first value, x, increases to the right. The second value, y, increases downward. So, the coordinate 3,0 is to the right of 0,0, and the coordinate 0,7 is below 0,0.
      
      Then, there is a list of fold instructions. Each instruction indicates a line on the transparent paper and wants you to fold the paper up (for horizontal y=... lines) or left (for vertical x=... lines).
    """.trimIndent())

    h4("Input")
    val input by answering {
      useDotsAndFolds { dots, _ ->
        dots
      }
    }

    input {
      it.render()
    }

    h4("Part 1")
    p("""
      The transparent paper is pretty big, so for now, focus on just completing the first fold. After the first fold in the example above, 17 dots are visible - dots that end up overlapping after the fold is completed count as a single dot.

      How many dots are visible after completing just the first fold instruction on your transparent paper?
    """.trimIndent())

    val part1 by answering {
      useDotsAndFolds { dots, folds ->
        folds.take(1).fold(dots, ::foldDots)
      }
    }

    part1 {
      it.render()
      Answer(it.count())
    }

    h4("Part 2")
    p("""
      Finish folding the transparent paper according to the instructions. The manual says the code is always eight capital letters.

      What code do you use to activate the infrared thermal imaging camera system?
    """.trimIndent())

    val part2 by answering {
      useDotsAndFolds { dots, folds ->
        folds.fold(dots, ::foldDots)
      }
    }

    part2 {
      it.render()
    }
  }

  inline fun <T> useDotsAndFolds(action: (dots: MutableList<Dot>, folds: Sequence<Fold>) -> T): T = inputStream().reader().useLines {
    val lines = it.iterator()
    val dots = mutableListOf<Dot>()
    var line = lines.next()
    while (line.isNotEmpty()) {
      val (x, y) = line.split(',')
      dots += Dot(x.toInt(), y.toInt())
      line = lines.next()
    }

    val folds = lines.asSequence().mapNotNull {
      val eqIndex = it.indexOf('=')
      val foldDirection = it[eqIndex - 1]
      val foldPosition = it.substring(eqIndex + 1).toInt()
      when (foldDirection) {
        'x' -> Fold.Left(foldPosition)
        'y' -> Fold.Up(foldPosition)
        else -> null
      }
    }
    action(dots, folds)
  }

  data class Dot(var x: Int, var y: Int)

  sealed interface Fold {
    val position: Int
    data class Up(override val position: Int): Fold
    data class Left(override val position: Int): Fold
  }

  fun foldDots(dots: List<Dot>, fold: Fold): List<Dot> {
    val prop = when (fold) {
      is Fold.Left -> Dot::x
      is Fold.Up -> Dot::y
    }
    val crease = fold.position
    dots.forEachIndexed { index, dot ->
      val position = prop.get(dot)
      if (position > crease) {
        prop.set(dot, 2 * crease - position)
      }
    }
    return dots.distinct()
  }

  @Composable
  fun List<Dot>.render() = Box(Modifier.width(1024.dp).height(512.dp)) {
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Box(
      Modifier
        .fillMaxSize()
        .horizontalScroll(horizontalScroll)
        .verticalScroll(verticalScroll)
    ) {

      val width = maxOf { it.x } + 10
      val height = maxOf { it.y } + 10
      val scale = if (width < 1024) 1024f / width else 1f
      Canvas(
        modifier = Modifier
          .width(width.dp)
          .height(height.dp)
      ) {
        scale(scale, pivot = Offset.Zero) {
          forEach { (x, y) ->
            drawCircle(
              color = Color.Black,
              center = Offset(x + 5f, y + 5f),
              radius = 5f / scale,
              alpha = 0.5f
            )
          }
        }
      }
    }

    VerticalScrollbar(
      rememberScrollbarAdapter(verticalScroll),
      Modifier.align(Alignment.CenterEnd).fillMaxHeight()
    )

    HorizontalScrollbar(
      rememberScrollbarAdapter(horizontalScroll),
      Modifier.align(Alignment.BottomStart).fillMaxWidth()
    )
  }
}
