package js.apps.snakegame

import android.graphics.Path
import kotlin.random.Random

data class SnakeGameState(
    val xAxisGridSize: Int = 16,
    val yAxisGridSize: Int = 20,
    val direction: SnakeDirection = SnakeDirection.RIGHT,
    val snakeBody: List<Coordinate> = listOf(Coordinate(5, 5)),
    val food: Coordinate = generateRandomFood(),
    val isGameOver: Boolean = false,
    val gameState: GameState = GameState.IDLE
){
    companion object{
        fun generateRandomFood(): Coordinate {
            return Coordinate(
                x  = Random.nextInt(1, 15),
                y = Random.nextInt(1, 19)
            )
        }
    }
}
enum class GameState {
    IDLE, STARTED, PAUSED
}
enum class SnakeDirection {
    UP, DOWN, LEFT, RIGHT
}

data class Coordinate(val x: Int, val y: Int)
