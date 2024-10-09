package js.apps.snakegame

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import js.apps.snakegame.ui.theme.Green
import js.apps.snakegame.ui.theme.SnakeGameTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnakeGameTheme {
                val gameViewModel by viewModels<SnakeViewModel>()
                val gameState by gameViewModel.gameState.collectAsStateWithLifecycle()
                SnakeGame(
                    gameState = gameState,
                    onEvent = gameViewModel::onEvent
                )
            }
        }
    }
}

@Composable
private fun SnakeGame(gameState: SnakeGameState, onEvent: (SnakeEvent) -> Unit) {

    val recordSP = RecordSP(context = LocalContext.current)

    var isInstructions by remember {
        mutableStateOf(false)
    }
    val pinaBitmap = ImageBitmap.imageResource(id = R.drawable.pina)
    Column(modifier = Modifier
        .fillMaxSize()
        .blur(if (gameState.isGameOver) 20.dp else 0.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        Card(onClick = { /*TODO*/ }, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {
            Text(text = "Record: ${recordSP.getRecord()}", fontSize = 20.sp, modifier = Modifier.padding(10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .pointerInput(gameState.gameState) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    when {
                        x > 50 -> {
                            onEvent(SnakeEvent.Move(SnakeDirection.RIGHT, size.width))
                        }

                        x < -50 -> {
                            onEvent(SnakeEvent.Move(SnakeDirection.LEFT, size.width))
                        }

                        y > 50 -> {
                            onEvent(SnakeEvent.Move(SnakeDirection.DOWN, size.width))
                        }

                        y < -50 -> {
                            onEvent(SnakeEvent.Move(SnakeDirection.UP, size.width))
                        }
                    }

                }
            }) {


            val cellSize = size.width / 16

            drawGameBoard(
                cellSize = cellSize,
                cellColor = Green,
                borderCellColor = Color.Blue,
                gridWidth = gameState.xAxisGridSize,
                gridHeight = gameState.yAxisGridSize
            )
            drawFood(
                foodImage = pinaBitmap,
                foodCoordinate = gameState.food,
                cellSize = cellSize.toInt()
            )
            drawSnake(
                snake = gameState.snakeBody,
                cellSize = cellSize
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(text = "Score: ${gameState.snakeBody.size - 1}", fontSize = 20.sp)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { isInstructions = true }) {
                Text(text = "Instrucciones", color = Color.Blue)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            onEvent(SnakeEvent.Start)
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 60.dp)) {
            Text(text = "Jugar")
            Image(painter = painterResource(id = R.drawable.jugar), contentDescription = "")
        }
    }
    if (gameState.isGameOver) {

        GameOverView(
            onRestart = { onEvent(SnakeEvent.Restart) },
            score = gameState.snakeBody.size - 1,
            record = recordSP.getRecord())
}
    if (isInstructions) {
        InstruccionsDialog(onDismiss = { isInstructions = false })
    }
}



private fun DrawScope.drawGameBoard(
    cellSize: Float,
    cellColor: Color,
    borderCellColor: Color,
    gridWidth: Int,
    gridHeight: Int
) {

    for (i in 0 until gridWidth) {
        for (j in 0 until gridHeight) {
            val isBorderCell = i == 0 || j == 0 || i == gridWidth - 1 || j == gridHeight - 1
            drawRect(
                color = if (isBorderCell) borderCellColor
                else if ((i + j) % 2 == 0) cellColor else cellColor.copy(alpha = 0.5f),
                topLeft = Offset(
                    x = i * cellSize,
                    y = j * cellSize
                ),
                size = Size(cellSize, cellSize)
            )

        }
    }


}

private fun DrawScope.drawFood(
    foodImage:ImageBitmap,
    foodCoordinate: Coordinate,
    cellSize: Int
){

    this.drawImage(
        image = foodImage,
        dstSize = IntSize(cellSize, cellSize),
        dstOffset = IntOffset(
            x = (foodCoordinate.x * cellSize),
            y = (foodCoordinate.y * cellSize))
    )

}

private fun DrawScope.drawSnake(
    snake: List<Coordinate>,
    cellSize: Float
){
    val cellSizeInt = cellSize.toInt()
    snake.forEachIndexed { index, coordinate ->
        drawRect(
            color = Color.Cyan,
            topLeft = Offset(
                x = coordinate.x * cellSize,
                y = coordinate.y * cellSize
            ),
            size = Size(cellSize, cellSize)
        )
    }


}

@Composable
fun GameOverView(onRestart: () -> Unit, score: Int, record: Int) {
    val localContext = LocalContext.current
    if (score > record){
        val recordSP = RecordSP(context = localContext)
        recordSP.setRecord(score)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Transparent)) {
        Spacer(modifier = Modifier.height(200.dp))
        Card (modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp)){
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Spacer(modifier = Modifier.height(36.dp))
                Text(text = if (score > record) "¡Nuevo récord!" else "Game Over", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Image(painter = painterResource(id = R.drawable.pina128), contentDescription = "",)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Score: $score", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(26.dp))
                Image(painter = painterResource(id = R.drawable.trofeo_premio), contentDescription = "",)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Record: $record", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(26.dp))

            }
        }
        Button(onClick = { onRestart() }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)) {
            Text(text = "Jugar")
            Image(painter = painterResource(id = R.drawable.jugar), contentDescription = "", modifier = Modifier.size(40.dp))

        }

    }
}

@Composable
fun InstruccionsDialog(
    onDismiss: () -> Unit
) {

    Card(modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 50.dp, horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)

    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = CenterHorizontally) {


            Text(text = "Instrucciones", fontSize = 20.sp, modifier = Modifier.padding(10.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Objetivo\n" +
                        "El objetivo del juego de la serpiente (Snake) es controlar una serpiente que se mueve por la pantalla, comiendo comida para crecer. El jugador debe evitar que la serpiente choque contra las paredes o contra su propio cuerpo. El juego termina cuando la serpiente choca con un obstáculo, y el objetivo es conseguir la puntuación más alta posible comiendo la mayor cantidad de comida.\n" +
                        "Jugabilidad\n" +
                        "Movimiento: La serpiente se mueve continuamente en una dirección. El jugador puede cambiar la dirección deslizando el dedo por la pantalla.\n" +
                        "Comida: La comida aparece aleatoriamente en la pantalla. Cuando la cabeza de la serpiente toca la comida, la serpiente la come y crece un segmento más.\n" +
                        "Crecimiento: Cada vez que la serpiente come, crece un segmento más de largo.\n" +
                        "Colisión: Si la cabeza de la serpiente choca contra las paredes o contra cualquier parte de su propio cuerpo, el juego termina.\n" +
                        "Puntuación: La puntuación del jugador se basa en la cantidad de comida que la serpiente ha comido.\n",
                textAlign = TextAlign.Justify
            )

            Button(onClick = { onDismiss() }) {
                Text(text = "Aceptar")
            }

        }
    }
}