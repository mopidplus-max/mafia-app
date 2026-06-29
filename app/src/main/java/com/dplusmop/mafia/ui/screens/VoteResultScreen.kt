package com.dplusmop.mafia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dplusmop.mafia.model.GameViewModel
import com.dplusmop.mafia.model.RolesData
import com.dplusmop.mafia.ui.components.MafiaTopBar
import com.dplusmop.mafia.ui.components.PillLabel
import com.dplusmop.mafia.ui.components.PrimaryActionButton
import com.dplusmop.mafia.ui.theme.MafiaGold
import com.dplusmop.mafia.ui.theme.MafiaRed

@Composable
fun VoteResultScreen(vm: GameViewModel) {
    val engine = vm.engine ?: return
    val sorted = vm.voteCounts.entries.sortedByDescending { it.value }
    val eliminated = vm.lastEliminated

    Column(modifier = Modifier.fillMaxSize()) {
        MafiaTopBar(title = "Результаты голосования")

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sorted) { (name, votes) ->
                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, style = MaterialTheme.typography.titleMedium)
                        PillLabel(text = "$votes ${if (votes == 1) "голос" else "голосов"}", color = MafiaGold)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            if (eliminated != null) {
                val info = RolesData.info(eliminated.role)
                Surface(shape = RoundedCornerShape(16.dp), color = MafiaRed.copy(alpha = 0.14f), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.HowToVote, contentDescription = null, tint = MafiaRed)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Город проголосовал! ${eliminated.name} исключён(а) из игры.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Роль: ${eliminated.role.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = info.color
                            )
                        }
                    }
                }
                if (vm.jesterEliminatedThisVote) {
                    Spacer(Modifier.height(10.dp))
                    Surface(shape = RoundedCornerShape(16.dp), color = androidx.compose.ui.graphics.Color(0xFFD946EF).copy(alpha = 0.18f), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.TheaterComedy, contentDescription = null, tint = androidx.compose.ui.graphics.Color(0xFFD946EF))
                            Spacer(Modifier.width(12.dp))
                            Text("Вы исключили Шута! Шут добился своего!", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Голоса разделились — никто не исключён.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            PrimaryActionButton(text = "Продолжить", onClick = { vm.afterVoteResult() })
        }
    }
}
