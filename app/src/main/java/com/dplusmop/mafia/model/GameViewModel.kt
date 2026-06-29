package com.dplusmop.mafia.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/** Экран приложения для простой навигации внутри одного ViewModel. */
sealed class Screen {
    object Menu : Screen()
    object Lobby : Screen()
    object RolesInfo : Screen()
    object RoleReveal : Screen()      // раздача ролей перед началом партии
    object Night : Screen()
    object NightSummary : Screen()
    object SniperDay : Screen()
    object DayVote : Screen()
    object VoteResult : Screen()
    object GameOver : Screen()
}

/**
 * Центральная ViewModel приложения. Хранит список игроков лобби,
 * активную партию (GameEngine) и состояние текущего экрана/шага внутри ночи или дня.
 */
class GameViewModel : ViewModel() {

    // ---- Лобби ----
    var lobbyPlayers = mutableStateOf<List<LobbyPlayer>>(emptyList())
        private set

    var screen by mutableStateOf<Screen>(Screen.Menu)

    fun addPlayer(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return "Имя не может быть пустым"
        if (lobbyPlayers.value.any { it.name.equals(trimmed, ignoreCase = true) }) {
            return "Игрок с таким именем уже есть"
        }
        lobbyPlayers.value = lobbyPlayers.value + LobbyPlayer(trimmed)
        return null
    }

    fun removePlayer(name: String) {
        lobbyPlayers.value = lobbyPlayers.value.filterNot { it.name == name }
    }

    // ---- Активная партия ----
    var engine: GameEngine? = null
        private set

    var roleRevealIndex by mutableStateOf(0)
    var nightStepIndex by mutableStateOf(0)
    var nightSteps by mutableStateOf<List<NightStep>>(emptyList())
    var nightResults by mutableStateOf(NightResults())
    var nightEventsLog by mutableStateOf<List<NightEvent>>(emptyList())
    var voteCounts by mutableStateOf<Map<String, Int>>(emptyMap())
    var voteVoterIndex by mutableStateOf(0)
    var beautySkipPlayerName by mutableStateOf<String?>(null)
    var lastEliminated by mutableStateOf<GamePlayer?>(null)
    var jesterEliminatedThisVote by mutableStateOf(false)
    var winner by mutableStateOf<Winner?>(null)
    var sniperEventThisDay by mutableStateOf<NightEvent?>(null)

    // Временное состояние ведьмы во время её шага (выбор первого/второго игрока)
    var witchFirstPick by mutableStateOf<GamePlayer?>(null)

    fun startGame() {
        val gamePlayers = lobbyPlayers.value.map { GamePlayer(it.name, Role.CIVILIAN) }.toMutableList()
        val roles = RolesData.assignRoles(gamePlayers.size)
        gamePlayers.forEachIndexed { i, p -> p.role = roles[i] }
        engine = GameEngine(gamePlayers)
        roleRevealIndex = 0
        screen = Screen.RoleReveal
    }

    fun beginNight() {
        val e = engine ?: return
        nightResults = NightResults()
        nightSteps = e.buildNightSteps(nightResults)
        nightStepIndex = 0
        witchFirstPick = null
        screen = Screen.Night
    }

    fun advanceNightStep() {
        if (nightStepIndex < nightSteps.size - 1) {
            nightStepIndex++
        } else {
            // Ночь окончена — применяем результаты
            val e = engine ?: return
            nightEventsLog = e.applyNightResults(nightResults)
            beautySkipPlayerName = nightResults.beautyTarget?.name
            screen = Screen.NightSummary
        }
    }

    fun afterNightSummary() {
        val e = engine ?: return
        val w = e.checkWinner()
        if (w != null) {
            winner = w
            screen = Screen.GameOver
            return
        }
        // Проверяем снайпера
        val sniper = e.sniperAlive()
        sniperEventThisDay = null
        if (sniper != null) {
            screen = Screen.SniperDay
        } else {
            beginVote()
        }
    }

    fun resolveSniperShot(target: GamePlayer?) {
        val e = engine ?: return
        if (target != null) {
            val sniper = e.sniperAlive()
            if (sniper != null) {
                sniperEventThisDay = e.sniperShoot(target, sniper)
            }
        }
        val w = e.checkWinner()
        if (w != null) {
            winner = w
            screen = Screen.GameOver
            return
        }
        beginVote()
    }

    fun skipSniperShot() {
        val e = engine ?: return
        val w = e.checkWinner()
        if (w != null) {
            winner = w
            screen = Screen.GameOver
            return
        }
        beginVote()
    }

    private fun beginVote() {
        val e = engine ?: return
        voteCounts = e.alivePlayers().associate { it.name to 0 }
        voteVoterIndex = 0
        screen = Screen.DayVote
    }

    fun castVote(voterName: String, targetName: String?, weight: Int) {
        if (targetName != null) {
            voteCounts = voteCounts.toMutableMap().apply {
                this[targetName] = (this[targetName] ?: 0) + weight
            }
        }
        val e = engine ?: return
        if (voteVoterIndex < e.alivePlayers().size - 1) {
            voteVoterIndex++
        } else {
            finishVote()
        }
    }

    private fun finishVote() {
        val e = engine ?: return
        val (eliminated, counts, jesterOut) = e.resolveVotes(voteCounts)
        voteCounts = counts
        lastEliminated = eliminated
        jesterEliminatedThisVote = jesterOut
        if (eliminated != null) {
            eliminated.alive = false
            if (jesterOut) e.jesterWon = true
        }
        screen = Screen.VoteResult
    }

    fun afterVoteResult() {
        val e = engine ?: return
        val w = e.checkWinner()
        if (w != null) {
            winner = w
            screen = Screen.GameOver
            return
        }
        e.dayNumber += 1
        beginNight()
    }

    fun returnToMenu() {
        engine = null
        winner = null
        screen = Screen.Menu
    }

    fun newGameSamePlayers() {
        winner = null
        startGame()
    }
}
