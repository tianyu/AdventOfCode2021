import SyntaxScoring.Syntax.Incomplete
import SyntaxScoring.Syntax.Ok
import SyntaxScoring.checkSyntax
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class SyntaxScoringTest {
  @Test
  fun empty() = assertThat("".checkSyntax()).isEqualTo(Ok(0))

  @Test
  fun singleBlock() = assertThat("()".checkSyntax()).isEqualTo(Ok(2))

  @Test
  fun nestedBlock() = assertThat("(())".checkSyntax()).isEqualTo(Ok(4))

  @Test
  fun sequencedBlock() = assertThat("()()".checkSyntax()).isEqualTo(Ok(4))

  @Test
  fun nestedSequencedBlock() = assertThat("([]<>)".checkSyntax()).isEqualTo(Ok(6))

  @Test
  fun sequencedNested() = assertThat("([])<()>".checkSyntax()).isEqualTo(Ok(8))

  @Test
  fun incompleteBlock() = assertThat("(".checkSyntax()).isEqualTo(Incomplete(")"))

  @Test
  fun incompleteBlockSequence() = assertThat("[]<>(".checkSyntax()).isEqualTo(Incomplete(")"))

  @Test
  fun incompleteNestedBlock() = assertThat("[](<>[[<>]".checkSyntax()).isEqualTo(Incomplete("])"))
}