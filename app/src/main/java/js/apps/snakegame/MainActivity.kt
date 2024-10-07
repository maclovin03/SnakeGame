package js.apps.snakegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import js.apps.snakegame.ui.theme.Green
import js.apps.snakegame.ui.theme.SnakeGameTheme

class MainActivity : ComponentActivity() {
    val gameState = SnakeGameState()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnakeGameTheme {
                SnakeGame(gameState)
            }
        }
    }
}

@Composable
private fun SnakeGame(gameState: SnakeGameState) {
    val pinaBitmap = ImageBitmap.imageResource(id = R.drawable.pina)
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Record: 0")
        Spacer(modifier = Modifier.height(20.dp))
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 2 / 3f)) {


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
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Score: ${gameState.snakeBody.size - 1}")
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Jugar")
        }
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