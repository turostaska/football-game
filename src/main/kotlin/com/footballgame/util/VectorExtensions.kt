package com.footballgame.util

import com.sun.javafx.geom.Vec2f

val ZERO_VEC = Vec2f(0F, 0F)

operator fun Vec2f.plus(other: Vec2f) = Vec2f(this.x + other.x, this.y + other.y)

operator fun Vec2f.times(amount: Float) = Vec2f(this.x * amount, this.y * amount)

fun Vec2f.normalize(): Vec2f {
    if (this == ZERO_VEC)
        return this
    return this / this.length()
}

operator fun Vec2f.div(amount: Float): Vec2f = Vec2f(this.x / amount, this.y / amount)

fun Vec2f.length(): Float = this.distance(ZERO_VEC)

fun Vec2f.dot(other: Vec2f) = this.x * other.x + this.y * other.y

operator fun Vec2f.minus(other: Vec2f) = this + (other * -1F)

fun Vec2f.lengthSq() = this.length() * this.length()