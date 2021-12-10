package com.footballgame.gameobject

import com.footballgame.*
import com.footballgame.control.ControlProfile
import com.footballgame.game.Player
import com.sun.javafx.geom.Vec2f
import javafx.scene.input.KeyCode
import javafx.scene.shape.Shape

abstract class GameObject(
    private val invMass: Float,
    var position: Vec2f,
    private val controlProfile: ControlProfile,
) {
    var momentum = Vec2f(0F, 0F)
    var actingForce = Vec2f(0F, 0F)

    val mass get() = 1 / invMass
    val velocity get() = momentum * invMass
    private val acceleration get() = actingForce * invMass
    val posX get() = position.x
    val posY get() = position.y

    val startingPosition = position

    val alreadyCollidedWith = mutableListOf<GameObject>()

    abstract var shape: Shape

    abstract fun collidesWith(other: GameObject) : Boolean

    private fun collideWith(other: GameObject) {
        val u1 = velocityAfterCollision(this, other)
        val u2 = velocityAfterCollision(other, this)

        this.momentum = u1 * mass
        other.momentum = u2 * other.mass

        this.alreadyCollidedWith += other
        other.alreadyCollidedWith += this
    }

    open fun step(
        deltaTimeNano: Long,
        activeKeys: Set<KeyCode>,
        gameObjects: List<GameObject>,
        playersWhoCanTouchTheBall: Set<GameObject>
    ) {
        val deltaTimeSec = deltaTimeNano / 1_000_000_000F

        gameObjects.forEach { entity ->
            if (entity != this && entity !in alreadyCollidedWith)
                if (this.collidesWith(entity)) {
                    this.collideWith(entity)
                    (entity as? MatchBall)?.let { it.wasTouched = true }
                    (this as? MatchBall)?.let { it.wasTouched = true }
                }

        }

        position += velocity * deltaTimeSec
        momentum += acceleration * deltaTimeSec

        controlProfile.applyControls(this, activeKeys, deltaTimeSec)

        momentum *= (1F / (1F + deltaTimeSec * FRICTION * mass))
        actingForce *= FORCE_DECAY * deltaTimeSec
    }

    companion object {
        const val FRICTION = 0.05F
        const val FORCE_DECAY = 0.999F
    }

    open fun reset() {
        this.momentum = ZERO_VEC
        this.actingForce = ZERO_VEC
        this.position = startingPosition
    }

}
