import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.CoroutineScope

sealed interface Answer<out T> {
  object Loading: Answer<Nothing>
  data class Of<T>(val value: T): Answer<T>
}

@Composable
fun Answer(value: Any?) = SelectionContainer {
  Text("Answer: $value\n", style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold)
}

@Composable
inline fun Answer(crossinline produceAnswer: CoroutineScope.() -> Any?) {
  val answer by answering { produceAnswer() }
  answer.invoke()
}

@Composable
inline fun <T> answering(@BuilderInference crossinline produce: CoroutineScope.() -> T) =
  produceState<Answer<T>>(Answer.Loading) { value = Answer.Of(produce()) }

@Composable
inline operator fun <T> Answer<T>.invoke(render: @Composable (T) -> Unit = { Answer(it) }): Unit = when (this) {
  Answer.Loading -> p("Computing answer...")
  is Answer.Of -> render(value)
}

@Composable
inline fun <T> Answer(vararg dependencies: Answer<T>, crossinline produceAnswer: CoroutineScope.(List<T>) -> Any?) {
  val answer by produceState<Answer<Any?>>(Answer.Loading, *dependencies) {
    value = Answer.Of(produceAnswer(
      dependencies.map {
        when (it) {
          Answer.Loading -> {
            value = Answer.Loading
            return@produceState
          }
          is Answer.Of -> it.value
        }
      }
    ))
  }

  answer.invoke()
}

@Composable
fun <T, U> Answer<T>.transform(produce: CoroutineScope.(T) -> U) =
  produceState<Answer<U>>(Answer.Loading, this) {
    value = when (this@transform) {
      is Answer.Loading -> Answer.Loading
      is Answer.Of<T> -> Answer.Of(produce(this@transform.value))
    }
  }
