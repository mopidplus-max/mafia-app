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







































@Composable
fun NightScreen(vm: GameViewModel) {
    val engine = vm.engine ?: return
    val steps = vm.nightSteps
    if (steps.isEmpty() || vm.nightStepIndex >= steps.size) return
    val step = steps[vm.nightStepIndex]
    val results = vm.nightResults

    // Каждое секретное действие сначала требует "передать устройство", кроме общих экранов
    var passed by remember(vm.nightStepIndex) { mutableStateOf(false) }

    when (step) {
        is NightStep.CityFallsAsleep -> {
            NightStepScaffold("Город засыпает...", MafiaGold, Icons.Filled.NightlightRound) {
                Spacer(Modifier.weight(1f))
                Text(
                    "Ночь ${engine.dayNumber}",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.weight(1f))
                PrimaryActionButton(text = "Продолжить", onClick = { vm.advanceNightStep() })
            }
        }

        is NightStep.JunkieTurn -> {
            if (!passed) {
                PassDeviceScreen(playerName = step.junkie.name, onReady = { passed = true })
            } else {
                NightStepScaffold("Наркоман", com.dplusmop.mafia.ui.theme.MafiaGreen, Icons.Filled.NightlightRound) {
                    val targets = engine.alivePlayers().filter { it.name != step.junkie.name }
                    TargetPicker(
                        label = "Кому подсыпать порошок?",
                        targets = targets,
                        accent = com.dplusmop.mafia.ui.theme.MafiaGreen,
                        allowSkip = true,
                        skipLabel = "Не использовать сегодня"
                    ) { target ->
                        if (target != null) {
                            results.junkieTarget = target
                            results.junkieBlocked.add(target.name)
                        }
                        vm.advanceNightStep()
                    }
                }
            }
        }

        is NightStep.BeautyTurn -> {
            if (!passed) {
                PassDeviceScreen(playerName = step.beauty.name, onReady = { passed = true })
            } else {
                NightStepScaffold("Красотка", androidx.compose.ui.graphics.Color(0xFFEC4899), Icons.Filled.NightlightRound) {
                    if (results.isBlocked(step.beauty)) {
                        Text(
                            "Красотка под воздействием наркомана — пропускает ход!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        PrimaryActionButton(text = "Продолжить", onClick = { vm.advanceNightStep() })
                    } else {
                        val targets = engine.alivePlayers().filter { it.name != step.beauty.name }
                        TargetPicker(
                            label = "К кому пойти этой ночью?",
                            targets = targets,
                            accent = androidx.compose.ui.graphics.Color(0xFFEC4899),
                            allowSkip = true,
                        ) { target ->
                            if (target != null) {
                                results.beautyTarget = target
                                results.beautyBlocked.add(target.name)
                            }
                            vm.advanceNightStep()
                        }
                    }
                }
            }
        }

        is NightStep.MafiaTurn -> {
            val allAwake = step.mafia + listOfNotNull(step.advocate)
            if (!passed) {
                val title = if (allAwake.size == 1) allAwake.first().name
                else "Мафия: ${allAwake.joinToString(", ") { it.name }}"
                PassDeviceScreen(
                    playerName = title,
                    subtitle = if (allAwake.size == 1) "Передайте устройство этому игроку" else "Передайте устройство этим игрокам",
                    onReady = { passed = true }
                )
            } else {
                NightStepScaffold("Мафия", MafiaRed, Icons.Filled.NightlightRound) {
                    if (step.advocate != null) {
                        Surface(shape = RoundedCornerShape(14.dp), color = MafiaRed.copy(alpha = .12f), modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Адвокат приглашён и знает состав мафии.",
                                modifier = Modifier.padding(14.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                    }
                    val activeMafia = step.mafia.filter { !results.isBlocked(it) }
                    if (activeMafia.isEmpty()) {
                        Text(
                            "Мафия заблокирована этой ночью — убийство не произойдёт.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        PrimaryActionButton(text = "Продолжить", onClick = { vm.advanceNightStep() })
                    } else {
                        val targets = engine.alivePlayers().filter {
                            RolesData.info(it.role).team != Team.MAFIA && it.role != Role.ADVOCATE
                        }
                        TargetPicker(
                            label = "Кого мафия убивает этой ночью?",
                            targets = targets,
                            accent = MafiaRed,
                        ) { target ->
                            results.mafiaKillTarget = target
                            vm.advanceNightStep()
                        }
                    }
                }
            }
        }

        is NightStep.DoctorTurn -> {
            if (!passed) {
                PassDeviceScreen(playerName = step.doctor.name, onReady = { passed = true })
            } else {
                NightStepScaffold("Доктор", com.dplusmop.mafia.ui.theme.MafiaGreen, Icons.Filled.NightlightRound) {
                    if (results.isBlocked(step.doctor)) {
                        Text(
                            "Доктор заблокирован этой ночью — лечение не произойдёт.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        PrimaryActionButton(text = "Продолжить", onClick = { vm.advanceNightStep() })
                    } else {
                        TargetPicker(
                            label = "Кого вылечить этой ночью?",
                            targets = engine.alivePlayers(),
                            accent = com.dplusmop.mafia.ui.theme.MafiaGreen,
                        ) { target ->
                            results.doctorHealTarget = target
                            vm.advanceNightStep()
                        }
                    }
                }
            }
        }

        is NightStep.CommissionerTurn -> {
            if (!passed) {
                PassDeviceScreen(playerName = step.commissioner.name, onReady = { passed = true })
            } else {
                NightStepScaffold("Комиссар", com.dplusmop.mafia.ui.theme.MafiaBlue, Icons.Filled.NightlightRound) {
                    if (results.isBlocked(step.commissioner)) {
                        Text(
                            "Комиссар заблокирован этой ночью — проверка не произойдёт.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        PrimaryActionButton(text = "Продолжить", onClick = { vm.advanceNightStep() })
                    } else {
                        val targets = engine.alivePlayers().filter { it.name != step.commissioner.name }
                        TargetPicker(
                            label = "Кого проверить этой ночью?",
                            targets = targets,
                            accent = com.dplusmop.mafia.ui.theme.MafiaBlue,
                        ) { target ->
                            results.commissionerCheckTarget = target
                            if (target != null) {
                                val isMafia = RolesData.info(target.role).team == Team.MAFIA && target.role != Role.DON
                                results.commissionerResultIsMafia = isMafia
                            }
                            vm.advanceNightStep()
                        }
                    }
                }
            }
        }

        is NightStep.AngelTurn -> {
            if (!passed) {
                PassDeviceScreen(playerName = step.angel.name, onReady = { passed = true })
            } else {
                NightStepScaffold("Ангел", androidx.compose.ui.graphics.Color(0xFFF3F4F6), Icons.Filled.NightlightRound) {
                    if (results.isBlocked(step.angel)) {
                        Text(
                            "Ангел заблокирован этой ночью — воскрешение не произойдёт.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        PrimaryActionButton(text = "Продолжить", onClick = { vm.advanceNightStep() })
                    } else if (!step.canRevive) {
                        Text(
                            "Воскрешение доступно только начиная со 2-й ночи. Ангел терпеливо ждёт...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        PrimaryActionButton(text = "Продолжить", onClick = { vm.advanceNightStep() })
                    } else {
                        TargetPicker(
                            label = "Кого воскресить?",
                            targets = engine.deadPlayers(),
                            accent = androidx.compose.ui.graphics.Color(0xFFF3F4F6),
                            allowSkip = true,
                            skipLabel = "Не воскрешать сегодня"
                        ) { target ->
                            results.angelReviveTarget = target
                            vm.advanceNightStep()
                        }
                    }
                }
            }
        }

        is NightStep.WitchTurn -> {
            if (!passed) {
                PassDeviceScreen(playerName = step.witch.name, onReady = { passed = true })
            } else {
                NightStepScaffold("Ведьма", androidx.compose.ui.graphics.Color(0xFFA855F7), Icons.Filled.NightlightRound) {
                    if (results.isBlocked(step.witch)) {
                        Text(
                            "Ведьма заблокирована этой ночью — обмен не произойдёт.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        PrimaryActionButton(text = "Продолжить", onClick = { vm.advanceNightStep() })
                    } else if (vm.witchFirstPick == null) {
                        Text(
                            "Использовать обмен ролями между двумя игроками? Эта способность одноразовая на всю игру.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(20.dp))
                        TargetPicker(
                            label = "Выберите ПЕРВОГО игрока для обмена:",
                            targets = engine.alivePlayers(),
                            accent = androidx.compose.ui.graphics.Color(0xFFA855F7),
                            allowSkip = true,
                            skipLabel = "Не использовать сегодня"
                        ) { first ->
                            if (first == null) {
                                vm.advanceNightStep()
                            } else {
                                vm.witchFirstPick = first
                            }
                        }
                    } else {
                        val first = vm.witchFirstPick!!
                        val remaining = engine.alivePlayers().filter { it.name != first.name }
                        TargetPicker(
                            label = "Выберите ВТОРОГО игрока для обмена с ${first.name}:",
                            targets = remaining,
                            accent = androidx.compose.ui.graphics.Color(0xFFA855F7),
                        ) { second ->
                            if (second != null) {
                                val tmp = first.role
                                first.role = second.role
                                second.role = tmp
                                engine.witchUsed = true
                                results.witchEvents.add(
                                    NightEvent("🧙 Ведьма поменяла роли ${first.name} и ${second.name}!", NightEventIcon.SWAP)
                                )
                            }
                            vm.witchFirstPick = null
                            vm.advanceNightStep()
                        }
                    }
                }
            }
        }

        is NightStep.WitchSwapPickFirst, is NightStep.WitchSwapPickSecond -> {
            // обрабатывается полностью внутри WitchTurn выше через vm.witchFirstPick
            vm.advanceNightStep()
        }

        is NightStep.ManiacTurn -> {
            if (!passed) {
                PassDeviceScreen(playerName = step.maniac.name, onReady = { passed = true })
            } else {
                NightStepScaffold("Маньяк", androidx.compose.ui.graphics.Color(0xFFF59E0B), Icons.Filled.NightlightRound) {
                    if (results.isBlocked(step.maniac)) {
                        Text(
                            "Маньяк заблокирован этой ночью — убийство не произойдёт.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        PrimaryActionButton(text = "Продолжить", onClick = { vm.advanceNightStep() })
                    } else {
                        val targets = engine.alivePlayers().filter { it.name != step.maniac.name }
                        TargetPicker(
                            label = "Кого убивает маньяк этой ночью?",
                            targets = targets,
                            accent = androidx.compose.ui.graphics.Color(0xFFF59E0B),
                        ) { target ->
                            results.maniacKillTarget = target
                            vm.advanceNightStep()
                        }
                    }
                }
            }
        }

        is NightStep.Dawn -> {
            NightStepScaffold("Город просыпается...", MafiaGold, Icons.Filled.WbSunny) {
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.WbSunny, contentDescription = null, tint = MafiaGold, modifier = Modifier.size(72.dp).align(Alignment.CenterHorizontally))
                Spacer(Modifier.weight(1f))
                PrimaryActionButton(text = "Узнать итоги ночи", onClick = { vm.advanceNightStep() })
            }
        }
    }
}
