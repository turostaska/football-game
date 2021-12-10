package com.footballgame

import com.footballgame.game.Game
import com.footballgame.gameobject.GameObject
import com.sun.javafx.geom.Vec2f
import org.apache.commons.lang3.time.StopWatch
import kotlin.math.*

val ZERO_VEC = Vec2f(0F, 0F)

fun getResource(filename: String): String {
    return Game::class.java.getResource(filename).toString()
}

operator fun Vec2f.plus(other: Vec2f) = Vec2f(this.x + other.x, this.y + other.y)

operator fun Vec2f.times(amount: Float) = Vec2f(this.x * amount, this.y * amount)

fun Vec2f.normalize(): Vec2f {
    if (this == Vec2f(0F, 0F))
        return this
    return this / this.length()
}

operator fun Vec2f.div(amount: Float): Vec2f = Vec2f(this.x / amount, this.y / amount)

fun Vec2f.length(): Float = this.distance(Vec2f(0F, 0F))

fun Vec2f.dot(other: Vec2f) = this.x * other.x + this.y * other.y

fun Vec2f.angle(other: Vec2f): Float = acos(this.dot(other) / this.length() / other.length())

operator fun Vec2f.minus(other: Vec2f) = this + (other * -1F)

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

fun Vec2f.lengthSq() = this.length() * this.length()

fun StopWatch.timeLeftSecs(totalSecs: Int) = max(0, totalSecs - this.time / 1000)