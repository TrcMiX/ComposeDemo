@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

@Composable
@Preview
fun App() {

    val textButton = remember { mutableStateOf("Hello, World!") }
    val count = remember { mutableStateOf(0) }
    var color = remember { mutableStateOf(Color.Magenta) }
    MaterialTheme {
        Column(Modifier.fillMaxSize()
            .onPointerEvent(PointerEventType.Move) {
                val position = it.changes.first().position
                color.value = Color(position.x.toInt() % 256, position.y.toInt() % 256, 0)
            }) {
            lineOne()
            lineTwo(textButton, count)
            lineThree(count)
            lineFour(color)
        }
    }
}

@Composable
fun lineOne() {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF0CCFD6)),
        horizontalArrangement = Arrangement.spacedBy
            (10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource("aaa.jpg"),
            "aaa",
            modifier = Modifier.size(100.dp)
        )

        AsyncImage(
            load = { loadImageBitmapCus("https://www.baidu.com/img/PCfb_5bf082d29588c07f842ccde3f97243ea.png") },
            painterFor = { remember { BitmapPainter(it) } },
            contentDescription = "Idea logo",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.width(100.dp).background(Color(0xFF123DDD))
        )
    }
}

@Composable
fun lineTwo(textButton: MutableState<String>, count: MutableState<Int>) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF018786)),
        horizontalArrangement = Arrangement.spacedBy
            (10.dp, Alignment.CenterHorizontally),
    ) {
        Button(onClick = {
            textButton.value = "Hello, Desktop!"
            count.value++
        }) {
            Text("${textButton.value}${count.value}")
        }

        Text(
            "OH!${count.value}",
            modifier = Modifier.align(Alignment.CenterVertically),
            color = Color(0xFFffffff)
        )
    }

}

@Composable
fun lineThree(count: MutableState<Int>) {
    Row(
        Modifier.fillMaxWidth().background(Color(0xFF01CC86)),
        horizontalArrangement = Arrangement.spacedBy
            (10.dp, Alignment.CenterHorizontally),
    ) {
        Button(
            onClick = {
                count.value++
            }) {
            Text(if (count.value == 0) "Hello World" else "Clicked ${count.value}!")
        }
        Button(
            onClick = {
                count.value = 0
            }) {
            Text("Reset")
        }
    }
}

@Composable
fun lineFour(color: MutableState<Color>) {
    var count by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("Click magenta box!") }
    var active by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().background(if (active) Color(0xFF01FC86) else Color(0xFFA2E38D)).padding(
            0.dp,
            5.dp
        )
            .onPointerEvent(PointerEventType.Enter) { active = true }
            .onPointerEvent(PointerEventType.Exit) { active = false },
        horizontalArrangement = Arrangement.spacedBy
            (10.dp, Alignment.CenterHorizontally),
    ) {
        Box(
            modifier = Modifier
                .background(color.value, shape = RoundedCornerShape(10.dp, 0.dp, 10.dp, 0.dp))
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.2f)
                .combinedClickable(
                    onClick = {
                        text = "Click! ${count++}"
                    },
                    onDoubleClick = {
                        text = "Double click! ${count++}"
                    },
                    onLongClick = {
                        text = "Long click! ${count++}"
                    }
                ),

            ) {
            Text(
                text = text,
                fontSize = if (active) 22.sp else 16.sp,
                fontStyle = if (active) FontStyle.Italic else FontStyle.Normal,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

}

@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: T? by produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: Exception) {
                // instead of printing to console, you can also write this to log,
                // or show some error placeholder
                e.printStackTrace()
                null
            }
        }
    }

    if (image != null) {
        Image(
            painter = painterFor(image!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

fun loadImageBitmapCus(file: File): ImageBitmap =
    file.inputStream().buffered().use(::loadImageBitmap)

fun loadImageBitmapCus(url: String): ImageBitmap =
    URL(url).openStream().buffered().use(::loadImageBitmap)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        icon = painterResource("aaa.jpg"),
        title = "Compose Demo"
    )
    {
        App()
    }
}
