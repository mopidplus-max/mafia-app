package com.dplusmop.mafia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dplusmop.mafia.R
import com.dplusmop.mafia.model.GameViewModel
import com.dplusmop.mafia.model.Screen
import com.dplusmop.mafia.ui.components.PrimaryActionButton
import com.dplusmop.mafia.ui.components.SecondaryActionButton
import com.dplusmop.mafia.ui.theme.MafiaGold
import com.dplusmop.mafia.ui.theme.MafiaRed

@Composable
fun MenuScreen(vm: GameViewModel) {
    val playerCount = vm.lobbyPlayers.value.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MafiaRed.copy(alpha = 0.10f), MaterialTheme.colorScheme.background)))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        Box(
            modifier = Modifier
                .size(88.dp)
                .background(
                    Brush.radialGradient(listOf(MafiaRed.copy(alpha = .4f), MafiaRed.copy(alpha = 0f))),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Логотип",
                modifier = Modifier.size(72.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "МАФИЯ",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Классическая ролевая игра для компании",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(36.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Groups, contentDescription = null, tint = MafiaGold)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (playerCount == 0) "Игроков пока нет" else "Игроков добавлено: $playerCount",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            PrimaryActionButton(
                text = "Игроки",
                icon = Icons.Filled.PersonAdd,
                onClick = { vm.screen = Screen.Lobby }
            )
            PrimaryActionButton(
                text = "Начать игру",
                icon = Icons.Filled.PlayArrow,
                enabled = playerCount >= 4,
                containerColor = MafiaGold,
                onClick = { vm.startGame() }
            )
            SecondaryActionButton(
                text = "Роли в игре",
                icon = Icons.Filled.MenuBook,
                onClick = { vm.screen = Screen.RolesInfo }
            )
        }

        if (playerCount in 1..3) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Нужно минимум 4 игрока для начала игры",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "BY DPLUSMOP (TG LEFTWHEEL)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(24.dp))
    }
}
