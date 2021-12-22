import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable

object Lanternfish: Day {
  override val name = "Lanternfish"

  @Composable
  override fun ColumnScope.content() {
    p("""
      The sea floor is getting steeper. Maybe the sleigh keys got carried this way?

      A massive school of glowing lanternfish swims past. They must spawn quickly to reach such large numbers - maybe exponentially quickly? You should model their growth rate to be sure.

      Although you know nothing about this specific species of lanternfish, you make some guesses about their attributes. Surely, each lanternfish creates a new lanternfish once every 7 days.

      However, this process isn't necessarily synchronized between every lanternfish - one lanternfish might have 2 days left until it creates another lanternfish, while another might have 4. So, you can model each fish as a single number that represents the number of days until it creates a new lanternfish.

      Furthermore, you reason, a new lanternfish would surely need slightly longer before it's capable of producing more lanternfish: two more days for its first cycle.
    """.trimIndent())

    h4("Part 1")
    p("""
      Realizing what you're trying to do, the submarine automatically produces a list of the ages of several hundred nearby lanternfish (your puzzle input).
      
      Find a way to simulate lanternfish. How many lanternfish would there be after 80 days?
    """.trimIndent())

    answer {
      val tracker = IntArray(9)
      forEachFish {
        tracker[it] += 1
      }
      repeat(80) {
        val spawning = tracker[0]
        tracker.copyInto(tracker, 0, 1, 9)
        tracker[6] += spawning
        tracker[8] = spawning
      }
      tracker.sum()
    }

    h4("Part 2")
    p("""
      Suppose the lanternfish live forever and have unlimited food and space. Would they take over the entire ocean?

      How many lanternfish would there be after 256 days?
    """.trimIndent())

    answer {
      val tracker = LongArray(9)
      forEachFish {
        tracker[it] += 1L
      }
      repeat(256) {
        val spawning = tracker[0]
        tracker.copyInto(tracker, 0, 1, 9)
        tracker[6] += spawning
        tracker[8] = spawning
      }
      tracker.sum()
    }
  }

  private inline fun forEachFish(crossinline action: (fish: Int) -> Unit) {
    inputStream().reader().forEachLine { line ->
      line.splitToSequence(',').forEach {
        action(it.toInt())
      }
    }
  }
}