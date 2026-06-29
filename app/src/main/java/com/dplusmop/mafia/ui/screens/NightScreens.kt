package com.dplusmop.mafia.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dplusmop.mafia.data.Player
import com.dplusmop.mafia.data.RolesData
import com.dplusmop.mafia.data.Team
import com.dplusmop.mafia.game.GameEngine
import com.dplusmop.mafia.game.GameViewModel
import com.dplusmop.mafia.ui.components.*
import com.dplusmop.mafia.ui.theme.*

@Composable
fun NightIntroScreen(vm: GameViewModel, dayNum: Int) {
    val inf = rememberInfiniteTransition(label = "moon")
    val alpha by inf.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF000010), Color(0xFF05050F)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🌙", fontSize = 80.sp)
            Spacer(Modifier.height(24.dp))
            Text(
                "НОЧЬ $dayNum",
                color = Color(0xFF546E7A).copy(alpha = alpha),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp
            )
            Spacer(Modifier.height(12.dp))
            Text("Город засыпает...", color = MafiaTextDim.copy(alpha = 0.6f), fontSize = 16.sp)
            Spacer(Modifier.height(48.dp))
            GradientButton(
                text = "Начать ночную фазу",
                onClick = { vm.startNightPhase() },
                colors = listOf(Color(0xFF1A237E), Color(0xFF283593)),
                icon = Icons.Filled.NightsStay,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun NightActionScreen(vm: GameViewModel, role: String, dayNum: Int) {
    val state by vm.state.collectAsState()
    val alive = state.gamePlayers.filter { it.alive }
    val nr = state.nightResults
    val isBlocked = { p: Player -> p.name in nr.beautyBlocked || p.name in nr.drugBlocked }

    val roleInfo = RolesData.roles[role]
    val color = roleInfo?.color ?: MafiaText

    when (role) {
        "наркоман" -> {
            val targets = alive.filter { it.role != "наркоман" }
            NightTargetPicker(
                title = "💊 НАРКОМАН просыпается",
                subtitle = "Выберите кому подсыпать порошок",
                color = RolesData.GREEN_BRIGHT,
                targets = targets,
                playerName = alive.find { it.role == "наркоман" }?.name ?: "",
                onPick = { vm.setNarcomanTarget(it) },
                onSkip = null
            )
        }
        "красотка" -> {
            val beauty = alive.find { it.role == "красотка" }
            if (beauty != null && isBlocked(beauty)) {
                BlockedScreen(role = "красотка", color = RolesData.MAGENTA, onContinue = { vm.advanceNightQueue() })
            } else {
                val targets = alive.filter { it.role != "красотка" }
                NightTargetPicker(
                    title = "💋 КРАСОТКА просыпается",
                    subtitle = "К кому пойти этой ночью?",
                    color = RolesData.MAGENTA,
                    targets = targets,
                    playerName = beauty?.name ?: "",
                    onPick = { vm.setBeautyTarget(it) },
                    onSkip = null
                )
            }
        }
        "мафия" -> {
            val mafiaPlayers = alive.filter { RolesData.roles[it.role]?.team == Team.MAFIA }
            val activeMafia = mafiaPlayers.filter { !isBlocked(it) }
            val advocate = alive.find { it.role == "адвокат" }
            val mafiaNames = mafiaPlayers.joinToString(", ") { it.name }
            val allAwake = if (advocate != null) "$mafiaNames, ${advocate.name} (Адвокат)" else mafiaNames

            if (activeMafia.isEmpty()) {
                BlockedScreen(role = "мафия", color = RolesData.RED, message = "Мафия заблокирована — убийства не будет!", onContinue = { vm.advanceNightQueue() })
            } else {
                val targets = alive.filter { RolesData.roles[it.role]?.team != Team.MAFIA && it.role != "адвокат" }
                NightTargetPicker(
                    title = "🔴 МАФИЯ просыпается",
                    subtitle = "Выберите жертву",
                    color = RolesData.RED,
                    targets = targets,
                    playerName = allAwake,
                    playerLabel = "Просыпаются",
                    extraNote = if (advocate != null) "⚖️ Адвокат, запомните состав — защищайте их днём!" else null,
                    onPick = { vm.setMafiaKill(it) },
                    onSkip = null
                )
            }
        }
        "доктор" -> {
            val doctor = alive.find { it.role == "доктор" }
            if (doctor != null && isBlocked(doctor)) {
                BlockedScreen(role = "доктор", color = RolesData.GREEN, onContinue = { vm.advanceNightQueue() })
            } else {
                val targets = alive
                NightTargetPicker(
                    title = "💚 ДОКТОР просыпается",
                    subtitle = "Кого вылечить этой ночью?",
                    color = RolesData.GREEN,
                    targets = targets,
                    playerName = doctor?.name ?: "",
                    onPick = { vm.setDoctorHeal(it) },
                    onSkip = null
                )
            }
        }
        "комиссар" -> {
            val comm = alive.find { it.role == "комиссар" }
            if (comm != null && isBlocked(comm)) {
                BlockedScreen(role = "комиссар", color = RolesData.BLUE, onContinue = { vm.advanceNightQueue() })
            } else {
                val targets = alive.filter { it.role != "комиссар" }
                NightTargetPicker(
                    title = "🔵 КОМИССАР просыпается",
                    subtitle = "Кого проверить?",
                    color = RolesData.BLUE,
                    targets = targets,
                    playerName = comm?.name ?: "",
                    onPick = { vm.setCommissionerCheck(it) },
                    onSkip = null
                )
            }
        }
        "ангел" -> {
            val angel = alive.find { it.role == "ангел" }
            if (angel != null && isBlocked(angel)) {
                BlockedScreen(role = "ангел", color = RolesData.WHITE, onContinue = { vm.advanceNightQueue() })
            } else if (dayNum < 2) {
                BlockedScreen(
                    role = "ангел", color = RolesData.WHITE,
                    message = "Воскрешение доступно только со 2-й ночи. Ангел ждёт...",
                    onContinue = { vm.advanceNightQueue() }
                )
            } else {
                val dead = state.gamePlayers.filter { !it.alive }
                if (dead.isEmpty()) {
                    BlockedScreen(role = "ангел", color = RolesData.WHITE, message = "Нет погибших для воскрешения.", onContinue = { vm.advanceNightQueue() })
                } else {
                    NightTargetPicker(
                        title = "😇 АНГЕЛ просыпается",
                        subtitle = "Кого воскресить?",
                        color = RolesData.WHITE,
                        targets = dead,
                        playerName = angel?.name ?: "",
                        onPick = { vm.setAngelRevive(it) },
                        onSkip = { vm.setAngelRevive(null) },
                        skipLabel = "Пропустить воскрешение"
                    )
                }
            }
        }
        "ведьма" -> {
            val witch = alive.find { it.role == "ведьма" }
            if (witch != null && isBlocked(witch)) {
                BlockedScreen(role = "ведьма", color = RolesData.PURPLE, onContinue = { vm.advanceNightQueue() })
            } else {
                val pickedA = remember { mutableStateOf<Player?>(null) }
                if (pickedA.value == null) {
                    NightTargetPicker(
                        title = "🧙 ВЕДЬМА просыпается",
                        subtitle = "Выберите ПЕРВОГО игрока для обмена ролями",
                        color = RolesData.PURPLE,
                        targets = alive.filter { it.role != "ведьма" },
                        playerName = witch?.name ?: "",
                        onPick = { pickedA.value = it },
                        onSkip = { vm.witchSkip() }
                    )
                } else {
                    NightTargetPicker(
                        title = "🧙 ВЕДЬМА просыпается",
                        subtitle = "Выберите ВТОРОГО игрока для обмена",
                        color = RolesData.PURPLE,
                        targets = alive.filter { it.role != "ведьма" && it.name != pickedA.value!!.name },
                        playerName = witch?.name ?: "",
                        onPick = { vm.witchSwapComplete(pickedA.value!!, it) },
                        onSkip = null
                    )
                }
            }
        }
        "маньяк" -> {
            val maniac = alive.find { it.role == "маньяк" }
            val targets = alive.filter { it.role != "маньяк" }
            NightTargetPicker(
                title = "🔪 МАНЬЯК просыпается",
                subtitle = "Выберите жертву",
                color = RolesData.YELLOW,
                targets = targets,
                playerName = maniac?.name ?: "",
                onPick = { vm.setManiacKill(it) },
                onSkip = null
            )
        }
        else -> {
            vm.advanceNightQueue()
        }
    }
}

@Composable
private fun NightTargetPicker(
    title: String,
    subtitle: String,
    color: Color,
    targets: List<Player>,
    playerName: String,
    playerLabel: String = "Игрок",
    extraNote: String? = null,
    onPick: (Player) -> Unit,
    onSkip: (() -> Unit)?,
    skipLabel: String = "Пропустить"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.08f), MafiaDark, Color(0xFF0A0A0F))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.copy(alpha = 0.12f))
                    .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text(title, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Spacer(Modifier.height(6.dp))
                Text("$playerLabel: $playerName", color = MafiaTextDim, fontSize = 13.sp)
                extraNote?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MafiaGold, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(subtitle, color = MafiaText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))

            targets.forEachIndexed { index, player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MafiaCardLight)
                        .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .clickable { onPick(player) }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = color, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(player.name, color = MafiaText, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.ChevronRight, null, tint = color.copy(alpha = 0.5f))
                }
            }

            onSkip?.let {
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MafiaCard)
                        .border(1.dp, MafiaOutline, RoundedCornerShape(14.dp))
                        .clickable { it() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(skipLabel, color = MafiaTextDim, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BlockedScreen(role: String, color: Color, message: String? = null, onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.05f), MafiaDark))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Filled.Block, null, tint = color, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(20.dp))
            Text(
                message ?: "${role.replaceFirstChar { it.uppercase() }} заблокирован(а) этой ночью",
                color = MafiaTextDim,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(Modifier.height(40.dp))
            GradientButton(
                text = "Продолжить",
                onClick = onContinue,
                colors = listOf(color.copy(alpha = 0.6f), color.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun WitchSwapBScreen(vm: GameViewModel, playerA: Player, alive: List<Player>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(RolesData.PURPLE.copy(alpha = 0.1f), MafiaDark)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Первый: ${playerA.name}", color = RolesData.PURPLE, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text("Выберите ВТОРОГО игрока:", color = MafiaText, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            alive.filter { it.name != playerA.name }.forEachIndexed { idx, player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MafiaCardLight)
                        .border(1.dp, RolesData.PURPLE.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .clickable { vm.witchSwapComplete(playerA, player) }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(RolesData.PURPLE.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) { Text("${idx + 1}", color = RolesData.PURPLE, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Text(player.name, color = MafiaText, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.ChevronRight, null, tint = RolesData.PURPLE.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun WitchRevealScreen(vm: GameViewModel, playerA: Player, playerB: Player, isSecond: Boolean) {
    val player = if (isSecond) playerB else playerA
    val roleInfo = RolesData.roles[player.role]
    val color = roleInfo?.color ?: MafiaText

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.1f), MafiaDark))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(roleInfo?.icon ?: "❓", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(player.name, color = MafiaText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("ваша роль:", color = MafiaTextDim, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            Text(player.role.replaceFirstChar { it.uppercase() }, color = color, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                    .background(MafiaCardLight).border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Text(roleInfo?.description ?: "", color = MafiaTextDim, fontSize = 14.sp, lineHeight = 22.sp)
                Spacer(Modifier.height(10.dp))
                Text("⚠️ Ведьма изменила вашу роль!", color = MafiaGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(28.dp))
            GradientButton(
                text = "Запомнил(а), передать устройство",
                onClick = {
                    if (isSecond) vm.witchRevealBDone()
                    else vm.witchRevealADone(playerA, playerB)
                },
                colors = listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.5f)),
                icon = Icons.Filled.Check,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun NightResultsScreen(vm: GameViewModel) {
    val state by vm.state.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF000010), MafiaDark)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            MafiaTopBar("📋 Итоги ночи")
            Spacer(Modifier.height(20.dp))

            if (state.nightEvents.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                        .background(MafiaCardLight).border(1.dp, MafiaOutline, RoundedCornerShape(20.dp)).padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Тихая ночь. Ничего не произошло.", color = MafiaTextDim, textAlign = TextAlign.Center)
                }
            } else {
                state.nightEvents.forEach { event ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp)).background(MafiaCardLight)
                            .border(1.dp, MafiaOutline, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(event, color = MafiaText, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            val alive = state.gamePlayers.filter { it.alive }
            Text("Живые (${alive.size}):", color = MafiaTextDim, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            alive.forEach { player ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    PulsingDot(MafiaRed.copy(alpha = 0f), 8.dp)
                    Text("• ${player.name}", color = MafiaText.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
            GradientButton(
                text = "Продолжить",
                onClick = { vm.afterNightResults() },
                colors = listOf(Color(0xFF1A237E), Color(0xFF283593)),
                icon = Icons.Filled.ArrowForward,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}