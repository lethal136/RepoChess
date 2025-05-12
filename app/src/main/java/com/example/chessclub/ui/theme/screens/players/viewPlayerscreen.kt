package com.example.chessclub.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.chessclub.data.PlayerViewModel
import com.example.chessclub.models.PlayerModel
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import com.example.chessclub.navigation.ROUTE_UPDATE_PLAYER

@Composable
fun ViewPlayersScreen(
    navController: NavHostController,
    playerViewModel: PlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    val players = remember { SnapshotStateList<PlayerModel>() }
    val selectedPlayer = remember { mutableStateOf(PlayerModel()) }
    var isDataLoaded by remember { mutableStateOf(false) }

    // Fetch players when the composable is created
    LaunchedEffect(Unit) {
        playerViewModel.viewPlayers(selectedPlayer, players, context) {
            isDataLoaded = true
        }
    }

    // Handle UI states
    when {
        !isDataLoaded -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Loading players...",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
        players.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No players found")
            }
        }
        else -> {
            PlayersList(
                players = players,
                navController = navController,
                playerViewModel = playerViewModel
            )
        }
    }
}

@Composable
fun PlayersList(
    players: SnapshotStateList<PlayerModel>,
    navController: NavHostController,
    playerViewModel: PlayerViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(players) { player ->
            PlayerCard(
                player = player,
                navController = navController,
                playerViewModel = playerViewModel
            )
        }
    }
}

@Composable
fun PlayerCard(
    player: PlayerModel,
    navController: NavHostController,
    playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Player Image with rounded corners
                Image(
                    painter = rememberAsyncImagePainter(model = player.imageUrl),
                    contentDescription = "${player.name}'s profile image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.CenterVertically)
                )

                // Player Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Username: ${player.username}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Nationality: ${player.nationality}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Gender: ${player.gender}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = player.desc,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { navController.navigate(ROUTE_UPDATE_PLAYER)},
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit player",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = { playerViewModel.deletePlayer(context, player.playerId, navController) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete player",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}