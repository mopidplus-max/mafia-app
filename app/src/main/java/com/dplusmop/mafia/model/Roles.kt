package com.dplusmop.mafia.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/** Команда, к которой принадлежит роль (для подсчёта победителя). */
enum class Team {
    MAFIA, PEACEFUL, MANIAC, JESTER
}

/** Все роли игры. */
enum class Role(val displayName: String) {
    MAFIA("Мафия"),
    DON("Дон мафии"),
    ADVOCATE("Адвокат"),
    DOCTOR("Доктор"),
    COMMISSIONER("Комиссар"),
    BEAUTY("Красотка"),
    ANGEL("Ангел"),
    MAYOR("Мэр"),
    JESTER("Шут"),
    WITCH("Ведьма"),
    JUNKIE("Наркоман"),
    MANIAC("Маньяк"),
    SNIPER("Снайпер"),
    CIVILIAN("Мирный")
}

/** Статичная информация о роли: иконка, цвет, описание, команда. */
data class RoleInfo(
    val icon: ImageVector,
    val color: Color,
    val description: String,
    val team: Team
)

object RolesData {

    // Палитра ролей — насыщенные, читаемые на тёмном фоне цвета
    private val red = Color(0xFFEF4444)
    private val darkRed = Color(0xFFB91C1C)
    private val amber = Color(0xFFF59E0B)
    private val green = Color(0xFF22C55E)
    private val blue = Color(0xFF3B82F6)
    private val pink = Color(0xFFEC4899)
    private val white = Color(0xFFF3F4F6)
    private val gold = Color(0xFFEAB308)
    private val magenta = Color(0xFFD946EF)
    private val purple = Color(0xFFA855F7)

    val infoMap: Map<Role, RoleInfo> = mapOf(
        Role.MAFIA to RoleInfo(
            icon = Icons.Filled.Visibility,
            color = red,
            description = "Ночью вместе с другими мафиози выбирает жертву для убийства. Побеждает, когда мафия численно равна или превосходит мирных.",
            team = Team.MAFIA
        ),
        Role.DON to RoleInfo(
            icon = Icons.Filled.Star,
            color = darkRed,
            description = "Глава мафии. Ночью выбирает жертву. Невидим для комиссара — проверка покажет «мирный». Побеждает вместе с мафией.",
            team = Team.MAFIA
        ),
        Role.ADVOCATE to RoleInfo(
            icon = Icons.Filled.Gavel,
            color = amber,
            description = "Просыпается вместе с мафией и знает её состав. Днём должен защищать мафиози на голосованиях. Сам мирный житель — побеждает с мирными. Если мафия проиграет — адвокат тоже проигрывает.",
            team = Team.PEACEFUL
        ),
        Role.DOCTOR to RoleInfo(
            icon = Icons.Filled.LocalHospital,
            color = green,
            description = "Ночью выбирает игрока для лечения. Если мафия или маньяк выбрали ту же цель — жертва выживает. Может лечить себя.",
            team = Team.PEACEFUL
        ),
        Role.COMMISSIONER to RoleInfo(
            icon = Icons.Filled.LocalPolice,
            color = blue,
            description = "Ночью проверяет одного игрока на принадлежность к мафии. Внимание: Дон мафии выглядит как мирный! Побеждает вместе с мирными.",
            team = Team.PEACEFUL
        ),
        Role.BEAUTY to RoleInfo(
            icon = Icons.Filled.Favorite,
            color = pink,
            description = "Ночью посещает одного игрока. Тот игрок пропускает своё ночное действие и голосование. Побеждает с мирными.",
            team = Team.PEACEFUL
        ),
        Role.ANGEL to RoleInfo(
            icon = Icons.Filled.AutoAwesome,
            color = white,
            description = "Ночью может воскресить одного погибшего игрока. Воскрешение доступно только начиная со 2-й ночи. Воскрешённый игрок возвращается со своей ролью. Побеждает с мирными.",
            team = Team.PEACEFUL
        ),
        Role.MAYOR to RoleInfo(
            icon = Icons.Filled.AccountBalance,
            color = gold,
            description = "Уважаемый житель города. Его голос на дневном голосовании считается за два. Побеждает с мирными.",
            team = Team.PEACEFUL
        ),
        Role.JESTER to RoleInfo(
            icon = Icons.Filled.TheaterComedy,
            color = magenta,
            description = "Особая роль! Шут должен вести себя подозрительно, делая вид что он мафия, чтобы его проголосовали. Если шута исключают голосованием — ШУТ ПОБЕЖДАЕТ! Если шут выживает до конца — он проигрывает.",
            team = Team.JESTER
        ),
        Role.WITCH to RoleInfo(
            icon = Icons.Filled.AutoFixHigh,
            color = purple,
            description = "Один раз за игру может поменять роли двух игроков местами. После обмена оба игрока узнают свою новую роль в тайне. Побеждает с мирными.",
            team = Team.PEACEFUL
        ),
        Role.JUNKIE to RoleInfo(
            icon = Icons.Filled.Medication,
            color = green,
            description = "Ночью подсыпает порошок одному игроку. Тот игрок пропускает своё ночное действие в эту ночь. Если цель — мафия, мафия не убивает этой ночью. Побеждает с мирными.",
            team = Team.PEACEFUL
        ),
        Role.MANIAC to RoleInfo(
            icon = Icons.Filled.Dangerous,
            color = amber,
            description = "Одиночка. Ночью убивает одного игрока. Побеждает, когда остаётся последним живым игроком. Врагом считает всех.",
            team = Team.MANIAC
        ),
        Role.SNIPER to RoleInfo(
            icon = Icons.Filled.GpsFixed,
            color = white,
            description = "Один раз за игру может выстрелить в подозреваемого днём. Если цель — мафия, она погибает. Если цель — мирный, снайпер погибает сам. Побеждает с мирными.",
            team = Team.PEACEFUL
        ),
        Role.CIVILIAN to RoleInfo(
            icon = Icons.Filled.Person,
            color = white,
            description = "Обычный житель города. Не имеет специальных способностей. Участвует в дневных голосованиях. Побеждает когда все мафиози и маньяк уничтожены.",
            team = Team.PEACEFUL
        ),
    )

    fun info(role: Role): RoleInfo = infoMap.getValue(role)

    /** Раздача ролей под количество игроков — логика 1:1 как в исходном Python. */
    fun assignRoles(n: Int): List<Role> {
        val roles: MutableList<Role> = when (n) {
            4 -> mutableListOf(Role.MAFIA, Role.DOCTOR, Role.COMMISSIONER, Role.CIVILIAN)
            5 -> mutableListOf(Role.MAFIA, Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.CIVILIAN)
            6 -> mutableListOf(Role.MAFIA, Role.MAFIA, Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.CIVILIAN)
            7 -> mutableListOf(Role.MAFIA, Role.MAFIA, Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.MANIAC, Role.CIVILIAN)
            8 -> mutableListOf(Role.MAFIA, Role.DON, Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.MANIAC, Role.ANGEL, Role.CIVILIAN)
            9 -> mutableListOf(Role.MAFIA, Role.DON, Role.ADVOCATE, Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.MANIAC, Role.ANGEL, Role.CIVILIAN)
            10 -> mutableListOf(Role.MAFIA, Role.DON, Role.ADVOCATE, Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.MANIAC, Role.ANGEL, Role.MAYOR, Role.JUNKIE)
            11 -> mutableListOf(Role.MAFIA, Role.MAFIA, Role.DON, Role.ADVOCATE, Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.MANIAC, Role.ANGEL, Role.MAYOR, Role.JUNKIE)
            12 -> mutableListOf(Role.MAFIA, Role.MAFIA, Role.DON, Role.ADVOCATE, Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.MANIAC, Role.ANGEL, Role.MAYOR, Role.JUNKIE, Role.JESTER)
            13 -> mutableListOf(Role.MAFIA, Role.MAFIA, Role.DON, Role.ADVOCATE, Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.MANIAC, Role.ANGEL, Role.MAYOR, Role.JUNKIE, Role.JESTER, Role.WITCH)
            else -> {
                val mafiaCount = maxOf(1, n / 3)
                val list = mutableListOf<Role>()
                repeat(maxOf(1, mafiaCount - 1)) { list.add(Role.MAFIA) }
                list.add(Role.DON)
                val specials = listOf(
                    Role.DOCTOR, Role.COMMISSIONER, Role.BEAUTY, Role.MANIAC,
                    Role.ANGEL, Role.MAYOR, Role.JESTER, Role.WITCH, Role.JUNKIE,
                    Role.SNIPER, Role.ADVOCATE
                )
                list.addAll(specials)
                val remaining = n - list.size
                if (remaining > 0) repeat(remaining) { list.add(Role.CIVILIAN) }
                val trimmed = list.take(n).toMutableList()
                while (trimmed.size < n) trimmed.add(Role.CIVILIAN)
                trimmed
            }
        }
        roles.shuffle()
        return roles
    }
}

val Team.label: String
    get() = when (this) {
        Team.MAFIA -> "Мафия"
        Team.PEACEFUL -> "Мирные"
        Team.MANIAC -> "Маньяк"
        Team.JESTER -> "Шут"
    }
