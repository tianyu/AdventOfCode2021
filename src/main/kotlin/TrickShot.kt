import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlin.math.sign

object TrickShot : Day {
  override val name = "Trick Shot"

  @Composable
  override fun ColumnScope.content() {
    p("""
      You finally decode the Elves' message. HI, the message says. You continue searching for the sleigh keys.

      Ahead of you is what appears to be a large ocean trench. Could the keys have fallen into it? You'd better send a probe to investigate.

      The probe launcher on your submarine can fire the probe with any integer velocity in the x (forward) and y (upward, or downward if negative) directions. For example, an initial x,y velocity like 0,10 would fire the probe straight up, while an initial velocity like 10,-1 would fire the probe forward at a slight downward angle.

      The probe's x,y position starts at 0,0. Then, it will follow some trajectory by moving in steps. On each step, these changes occur in the following order:

         - The probe's x position increases by its x velocity.
         - The probe's y position increases by its y velocity.
         - Due to drag, the probe's x velocity changes by 1 toward the value 0; that is, it decreases by 1 if it is greater than 0, increases by 1 if it is less than 0, or does not change if it is already 0.
         - Due to gravity, the probe's y velocity decreases by 1.

      For the probe to successfully make it into the trench, the probe must be on some trajectory that causes it to be within a target area after any step. The submarine computer has already calculated this target area (your puzzle input). For example:

      target area: x=20..30, y=-10..-5

      This target area means that you need to find initial x,y velocity values such that after any step, the probe's x position is at least 20 and at most 30, and the probe's y position is at least -10 and at most -5.
    """.trimIndent())

    h4("Target Area")
    val input by answering {
      val text = inputStream().reader().readText().trim()
      val (x, y) = text.removePrefix("target area: ").split(", ")
      val (xmin, xmax) = x.removePrefix("x=").split("..")
      val (ymin, ymax) = y.removePrefix("y=").split("..")
      (xmin.toInt()..xmax.toInt()) to (ymin.toInt()..ymax.toInt())
    }

    h4("Part 1")
    p("""
      If you're going to fire a highly scientific probe out of a super cool probe launcher, you might as well do it with style. How high can you make the probe go while still reaching the target area?
      
      Find the initial velocity that causes the probe to reach the highest y position and still eventually be within the target area after any step. What is the highest y position it reaches on this trajectory?
    """.trimIndent())

    input { (_, yrange) ->
      p("""
        We solve this analytically.
        
        With respect to time:
        - The y-acceleration is constant, therefore
        - The y-velocity is linear, therefore
        - The y-position is parabolic, and is symmetric around the time when y-velocity = 0.
        
        Since the y-position is symmetric and starts at 0 with a velocity of dy, there must exist at some time where the y-position becomes 0 with a velocity of -dy. At the next step, we get to depth -dy-1.
        
        We want to maximize the value of dy while also ensuring that -dy-1 is in the target range of y-positions, so we set:
        
            -dy - 1 = min(yrange)
                -dy = min(yrange) + 1
                 dy = -min(yrange) - 1
        
        Starting at a velocity of dy, we achieve a maximum height of:
        
            dy(dy + 1)/2
      """.trimIndent())

      val dy = -yrange.first - 1
      val maxHeight = dy * (dy + 1) / 2

      Answer("max height = $maxHeight (dy = $dy)")
    }

    h4("Part 2")
    p("""
      Maybe a fancy trick shot isn't the best idea; after all, you only have one probe, so you had better not miss.

      To get the best idea of what your options are for launching the probe, you need to find every initial velocity that causes the probe to eventually be within the target area after any step.
      
      How many distinct initial velocity values cause the probe to be within the target area after any step?
    """.trimIndent())

    val part2 by input.transform { (xrange, yrange) ->
      xrange to yrange to buildList {
        (1..xrange.last).forEach { dx ->
          (yrange.first..-yrange.first).forEach { dy ->
            val inRange = launch(dx, xrange.last, dy, yrange.first)
              .any { (x, y) -> x in xrange && y in yrange }
            if (inRange) add(dx to dy)
          }
        }
      }
    }

    part2 { (_, solutions) ->
      Answer(solutions.size)
    }
  }

  private fun launch(dx: Int, xLimit: Int, dy: Int, yLimit: Int) =
    xs(dx, xLimit) zip ys(dy, yLimit)

  private fun xs(velocity: Int, limit: Int) = sequence {
    var dx = velocity
    var x = 0
    while (x <= limit) {
      yield(x)
      x += dx
      dx -= dx.sign
    }
  }

  private fun ys(velocity: Int, limit: Int) = sequence {
    var dy = velocity
    var y = 0
    while (y >= limit) {
      yield(y)
      y += dy
      dy -= 1
    }
  }
}
