package com.footballgame.game

import com.footballgame.gameobject.Ball
import com.footballgame.gameobject.GameObject

class Player(
    val avatar: GameObject,
    val goalCondition: (Ball) -> Boolean
)