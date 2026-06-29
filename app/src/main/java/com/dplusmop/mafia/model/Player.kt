package com.dplusmop.mafia.model

/**
 * Игрок в лобби (до начала игры) — только имя.
 */
data class LobbyPlayer(
    val name: String
)

/**
 * Игрок внутри запущенной игровой партии — с ролью и статусом жив/мёртв.
 */
data class GamePlayer(
    val name: String,
    var role: Role,
    var alive: Boolean = true
)

/** Итог одной ночи — что произошло, для лога событий. */
data class NightEvent(
    val text: String,
    val icon: NightEventIcon = NightEventIcon.INFO
)

enum class NightEventIcon { KILL, HEAL, REVIVE, CHECK_MAFIA, CHECK_PEACEFUL, INFO, BLOCK, SWAP }

/** Победитель партии. */
enum class Winner { MAFIA, PEACEFUL, MANIAC, JESTER }
