package com.dplusmop.mafia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dplusmop.mafia.model.GameViewModel
import com.dplusmop.mafia.model.NightEventIcon
import com.dplusmop.mafia.ui.components.MafiaTopBar
import com.dplusmop.mafia.ui.components.PrimaryActionButton
import com.dplusmop.mafia.ui.theme.MafiaGold
import com.dplusmop.mafia.ui.theme.MafiaGreen
import com.dplusmop.mafia.ui.theme.MafiaRed

private fun iconFor(kind: NightEventIcon): ImageVector = when (kind) {
    NightEventIcon.KILL -> Icons.Filled.Close
    NightEventIcon.HEAL -> Icons.Filled.Favorite
    NightEventIcon.REVIVE -> Icons.Filled.AutoAwesome
    NightEventIcon.CHECK_MAFIA -> Icons.Filled.ReportProblem
    NightEventIcon.CHECK_PEACEFUL -> Icons.Filled.CheckCircle
    NightEventIcon.BLOCK -> Icons.Filled.Block
    NightEventIcon.SWAP -> Icons.Filled.SwapHoriz
    NightEventIcon.INFO -> Icons.Filled.Info
}

private fun colorFor(kind: NightEventIcon) = when (kind) {
    NightEventIcon.KILL -> MafiaRed
    NightEventIcon.HEAL, NightEventIcon.CHECK_PEACEFUL -> MafiaGreen
    NightEventIcon.REVIVE -> MafiaGold
    NightEventIcon.CHECK_MAFIA -> MafiaRed
    else -> MafiaGold
}

@Composable
fun NightSummaryScreen(vm: GameViewModel) {
    val engine = vm.engine ?: return
    Column(modifier = Modifier.fillMaxSize()) {
        MafiaTopBar(title = "Итоги ночи ${engine.dayNumber}")

        if (vm.nightEventsLog.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Эта ночь прошла тихо — никто не пострадал.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(vm.nightEventsLog) { event ->
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(iconFor(event.icon), contentDescription = null, tint = colorFor(event.icon), modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(event.text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            val aliveNames = engine.alivePlayers().joinToString(", ") { it.name }
            Text(
                text = "Живые: $aliveNames",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))
            PrimaryActionButton(text = "Продолжить", onClick = { vm.afterNightSummary() })
        }
    }
}
