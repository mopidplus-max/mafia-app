package com.dplusmop.mafia.model

/**
 * Шаги ночной фазы, по которым последовательно проводится UI.
 * Каждый шаг соответствует пробуждению одной роли (или общему событию).
 */
sealed class NightStep {
    object CityFallsAsleep : NightStep()
    data class JunkieTurn(val junkie: GamePlayer) : NightStep()
    data class BeautyTurn(val beauty: GamePlayer) : NightStep()
    data class MafiaTurn(val mafia: List<GamePlayer>, val advocate: GamePlayer?) : NightStep()
    data class DoctorTurn(val doctor: GamePlayer) : NightStep()
    data class CommissionerTurn(val commissioner: GamePlayer) : NightStep()
    data class AngelTurn(val angel: GamePlayer, val canRevive: Boolean) : NightStep()
    data class WitchTurn(val witch: GamePlayer) : NightStep()
    data class WitchSwapPickFirst(val witch: GamePlayer) : NightStep()
    data class WitchSwapPickSecond(val witch: GamePlayer, val first: GamePlayer) : NightStep()
    data class ManiacTurn(val maniac: GamePlayer) : NightStep()
    object Dawn : NightStep()
}

/** Накопленные результаты одной ночи — заполняются по мере прохождения шагов. */
class NightResults {
    var mafiaKillTarget: GamePlayer? = null
    var doctorHealTarget: GamePlayer? = null
    var commissionerCheckTarget: GamePlayer? = null
    var commissionerResultIsMafia: Boolean? = null
    var beautyTarget: GamePlayer? = null
    var maniacKillTarget: GamePlayer? = null
    var angelReviveTarget: GamePlayer? = null
    var junkieTarget: GamePlayer? = null
    val witchEvents = mutableListOf<NightEvent>()

    val beautyBlocked = mutableSetOf<String>()
    val junkieBlocked = mutableSetOf<String>()

    fun isBlocked(p: GamePlayer): Boolean =
        p.name in beautyBlocked || p.name in junkieBlocked
}

/**
 * Главный игровой движок. Хранит список игроков партии и проводит
 * ночь/день шаг за шагом, чтобы UI мог показывать по одному экрану на действие,
 * передавая устройство по кругу — как и было задумано в оригинальной консольной игре.
 */
class GameEngine(val players: MutableList<GamePlayer>) {

    var sniperUsed: Boolean = false
    var witchUsed: Boolean = false
    var jesterWon: Boolean = false
    var dayNumber: Int = 1

    fun alivePlayers(): List<GamePlayer> = players.filter { it.alive }
    fun deadPlayers(): List<GamePlayer> = players.filter { !it.alive }

    private fun aliveOfRole(role: Role): GamePlayer? =
        players.firstOrNull { it.role == role && it.alive }

    /** Строит последовательность шагов ночи под текущий состав живых ролей. */
    fun buildNightSteps(results: NightResults): List<NightStep> {
        val steps = mutableListOf<NightStep>()
        steps.add(NightStep.CityFallsAsleep)

        aliveOfRole(Role.JUNKIE)?.let { steps.add(NightStep.JunkieTurn(it)) }
        aliveOfRole(Role.BEAUTY)?.let { steps.add(NightStep.BeautyTurn(it)) }

        val mafiaTeam = players.filter { it.alive && RolesData.info(it.role).team == Team.MAFIA }
        if (mafiaTeam.isNotEmpty()) {
            val advocate = aliveOfRole(Role.ADVOCATE)
            steps.add(NightStep.MafiaTurn(mafiaTeam, advocate))
        }

        aliveOfRole(Role.DOCTOR)?.let { steps.add(NightStep.DoctorTurn(it)) }
        aliveOfRole(Role.COMMISSIONER)?.let { steps.add(NightStep.CommissionerTurn(it)) }

        aliveOfRole(Role.ANGEL)?.let {
            val canRevive = dayNumber >= 2 && deadPlayers().isNotEmpty()
            steps.add(NightStep.AngelTurn(it, canRevive))
        }

        if (!witchUsed) {
            aliveOfRole(Role.WITCH)?.let { steps.add(NightStep.WitchTurn(it)) }
        }

        aliveOfRole(Role.MANIAC)?.let { steps.add(NightStep.ManiacTurn(it)) }

        steps.add(NightStep.Dawn)
        return steps
    }

    /** Применяет накопленные результаты ночи: убийства, лечение, воскрешение. Возвращает лог событий. */
    fun applyNightResults(results: NightResults): List<NightEvent> {
        val events = mutableListOf<NightEvent>()
        val killedNames = mutableSetOf<String>()
        val revivedNames = mutableSetOf<String>()

        results.angelReviveTarget?.let { revived ->
            revived.alive = true
            revivedNames.add(revived.name)
            events.add(NightEvent("\ud83d\ude07 Ангел воскресил ${revived.name}! Он(а) возвращается в игру.", NightEventIcon.REVIVE))
        }

        val mafiaTarget = results.mafiaKillTarget
        if (mafiaTarget != null && mafiaTarget.name !in revivedNames) {
            val healed = results.doctorHealTarget?.name == mafiaTarget.name
            if (healed) {
                events.add(NightEvent("Доктор спас ${mafiaTarget.name} от мафии! \ud83d\udc9a", NightEventIcon.HEAL))
            } else {
                mafiaTarget.alive = false
                killedNames.add(mafiaTarget.name)
                events.add(NightEvent("Мафия убила ${mafiaTarget.name}", NightEventIcon.KILL))
            }
        }

        val doctorTarget = results.doctorHealTarget
        if (doctorTarget != null && doctorTarget.name !in killedNames) {
            events.add(NightEvent("Доктор вылечил ${doctorTarget.name}", NightEventIcon.HEAL))
        }

        val maniacTarget = results.maniacKillTarget
        if (maniacTarget != null && maniacTarget.name !in revivedNames) {
            val healed = results.doctorHealTarget?.name == maniacTarget.name
            when {
                healed -> events.add(NightEvent("Доктор спас ${maniacTarget.name} от маньяка! \ud83d\udc9a", NightEventIcon.HEAL))
                maniacTarget.name in killedNames -> events.add(
                    NightEvent("Маньяк тоже выбрал ${maniacTarget.name}, но тот уже мёртв.", NightEventIcon.INFO)
                )
                else -> {
                    maniacTarget.alive = false
                    killedNames.add(maniacTarget.name)
                    events.add(NightEvent("Маньяк убил ${maniacTarget.name}", NightEventIcon.KILL))
                }
            }
        }

        results.commissionerCheckTarget?.let { checked ->
            if (results.commissionerResultIsMafia == true) {
                events.add(NightEvent("Комиссар проверил ${checked.name} — МАФИЯ!", NightEventIcon.CHECK_MAFIA))
            } else {
                events.add(NightEvent("Комиссар проверил ${checked.name} — мирный.", NightEventIcon.CHECK_PEACEFUL))
            }
        }

        results.beautyTarget?.let { beauty ->
            events.add(NightEvent("Красотка провела ночь с ${beauty.name} — он(а) пропускает голосование. \ud83d\udc8b", NightEventIcon.BLOCK))
        }

        results.junkieTarget?.let { target ->
            events.add(NightEvent("Наркоман подсыпал порошок ${target.name} — его(её) действие заблокировано этой ночью.", NightEventIcon.BLOCK))
        }

        events.addAll(results.witchEvents)

        return events
    }

    /** Снайпер днём: возвращает событие выстрела, если выстрел сделан. */
    fun sniperShoot(target: GamePlayer, sniper: GamePlayer): NightEvent {
        sniperUsed = true
        return if (RolesData.info(target.role).team == Team.MAFIA) {
            target.alive = false
            NightEvent("\ud83c\udfaf Снайпер выстрелил в ${target.name} — попал! Это была мафия!", NightEventIcon.KILL)
        } else {
            sniper.alive = false
            NightEvent("\ud83c\udfaf Снайпер выстрелил в ${target.name} — мирный! Снайпер погибает сам.", NightEventIcon.KILL)
        }
    }

    fun sniperAlive(): GamePlayer? =
        if (!sniperUsed) aliveOfRole(Role.SNIPER) else null

    fun mayor(): GamePlayer? = aliveOfRole(Role.MAYOR)

    fun jesterAlive(): GamePlayer? = aliveOfRole(Role.JESTER)

    /** Подводит итог голосования. Возвращает (исключённый или null, голоса по имени, шут исключён?). */
    fun resolveVotes(votes: Map<String, Int>): Triple<GamePlayer?, Map<String, Int>, Boolean> {
        if (votes.values.all { it == 0 }) return Triple(null, votes, false)
        val maxVotes = votes.values.max()
        val leaders = votes.filter { it.value == maxVotes }.keys
        if (leaders.size == 1) {
            val eliminated = alivePlayers().first { it.name == leaders.first() }
            val jesterOut = eliminated.role == Role.JESTER
            return Triple(eliminated, votes, jesterOut)
        }
        return Triple(null, votes, false)
    }

    /** Проверка условий победы — портировано 1:1 из Python check_winner(). */
    fun checkWinner(): Winner? {
        if (jesterWon) return Winner.JESTER

        val alive = alivePlayers()
        val aliveMafia = alive.filter { RolesData.info(it.role).team == Team.MAFIA }
        val aliveManiac = alive.filter { it.role == Role.MANIAC }
        val alivePeaceful = alive.filter {
            val t = RolesData.info(it.role).team
            t == Team.PEACEFUL || t == Team.JESTER
        }

        if (aliveMafia.isEmpty() && aliveManiac.isEmpty()) return Winner.PEACEFUL
        if (aliveMafia.size >= alivePeaceful.size && aliveManiac.isEmpty()) return Winner.MAFIA
        if (aliveManiac.isNotEmpty() && aliveManiac.size >= alive.size - aliveManiac.size) return Winner.MANIAC
        return null
    }
}
