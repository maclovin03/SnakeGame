package js.apps.snakegame

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SnakeViewModel : ViewModel() {


    private val _gameState = MutableStateFlow(SnakeGameState())
    val gameState: StateFlow<SnakeGameState> = _gameState

    fun onEvent(event: SnakeEvent) {
        when (event) {
            is SnakeEvent.Move -> {
                updateDirection(event.direction, event.canvasWidth)
            }

            SnakeEvent.Pause -> {
                _gameState.update {
                    it.copy(gameState = GameState.PAUSED)
                }

            }

            SnakeEvent.Restart -> {
                _gameState.update {
                    SnakeGameState()
                }
            }

            SnakeEvent.Start -> {
                _gameState.update {
                    it.copy(gameState = GameState.STARTED)}
                    viewModelScope.launch {
                        while (gameState.value.gameState == GameState.STARTED){
                            delay(150)
                            _gameState.update {
                                updateGameState(gameState.value)
                            }
                        }
                    }
                }
            }
        }

    private fun updateDirection(direction: SnakeDirection, canvasWidth: Int){
        if (!gameState.value.isGameOver){
            if (gameState.value.direction != direction){
                if (direction == SnakeDirection.UP && gameState.value.direction == SnakeDirection.DOWN){
                    return
                }
                if (direction == SnakeDirection.DOWN && gameState.value.direction == SnakeDirection.UP){
                    return

                }
                if (direction == SnakeDirection.LEFT && gameState.value.direction == SnakeDirection.RIGHT){
                    return
                }
                if (direction == SnakeDirection.RIGHT && gameState.value.direction == SnakeDirection.LEFT){
                    return
                }
                _gameState.update {
                    it.copy(direction = direction)
                }
            }
        }
    }
    }



    private fun updateGameState(gameState: SnakeGameState): SnakeGameState {
        if (gameState.isGameOver) {
            return gameState
        }

        val head = gameState.snakeBody.first()

        val newHead = when (gameState.direction) {
            SnakeDirection.UP -> Coordinate(head.x, (head.y - 1))
            SnakeDirection.DOWN -> Coordinate(head.x, (head.y + 1))
            SnakeDirection.LEFT -> Coordinate((head.x - 1), head.y)
            SnakeDirection.RIGHT -> Coordinate((head.x + 1), head.y)

        }

        if(gameState.snakeBody.contains(newHead) || !isWithinBounds(newHead, gameState.xAxisGridSize, gameState.yAxisGridSize)){
            return gameState.copy(isGameOver = true)
            }

        var newSnakeBody = mutableListOf(newHead) + gameState.snakeBody
        val newFood = if (newHead == gameState.food) SnakeGameState.generateRandomFood() else gameState.food

        if (newHead != gameState.food){
            newSnakeBody = newSnakeBody.toMutableList()
            newSnakeBody.removeAt(newSnakeBody.size - 1)
        }
        return gameState.copy(
            snakeBody = newSnakeBody,
            food = newFood
        )
    }

    private fun isWithinBounds(
        coordinate: Coordinate,
        xAxisGridSize: Int,
        yAxisGridSize: Int) = coordinate.x in 1 until xAxisGridSize - 1 && coordinate.y in 1 until yAxisGridSize - 1

