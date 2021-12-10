package com.footballgame.control

import com.footballgame.div
import com.footballgame.gameobject.GameObject
import com.footballgame.normalize
import com.footballgame.plus
import com.footballgame.times
import com.sun.javafx.geom.Vec2f
import javafx.scene.input.KeyCode

val player1Controls = mapOf(
    Action.UP to KeyCode.UP,
    Action.DOWN to KeyCode.DOWN,
    Action.RIGHT to KeyCode.RIGHT,
    Action.LEFT to KeyCode.LEFT)

val player2Controls = mapOf(
    Action.UP to KeyCode.W,
    Action.DOWN to KeyCode.S,
    Action.RIGHT to KeyCode.D,
    Action.LEFT to KeyCode.A)

open class ControlProfile(
    private val actionKeyMapping: Map<Action, KeyCode>,
) {
    open fun applyControls(gameObject: GameObject, activeKeys: Set<KeyCode>, deltaTimeSec: Float) {
        var deltaDistance = Vec2f(0F, 0F)

        for ((action, keyCode) in actionKeyMapping)
            if (keyCode in activeKeys)
                deltaDistance += when (action) {
                    Action.UP -> Vec2f(0F, -1F)
                    Action.DOWN -> Vec2f(0F, 1F)
                    Action.RIGHT -> Vec2f(1F, 0F)
                    Action.LEFT -> Vec2f(-1F, 0F)
                }

        val normalizedDeltaDistance = deltaDistance.normalize()
        gameObject.apply {
            actingForce += normalizedDeltaDistance * mass / (deltaTimeSec * deltaTimeSec) * FORCE_MULTIPLIER
        }
    }

    companion object {
        const val FORCE_MULTIPLIER = 600F
    }
}

class PassiveControlProfile(
    actionKeyMapping: Map<Action, KeyCode> = emptyMap()
) : ControlProfile(actionKeyMapping) {
    override fun applyControls(gameObject: GameObject, activeKeys: Set<KeyCode>, deltaTimeSec: Float) {}
}