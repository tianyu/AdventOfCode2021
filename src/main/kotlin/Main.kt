import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

private val days = listOf(
  HydrothermalVenture,
  Lanternfish,
  WhaleTreachery,
  SevenSegmentSearch,
  SmokeBasin,
  SyntaxScoring,
  DumboOctopus,
  PassagePathing,
)

@Composable
fun App(default: Day? = days.first()) = Scaffold(
  topBar = {
    TopAppBar(
      title = { Text("Advent of Code 2021") },
      elevation = 10.dp,
    )
  }
) {
  var content: Day? by remember { mutableStateOf(default) }
  Row(Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .widthIn(100.dp, 400.dp)
    ) {
      itemsIndexed(days, { i, _ -> i }) { i, day ->
        ListItem(
          modifier = Modifier
            .clickable { content = day }
            .onlyIf({ day === content}) {
              background(color = MaterialTheme.colors.primarySurface.copy(alpha = 0.125f))
            },
          text = { Text(day.name) },
          overlineText =  { Text("Day ${i + 1}")}
        )
      }
    }
    Divider(
      modifier = Modifier
        .fillMaxHeight()
        .width(1.dp)
    )
    Crossfade(content) {
      it?.render()
    }
  }
}

fun main(vararg args: String) = application {
  Window(onCloseRequest = ::exitApplication) {
    Theme {
      App(args.getOrNull(0)?.toIntOrNull()?.let { days[it + 1] })
    }
  }
}
