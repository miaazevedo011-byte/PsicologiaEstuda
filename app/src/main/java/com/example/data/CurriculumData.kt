package com.example.data

import com.example.model.Discipline

object CurriculumData {

    val popularInstitutions = listOf(
        "USP - Univ. de São Paulo",
        "PUC - Pontifícia Univ. Católica",
        "UNIP - Univ. Paulista",
        "UFMG - Univ. Federal de Minas Gerais",
        "UFRJ - Univ. Federal do Rio de Janeiro",
        "Estácio",
        "Mackenzie",
        "UnB - Univ. de Brasília",
        "UFBA",
        "Unifesp"
    )

    fun getDisciplinesFor(institution: String, semester: Int): List<Discipline> {
        val instPrefix = when {
            institution.contains("USP", ignoreCase = true) -> "USP"
            institution.contains("PUC", ignoreCase = true) -> "PUC"
            institution.contains("UNIP", ignoreCase = true) -> "UNIP"
            institution.contains("UFMG", ignoreCase = true) -> "UFMG"
            institution.contains("UFRJ", ignoreCase = true) -> "UFRJ"
            institution.contains("Estácio", ignoreCase = true) || institution.contains("Estacio", ignoreCase = true) -> "ESTACIO"
            institution.contains("Mackenzie", ignoreCase = true) -> "MACK"
            else -> "PSI"
        }

        return when (semester) {
            1 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_1_1",
                    name = "Introdução à Psicologia",
                    code = "$instPrefix-101",
                    professor = "Prof. Dr. Ricardo Antunes",
                    progressPercent = 45,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Gênese histórica, campos de atuação profissional e principais sistemas conceituais da psicologia contemporânea segundo a grade de $institution.",
                    learningObjectives = listOf(
                        "Diferenciar senso comum de conhecimento psicológico científico" to true,
                        "Mapear as áreas de atuação regulamentadas pelo CFP" to true,
                        "Compreender a pluralidade epistemológica da Psicologia" to false
                    ),
                    materialsCount = 6,
                    flashcardsCount = 24,
                    audioDurationMin = 10,
                    semester = 1
                ),
                Discipline(
                    id = "disc_${instPrefix}_1_2",
                    name = "História da Psicologia",
                    code = "$instPrefix-102",
                    professor = "Profª. Dra. Beatriz Menezes",
                    progressPercent = 60,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Das raízes filosóficas na Grécia Antiga ao nascimento da psicologia como ciência autônoma (Wundt, Leipzig, 1879).",
                    learningObjectives = listOf(
                        "Estruturalismo vs. Funcionalismo vs. Associacionismo" to true,
                        "O laboratório de Leipzig e a psicofísica de Fechner" to true,
                        "Evolução das primeiras escolas no Brasil" to false
                    ),
                    materialsCount = 5,
                    flashcardsCount = 18,
                    audioDurationMin = 12,
                    semester = 1
                ),
                Discipline(
                    id = "disc_${instPrefix}_1_3",
                    name = "Bases Biológicas do Comportamento",
                    code = "$instPrefix-103",
                    professor = "Prof. Dr. Carlos Eduardo Nobre",
                    progressPercent = 35,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Estrutura do neurônio, sinapses, potencial de ação e organização do sistema nervoso central e periférico.",
                    learningObjectives = listOf(
                        "Mecanismos iônicos da condução nervosa" to true,
                        "Neurotransmissores e neuromodulação comportamental" to false,
                        "Neuroplasticidade e desenvolvimento cortical" to false
                    ),
                    materialsCount = 8,
                    flashcardsCount = 30,
                    audioDurationMin = 15,
                    semester = 1
                ),
                Discipline(
                    id = "disc_${instPrefix}_1_4",
                    name = "Metodologia Científica Aplicada à Psicologia",
                    code = "$instPrefix-104",
                    professor = "Profª. Dra. Cláudia Furtado",
                    progressPercent = 50,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Métodos quantitativos e qualitativos, elaboração de projetos de pesquisa e normas ABNT.",
                    learningObjectives = listOf(
                        "Construção do problema e hipótese de pesquisa" to true,
                        "Ética na pesquisa com seres humanos (Conep)" to true,
                        "Revisão sistemática de literatura" to false
                    ),
                    materialsCount = 4,
                    flashcardsCount = 14,
                    audioDurationMin = 8,
                    semester = 1
                )
            )
            2 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_2_1",
                    name = "Processos Psicológicos Básicos I",
                    code = "$instPrefix-201",
                    professor = "Prof. Dr. Marcelo Vasconcelos",
                    progressPercent = 55,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Sensação, percepção, atenção e memória. Mecanismos cognitivos de processamento da informação.",
                    learningObjectives = listOf(
                        "Limiares sensoriais e psicofísica" to true,
                        "Leis da Gestalt na organização perceptual" to true,
                        "Modelos de memória: sensorial, trabalho e longo prazo" to false
                    ),
                    materialsCount = 7,
                    flashcardsCount = 28,
                    audioDurationMin = 14,
                    semester = 2
                ),
                Discipline(
                    id = "disc_${instPrefix}_2_2",
                    name = "Neuroanatomia e Neurofisiologia",
                    code = "$instPrefix-202",
                    professor = "Profª. Dra. Vanessa Martins",
                    progressPercent = 40,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Córtex cerebral, lobos encefálicos, sistema límbico, vias neurais e regulação neuroendócrina.",
                    learningObjectives = listOf(
                        "Anatomia funcional dos lobos frontal e temporal" to true,
                        "Amígdala e hipocampo no processamento emocional" to false,
                        "Circuitos dopaminérgicos de recompensa" to false
                    ),
                    materialsCount = 9,
                    flashcardsCount = 34,
                    audioDurationMin = 16,
                    semester = 2
                ),
                Discipline(
                    id = "disc_${instPrefix}_2_3",
                    name = "Psicologia Social I",
                    code = "$instPrefix-203",
                    professor = "Prof. Dr. Roberto Fonseca",
                    progressPercent = 70,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Interação social, atitudes, preconceito, conformismo e a relação indivíduo-sociedade segundo a matriz curricular da $institution.",
                    learningObjectives = listOf(
                        "Experimentos clássicos de Asch e Milgram" to true,
                        "Teoria da Dissonância Cognitiva de Festinger" to true,
                        "Identidade social e relações intergrupais de Tajfel" to false
                    ),
                    materialsCount = 6,
                    flashcardsCount = 22,
                    audioDurationMin = 11,
                    semester = 2
                ),
                Discipline(
                    id = "disc_${instPrefix}_2_4",
                    name = "Epistemologia e Filosofia da Mente",
                    code = "$instPrefix-204",
                    professor = "Prof. Dr. Gilberto Souza",
                    progressPercent = 50,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "O problema mente-corpo, dualismo cartesiano, materialismo e as bases epistemológicas das abordagens psicológicas.",
                    learningObjectives = listOf(
                        "O problema da consciência e qualia" to true,
                        "Positivismo lógico vs. Hermenêutica" to true,
                        "Fenomenologia e intencionalidade" to false
                    ),
                    materialsCount = 4,
                    flashcardsCount = 16,
                    audioDurationMin = 9,
                    semester = 2
                )
            )
            3 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_3_1",
                    name = "Psicologia do Desenvolvimento I (Infância)",
                    code = "$instPrefix-301",
                    professor = "Profª. Dra. Mariana Lins",
                    progressPercent = 65,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Desenvolvimento físico, cognitivo e socioemocional do nascimento à infância. Teorias de Piaget, Vygotsky e Wallon.",
                    learningObjectives = listOf(
                        "Estágios do desenvolvimento cognitivo de Piaget" to true,
                        "Zona de Desenvolvimento Proximal e mediação em Vygotsky" to true,
                        "Teoria do apego de Bowlby e Ainsworth" to false
                    ),
                    materialsCount = 8,
                    flashcardsCount = 32,
                    audioDurationMin = 15,
                    semester = 3
                ),
                Discipline(
                    id = "disc_${instPrefix}_3_2",
                    name = "Teorias da Personalidade I",
                    code = "$instPrefix-302",
                    professor = "Prof. Dr. Alexandre Borges",
                    progressPercent = 50,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Modelos psicodinâmicos clássicos: Sigmund Freud, Carl Jung, Alfred Adler e Karen Horney.",
                    learningObjectives = listOf(
                        "Primeira e Segunda Tópicas Freudianas (Id, Ego, Superego)" to true,
                        "Inconsciente coletivo e arquétipos em Jung" to true,
                        "Mecanismos de defesa do ego" to false
                    ),
                    materialsCount = 7,
                    flashcardsCount = 26,
                    audioDurationMin = 13,
                    semester = 3
                ),
                Discipline(
                    id = "disc_${instPrefix}_3_3",
                    name = "Análise Experimental do Comportamento",
                    code = "$instPrefix-303",
                    professor = "Prof. Dr. Fábio Diniz",
                    progressPercent = 75,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Condicionamento respondente e operante, esquemas de reforçamento, punição, extinção e controle de estímulos.",
                    learningObjectives = listOf(
                        "Diferença entre reforço negativo e punição positiva" to true,
                        "Esquemas de razão e intervalo fixo e variável" to true,
                        "Equivalência de estímulos e comportamento verbal" to false
                    ),
                    materialsCount = 9,
                    flashcardsCount = 38,
                    audioDurationMin = 18,
                    semester = 3
                ),
                Discipline(
                    id = "disc_${instPrefix}_3_4",
                    name = "Estatística Aplicada à Psicologia",
                    code = "$instPrefix-304",
                    professor = "Profª. Dra. Renata Silveira",
                    progressPercent = 40,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Estatística descritiva e inferencial, distribuição normal, correlação, testes paramétricos e não paramétricos.",
                    learningObjectives = listOf(
                        "Média, mediana, desvio-padrão e variância" to true,
                        "Curva de Gauss e escores z" to true,
                        "Interpretação do valor-p e significância estatística" to false
                    ),
                    materialsCount = 5,
                    flashcardsCount = 20,
                    audioDurationMin = 10,
                    semester = 3
                )
            )
            4 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_4_1",
                    name = "Psicologia do Desenvolvimento II (Adolescência e Vida Adulta)",
                    code = "$instPrefix-401",
                    professor = "Profª. Dra. Patrícia Alencar",
                    progressPercent = 60,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Construção de identidade, transições psicossociais e teorias do ciclo vital (Erik Erikson).",
                    learningObjectives = listOf(
                        "Os oito estágios psicossociais de Erikson" to true,
                        "Crise de identidade na adolescência" to true,
                        "Envelhecimento e processos de luto e resiliência" to false
                    ),
                    materialsCount = 6,
                    flashcardsCount = 24,
                    audioDurationMin = 12,
                    semester = 4
                ),
                Discipline(
                    id = "disc_${instPrefix}_4_2",
                    name = "Teorias Psicanalíticas II",
                    code = "$instPrefix-402",
                    professor = "Profª. Dra. Simone Duarte",
                    progressPercent = 70,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Pós-freudianos e contemporâneos: Melanie Klein, Donald Winnicott, Wilfred Bion e Jacques Lacan.",
                    learningObjectives = listOf(
                        "Posição esquizoparanoide e depressiva em Melanie Klein" to true,
                        "Mãe suficientemente boa e objeto transicional em Winnicott" to true,
                        "O estádio do espelho e os registros RSI em Lacan" to false
                    ),
                    materialsCount = 8,
                    flashcardsCount = 35,
                    audioDurationMin = 17,
                    semester = 4
                ),
                Discipline(
                    id = "disc_${instPrefix}_4_3",
                    name = "Psicopatologia Geral",
                    code = "$instPrefix-403",
                    professor = "Prof. Dr. Henrique Paiva",
                    progressPercent = 80,
                    currentModule = 4,
                    totalModules = 4,
                    syllabusSummary = "Semiologia das alterações da consciência, atenção, memória, orientação, afeto e pensamento.",
                    learningObjectives = listOf(
                        "Exame das funções psíquicas elementares" to true,
                        "Delírios vs. Ideias prevalentes" to true,
                        "Alucinações vs. Pseudoalucinações e Ilusões" to true
                    ),
                    materialsCount = 10,
                    flashcardsCount = 42,
                    audioDurationMin = 20,
                    semester = 4
                ),
                Discipline(
                    id = "disc_${instPrefix}_4_4",
                    name = "Técnicas de Observação do Comportamento",
                    code = "$instPrefix-404",
                    professor = "Prof. Dr. Thiago Gusmão",
                    progressPercent = 55,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Definição operacional, registro contínuo e por intervalos, fidedignidade entre observadores.",
                    learningObjectives = listOf(
                        "Redigir definições comportamentais sem viés inferencial" to true,
                        "Construção de protocolos de registro observacional" to true,
                        "Análise funcional de contingências tríplices (A-B-C)" to false
                    ),
                    materialsCount = 5,
                    flashcardsCount = 18,
                    audioDurationMin = 9,
                    semester = 4
                )
            )
            5 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_5_1",
                    name = "Psicopatologia Clínica I",
                    code = "$instPrefix-501",
                    professor = "Profª. Dra. Helena Siqueira",
                    progressPercent = 72,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Critérios diagnósticos segundo o DSM-5-TR e CID-11. Transtornos de humor, ansiedade, TOC e estresse pós-traumático.",
                    learningObjectives = listOf(
                        "Diferenciação semiológica: Tristeza patológica vs. Eutimia" to true,
                        "Critérios do Episódio Depressivo Maior e Transtorno de Pânico" to true,
                        "Formulação diagnóstica e estudo de casos clínicos" to false
                    ),
                    materialsCount = 8,
                    flashcardsCount = 36,
                    audioDurationMin = 18,
                    semester = 5
                ),
                Discipline(
                    id = "disc_${instPrefix}_5_2",
                    name = "Terapia Cognitivo-Comportamental (TCC)",
                    code = "$instPrefix-502",
                    professor = "Prof. Dr. Eduardo Camargo",
                    progressPercent = 65,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Modelo cognitivo de Aaron Beck: pensamentos automáticos, distorções cognitivas, crenças intermediárias e centrais.",
                    learningObjectives = listOf(
                        "Conceitualização cognitiva de caso clínico" to true,
                        "Registro de Pensamentos Disfuncionais (RPD) e reestruturação" to true,
                        "Técnicas de exposição e experimentos comportamentais" to false
                    ),
                    materialsCount = 7,
                    flashcardsCount = 32,
                    audioDurationMin = 16,
                    semester = 5
                ),
                Discipline(
                    id = "disc_${instPrefix}_5_3",
                    name = "Técnicas de Avaliação Psicológica I (Psicometria)",
                    code = "$instPrefix-503",
                    professor = "Profª. Dra. Luísa Prado",
                    progressPercent = 50,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Fundamentos psicométricos: validade, fidedignidade, normatização e sistema SATEPSI do CFP.",
                    learningObjectives = listOf(
                        "Critérios de validação de testes psicológicos" to true,
                        "Aplicação e correção das escalas Wechsler (WAIS-IV / WISC-IV)" to true,
                        "Elaboração de laudos e pareceres conforme Resolução CFP" to false
                    ),
                    materialsCount = 6,
                    flashcardsCount = 25,
                    audioDurationMin = 14,
                    semester = 5
                ),
                Discipline(
                    id = "disc_${instPrefix}_5_4",
                    name = "Ética Profissional e Bioética",
                    code = "$instPrefix-504",
                    professor = "Prof. Dr. Mauro Guimarães",
                    progressPercent = 85,
                    currentModule = 4,
                    totalModules = 4,
                    syllabusSummary = "Código de Ética do Psicólogo, sigilo profissional, direitos humanos e dilemas na prática clínica.",
                    learningObjectives = listOf(
                        "Dever de confidencialidade e quebra justificada de sigilo" to true,
                        "Relações duplas e conflito de interesse" to true,
                        "Documentos psicológicos: declaração, relatório e laudo" to true
                    ),
                    materialsCount = 5,
                    flashcardsCount = 20,
                    audioDurationMin = 10,
                    semester = 5
                )
            )
            6 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_6_1",
                    name = "Psicopatologia Clínica II (Psicoses e Transtornos de Personalidade)",
                    code = "$instPrefix-601",
                    professor = "Prof. Dr. Marcos Vinícius Brandão",
                    progressPercent = 40,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Esquizofrenia, transtorno esquizotípico, transtorno bipolar I e II, e transtornos de personalidade (Borderline, Narcisista, Antissocial).",
                    learningObjectives = listOf(
                        "Sintomas positivos vs. negativos na Esquizofrenia" to true,
                        "Diagnóstico diferencial entre Transtorno Bipolar e Borderline" to true,
                        "Manejo do risco de suicídio e automutilação" to false
                    ),
                    materialsCount = 8,
                    flashcardsCount = 38,
                    audioDurationMin = 19,
                    semester = 6
                ),
                Discipline(
                    id = "disc_${instPrefix}_6_2",
                    name = "Técnicas Projetivas de Avaliação Psicológica",
                    code = "$instPrefix-602",
                    professor = "Profª. Dra. Giselle Albuquerque",
                    progressPercent = 35,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Métodos projetivos gráficos e temáticos: H-T-P, TAT, CAT e fundamentos do Psicodiagnóstico de Rorschach.",
                    learningObjectives = listOf(
                        "Mecanismos de projeção e apercepção temática" to true,
                        "Administração e interpretação do teste H-T-P" to false,
                        "Integração de testes psicométricos e projetivos na bateria clínica" to false
                    ),
                    materialsCount = 7,
                    flashcardsCount = 28,
                    audioDurationMin = 15,
                    semester = 6
                ),
                Discipline(
                    id = "disc_${instPrefix}_6_3",
                    name = "Psicologia Humanista e Fenomenológica",
                    code = "$instPrefix-603",
                    professor = "Prof. Dr. Daniel Moretti",
                    progressPercent = 60,
                    currentModule = 3,
                    totalModules = 4,
                    syllabusSummary = "Abordagem Centrada na Pessoa (Carl Rogers), Gestalt-terapia (Fritz Perls) e Logoterapia (Viktor Frankl).",
                    learningObjectives = listOf(
                        "As três condições facilitadoras de Rogers (Empatia, Congruência, Aceitação)" to true,
                        "Ciclo de contato e mecanismos de evitação na Gestalt" to true,
                        "A busca pelo sentido existencial em Frankl" to false
                    ),
                    materialsCount = 6,
                    flashcardsCount = 26,
                    audioDurationMin = 14,
                    semester = 6
                ),
                Discipline(
                    id = "disc_${instPrefix}_6_4",
                    name = "Psicologia Institucional e Comunitária",
                    code = "$instPrefix-604",
                    professor = "Profª. Dra. Cíntia Ribeiro",
                    progressPercent = 50,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Atuação no SUS, CRAS, CREAS, atenção psicossocial (CAPS) e intervenções em grupos vulneráveis.",
                    learningObjectives = listOf(
                        "A Reforma Psiquiátrica e a Luta Antimanicomial no Brasil" to true,
                        "Políticas públicas de Saúde Mental e assistência social" to true,
                        "Metodologias participativas e clínica ampliada" to false
                    ),
                    materialsCount = 5,
                    flashcardsCount = 22,
                    audioDurationMin = 11,
                    semester = 6
                )
            )
            7 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_7_1",
                    name = "Psicodiagnóstico e Elaboração de Documentos",
                    code = "$instPrefix-701",
                    professor = "Profª. Dra. Letícia Vilar",
                    progressPercent = 30,
                    currentModule = 1,
                    totalModules = 4,
                    syllabusSummary = "Processo psicodiagnóstico estruturado: enquadre, entrevistas iniciais, escolha da bateria de testes, devolutiva e laudo pericial.",
                    learningObjectives = listOf(
                        "Entrevista lúdica com crianças e anamnese com pais" to true,
                        "Devolutiva clínica e comunicação empática de resultados" to false,
                        "Redação técnica conforme a Resolução CFP nº 06/2019" to false
                    ),
                    materialsCount = 8,
                    flashcardsCount = 30,
                    audioDurationMin = 16,
                    semester = 7
                ),
                Discipline(
                    id = "disc_${instPrefix}_7_2",
                    name = "Psicofarmacologia Básica para Psicólogos",
                    code = "$instPrefix-702",
                    professor = "Prof. Dr. André Castro",
                    progressPercent = 45,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Antidepressivos (ISRS, IRSN, tricíclicos), ansiolíticos, antipsicóticos típicos e atípicos, e estabilizadores de humor.",
                    learningObjectives = listOf(
                        "Mecanismos de ação e janela terapêutica de antidepressivos" to true,
                        "Efeitos colaterais e síndrome de descontinuação" to false,
                        "Colaboração interdisciplinar médico-psicólogo" to false
                    ),
                    materialsCount = 6,
                    flashcardsCount = 28,
                    audioDurationMin = 15,
                    semester = 7
                ),
                Discipline(
                    id = "disc_${instPrefix}_7_3",
                    name = "Estágio Básico Supervisionado I",
                    code = "$instPrefix-703",
                    professor = "Supervisores da Clínica-Escola $institution",
                    progressPercent = 50,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Primeiras vivências práticas: triagens, acolhimento clínico e observação institucional em serviços conveniados.",
                    learningObjectives = listOf(
                        "Prontuário psicológico e registros de evolução" to true,
                        "Manejo do enquadre clínico e sigilo de estágio" to true,
                        "Apresentação reflexiva em reunião de supervisão" to false
                    ),
                    materialsCount = 10,
                    flashcardsCount = 20,
                    audioDurationMin = 12,
                    semester = 7
                )
            )
            8 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_8_1",
                    name = "Psicologia da Saúde e Hospitalar",
                    code = "$instPrefix-801",
                    professor = "Profª. Dra. Carolina Brant",
                    progressPercent = 25,
                    currentModule = 1,
                    totalModules = 4,
                    syllabusSummary = "Acolhimento em UTI, pré e pós-operatório, adesão ao tratamento, cuidados paliativos e psico-oncologia.",
                    learningObjectives = listOf(
                        "Avaliação psicológica em pacientes internados" to true,
                        "Comunicação de más notícias (Protocolo SPIKES)" to false,
                        "Cuidados ao final da vida e suporte à família enlutada" to false
                    ),
                    materialsCount = 7,
                    flashcardsCount = 24,
                    audioDurationMin = 14,
                    semester = 8
                ),
                Discipline(
                    id = "disc_${instPrefix}_8_2",
                    name = "Psicologia Jurídica e Forense",
                    code = "$instPrefix-802",
                    professor = "Prof. Dr. Wagner Toledo",
                    progressPercent = 40,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Perícia psicológica em varas de família (guarda, alienação parental) e criminais (imputabilidade e periculosidade).",
                    learningObjectives = listOf(
                        "Papel do perito judicial vs. assistente técnico" to true,
                        "Avaliação da capacidade de discernimento e imputabilidade penal" to false,
                        "Depoimento especial de crianças e adolescentes" to false
                    ),
                    materialsCount = 6,
                    flashcardsCount = 22,
                    audioDurationMin = 13,
                    semester = 8
                ),
                Discipline(
                    id = "disc_${instPrefix}_8_3",
                    name = "Estágio Específico Supervisionado I (Ênfase Clínica)",
                    code = "$instPrefix-803",
                    professor = "Supervisores Clínicos $institution",
                    progressPercent = 35,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Atendimento psicoterápico individual a pacientes adultos, adolescentes ou infantis na Clínica-Escola.",
                    learningObjectives = listOf(
                        "Estabelecimento da aliança terapêutica" to true,
                        "Condução de sessões estruturadas na abordagem escolhida" to false,
                        "Supervisão semanal e elaboração do projeto terapêutico" to false
                    ),
                    materialsCount = 9,
                    flashcardsCount = 25,
                    audioDurationMin = 18,
                    semester = 8
                )
            )
            9 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_9_1",
                    name = "Estágio Específico Supervisionado II",
                    code = "$instPrefix-901",
                    professor = "Coordenação de Estágios $institution",
                    progressPercent = 20,
                    currentModule = 1,
                    totalModules = 4,
                    syllabusSummary = "Aprofundamento da prática clínica ambulatorial, intervenções em crise e formulações avançadas.",
                    learningObjectives = listOf(
                        "Manejo de resistências e defesas rígidas" to true,
                        "Intervenções em ideação suicida e situações de emergência" to false,
                        "Elaboração de relatórios parciais de atendimento" to false
                    ),
                    materialsCount = 8,
                    flashcardsCount = 20,
                    audioDurationMin = 15,
                    semester = 9
                ),
                Discipline(
                    id = "disc_${instPrefix}_9_2",
                    name = "Trabalho de Conclusão de Curso I (TCC I)",
                    code = "$instPrefix-902",
                    professor = "Prof. Orientador Especialista",
                    progressPercent = 50,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Definição do projeto de pesquisa em Psicologia, submissão ao Comitê de Ética em Pesquisa (CEP/Conep).",
                    learningObjectives = listOf(
                        "Estruturação do referencial teórico e justificativa" to true,
                        "Coleta piloto e instrumentos de pesquisa" to false,
                        "Qualificação do projeto perante banca examinadora" to false
                    ),
                    materialsCount = 6,
                    flashcardsCount = 15,
                    audioDurationMin = 10,
                    semester = 9
                )
            )
            10 -> listOf(
                Discipline(
                    id = "disc_${instPrefix}_10_1",
                    name = "Estágio Específico Supervisionado III & Encerramento Clínico",
                    code = "$instPrefix-1001",
                    professor = "Supervisores Seniores $institution",
                    progressPercent = 15,
                    currentModule = 1,
                    totalModules = 4,
                    syllabusSummary = "Processo de alta psicoterápica, encaminhamentos pós-clínica-escola e síntese da prática clínica supervisionada.",
                    learningObjectives = listOf(
                        "Condução do encerramento terapêutico e luto da relação" to true,
                        "Avaliação dos ganhos e manutenção dos resultados" to false,
                        "Relatório final circunstanciado de alta" to false
                    ),
                    materialsCount = 8,
                    flashcardsCount = 18,
                    audioDurationMin = 14,
                    semester = 10
                ),
                Discipline(
                    id = "disc_${instPrefix}_10_2",
                    name = "Trabalho de Conclusão de Curso II (Defesa Final de TCC)",
                    code = "$instPrefix-1002",
                    professor = "Banca Examinadora $institution",
                    progressPercent = 40,
                    currentModule = 2,
                    totalModules = 4,
                    syllabusSummary = "Análise dos dados empíricos ou teóricos, discussão dos resultados, redação do artigo científico e defesa pública.",
                    learningObjectives = listOf(
                        "Discussão crítica à luz do estado da arte na Psicologia" to true,
                        "Apresentação oral e resposta aos arguidores da banca" to false,
                        "Encaminhamento para publicação em periódico indexado" to false
                    ),
                    materialsCount = 7,
                    flashcardsCount = 16,
                    audioDurationMin = 12,
                    semester = 10
                )
            )
            else -> getDisciplinesFor(institution, 5)
        }
    }
}
