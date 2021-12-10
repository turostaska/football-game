package com.footballgame.gameobject

import com.footballgame.Game
import com.sun.javafx.geom.Vec2f
import kotlin.math.abs

class MatchBall(
    invMass: Float,
    position: Vec2f,
    radius: Float
) : Ball(invMass, position, radius) {
    var wasTouched = false

    override fun checkBoundaries() {
        if (posX < Game.LEFT_BORDER + radius && velocity.x < 0F && posY.toInt() !in Game.GOAL_RANGE)
            momentum.x = abs(momentum.x)

        if (posX > Game.RIGHT_BORDER - radius && velocity.x > 0F && posY.toInt() !in Game.GOAL_RANGE)
            momentum.x = -abs(momentum.x)

        if (posY > Game.LOWER_BORDER - radius && velocity.y > 0F) momentum.y = -abs(momentum.y)

        if (posY < Game.UPPER_BORDER + radius && velocity.y < 0F) momentum.y = abs(momentum.y)
    }

    override fun reset() {
        super.reset()
        wasTouched = false
    }
}