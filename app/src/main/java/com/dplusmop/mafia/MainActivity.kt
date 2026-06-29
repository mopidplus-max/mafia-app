package com.dplusmop.mafia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dplusmop.mafia.model.GameViewModel
import com.dplusmop.mafia.model.Screen
import com.dplusmop.mafia.ui.screens.*
import com.dplusmop.mafia.ui.theme.MafiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MafiaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MafiaApp()
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun MafiaApp(vm: GameViewModel = viewModel()) {
    when (vm.screen) {
        Screen.Menu -> MenuScreen(vm)
        Screen.Lobby -> LobbyScreen(vm)
        Screen.RolesInfo -> RolesInfoScreen(vm)
        Screen.RoleReveal -> RoleRevealScreen(vm)
        Screen.Night -> NightScreen(vm)
        Screen.NightSummary -> NightSummaryScreen(vm)
        Screen.SniperDay -> SniperDayScreen(vm)
        Screen.DayVote -> DayVoteScreen(vm)
        Screen.VoteResult -> VoteResultScreen(vm)
        Screen.GameOver -> GameOverScreen(vm)
    }
}
