package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CardMasteryStatus
import com.example.model.Deck
import com.example.model.Flashcard
import com.example.ui.theme.*
import com.example.viewmodel.PsicoUiState

@Composable
fun FlashcardsScreen(
    uiState: PsicoUiState,
    onSelectDeck: (Deck) -> Unit,
    onFlipCard: () -> Unit,
    onToggleHint: () -> Unit,
    onRateCard: (CardMasteryStatus) -> Unit,
    onNextCard: () -> Unit,
    onPreviousCard: () -> Unit,
    onOpenAddMaterial: () -> Unit,
    onOpenAllDecks: () -> Unit = {},
    onPlayAudioReading: () -> Unit,
    onGenerateFromCustomText: (String, String) -> Unit = { _, _ -> }
) {
    var quickPromptText by remember { mutableStateOf("") }
    var isCustomTextDialogOpen by remember { mutableStateOf(false) }
    var customTextTopic by remember { mutableStateOf("") }
    var customTextContent by remember { mutableStateOf("") }

    val activeCards = remember(uiState.flashcards, uiState.activeDeck) {
        val deckFiltered = uiState.flashcards.filter {
            it.deckId == uiState.activeDeck.id ||
            it.category.equals(uiState.activeDeck.category, ignoreCase = true) ||
            uiState.activeDeck.title.contains(it.category, ignoreCase = true)
        }
        if (deckFiltered.isNotEmpty()) deckFiltered else uiState.flashcards
    }

    val currentCard: Flashcard? = remember(activeCards, uiState.currentCardIndex) {
        if (activeCards.isNotEmpty()) {
            activeCards.getOrNull(uiState.currentCardIndex % activeCards.size)
        } else null
    }

    // Dynamic Retention Metrics based on actual studied flashcards
    val totalAllFlashcards = uiState.flashcards.size
    val masteredCardsCount = uiState.flashcards.count { it.status == CardMasteryStatus.DOMINADO }
    val inStudyCardsCount = uiState.flashcards.count { it.status == CardMasteryStatus.BOM }
    val toReviewCardsCount = uiState.flashcards.count { it.status == CardMasteryStatus.DIFICIL }
    val studiedCount = masteredCardsCount + inStudyCardsCount + toReviewCardsCount
    val retentionPercentage = if (studiedCount > 0) {
        (((masteredCardsCount * 1.0f + inStudyCardsCount * 0.5f) / studiedCount) * 100).toInt()
    } else 0
    val masteredRatio = if (totalAllFlashcards > 0) (masteredCardsCount * 100 / totalAllFlashcards) else 0
    val inStudyRatio = if (totalAllFlashcards > 0) (inStudyCardsCount * 100 / totalAllFlashcards) else 0
    val toReviewRatio = if (totalAllFlashcards > 0) (toReviewCardsCount * 100 / totalAllFlashcards) else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flashcards_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Métricas de Retenção & Algoritmo SM-2 (100% Dinâmico)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("retention_metrics_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Métricas de Retenção",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SageSecondaryContainer
                        ) {
                            Text(
                                text = "Algoritmo SM-2 Ativo",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSageSecondaryContainer,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL NO BARALHO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = SageSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$totalAllFlashcards",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "flashcards",
                                    style = MaterialTheme.typography.labelMedium.copy(color = SageSecondary)
                                )
                            }
                            Text(
                                text = if (studiedCount > 0) "🧠 Curva baseada em $studiedCount cards estudados" else "🧠 Avalie os cards abaixo para calibrar a retenção",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextOnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Circular Gauge Dinâmico
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(SageSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerLowest),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$retentionPercentage%",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PinePrimary
                                        )
                                    )
                                    Text(
                                        text = "retenção",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            color = SageSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Segmented dynamic stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainerLow,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Dominados", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = SageSecondary))
                                Text("$masteredCardsCount ($masteredRatio%)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PinePrimary))
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainerLow,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Em Estudo", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = SageSecondary))
                                Text("$inStudyCardsCount ($inStudyRatio%)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PinePrimary))
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PeachTertiaryFixed.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Revisar", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = OnPeachTertiaryVariant))
                                Text("$toReviewCardsCount ($toReviewRatio%)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PeachTertiary))
                            }
                        }
                    }
                }
            }
        }

        // 2. Baralhos em Aberto Carousel
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Layers, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Baralhos em Aberto",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SageSecondaryContainer,
                        modifier = Modifier.clickable { onOpenAllDecks() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.GridView, contentDescription = null, tint = OnSageSecondaryContainer, modifier = Modifier.size(13.dp))
                            Text(
                                text = "Ver todos (${uiState.decks.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSageSecondaryContainer,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.decks) { deck ->
                        val isSelected = uiState.activeDeck.id == deck.id
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .clickable { onSelectDeck(deck) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SurfaceContainerHigh else SurfaceContainerLowest
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) SageSecondaryContainer else SurfaceContainerLow
                                    ) {
                                        Text(
                                            text = deck.category,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) OnSageSecondaryContainer else SageSecondary
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "${deck.totalCards} cards",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SageSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Text(
                                    text = deck.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary
                                    ),
                                    maxLines = 2
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Meta diária: ${deck.dailyGoal}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                                    )
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.PlayCircle else Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = PinePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Sessão Prática Ativa (Cartão Interativo 3D Flip)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header with counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (activeCards.isNotEmpty()) {
                            "Cartão ${(uiState.currentCardIndex % activeCards.size) + 1} de ${activeCards.size} • ${uiState.activeDeck.category}"
                        } else {
                            "0 cartões • ${uiState.activeDeck.category}"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onPlayAudioReading, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Ouvir leitura", tint = PinePrimary)
                        }
                        IconButton(onClick = { /* bookmark card */ }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Salvar cartão", tint = SageSecondary)
                        }
                    }
                }

                // 3D-styled Flashcard Box
                currentCard?.let { card ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("interactive_flashcard"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Top Meta Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SageSecondaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = "Gerado por IA via Paulo Dalgalarrondo",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = OnSageSecondaryContainer,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                                Icon(Icons.Default.Flip, contentDescription = null, tint = SageSecondary)
                            }

                            // Question
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = card.category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SageSecondary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = card.question,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary,
                                        lineHeight = 22.sp
                                    )
                                )
                            }

                            // Pedagogic AI Hint
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleHint() },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = PeachTertiaryFixed.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = PeachTertiary, modifier = Modifier.size(16.dp))
                                            Text(
                                                text = "Dica Pedagógica da IA",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = PeachTertiary
                                                )
                                            )
                                        }
                                        Icon(
                                            imageVector = if (uiState.isHintExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = PeachTertiary
                                        )
                                    }
                                    AnimatedVisibility(visible = uiState.isHintExpanded) {
                                        Text(
                                            text = card.hint,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = OnPeachTertiaryVariant,
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp
                                            ),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Answer Revealed
                            AnimatedVisibility(visible = uiState.isCardFlipped) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SurfaceContainerLow,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(16.dp))
                                            Text(
                                                text = "Resposta Padrão-Ouro",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = PinePrimary
                                                )
                                            )
                                        }
                                        Text(
                                            text = card.answer,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextOnSurface,
                                                lineHeight = 20.sp
                                            )
                                        )
                                        Text(
                                            text = "📚 ${card.citation}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SageSecondary,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }

                            // Flip Trigger Button
                            Button(
                                onClick = onFlipCard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("flip_flashcard_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                            ) {
                                Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (uiState.isCardFlipped) "Ocultar Resposta e Refletir" else "Toque para ver a Resposta",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Rating SM-2 Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onRateCard(CardMasteryStatus.DIFICIL) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorContainerRed, contentColor = ErrorRed),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Difícil", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("10 min", fontSize = 9.sp)
                                    }
                                }
                                Button(
                                    onClick = { onRateCard(CardMasteryStatus.BOM) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = PinePrimary),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Bom", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("1 dia", fontSize = 9.sp)
                                    }
                                }
                                Button(
                                    onClick = { onRateCard(CardMasteryStatus.DOMINADO) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SageSecondaryContainer, contentColor = OnSageSecondaryContainer),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Dominado", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("4 dias", fontSize = 9.sp)
                                    }
                                }
                            }

                            // Next & Previous Navigation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = onPreviousCard) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Anterior", fontSize = 12.sp)
                                }
                                TextButton(onClick = onNextCard) {
                                    Text("Próximo", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                } ?: run {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("empty_flashcards_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(SageSecondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Layers, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(28.dp))
                            }
                            Text(
                                text = "Baralho vazio no momento",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "Importe fotos de cadernos, apostilas ou anotações na aba Biblioteca para que a IA gere flashcards automáticos desta disciplina!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextOnSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            )
                            Button(
                                onClick = onOpenAddMaterial,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Importar Material desta Matéria")
                            }
                        }
                    }
                }
            }
        }

        // 4. Criação Rápida de Baralhos
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoStories, contentDescription = null, tint = PinePrimary)
                        Text(
                            text = "Extração por Inteligência Artificial",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                    }
                    Text(
                        text = "Transforme PDFs, artigos científicos do SciELO ou suas anotações de aula em pares de pergunta/resposta otimizados para repetição espaçada.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextOnSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenAddMaterial,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Enviar PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        FilledTonalButton(
                            onClick = { isCustomTextDialogOpen = true },
                            modifier = Modifier.weight(1f).testTag("create_custom_flashcards_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = SurfaceContainerLowest)
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Criar com Texto", fontSize = 11.sp, color = PinePrimary)
                        }
                    }

                    // Quick prompt input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = quickPromptText,
                            onValueChange = { quickPromptText = it },
                            placeholder = { Text("Ex: 'Critérios diagnósticos de TDAH no DSM-5-TR'...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).testTag("flashcard_prompt_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerLowest,
                                unfocusedContainerColor = SurfaceContainerLowest
                            ),
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                if (quickPromptText.isNotBlank()) {
                                    val text = quickPromptText.trim()
                                    quickPromptText = ""
                                    onGenerateFromCustomText(
                                        "O estudo aprofundado sobre $text exige a compreensão de suas bases conceituais, critérios diagnósticos e formulação psicoterapêutica.",
                                        text
                                    )
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PinePrimary)
                                .testTag("submit_flashcard_prompt_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Gerar Flashcards", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Modal: Criar Flashcards com Texto Próprio do Aluno
    if (isCustomTextDialogOpen) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { isCustomTextDialogOpen = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.82f)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("custom_text_flashcards_dialog"),
                color = SurfaceContainerLowest,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(PinePrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PinePrimaryFixed)
                            }
                            Column {
                                Text(
                                    text = "Criar Flashcards com Texto",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary
                                    )
                                )
                                Text(
                                    text = "Cole resumos, páginas ou anotações para gerar perguntas e respostas",
                                    style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                                )
                            }
                        }
                        IconButton(
                            onClick = { isCustomTextDialogOpen = false },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextOnSurfaceVariant)
                        }
                    }

                    HorizontalDivider(color = OutlineVariant)

                    OutlinedTextField(
                        value = customTextTopic,
                        onValueChange = { customTextTopic = it },
                        label = { Text("Nome da Matéria ou Tema") },
                        placeholder = { Text("Ex: Psicopatologia, TCC, Rogers, Freud...") },
                        modifier = Modifier.fillMaxWidth().testTag("custom_flashcards_topic_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = customTextContent,
                        onValueChange = { customTextContent = it },
                        label = { Text("Texto ou Resumo de Estudo") },
                        placeholder = {
                            Text(
                                "Cole aqui o conteúdo que você quer fixar. O app analisará as teses centrais e criará perguntas, dicas e respostas padrão-ouro para você se autoavaliar!"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("custom_flashcards_content_input"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 12
                    )

                    Button(
                        onClick = {
                            if (customTextContent.isNotBlank()) {
                                onGenerateFromCustomText(customTextContent, customTextTopic)
                                isCustomTextDialogOpen = false
                                customTextContent = ""
                                customTextTopic = ""
                            }
                        },
                        enabled = customTextContent.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_create_custom_flashcards"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gerar Perguntas & Respostas com IA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
