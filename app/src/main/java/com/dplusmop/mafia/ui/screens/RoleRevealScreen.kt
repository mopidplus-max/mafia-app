package com.dplusmop.mafia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dplusmop.mafia.model.GameViewModel
import com.dplusmop.mafia.model.Role
import com.dplusmop.mafia.model.RolesData
import com.dplusmop.mafia.model.Team
import com.dplusmop.mafia.ui.components.PassDeviceScreen
import com.dplusmop.mafia.ui.components.PrimaryActionButton

/**
 * Раздача ролей: для каждого игрока сперва экран "передайте устройство",
 * затем сама роль с описанием, скрытая от посторонних глаз.
 */
@Composable
fun RoleRevealScreen(vm: GameViewModel) {
    val engine = vm.engine ?: return
    val players = engine.players
    val index = vm.roleRevealIndex
    if (index >= players.size) {
        vm.beginNight()
        return
    }
    val player = players[index]
    var revealed by remember(index) { mutableStateOf(false) }

    if (!revealed) {
        PassDeviceScreen(
            playerName = player.name,
            subtitle = "Передайте устройство игроку",
            onReady = { revealed = true }
        )
        return
    }

    val info = RolesData.info(player.role)
    val advocateMafiaNames = if (player.role == Role.ADVOCATE) {
        players.filter { RolesData.info(it.role).team == Team.MAFIA }.joinToString(", ") { it.name }
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(info.color.copy(alpha = 0.14f), MaterialTheme.colorScheme.background)))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${player.name}, ваша роль:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Brush.radialGradient(listOf(info.color.copy(alpha = .35f), info.color.copy(alpha = 0f))), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(info.icon, contentDescription = null, tint = info.color, modifier = Modifier.size(56.dp))
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = player.role.displayName.uppercase(),
            style = MaterialTheme.typography.displayLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit(28f, androidx.compose.ui.unit.TextUnitType.Sp)),
            color = info.color,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(18.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Text(
                text = info.description,
                modifier = Modifier.padding(18.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        if (advocateMafiaNames != null) {
            Spacer(Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
            ) {
                Text(
                    text = "Состав мафии (только для вас): $advocateMafiaNames",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(36.dp))
        PrimaryActionButton(
            text = if (index == players.size - 1) "Начинаем игру" else "Запомнил(а), дальше",
            onClick = {
                vm.roleRevealIndex += 1
            }
        )
    }
}
