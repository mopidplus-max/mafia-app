package com.dplusmop.mafia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dplusmop.mafia.model.GameViewModel
import com.dplusmop.mafia.model.RolesData
import com.dplusmop.mafia.model.Winner
import com.dplusmop.mafia.ui.components.PrimaryActionButton
import com.dplusmop.mafia.ui.components.SecondaryActionButton
import com.dplusmop.mafia.ui.theme.MafiaGold
import com.dplusmop.mafia.ui.theme.MafiaGreen
import com.dplusmop.mafia.ui.theme.MafiaRed

private data class WinnerVisual(val title: String, val subtitle: String, val color: Color, val icon: ImageVector)

private fun visualFor(winner: Winner): WinnerVisual = when (winner) {
    Winner.MAFIA -> WinnerVisual("МАФИЯ ПОБЕДИЛА!", "Мирные жители проиграли...", MafiaRed, Icons.Filled.Visibility)
    Winner.MANIAC -> WinnerVisual("МАНЬЯК ПОБЕДИЛ!", "Все остальные погибли...", androidx.compose.ui.graphics.Color(0xFFF59E0B), Icons.Filled.Dangerous)
    Winner.JESTER -> WinnerVisual("ШУТ ПОБЕДИЛ!", "Город сам себе выстрелил в ногу...", androidx.compose.ui.graphics.Color(0xFFD946EF), Icons.Filled.TheaterComedy)
    Winner.PEACEFUL -> WinnerVisual("МИРНЫЕ ПОБЕДИЛИ!", "Город в безопасности!", MafiaGreen, Icons.Filled.Shield)
}

@Composable
fun GameOverScreen(vm: GameViewModel) {
    val engine = vm.engine ?: return
    val winner = vm.winner ?: return
    val visual = visualFor(winner)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(visual.color.copy(alpha = 0.16f), MaterialTheme.colorScheme.background)))
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.CenterHorizontally)
                .background(Brush.radialGradient(listOf(visual.color.copy(alpha = .4f), Color.Transparent)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(visual.icon, contentDescription = null, tint = visual.color, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(
            visual.title,
            style = MaterialTheme.typography.headlineLarge,
            color = visual.color,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            visual.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
        Text("Финальный состав", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(engine.players) { p ->
                val info = RolesData.info(p.role)
                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(info.icon, contentDescription = null, tint = info.color, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(p.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        Text(p.role.displayName, style = MaterialTheme.typography.bodySmall, color = info.color)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            if (p.alive) "Жив" else "Убит",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (p.alive) MafiaGreen else MafiaRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        PrimaryActionButton(text = "Сыграть ещё раз (те же игроки)", onClick = { vm.newGameSamePlayers() })
        Spacer(Modifier.height(10.dp))
        SecondaryActionButton(text = "В главное меню", onClick = { vm.returnToMenu() })
        Spacer(Modifier.height(20.dp))
    }
}
