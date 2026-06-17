package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleRed
import com.example.ui.theme.ApplePink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val songs by viewModel.songs.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "Listen Now",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                
                // Profile Avatar Gradient
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.sweepGradient(listOf(AppleRed, ApplePink)),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(2.dp)
                        .background(Color.Black, shape = androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "MS",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Recently Added Section (Horizontal Grid-like)
            item {
                SectionHeader("Recently Added", showSeeAll = true)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(songs.take(10)) { song ->
                        GridSongItem(
                            song = song,
                            onClick = { viewModel.onSongClick(song) }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // Trending Now Section (Vertical List)
            item {
                SectionHeader("Trending Now", showSeeAll = false)
            }

            items(songs) { song ->
                SongItem(
                    song = song,
                    onClick = { viewModel.onSongClick(song) }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, showSeeAll: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (showSeeAll) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.bodyMedium,
                color = ApplePink,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
