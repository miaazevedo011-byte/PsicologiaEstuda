package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Author
import com.example.ui.theme.*

@Composable
fun AuthorDetailDialog(
    author: Author?,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onFilterVerbetesByAuthor: (String) -> Unit = {}
) {
    if (!isOpen || author == null) return

    val context = LocalContext.current
    var selectedBookForReading by remember { mutableStateOf<String?>(null) }

    // Extended bibliography & key concepts based on author
    val (bibliography, keyTheories, famousQuote) = when {
        author.name.contains("Freud", ignoreCase = true) -> Triple(
            listOf(
                "A Interpretação dos Sonhos (1900)",
                "Três Ensaios sobre a Teoria da Sexualidade (1905)",
                "O Ego e o Id (1923)",
                "O Mal-Estar na Civilização (1930)"
            ),
            listOf(
                "Primeira e Segunda Tópicas (Inconsciente, Pré-Consciente, Id, Ego, Superego)",
                "Mecanismos de Defesa (Repressão, Sublimação, Projeção)",
                "Teoria das Pulsões (Eros & Tânatos)",
                "Transferência e Contratransferência Clínica"
            ),
            "\"A voz da razão é suave, mas não descansa enquanto não obtém uma audiência.\""
        )
        author.name.contains("Vygotsky", ignoreCase = true) -> Triple(
            listOf(
                "A Formação Social da Mente (1930)",
                "Pensamento e Linguagem (1934)",
                "Psicologia da Arte (1925)"
            ),
            listOf(
                "Zona de Desenvolvimento Proximal (ZDP)",
                "Mediação Semiótica e Instrumentos Psicológicos",
                "Internalização das Funções Psicológicas Superiores",
                "Relação entre Pensamento, Fala e Cultura"
            ),
            "\"O que a criança pode fazer hoje com o auxílio de adultos, poderá fazer amanhã por si mesma.\""
        )
        author.name.contains("Rogers", ignoreCase = true) -> Triple(
            listOf(
                "Tornar-se Pessoa (1961)",
                "Terapia Centrada no Cliente (1951)",
                "Um Jeito de Ser (1980)"
            ),
            listOf(
                "Tendência Atualizante do Organismo Humano",
                "Consideração Positiva Incondicional",
                "Empatia Profunda e Compreensão Fenomenológica",
                "Congruência e Autenticidade do Terapeuta"
            ),
            "\"O curioso paradoxo é que quando me aceito exatamente como sou, posso então mudar.\""
        )
        author.name.contains("Skinner", ignoreCase = true) -> Triple(
            listOf(
                "O Comportamento dos Organismos (1938)",
                "Ciência e Comportamento Humano (1953)",
                "Walden II (1948)",
                "Sobre o Behaviorismo (1974)"
            ),
            listOf(
                "Condicionamento Operante e Tríplice Contingência",
                "Esquemas de Reforço Contínuo e Intermitente",
                "Controle de Estímulos e Discriminação",
                "Crítica à Punição Aversiva e Análise Funcional"
            ),
            "\"A educação é o que sobrevive depois que tudo o que foi aprendido foi esquecido.\""
        )
        author.name.contains("Piaget", ignoreCase = true) -> Triple(
            listOf(
                "A Construção do Real na Criança (1937)",
                "O Nascimento da Inteligência na Criança (1936)",
                "Psicologia e Pedagogia (1969)"
            ),
            listOf(
                "Estágios de Desenvolvimento Cognitivo (Sensório-Motor, Pré-Operatório, etc.)",
                "Assimilação, Acomodação e Equilibração Majorante",
                "Epistemologia Genética",
                "Moralidade e Autonomia Infantil"
            ),
            "\"O principal objetivo da educação é criar homens capazes de fazer coisas novas e não simplesmente repetir o que outras gerações fizeram.\""
        )
        author.name.contains("Beck", ignoreCase = true) -> Triple(
            listOf(
                "Terapia Cognitiva da Depressão (1979)",
                "Terapia Cognitiva dos Transtornos de Personalidade (1990)",
                "Prisioneiros do Ódio (1999)"
            ),
            listOf(
                "Tríade Cognitiva (Visão de si, do mundo e do futuro)",
                "Distorções Cognitivas (Catastrofização, Polarização, etc.)",
                "Crenças Centrais, Intermediárias e Pensamentos Automáticos",
                "Reestruturação Cognitiva e Registro de Pensamentos Disfuncionais (RPD)"
            ),
            "\"Não são as situações em si que determinam como nos sentimos, mas a forma como as interpretamos.\""
        )
        author.name.contains("Maslow", ignoreCase = true) -> Triple(
            listOf(
                "Motivação e Personalidade (1954)",
                "Introdução à Psicologia do Ser (1962)",
                "A Psicologia da Ciência (1966)"
            ),
            listOf(
                "Hierarquia das Necessidades Humanas (Pirâmide de Maslow)",
                "Experiências de Pico (Peak Experiences)",
                "Motivação de Deficiência vs. Motivação do Ser (D-Needs vs B-Needs)",
                "Características dos Indivíduos Autorrealizados"
            ),
            "\"O que um homem pode ser, ele deve ser. Essa necessidade podemos chamar de autorrealização.\""
        )
        author.name.contains("Perls", ignoreCase = true) -> Triple(
            listOf(
                "Gestalt-Terapia (1951)",
                "A Abordagem Gestáltica & Testemunha Ocular da Terapia (1973)",
                "Escarafunchando o Lixo (1969)"
            ),
            listOf(
                "Awareness (Tomada de Consciência Imediata)",
                "O Aqui-e-Agora e a Autorregulação Organísmica",
                "Ciclo de Contato e Interrupções (Retroflexão, Introjeção)",
                "Integração de Polaridades e Fechamento de Gestalten"
            ),
            "\"Amigo, não tenha medo dos seus erros. Os erros podem ser muito proveitosos. Não os enterre: estude-os com clareza.\""
        )
        author.name.contains("Frankl", ignoreCase = true) -> Triple(
            listOf(
                "Em Busca de Sentido (1946)",
                "A Vontade de Sentido (1969)",
                "Teoria e Terapia das Neuroses (1956)"
            ),
            listOf(
                "Vontade de Sentido como Motivação Primária",
                "Vazio Existencial e Neurose Noogênica",
                "Três Vias de Sentido: Criação, Vivência e Atitude",
                "A Tríade Trágica: Sofrimento, Culpa e Morte"
            ),
            "\"Quem tem um 'porquê' pelo qual viver pode suportar quase qualquer 'como'.\""
        )
        author.name.contains("Jung", ignoreCase = true) -> Triple(
            listOf(
                "O Homem e seus Símbolos (1964)",
                "Tipos Psicológicos (1921)",
                "Os Arquétipos e o Inconsciente Coletivo (1934)",
                "Psicologia do Inconsciente (1943)"
            ),
            listOf(
                "Inconsciente Coletivo e Arquétipos Universais",
                "Processo de Individuação e o Arquétipo do Self",
                "Persona, Sombra, Anima e Animus",
                "Sincronicidade como Princípio de Conexões Acausais"
            ),
            "\"Quem olha para fora sonha; quem olha para dentro desperta.\""
        )
        author.name.contains("Winnicott", ignoreCase = true) -> Triple(
            listOf(
                "O Brincar e a Realidade (1971)",
                "A Criança e o seu Mundo (1964)",
                "Da Pediatria à Psicanálise (1958)"
            ),
            listOf(
                "Objeto e Fenômenos Transicionais",
                "Mãe Suficientemente Boa e Ambiente Facilitador",
                "Holding, Handling e Apresentação do Objeto",
                "Verdadeiro Self vs. Falso Self Adaptativo"
            ),
            "\"É no brincar, e somente no brincar, que o indivíduo, criança ou adulto, pode ser criativo e utilizar sua personalidade integral.\""
        )
        author.name.contains("Bowlby", ignoreCase = true) -> Triple(
            listOf(
                "Apego e Perda: Vol 1 Apego (1969)",
                "Apego e Perda: Vol 2 Separação (1973)",
                "Uma Base Segura (1988)"
            ),
            listOf(
                "Apego como Sistema Comportamental Inato de Sobrevivência",
                "Padrões de Apego Seguro, Ansioso-Ambivalente e Evitativo",
                "A Base Segura para Exploração do Mundo",
                "Modelos Operacionais Internos nas Relações Adultas"
            ),
            "\"O que não pode ser comunicado à mãe, não pode ser comunicado a si mesmo.\""
        )
        author.name.contains("Klein", ignoreCase = true) -> Triple(
            listOf(
                "A Psicanálise de Crianças (1932)",
                "Amor, Culpa e Reparação (1937)",
                "Inveja e Gratidão (1957)"
            ),
            listOf(
                "Posição Esquizoparanóide (Cisão, Objeto Parcial, Projeção)",
                "Posição Depressiva (Objeto Total, Culpa e Reparação)",
                "Técnica do Brincar como Associação Livre Infantil",
                "Inveja Primária vs. Gratidão"
            ),
            "\"A capacidade de amar e o sentimento de gratidão por benefícios passados são elementos indispensáveis de uma mente sã.\""
        )
        author.name.contains("Lacan", ignoreCase = true) -> Triple(
            listOf(
                "Escritos (1966)",
                "O Seminário, Livro 11: Os Quatro Conceitos Fundamentais da Psicanálise (1964)",
                "Outros Escritos (2001)"
            ),
            listOf(
                "O Estádio do Espelho e a Formação do Eu",
                "Os Três Registros: Real, Simbólico e Imaginário (RSI)",
                "O Inconsciente Estruturado como uma Linguagem",
                "O Objeto Pequeno a e o Desejo do Outro"
            ),
            "\"O inconsciente é o discurso do Outro.\""
        )
        else -> Triple(
            listOf(
                "Obras Selecionadas e Artigos Seminais",
                "Tratado de Fundamentos e Prática Clínica",
                "Artigos Clínicos em Periódicos Internacionais"
            ),
            listOf(
                "Fundamentos Epistemológicos e Metodológicos",
                "Aplicações Clínicas, Educacionais e Sociais",
                "Desenvolvimento Humano e Relações Interpessoais"
            ),
            "\"A compreensão profunda do psiquismo humano exige rigor teórico e sensibilidade empática.\""
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("author_detail_dialog"),
            color = SurfaceContainerLowest,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PinePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = author.initial,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Column {
                            Text(
                                text = author.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "${author.dates} • ${author.school}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SageSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextOnSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = OutlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Biografia
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Biografia & Relevância Histórica",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                        }
                        Text(
                            text = author.bio,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextOnSurface,
                                lineHeight = 20.sp
                            )
                        )
                    }

                    // Famosa Citação
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SageSecondaryContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PinePrimary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.FormatQuote, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(24.dp))
                            Text(
                                text = famousQuote,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnSageSecondaryContainer,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }

                    // Teorias & Conceitos Centrais
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Conceitos & Teorias Centrais",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                        }
                        keyTheories.forEach { theory ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PinePrimary)
                                )
                                Text(
                                    text = theory,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextOnSurface,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }

                    // Bibliografia Fundamental
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Bibliografia Seminal (Obras-Chave)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                        }
                        bibliography.forEach { book ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedBookForReading = book }
                                    .testTag("book_item_${book.take(10)}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = book,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextOnSurface,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SageSecondaryContainer
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.AutoStories, contentDescription = null, tint = OnSageSecondaryContainer, modifier = Modifier.size(12.dp))
                                            Text(
                                                text = "Ler",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = OnSageSecondaryContainer,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action: Redirect to Google Search as requested by user
                Button(
                    onClick = {
                        val query = "Psicologia ${author.name} verbetes obras teorias artigos"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("explore_author_google_search"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Explorar no Google: Verbetes & Obras de ${author.name}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Book reading modal
    BookReaderDialog(
        bookTitle = selectedBookForReading,
        authorName = author.name,
        isOpen = selectedBookForReading != null,
        onDismiss = { selectedBookForReading = null }
    )
}
