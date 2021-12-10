package com.footballgame.gameobject

import com.footballgame.Game
import com.footballgame.Game.Companion.GOAL_RANGE
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

    override fun step(deltaTimeNano: Long, activeKeys: Set<KeyCode>, gameObjects: List<GameObject>) {
        super.step(deltaTimeNano, activeKeys, gameObjects)

        checkBoundaries()

        (shape as Circle).let {
            it.centerX = posX.toDouble()
            it.centerY = posY.toDouble()
        }
    }

    protected open fun checkBoundaries() {
        if (posX < Game.LEFT_BORDER + radius && velocity.x < 0F)
                momentum.x = abs(momentum.x)

        if (posX > Game.RIGHT_BORDER - radius && velocity.x > 0F)
                momentum.x = -abs(momentum.x)

        if (posY > Game.LOWER_BORDER - radius && velocity.y > 0F) momentum.y = -abs(momentum.y)

        if (posY < Game.UPPER_BORDER + radius && velocity.y < 0F) momentum.y = abs(momentum.y)
    }
}