package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PodcastAudio
import com.example.ui.theme.*

@Composable
fun AudioPlayerBar(
    audio: PodcastAudio?,
    isPlaying: Boolean,
    currentPositionSeconds: Int,
    playbackSpeed: Float,
    onTogglePlayPause: () -> Unit,
    onStop: () -> Unit,
    onClose: () -> Unit,
    onNextTrack: () -> Unit,
    onPreviousTrack: () -> Unit,
    onChangeTrackClick: () -> Unit,
    onSeekBy: (Int) -> Unit,
    onCycleSpeed: () -> Unit,
    onCreateNoteFromAudio: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = audio != null,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        if (audio == null) return@AnimatedVisibility

        val progress = if (audio.totalDurationSeconds > 0) {
            (currentPositionSeconds.toFloat() / audio.totalDurationSeconds).coerceIn(0f, 1f)
        } else 0f

        val currentMin = currentPositionSeconds / 60
        val currentSec = currentPositionSeconds % 60
        val totalMin = audio.totalDurationSeconds / 60
        val totalSec = audio.totalDurationSeconds % 60
        val currentTimeFormatted = "%02d:%02d".format(currentMin, currentSec)
        val totalTimeFormatted = "%02d:%02d".format(totalMin, totalSec)

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("audio_player_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header row: Status + Tag + Actions (Trocar, Fechar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isPlaying) PeachTertiaryFixed else SurfaceContainerHigh
                        ) {
                            Text(
                                text = if (isPlaying) "EM REPRODUÇÃO" else "PAUSADO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPlaying) OnPeachTertiaryFixed else TextOnSurfaceVariant
                                ),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = audio.tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SageSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }

                    // Trocar Audiobook + Fechar Botão
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SageSecondaryContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onChangeTrackClick() }
                                .testTag("change_audiobook_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QueueMusic,
                                    contentDescription = "Trocar Audiobook",
                                    tint = OnSageSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Trocar",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = OnSageSecondaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Close button (Fechar o Audiobook)
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("close_audiobook_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar Audiobook",
                                tint = TextOnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Audio Info & Main Play/Pause/Stop Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SageSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = null,
                                tint = PinePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = audio.episodeTitle,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = audio.speakerAndDiscipline,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextOnSurfaceVariant,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Main Controls Cluster: Previous, Play/Pause, Next, Stop
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Previous Track
                        IconButton(
                            onClick = onPreviousTrack,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("audio_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Faixa Anterior",
                                tint = PinePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Play/Pause button
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(PinePrimary)
                                .testTag("audio_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pausar áudio" else "Reproduzir áudio",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Next Track
                        IconButton(
                            onClick = onNextTrack,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("audio_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Próxima Faixa",
                                tint = PinePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Stop button (Parar completamente)
                        IconButton(
                            onClick = onStop,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                                .testTag("audio_stop_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Parar Audiobook",
                                tint = PinePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Waveform / Progress bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PinePrimary,
                        trackColor = SurfaceContainerHigh
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentTimeFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextOnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = totalTimeFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextOnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Controls row: Speed, -15s, +30s, Note Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledTonalButton(
                            onClick = onCycleSpeed,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SurfaceContainerHigh,
                                contentColor = PinePrimary
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        IconButton(
                            onClick = { onSeekBy(-15) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Retroceder 15s",
                                tint = SageSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { onSeekBy(30) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward30,
                                contentDescription = "Avançar 30s",
                                tint = SageSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Button to create note from current audio point
                    Button(
                        onClick = {
                            val topic = "${audio.episodeTitle} (Trecho em $currentTimeFormatted)"
                            onCreateNoteFromAudio(topic, audio.tag)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SageSecondaryContainer,
                            contentColor = OnSageSecondaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("save_audio_snippet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Criar Nota",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
