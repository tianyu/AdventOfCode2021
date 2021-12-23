import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable

object SevenSegmentSearch: Day {
  override val name = "Seven Segment Search"

  @Composable
  override fun ColumnScope.content() {
    p("""
      You barely reach the safety of the cave when the whale smashes into the cave mouth, collapsing it. Sensors indicate another exit to this cave at a much greater depth, so you have no choice but to press on.

      As your submarine slowly makes its way through the cave system, you notice that the four-digit seven-segment displays in your submarine are malfunctioning; they must have been damaged during the escape. You'll be in a lot of trouble without them, so you'd better figure out what's wrong.

      Each digit of a seven-segment display is rendered by turning on or off any of seven segments named a through g.
      
      So, to render a 1, only segments c and f would be turned on; the rest would be off. To render a 7, only segments a, c, and f would be turned on.

      The problem is that the signals which control the segments have been mixed up on each display. The submarine is still trying to display numbers by producing output on signal wires a through g, but those wires are connected to segments randomly. Worse, the wire/segment connections are mixed up separately for each four-digit display! (All of the digits within a display use the same connections, though.)

      So, you might know that only signal wires b and g are turned on, but that doesn't mean segments b and g are turned on: the only digit that uses two segments is 1, so it must mean segments c and f are meant to be on. With just that information, you still can't tell which wire (b/g) goes to which segment (c/f). For that, you'll need to collect more information.

      For each display, you watch the changing signals for a while, make a note of all ten unique signal patterns you see, and then write down a single four digit output value (your puzzle input). Using the signal patterns, you should be able to work out which pattern corresponds to which digit.
      
      Each entry consists of ten unique signal patterns, a | delimiter, and finally the four digit output value. Within an entry, the same wire/segment connections are used (but you don't know what the connections actually are). The unique signal patterns correspond to the ten different ways the submarine tries to render a digit using the current wire/segment connections. Because 7 is the only digit that uses three segments, dab in the above example means that to render a 7, signal lines d, a, and b are on. Because 4 is the only digit that uses four segments, eafb means that to render a 4, signal lines e, a, f, and b are on.

      Using this information, you should be able to work out which combination of signal wires corresponds to each of the ten digits. Then, you can decode the four digit output value.
    """.trimIndent())

    h4("Part 1")
    p("""
      For now, focus on the easy digits.
      
      Because the digits 1, 4, 7, and 8 each use a unique number of segments, you should be able to tell which combinations of signals correspond to those digits.
      
      In the output values, how many times do digits 1, 4, 7, or 8 appear?
    """.trimIndent())

    Answer {
      var sum = 0
      forEachSignalPattern { signalPattern ->
        val d1478 = intArrayOf(2, 4, 3, 7)
        sum += signalPattern.slice(10 until signalPattern.size).count {
          it.countOneBits() in d1478
        }
      }
      sum
    }

    h4("Part 2")
    p("""
      Through a little deduction, you should now be able to determine the remaining digits.
      
      For each entry, determine all of the wire/segment connections and decode the four-digit output values. What do you get if you add up all of the output values?
    """.trimIndent())

    Answer {
      var sum = 0
      forEachSignalPattern { signalPattern ->
        // 1, 7, 4, (2/3/5), (0/6/9), 8
        val signals = signalPattern.take(10).sortedBy(Int::countOneBits)
        val s235 = signals.subList(3, 6).toMutableList()
        val s069 = signals.subList(6, 9).toMutableList()

        val d = IntArray(10)
        d[1] = signals[0]
        d[7] = signals[1]
        d[4] = signals[2]
        d[8] = signals[9]
        d[9] = s069.takeFirst { (it except (d[4] or d[7])).countOneBits() == 1 }
        d[0] = s069.takeFirst { it or d[7] == it }
        d[6] = s069[0]
        d[2] = s235.takeFirst { (it except d[9]) != 0 }
        d[3] = s235.takeFirst { it or d[1] == it }
        d[5] = s235[0]

        sum += signalPattern.sliceArray(10 until signalPattern.size).fold(0) { value, signal ->
          10 * value + d.indexOf(signal)
        }
      }
      sum
    }
  }

  private inline fun forEachSignalPattern(crossinline action: (IntArray) -> Unit) = inputStream().reader().forEachLine { line ->
    line.splitToSequence(' ')
      .filterNot { it == "|" }
      .map {
        it.fold(0) { signal, char -> signal or (1 shl (char - 'a')) }
      }
      .toList()
      .toIntArray()
      .let(action)
  }

  private fun <T> MutableList<T>.takeFirst(predicate: (T) -> Boolean): T {
    return removeAt(indexOfFirst(predicate))
  }

  private infix fun Int.except(bits: Int) = this and bits.inv()
}