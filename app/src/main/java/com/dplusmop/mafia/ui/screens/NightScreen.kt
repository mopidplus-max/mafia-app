package com.dplusmop.mafia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dplusmop.mafia.model.*
import com.dplusmop.mafia.ui.components.PassDeviceScreen
import com.dplusmop.mafia.ui.components.PrimaryActionButton
import com.dplusmop.mafia.ui.components.SecondaryActionButton
import com.dplusmop.mafia.ui.theme.MafiaGold
import com.dplusmop.mafia.ui.theme.MafiaRed

/** Общий контейнер ночного экрана: заголовок шага + контент шага. */
@Composable
private fun NightStepScaffold(
    title: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.12f), MaterialTheme.colorScheme.background)))
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(Modifier.width(14.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.height(24.dp))
        content()
    }
}

/** Список живых игроков для выбора цели (одиночный выбор, с подтверждением). */
@Composable
private fun TargetPicker(
    label: String,
    targets: List<GamePlayer>,
    accent: androidx.compose.ui.graphics.Color,
    allowSkip: Boolean = false,
    skipLabel: String = "Пропустить",
    onPicked: (GamePlayer?) -> Unit,
) {
    var selected by remember(targets) { mutableStateOf<GamePlayer?>(null) }
    Column(modifier = Modifier.fillMaxHeight()) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(14.dp))
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(targets, key = { it.name }) { p ->
                val isSelected = selected?.name == p.name
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) accent.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = p }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            p.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) accent else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryActionButton(
            text = "Подтвердить",
            enabled = selected != null,
            containerColor = accent,
            onClick = { onPicked(selected) }
        )
        if (allowSkip) {
            Spacer(Modifier.height(10.dp))
            SecondaryActionButton(text = skipLabel, onClick = { onPicked(null) })
        }
    }
}

@Composable
fun NightScreen(vm: GameViewModel) {
    var step by remember { mutableStateOf(0) }
    val playersAlive = vm.playersAlive.value
    val playersMafia = playersAlive.filter { it.role == Role.MAFIOSO }
    val playersSerialKiller = playersAlive.filter { it.role == Role.SERIAL_KILLER }
    val playersSniper = playersAlive.filter { it.role == Role.SNIPER }

    when (step) {
        0 -> PassDeviceScreen(playerName = "МАФИЯ", onReady = { step++ })
        1 -> {
            if (playersMafia.isNotEmpty()) {
                NightStepScaffold(title = "Выбор мафией", color = MafiaRed, icon = Icons.Filled.NightlightRound) {
                    TargetPicker(label = "Выберите жертву", targets = playersAlive.filter { it.role != Role.MAFIOSO }, accent = MafiaRed) { victim ->
                        vm.mafiaNightTarget.value = victim
                        step++
                    }
                }
            } else {
                step++
            }
        }
        2 -> PassDeviceScreen(playerName = "КИЛЛЕР", onReady = { step++ })
        3 -> {
            if (playersSerialKiller.isNotEmpty()) {
                NightStepScaffold(title = "Выбор киллером", color = MafiaGold, icon = Icons.Filled.NightlightRound) {
                    TargetPicker(label = "Выберите жертву", targets = playersAlive.filter { it.role != Role.SERIAL_KILLER }, accent = MafiaGold) { victim ->
                        vm.killerNightTarget.value = victim
                        step++
                    }
                }
            } else {
                step++
            }
        }
        4 -> PassDeviceScreen(playerName = "СНАЙПЕР", onReady = { step++ })
        5 -> {
            if (playersSniper.isNotEmpty()) {
                NightStepScaffold(title = "Выбор снайпером", color = MafiaGold, icon = Icons.Filled.NightlightRound) {
                    TargetPicker(label = "Выберите подозреваемого", targets = playersAlive.filter { it.role != Role.SNIPER }, accent = MafiaGold) { victim ->
                        vm.sniperNightTarget.value = victim
                        step++
                    }
                }
            } else {
                step++
            }
        }
        else -> vm.nightToDay()
    }
}
