package com.footballgame.util

import com.footballgame.game.Game
import com.footballgame.gameobject.GameObject
import com.sun.javafx.geom.Vec2f
import org.apache.commons.lang3.time.StopWatch
import kotlin.math.max

fun getResource(filename: String): String {
    return Game::class.java.getResource(filename).toString()
}

fun velocityAfterCollision(what: GameObject, collidingWith: GameObject): Vec2f {
    val m1 = what.mass
    val v1 = what.velocity
    val m2 = collidingWith.mass
    val v2 = collidingWith.velocity
    val x1 = what.position
    val x2 = collidingWith.position
    val mSum = m1 + m2

    return v1 - (x1 - x2) * (v1 - v2).dot(x1 - x2) / ((x1 - x2).lengthSq()) * (2 * m2 / mSum)
}

fun StopWatch.timeLeftSecs(totalSecs: Int) = max(0, totalSecs - this.time / 1000)