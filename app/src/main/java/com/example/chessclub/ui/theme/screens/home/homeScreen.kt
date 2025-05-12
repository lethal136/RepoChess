package com.example.chessclub.ui.theme.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import  com.example.chessclub.R
import com.example.chessclub.data.AuthViewModel
import com.example.chessclub.navigation.ROUTE_ADD_PLAYER


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val selectedItem = remember { mutableStateOf(0) }
    val context = LocalContext.current
    Scaffold(
        bottomBar = { NavigationBar(containerColor = Color.Black){
            NavigationBarItem(
                selected = selectedItem.value == 0,
                onClick = {
                    selectedItem.value = 0
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto: info@emobilis.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Inquiry")
                        putExtra(Intent.EXTRA_TEXT, "Hello confirm to me my registration fee balance")
                    }
                    context.startActivity(intent)
                },
                icon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
                label = { Text(text = "Email")},
                alwaysShowLabel = true
            )
            NavigationBarItem(
                selected = selectedItem.value == 1,
                onClick = {selectedItem.value = 1
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data=Uri.parse("tel:0705500113")
                    }
                    context.startActivity(intent)
                },
                icon = { Icon(Icons.Filled.Phone, contentDescription = "phone") },
                label = { Text(text = "call")},
                alwaysShowLabel = true
            )
            NavigationBarItem(
                selected = selectedItem.value == 2,
                onClick = {selectedItem.value = 2
                    val sendIntent = Intent().apply {
                        action=Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT,"Download app here: https://www.dowmload.com")
                    }
                    val shareIntent = Intent.createChooser(sendIntent,null)
                    context.startActivity(shareIntent)
                },
                icon = { Icon(Icons.Filled.Share, contentDescription = "share") },
                label = { Text(text = "Share")},
                alwaysShowLabel = true
            )
        } }
    )
    { innerPadding ->

        Box() {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "background image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.padding(innerPadding)

            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = { Text(text = "eLite") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
//
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                    IconButton(onClick = {authViewModel.logout(navController, context)}) {
                        Icon(
                            imageVector = Icons.Filled.AccountBox,
                            contentDescription = "Logout"
                        )
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
            Row {
                Card(
                    modifier = Modifier
                        .size(width = 300.dp, height = 200.dp) // Increase size
                        .fillMaxSize() // Fill the entire screen

                        .clickable {
                        navController.navigate(ROUTE_ADD_PLAYER)
                    },
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
                    colors = CardDefaults.cardColors(Color.Black)
                )
                {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(100.dp)
                            .padding(25.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Players",
                            color = Color.White
                        )
                    }
                }
//                Card(
//                    modifier = Modifier.padding(10.dp),
//                    shape = RoundedCornerShape(20.dp),
//                    elevation = CardDefaults.cardElevation(10.dp),
//                    colors = CardDefaults.cardColors(Color.Black)
//                )
//                {
//                    Box(
//                        modifier = Modifier
//                            .height(100.dp)
//                            .padding(25.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "About Chess",
//                            color = Color.White
//                        )
//                    }
//                }
//                Card(
//                    modifier = Modifier.padding(10.dp),
//                    shape = RoundedCornerShape(20.dp),
//                    elevation = CardDefaults.cardElevation(10.dp),
//                    colors = CardDefaults.cardColors(Color.Black)
//                )
//                {
//                    Box(
//                        modifier = Modifier
//                            .height(100.dp)
//                            .padding(25.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "Fee",
//                            color = Color.White
//                        )
//                    }
//
//                }
            }
        }
    }
}
