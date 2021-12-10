package com.footballgame.gameobject

import com.footballgame.game.Game
import com.footballgame.control.ControlProfile
import com.footballgame.control.PassiveControlProfile
import com.sun.javafx.geom.Vec2f
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Shape
import kotlin.math.abs

open class Ball(
    invMass: Float,
    position: Vec2f,
    val radius: Float,
    controlProfile: ControlProfile = PassiveControlProfile(),
    colour: Color = Color.WHITE,
): GameObject(invMass, position, controlProfile) {
    override var shape: Shape = Circle(
        position.x.toDouble(),
        position.y.toDouble(),
        radius.toDouble(),
        colour,
    )

    override fun collidesWith(other: GameObject): Boolean {
        return when (other) {
            is Ball -> (position.distance(other.position) <= (radius + other.radius))
            else -> throw Exception("Unknown game object: $other")
        }
    }

    override fun step(
        deltaTimeNano: Long,
        activeKeys: Set<KeyCode>,
        gameObjects: List<GameObject>,
        playersWhoCanTouchTheBall: Set<GameObject>
    ) {
        super.step(deltaTimeNano, activeKeys, gameObjects, playersWhoCanTouchTheBall)

        checkBoundaries(playersWhoCanTouchTheBall)

        (shape as Circle).let {
            it.centerX = posX.toDouble()
            it.centerY = posY.toDouble()
        }
    }

    protected open fun checkBoundaries(playersWhoCanTouchTheBall: Set<GameObject>) {
        if (posX < Game.LEFT_BORDER + radius && velocity.x < 0F)
                momentum.x = abs(momentum.x)
        else if (posX > Game.RIGHT_BORDER - radius && velocity.x > 0F)
                momentum.x = -abs(momentum.x)

        if (posY > Game.LOWER_BORDER - radius && velocity.y > 0F) momentum.y = -abs(momentum.y)
        else if (posY < Game.UPPER_BORDER + radius && velocity.y < 0F) momentum.y = abs(momentum.y)

        if (this !in playersWhoCanTouchTheBall) {
            if (posX > Game.RIGHT_BORDER/2.0 && posX < Game.SECOND_THIRD + radius && velocity.x < 0F)
                momentum.x = abs(momentum.x)
            else if (posX < Game.RIGHT_BORDER/2.0 && posX > Game.FIRST_THIRD - radius && velocity.x > 0F)
                momentum.x = -abs(momentum.x)
        }
    }
}