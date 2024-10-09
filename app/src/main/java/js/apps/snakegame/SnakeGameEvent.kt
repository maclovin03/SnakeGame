package js.apps.snakegame

import androidx.compose.ui.geometry.Offset

sealed class SnakeEvent {
    data object Pause : SnakeEvent()
    data object Start : SnakeEvent()
    data object Restart : SnakeEvent()
    data class Move(val direction: SnakeDirection,  val canvasWidth: Int) : SnakeEvent()

}