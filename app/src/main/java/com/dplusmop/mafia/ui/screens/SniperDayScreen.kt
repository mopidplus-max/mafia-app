package com.dplusmop.mafia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dplusmop.mafia.model.GamePlayer
import com.dplusmop.mafia.model.GameViewModel
import com.dplusmop.mafia.ui.components.PassDeviceScreen
import com.dplusmop.mafia.ui.components.PrimaryActionButton
import com.dplusmop.mafia.ui.components.SecondaryActionButton

@Composable
fun SniperDayScreen(vm: GameViewModel) {
    val engine = vm.engine ?: return
    val sniper = engine.sniperAlive() ?: run { vm.skipSniperShot(); return }
    var passed by remember { mutableStateOf(false) }
    var asked by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<GamePlayer?>(null) }

    if (!passed) {
        PassDeviceScreen(playerName = sniper.name, subtitle = "Особое действие снайпера", onReady = { passed = true })
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.08f), MaterialTheme.colorScheme.background)))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.GpsFixed, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Text("Снайпер", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(24.dp))

        if (!asked) {
            Text(
                "Хотите использовать одноразовый выстрел сейчас?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            PrimaryActionButton(text = "Стрелять", onClick = { asked = true })
            Spacer(Modifier.height(10.dp))
            SecondaryActionButton(text = "Воздержаться", onClick = { vm.skipSniperShot() })
        } else {
            Text("В кого стреляем?", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(14.dp))
            val targets = engine.alivePlayers().filter { it.name != sniper.name }
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(targets, key = { it.name }) { p ->
                    val isSelected = selected?.name == p.name
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = .2f) else MaterialTheme.colorScheme.surface,
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
            PrimaryActionButton(text = "Выстрелить", enabled = selected != null, onClick = { vm.resolveSniperShot(selected) })
            Spacer(Modifier.height(10.dp))
            SecondaryActionButton(text = "Отмена", onClick = { vm.skipSniperShot() })
        }
    }
}
