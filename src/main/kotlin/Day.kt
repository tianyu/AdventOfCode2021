import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun Day.render() = Box(modifier = Modifier.fillMaxSize()) {
  val verticalScrollState = rememberScrollState()
  val horizontalScrollState = rememberScrollState()
  Box(
    modifier = Modifier.fillMaxSize()
      .verticalScroll(verticalScrollState)
      .horizontalScroll(horizontalScrollState)
  ) {
    Column(modifier = Modifier
      .align(Alignment.TopCenter)
      .widthIn(max = 1024.dp)) {
      h3(name)
      content()
    }
  }

  VerticalScrollbar(
    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
    adapter = rememberScrollbarAdapter(verticalScrollState),
  )

  HorizontalScrollbar(
    modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(),
    adapter = rememberScrollbarAdapter(horizontalScrollState),
  )
}