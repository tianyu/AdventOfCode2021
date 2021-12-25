import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.ClassLoader.getSystemResourceAsStream

interface Day {
  val name: String

  @Composable
  fun ColumnScope.content()

  fun inputStream(): InputStream = "${this::class.qualifiedName}.txt".let {
    getSystemResourceAsStream(it) ?: throw FileNotFoundException("resource: $it")
  }
}

@Composable
fun Day.render() = Column(
  modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 14.dp)
    .verticalScroll(rememberScrollState())
) {
  h3(name)
  content()
}