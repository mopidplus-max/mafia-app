package com.dplusmop.mafia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dplusmop.mafia.model.GameViewModel
import com.dplusmop.mafia.model.Role
import com.dplusmop.mafia.model.RolesData
import com.dplusmop.mafia.model.Screen
import com.dplusmop.mafia.model.Team
import com.dplusmop.mafia.model.label
import com.dplusmop.mafia.ui.components.MafiaTopBar
import com.dplusmop.mafia.ui.components.PillLabel

@Composable
fun RolesInfoScreen(vm: GameViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        MafiaTopBar(title = "Роли в игре", onBack = { vm.screen = Screen.Menu })

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(Role.entries.toList()) { role ->
                val info = RolesData.info(role)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(info.color.copy(alpha = 0.18f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(info.icon, contentDescription = null, tint = info.color)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = role.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(4.dp))
                                PillLabel(text = info.team.label, color = info.color)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = info.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
