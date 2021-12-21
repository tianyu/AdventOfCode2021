import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.ClassLoader.getSystemResourceAsStream

interface Day {
  val name: String
  @Composable operator fun invoke()

  fun inputStream(): InputStream = "${this::class.qualifiedName}.txt".let {
    getSystemResourceAsStream(it) ?: throw FileNotFoundException("resource: $it")
  }

  @Composable
  fun answer(compute: suspend CoroutineScope.() -> Any) {
    var answer by remember { mutableStateOf(null as Any?) }
    if (answer == null) {
      p("Computing answer...")
      LaunchedEffect(answer) {
        answer = Dispatchers.IO(compute)
      }
    } else {
      SelectionContainer {
        Text("Answer: $answer\n", style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold)
      }
    }
  }
}