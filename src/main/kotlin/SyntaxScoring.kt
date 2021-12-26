import SyntaxScoring.Syntax.*
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable

object SyntaxScoring: Day {
  override val name = "Syntax Scoring"

  @Composable
  override fun ColumnScope.content() {
    p("""
      You ask the submarine to determine the best route out of the deep-sea cave, but it only replies:

      Syntax error in navigation subsystem on line: all of them

      All of them?! The damage is worse than you thought. You bring up a copy of the navigation subsystem (your puzzle input).

      The navigation subsystem syntax is made of several lines containing chunks. There are one or more chunks on each line, and chunks contain zero or more other chunks. Adjacent chunks are not separated by any delimiter; if one chunk stops, the next chunk (if any) can immediately start. Every chunk must open and close with one of four legal pairs of matching characters:

        -  If a chunk opens with (, it must close with ).
        -  If a chunk opens with [, it must close with ].
        -  If a chunk opens with {, it must close with }.
        -  If a chunk opens with <, it must close with >.
      
      Some lines are incomplete, but others are corrupted.
    """.trimIndent())

    h4("Part 1")
    p("""
      Find and discard the corrupted lines first. A corrupted line is one where a chunk closes with the wrong character - that is, where the characters it opens and closes with do not form one of the four legal pairs listed above.
      
      Stop at the first incorrect closing character on each corrupted line.

      Did you know that syntax checkers actually have contests to see who can get the high score for syntax errors in a file? It's true! To calculate the syntax error score for a line, take the first illegal character on the line and look it up in the following table:

        -  ): 3 points.
        -  ]: 57 points.
        -  }: 1197 points.
        -  >: 25137 points.
          
      Find the first illegal character in each corrupted line of the navigation subsystem. What is the total syntax error score for those errors?
    """.trimIndent())

    Answer {
      var syntaxError = 0
      forEachLine {
        syntaxError += when (val result = it.checkSyntax()) {
          is Ok, is Incomplete -> 0
          is Error -> result.points
        }
      }
      syntaxError
    }

    h4("Part 2")
    p("""
      Now, discard the corrupted lines. The remaining lines are incomplete.

      Incomplete lines don't have any incorrect characters - instead, they're missing some closing characters at the end of the line. To repair the navigation subsystem, you just need to figure out the sequence of closing characters that complete all open chunks in the line.

      You can only use closing characters (), ], }, or >), and you must add them in the correct order so that only legal pairs are formed and all chunks end up closed.
      
      Did you know that autocomplete tools also have contests? It's true! The score is determined by considering the completion string character-by-character. Start with a total score of 0. Then, for each character, multiply the total score by 5 and then increase the total score by the point value given for the character in the following table:

        -  ): 1 point.
        -  ]: 2 points.
        -  }: 3 points.
        -  >: 4 points.
      
      Autocomplete tools are an odd bunch: the winner is found by sorting all of the scores and then taking the middle score. (There will always be an odd number of scores to consider.)

      Find the completion string for each incomplete line, score the completion strings, and sort the scores. What is the middle score?
    """.trimIndent())

    Answer {
      val autoCompleteScores = buildList {
        forEachLine {
          when (val result = it.checkSyntax()) {
            is Incomplete -> add(result.expected.fold(0L) { sum, char ->
              5 * sum + when (char) {
                ')' -> 1
                ']' -> 2
                '}' -> 3
                '>' -> 4
                else -> 0
              }
            })
            else -> {}
          }
        }
      }
      autoCompleteScores.sorted()[autoCompleteScores.size / 2]
    }
  }

  private inline fun forEachLine(crossinline action: (line: String) -> Unit) = inputStream().reader().useLines {
    it.forEach(action)
  }

  sealed interface Syntax {
    data class Ok(val index: Int): Syntax
    data class Incomplete(val expected: String): Syntax
    data class Error(val index: Int, val points: Int): Syntax
  }

  fun String.checkSyntax(start: Int = 0, closeChar: Char = 'x'): Syntax {
    var result: Syntax = Ok(start)
    while (result is Ok && result.index < length) {
      result = when (this[result.index]) {
        closeChar -> return Ok(result.index + 1)
        '(' -> checkSyntax(result.index + 1, ')')
        '[' -> checkSyntax(result.index + 1, ']')
        '{' -> checkSyntax(result.index + 1, '}')
        '<' -> checkSyntax(result.index + 1, '>')
        ')' -> Error(result.index, 3)
        ']' -> Error(result.index, 57)
        '}' -> Error(result.index, 1197)
        '>' -> Error(result.index, 25137)
        else -> Error(result.index, 0)
      }
    }
    return when {
      result is Ok && closeChar == 'x' -> result
      result is Ok -> Incomplete(closeChar.toString())
      result is Incomplete && closeChar != 'x' -> Incomplete(result.expected + closeChar)
      else -> result
    }
  }
}