package com.dplusmop.mafia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dplusmop.mafia.model.GamePlayer
import com.dplusmop.mafia.model.GameViewModel
import com.dplusmop.mafia.ui.components.PassDeviceScreen
import com.dplusmop.mafia.ui.components.PillLabel
import com.dplusmop.mafia.ui.components.PrimaryActionButton
import com.dplusmop.mafia.ui.components.SecondaryActionButton
import com.dplusmop.mafia.ui.theme.MafiaGold

@Composable
fun DayVoteScreen(vm: GameViewModel) {
    val engine = vm.engine ?: return
    val alive = engine.alivePlayers()
    if (vm.voteVoterIndex >= alive.size) return
    val voter = alive[vm.voteVoterIndex]

    // Игрок, пропускающий голосование из-за визита красотки
    if (voter.name == vm.beautySkipPlayerName) {
        LaunchedEffect(voter.name) {
            vm.castVote(voter.name, null, 1)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "${voter.name} пропускает голосование (красотка была у него(неё) этой ночью)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(32.dp)
            )
        }
        return
    }

    var passed by remember(voter.name) { mutableStateOf(false) }
    var selected by remember(voter.name) { mutableStateOf<GamePlayer?>(null) }

    if (!passed) {
        PassDeviceScreen(playerName = voter.name, subtitle = "Голосование дня — передайте устройство", onReady = { passed = true })
        return
    }

    val mayor = engine.mayor()
    val weight = if (mayor?.name == voter.name) 2 else 1
    val jester = engine.jesterAlive()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MafiaGold.copy(alpha = 0.10f), MaterialTheme.colorScheme.background)))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(MafiaGold.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Gavel, contentDescription = null, tint = MafiaGold)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("День ${engine.dayNumber} — Голосование", style = MaterialTheme.typography.headlineMedium)
                Text("Голосует: ${voter.name}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (jester != null) {
            Spacer(Modifier.height(12.dp))
            PillLabel(text = "🃏 Среди игроков есть Шут — следите за подозрительным поведением", color = androidx.compose.ui.graphics.Color(0xFFD946EF))
        }
        if (weight == 2) {
            Spacer(Modifier.height(8.dp))
            PillLabel(text = "Голос ×2 — Мэр", color = MafiaGold)
        }

        Spacer(Modifier.height(20.dp))

        val candidates = alive.filter { it.name != voter.name }
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(candidates, key = { it.name }) { p ->
                val isSelected = selected?.name == p.name
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = .22f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = p }
                ) {
                    Text(
                        p.name,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        PrimaryActionButton(
            text = "Голосовать",
            enabled = selected != null,
            onClick = { vm.castVote(voter.name, selected?.name, weight) }
        )
        Spacer(Modifier.height(10.dp))
        SecondaryActionButton(text = "Пропустить голос", onClick = { vm.castVote(voter.name, null, weight) })
    }
}
