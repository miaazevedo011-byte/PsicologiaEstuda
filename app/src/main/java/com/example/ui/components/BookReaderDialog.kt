package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.*

@Composable
fun BookReaderDialog(
    bookTitle: String?,
    authorName: String,
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen || bookTitle == null) return

    val context = LocalContext.current

    // Generate rich book synopsis, chapter outline, and seminal excerpt
    val bookContent = getBookDetails(bookTitle, authorName)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("book_reader_dialog"),
            color = SurfaceContainerLowest,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = PinePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bookTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                ),
                                maxLines = 2
                            )
                            Text(
                                text = "Obra Seminal • $authorName",
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

                // Scrollable Book Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Visão Geral e Contexto
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Visão Geral & Contexto Epistemológico",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                        }
                        Text(
                            text = bookContent.overview,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextOnSurface,
                                lineHeight = 21.sp
                            )
                        )
                    }

                    // Trecho Clássico em Destaque
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SageSecondaryContainer.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PinePrimary.copy(alpha = 0.2f)),
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
                                Icon(Icons.Default.FormatQuote, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Passagem Fundamental da Obra",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary
                                    )
                                )
                            }
                            Text(
                                text = bookContent.seminalQuote,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = OnSageSecondaryContainer,
                                    lineHeight = 19.sp
                                )
                            )
                        }
                    }

                    // Estrutura de Capítulos e Eixos Teóricos
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.ListAlt, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Capítulos Centrais & Eixos Conceituais",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                        }
                        bookContent.chapters.forEachIndexed { index, chapter ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(PinePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = chapter,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextOnSurface,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Aplicações Clínicas e Metodológicas
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Impacto Clínico & Diagnóstico Atual",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                        }
                        Text(
                            text = bookContent.clinicalImpact,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextOnSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action: Open in Google Books / Web search for full reading
                Button(
                    onClick = {
                        val query = "Livro \"$bookTitle\" \"$authorName\" leitura completa pdf"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_book_google_search"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ler / Acessar Obra Completa no Google Livros", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private data class BookContent(
    val overview: String,
    val seminalQuote: String,
    val chapters: List<String>,
    val clinicalImpact: String
)

private fun getBookDetails(title: String, author: String): BookContent {
    return when {
        title.contains("Sonhos", ignoreCase = true) -> BookContent(
            overview = "Publicada em 1900, 'A Interpretação dos Sonhos' é o marco inaugural da Psicanálise freudiana. Freud postula que o sonho é a realização disfarçada de um desejo reprimido inconsciente, dividindo-se entre conteúdo manifesto (a narrativa lembrada) e pensamentos latentes (o sentido inconsciente decifrado pelo trabalho do sonho: condensação e deslocamento).",
            seminalQuote = "\"A interpretação dos sonhos é a via régia que conduz ao conhecimento das atividades inconscientes da mente.\"",
            chapters = listOf(
                "Capítulo I: A Literatura Científica sobre os Problemas dos Sonhos",
                "Capítulo II: O Método de Interpretação dos Sonhos: Análise de uma Amostra",
                "Capítulo III: O Sonho como Realização de Desejo",
                "Capítulo IV: A Deformação Onírica e a Censura Psíquica",
                "Capítulo VI: O Trabalho do Sonho (Condensação, Deslocamento e Figuração)",
                "Capítulo VII: A Psicologia dos Processos Oníricos (O Aparelho Psíquico)"
            ),
            clinicalImpact = "Fundamenta a regra analítica da livre associação, a interpretação de resistências clínicas e a compreensão dos sintomas neuróticos como formações de compromisso psíquico análogas ao sonho."
        )
        title.contains("Ego e o Id", ignoreCase = true) -> BookContent(
            overview = "Publicado em 1923, introduz a Segunda Tópica da metapsicologia freudiana. Freud reestrutura o modelo psíquico com as três instâncias: o Id (reservatório pulsional de Eros e Tânatos regido pelo princípio do prazer), o Ego (instância executiva adaptativa regida pela realidade) e o Superego (herdeiro do Complexo de Édipo, instância moral e ideal).",
            seminalQuote = "\"O Ego não é senhor em sua própria casa. Ele serve a três senhores severos: o mundo externo, o Id e o Superego.\"",
            chapters = listOf(
                "Capítulo I: Consciência e o Inconsciente Revisitados",
                "Capítulo II: O Ego e o Id: A Diferenciação Psíquica",
                "Capítulo III: O Ego e o Superego (O Ideal do Ego)",
                "Capítulo IV: As Duas Classes de Pulsões (Eros e Pulsão de Morte)",
                "Capítulo V: Os Servos do Ego e os Sentimentos de Culpa Inconscientes"
            ),
            clinicalImpact = "Permite identificar os sentimentos inconscientes de culpa, a reação terapêutica negativa e os mecanismos estruturais de defesa do ego na neurose, psicose e perversão."
        )
        title.contains("Tornar-se Pessoa", ignoreCase = true) || title.contains("Rogers", ignoreCase = true) -> BookContent(
            overview = "Publicado em 1961, é o ápice da Abordagem Centrada na Pessoa (ACP). Carl Rogers sintetiza décadas de pesquisa clínica demonstrando que o ser humano possui uma tendência inata para a autorrealização (tendência atualizante), que floresce plenamente sob um clima relacional facilitador.",
            seminalQuote = "\"O curioso paradoxo é que quando me aceito exatamente como sou, posso então mudar. O indivíduo tem dentro de si vastos recursos para a autocompreensão.\"",
            chapters = listOf(
                "Parte I: 'Este sou Eu': O Desenvolvimento de Minha Filosofia Profissional",
                "Parte II: Como Posso Ajudar? As Características de uma Relação Facilitadora",
                "Parte III: O Processo de Tornar-se Pessoa e as Fases Terapêuticas",
                "Parte IV: Uma Filosofia das Pessoas: O Homem Plenamente Funcionante",
                "Parte V: Rumo a uma Teoria Centrada na Pessoa na Educação e Vida Familiar"
            ),
            clinicalImpact = "Revolucionou a prática clínica humanista estabelecendo as três atitudes ativas fundamentais: Congruência (autenticidade do terapeuta), Consideração Positiva Incondicional e Empatia Fenomenológica profunda."
        )
        title.contains("Ciência e Comportamento", ignoreCase = true) || title.contains("Skinner", ignoreCase = true) -> BookContent(
            overview = "Lançado em 1953 por B.F. Skinner, estabelece as bases teóricas do Behaviorismo Radical. Skinner examina como o comportamento humano em todas as suas esferas (educação, governo, religião, psicoterapia) é selecionado por suas consequências segundo contingências operantes ambientais.",
            seminalQuote = "\"Os homens agem sobre o mundo, modificam-no e são, por sua vez, modificados pelas consequências de sua ação.\"",
            chapters = listOf(
                "Seção I: A Possibilidade de uma Ciência do Comportamento Humano",
                "Seção II: A Análise do Comportamento Operante e Reforçamento",
                "Seção III: O Indivíduo como um Todo e Autocontrole",
                "Seção IV: O Comportamento das Pessoas em Grupo",
                "Seção V: Agências Controladoras (Governo, Lei, Religião e Psicoterapia)"
            ),
            clinicalImpact = "Base da Análise Aplicada do Comportamento (ABA), das terapias comportamentais contemporâneas e do manejo funcional de contingências em transtornos do neurodesenvolvimento e controle de impulsos."
        )
        title.contains("Inteligência", ignoreCase = true) || title.contains("Piaget", ignoreCase = true) -> BookContent(
            overview = "Publicado em 1936 por Jean Piaget, apresenta as observações longitudinais que fundaram a Epistemologia Genética. Piaget documenta a gênese da cognição no período sensório-motor, demonstrando que os bebês constroem esquemas de ação através de assimilação e acomodação ativas.",
            seminalQuote = "\"O conhecimento não é uma cópia da realidade; conhecer um objeto é agir sobre ele, transformá-lo e apreender o mecanismo dessa transformação.\"",
            chapters = listOf(
                "Capítulo I: O Primeiro Estágio: O Exercício dos Reflexos Hereditários",
                "Capítulo II: O Segundo Estágio: As Primeiras Adaptações Adquiridas e a Reação Circular Primária",
                "Capítulo III: O Terceiro Estágio: As Reações Circulares Secundárias",
                "Capítulo IV: O Quarto Estágio: A Coordenação dos Esquemas Secundários",
                "Capítulo V: O Quinto Estágio: As Reações Circulares Terciárias e a Descoberta de Novos Meios",
                "Capítulo VI: O Sexto Estágio: A Invenção de Novos Meios por Combinação Mental"
            ),
            clinicalImpact = "Referência indispensável para a Psicologia do Desenvolvimento e Psicopedagogia Clínica na avaliação de atrasos cognitivos e construção da permanência do objeto."
        )
        title.contains("Depressão", ignoreCase = true) || title.contains("Beck", ignoreCase = true) -> BookContent(
            overview = "Publicado em 1979 por Aaron T. Beck e colaboradores, consolidou a Terapia Cognitiva da Depressão como tratamento padrão-ouro baseado em evidências. Descreve a Tríade Cognitiva negativa e os métodos empíricos para reestruturar esquemas disfuncionais.",
            seminalQuote = "\"Ao corrigir crenças errôneas, podemos diminuir reações emocionais e comportamentais desadaptativas. O paciente torna-se cientista de seus próprios pensamentos.\"",
            chapters = listOf(
                "Capítulo 1: Uma Visão Geral da Terapia Cognitiva",
                "Capítulo 2: O Papel das Emoções na Depressão",
                "Capítulo 3: A Relação Terapêutica: Empirismo Colaborativo",
                "Capítulo 4: A Estrutura da Sessão Terapêutica",
                "Capítulo 5: Técnicas Comportamentais (Ativação e Agendamento)",
                "Capítulo 6: Técnicas Cognitivas (RPD, Questionamento Socrático e Teste de Hipóteses)",
                "Capítulo 7: Identificação e Modificação de Crenças Centrais"
            ),
            clinicalImpact = "Fundamento da TCC contemporânea para transtornos de humor e ansiedade, introduzindo protocolos de ativação comportamental e reestruturação cognitiva."
        )
        title.contains("Motivação", ignoreCase = true) || title.contains("Maslow", ignoreCase = true) -> BookContent(
            overview = "Obra máxima de Abraham Maslow (1954), apresenta a fundamentação científica da Psicologia Humanista e a hierarquia das necessidades humanas. Demonstra que a saúde mental não é mera ausência de doença, mas a expressão da autorrealização e plenitude das potencialidades humanas.",
            seminalQuote = "\"Se você planeja ser qualquer coisa menos do que aquilo que é capaz de ser, provavelmente será infeliz todos os dias de sua vida.\"",
            chapters = listOf(
                "Capítulo 1: Prefácio a uma Teoria Motivacional",
                "Capítulo 2: Uma Teoria da Motivação Humana e a Hierarquia das Necessidades",
                "Capítulo 3: A Gratificação das Necessidades Básicas e a Saúde Psicológica",
                "Capítulo 4: A Natureza Instintóide das Necessidades Básicas",
                "Capítulo 5: O Comportamento Expressivo vs. Comportamento Instrumental",
                "Capítulo 6: Pessoas Autorrealizadoras: Um Estudo da Saúde Psicológica",
                "Capítulo 7: O Amor em Pessoas Autorrealizadoras e Experiências de Pico"
            ),
            clinicalImpact = "Inspira o aconselhamento psicológico, psicologia positiva, saúde ocupacional e a compreensão das necessidades intrínsecas dos pacientes em psicoterapia."
        )
        title.contains("Sentido", ignoreCase = true) || title.contains("Frankl", ignoreCase = true) -> BookContent(
            overview = "Publicado em 1946 por Viktor E. Frankl, narra sua experiência como prisioneiro em Auschwitz e Dachau e apresenta a Logoterapia. Estabelece que mesmo nas circunstâncias mais extremas e desumanas, o ser humano conserva a liberdade interior de escolher sua atitude perante o sofrimento.",
            seminalQuote = "\"Tudo pode ser tirado de um homem, exceto uma coisa: a última das liberdades humanas — escolher sua atitude em qualquer circunstância, escolher seu próprio caminho.\"",
            chapters = listOf(
                "Parte I: Experiências em um Campo de Concentração: A Psicologia do Prisioneiro",
                "Fase 1: O Choque Inicial e a Ilusão de Graça",
                "Fase 2: A Apatia Emocional e a Fuga para o Interior",
                "Fase 3: A Psicologia do Prisioneiro após a Libertação",
                "Parte II: Conceitos Fundamentais da Logoterapia: A Vontade de Sentido",
                "Parte III: A Frustração Existencial, Neuroses Noogênicas e a Técnica da Intenção Paradoxal",
                "Parte IV: O Otimismo Trágico: Sentido diante da Dor, Culpa e Morte"
            ),
            clinicalImpact = "Ferramenta essencial em psico-oncologia, cuidados paliativos, manejo do luto e crises de desespero existencial, aplicando a derreflexão e a intenção paradoxal."
        )
        title.contains("Gestalt", ignoreCase = true) || title.contains("Perls", ignoreCase = true) -> BookContent(
            overview = "Publicado em 1951 por Fritz Perls, Ralph Hefferline e Paul Goodman, é a obra fundadora da Gestalt-Terapia. Transforma a terapia numa experiência direta no 'aqui-e-agora', ensinando o paciente a restabelecer o contato organísmico consigo mesmo e com o ambiente.",
            seminalQuote = "\"Eu faço as minhas coisas e você faz as suas. Não estou neste mundo para viver de acordo com suas expectativas, e você não está neste mundo para viver de acordo com as minhas.\"",
            chapters = listOf(
                "Volume 1: Mobilização do Self e Experimentos Práticos de Awareness",
                "Capítulo 1: O Contato com o Meio Ambiente e a Orientação Corporal",
                "Capítulo 2: A Técnica da Cadeira Vazia e Diálogo entre Polaridades",
                "Capítulo 3: Transformando a Ansiedade em Excitação Vital",
                "Volume 2: Teoria da Gestalt-Terapia: Estrutura do Self",
                "Capítulo 4: A Fronteira do Contato e os Mecanismos de Evitação",
                "Capítulo 5: Perda da Função Ego: Retroflexão, Introjeção e Projeção"
            ),
            clinicalImpact = "Desenvolveu técnicas vivenciais ativas (como a cadeira vazia, amplificação de gestos corporais e foco nas sensações do presente) amplamente adotadas na clínica humanista contemporânea."
        )
        title.contains("Símbolos", ignoreCase = true) || title.contains("Jung", ignoreCase = true) -> BookContent(
            overview = "Concebida e organizada por Carl Gustav Jung pouco antes de sua morte em 1961, é sua obra mais acessível e abrangente. Explora como o inconsciente se comunica através de símbolos, sonhos e mitos arquetípicos universais.",
            seminalQuote = "\"O homem não pode tolerar uma vida sem sentido. Seu intelecto não pode concebê-la, sua alma não pode suportá-la.\"",
            chapters = listOf(
                "Parte 1: Chegando ao Inconsciente (Carl G. Jung)",
                "Parte 2: Os Mitos Antigos e o Homem Moderno (Joseph L. Henderson)",
                "Parte 3: O Processo de Individuação (Marie-Louise von Franz)",
                "Parte 4: O Simbolismo nas Artes Plásticas (Aniela Jaffé)",
                "Parte 5: Os Símbolos e uma Análise Individual (Jolande Jacobi)"
            ),
            clinicalImpact = "Alavanca a clínica analítica na amplificação de sonhos, arteterapia, imaginação ativa e identificação das manifestações dos arquétipos na neurose."
        )
        title.contains("Brincar", ignoreCase = true) || title.contains("Winnicott", ignoreCase = true) -> BookContent(
            overview = "Publicado em 1971 por D.W. Winnicott, investiga a localização da experiência cultural e criativa humana, situada no espaço transicional entre a realidade interna e o mundo compartilhado.",
            seminalQuote = "\"É no brincar, e somente no brincar, que o indivíduo, criança ou adulto, pode ser criativo e utilizar sua personalidade integral; e é somente sendo criativo que o indivíduo descobre o eu.\"",
            chapters = listOf(
                "Capítulo 1: Objetos Transicionais e Fenômenos Transicionais",
                "Capítulo 2: O Sonhar, a Fantasia e a Vida",
                "Capítulo 3: O Brincar: Uma Exposição Teórica",
                "Capítulo 4: O Brincar: A Atividade Criativa e a Busca do Self",
                "Capítulo 5: A Criatividade e Suas Origens",
                "Capítulo 7: A Localização da Experiência Cultural"
            ),
            clinicalImpact = "Transformou a psicoterapia infantil e de adultos graves, estabelecendo que a análise é a sobreposição de duas áreas do brincar: a do paciente e a do terapeuta."
        )
        else -> BookContent(
            overview = "Esta obra clássica seminal de $author constitui um dos pilares da teoria e prática da Psicologia. Discute conceitos fundamentais, métodos de investigação científica e modelos conceituais estruturantes do psiquismo humano.",
            seminalQuote = "\"A compreensão científica do ser humano exige a articulação constante entre rigor metodológico, sensibilidade empática e compromisso ético com a transformação social.\"",
            chapters = listOf(
                "Capítulo I: Prolegômenos Epistemológicos e Metodológicos",
                "Capítulo II: Fundamentos Teóricos e Conceituação Estrutural",
                "Capítulo III: Investigações Empíricas e Observações Clínicas",
                "Capítulo IV: Modelos de Intervenção e Aplicações Práticas",
                "Capítulo V: Discussão Crítica e Perspectivas Contemporâneas"
            ),
            clinicalImpact = "Utilizada na formação profissional de psicólogos para formulação de hipóteses diagnósticas, planejamento de intervenções terapêuticas e fundamentação de laudos e perícias."
        )
    }
}
