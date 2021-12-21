import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

inline fun Modifier.onlyIf(predicate: () -> Boolean, action: Modifier.() -> Modifier): Modifier {
  return if (predicate()) action() else this
}

@Composable
fun h3(text: String) = Text(text, style = MaterialTheme.typography.h3)

@Composable
fun h4(text: String) = Text(text, style = MaterialTheme.typography.h4)

@Composable
fun p(text: String) = Text(text + '\n', style = MaterialTheme.typography.body1)
