package com.footballgame

import com.footballgame.control.ControlProfile
import com.footballgame.control.player1Controls
import com.footballgame.control.player2Controls
import com.footballgame.game.Player
import com.footballgame.gameobject.Ball
import com.footballgame.gameobject.GameObject
import com.footballgame.gameobject.MatchBall
import com.google.common.base.Stopwatch
import com.sun.javafx.geom.Vec2f
import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import org.apache.commons.lang3.time.StopWatch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class Game : Application() {
    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext

    private lateinit var pitch: Image

    private val gameObjects = mutableListOf<GameObject>()

    private var lastFrameTime: Long = System.nanoTime()

    // use a set so duplicates are not possible
    private val currentlyActiveKeys = mutableSetOf<KeyCode>()

    companion object {
        private const val WIDTH = 1280
        private const val HEIGHT = 800

        const val LEFT_BORDER = 0
        const val RIGHT_BORDER = WIDTH
        const val UPPER_BORDER = 0
        const val LOWER_BORDER = HEIGHT

        private const val GOAL_TOP = 270
        private const val GOAL_BOTTOM = 530

        val GOAL_RANGE = (GOAL_TOP until GOAL_BOTTOM)
    }

    private val root = Group()
    private fun addGameObjects(vararg gameObjects: GameObject) = gameObjects.forEach {
        this.gameObjects.add(it)
        root.children.add(it.shape)
    }

    private var active = true

    private val orangeBall = Ball(0.02F, Vec2f(WIDTH*0.25F, HEIGHT/2F), 30F, ControlProfile(player1Controls), Color.ORANGE)
    private val blueBall = Ball(0.02F, Vec2f(WIDTH*0.75F, HEIGHT/2F), 30F, ControlProfile(player2Controls), Color.BLUE)

    private val leftPlayer = Player(orangeBall) {ball -> ball.posX > RIGHT_BORDER + ball.radius}
    private val rightPlayer = Player(blueBall) {ball -> ball.posX < LEFT_BORDER - ball.radius}

    private val scores = mutableMapOf(leftPlayer to 0, rightPlayer to 0)

    private val stopWatch = Stopwatch.createUnstarted()

    override fun start(mainStage: Stage) {
        mainStage.title = "Football Game"

        mainScene = Scene(root)
        mainStage.scene = mainScene

        val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())
        root.children.add(canvas)

        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D

        pitch = Image(getResource("/pitch.png"))

        addGameObjects(
            Ball(0.02F, Vec2f(WIDTH*0.25F, HEIGHT/2F), 30F, ControlProfile(player1Controls), Color.ORANGE),
            Ball(0.02F, Vec2f(WIDTH*0.75F, HEIGHT/2F), 30F, ControlProfile(player2Controls), Color.BLUE),

            MatchBall(0.2F, Vec2f(WIDTH/2F, HEIGHT/2F), 10F),
        )

        // Main loop
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender(currentNanoTime)
            }
        }.start()

        mainStage.show()
    }

    private fun prepareActionHandlers() {
        mainScene.onKeyPressed = EventHandler { event ->
            currentlyActiveKeys.add(event.code)
        }
        mainScene.onKeyReleased = EventHandler { event ->
            currentlyActiveKeys.remove(event.code)
        }
        mainScene.onMouseClicked = EventHandler { event -> event.y.let(::println) }
    }

    private fun tickAndRender(currentNanoTime: Long) {
        // the time elapsed since the last frame, in nanoseconds
        // can be used for physics calculation, etc
        val elapsedNanos = currentNanoTime - lastFrameTime
        lastFrameTime = currentNanoTime

        // clear canvas
        graphicsContext.clearRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        // draw background
        graphicsContext.drawImage(pitch, 0.0, 0.0)

        gameObjects.forEach{ it.alreadyCollidedWith.clear() }

        for (gameObject in gameObjects) {
            gameObject.step(elapsedNanos, currentlyActiveKeys, gameObjects)
            (gameObject as? MatchBall)?.let {
                checkGoalCondition(it)
                if (it.wasTouched && stopWatch.isRunning.not()) stopWatch.start()
            }
        }

        // display crude fps counter
        val elapsedMs = elapsedNanos / 1_000_000
        if (elapsedMs != 0L) {
            graphicsContext.fill = Color.WHITE
            graphicsContext.font = Font.font(12.0)
            graphicsContext.fillText("${1000 / elapsedMs} fps", 10.0, 15.0)
        }

        graphicsContext.let {
            it.fill = Color.BLACK
            it.font = Font.font(48.0)
            it.fillText(scores[leftPlayer].toString(), 400.0, 50.0)
            it.fillText(scores[rightPlayer].toString(), WIDTH - 400.0, 50.0)

            val timeLeftSecs = stopWatch.timeLeftSecs(5*60)
            val secs = if ((timeLeftSecs % 60) == 0L) "00" else timeLeftSecs % 60
            it.fillText("${timeLeftSecs / 60}:$secs", WIDTH / 2.0 - 25.0, 50.0)
        }
    }

    private fun checkGoalCondition(matchBall: MatchBall) {
        if (!active)
            return

        for (player in scores.keys)
            if (player.goalCondition(matchBall)) {
                active = false
                scores.merge(player, 1, Int::plus)
                stopWatch.stop()

                Timer().schedule(2000) {
                    resetStage()
                }
                return
            }
    }

    private fun resetStage() {
        gameObjects.forEach(GameObject::reset)
        active = true
    }

}
