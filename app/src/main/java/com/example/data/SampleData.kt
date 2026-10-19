package com.example.data

import com.example.model.*

object SampleData {
    val userProfile = UserProfile(
        name = "Mariana",
        course = "Psicologia Clínica",
        institution = "Universidade de São Paulo (USP)",
        semester = "5º Período",
        semesterNumber = 5,
        shift = "Matutino",
        currentWeek = 9,
        totalWeeks = 18,
        weeklyDedicatedHours = 14.5f,
        weeklyTargetHours = 20.0f,
        verbetesRead = 18,
        cardsMastered = 42,
        audioClassesListened = 3
    )

    val initialEvents = listOf(
        AcademicEvent(
            id = "evt_1",
            title = "Processos Psicológicos Básicos",
            type = EventType.PROVA,
            dateDisplay = "Sexta, 24 Out",
            timeDisplay = "08:30 às 11:00",
            location = "Sala 304 Bloco B",
            daysRemaining = 3,
            discipline = "Processos Psicológicos"
        ),
        AcademicEvent(
            id = "evt_2",
            title = "Estágio Básico Observacional",
            type = EventType.RELATORIO,
            dateDisplay = "Segunda, 27 Out",
            timeDisplay = "Até às 23:59",
            location = "Portal Acadêmico",
            daysRemaining = 6,
            discipline = "Estágio Básico I"
        ),
        AcademicEvent(
            id = "evt_3",
            title = "Teoria do Apego (John Bowlby)",
            type = EventType.APRESENTACAO,
            dateDisplay = "Quarta, 29 Out",
            timeDisplay = "10:15",
            location = "Auditório 2 • Grupo 4",
            daysRemaining = 8,
            discipline = "Psicologia do Desenvolvimento"
        )
    )

    val studyTracks = listOf(
        StudyTrack(
            id = "track_1",
            title = "Síntese de Psicanálise Freudiana",
            subtitle = "Conceitos fundamentais da Primeira Tópica (Id, Ego e Superego) e Mecanismos de Defesa.",
            discipline = "Psicanálise I",
            durationMin = 12,
            tag = "Estudo Inteligente IA",
            colorType = "primary"
        ),
        StudyTrack(
            id = "track_2",
            title = "Psicopatologia I",
            subtitle = "15 questões objetivas baseadas no DSM-5-TR sobre Transtornos de Humor.",
            discipline = "Psicopatologia Clínica",
            durationMin = 20,
            tag = "Simulado Clínico",
            colorType = "secondary"
        ),
        StudyTrack(
            id = "track_3",
            title = "Assistente Socrático",
            subtitle = "Pratique formulações de casos com questionamento reflexivo em tempo real.",
            discipline = "TCC & Análise",
            durationMin = 15,
            tag = "Diálogo Clínico",
            colorType = "tertiary"
        ),
        StudyTrack(
            id = "track_4",
            title = "Flashcards: Behaviorismo Radical",
            subtitle = "Esquemas de Reforço, Extinção e Punição Positiva/Negativa.",
            discipline = "Análise do Comportamento",
            durationMin = 10,
            tag = "18 pendentes",
            colorType = "amber"
        )
    )

    val authors = listOf(
        Author(
            id = "author_freud",
            name = "Sigmund Freud",
            dates = "1856 – 1939",
            school = "Psicanálise Clássica",
            bio = "Fundador da psicanálise, formulou as teorias do inconsciente, pulsões de vida (Eros) e morte (Tânatos), fases psicossexuais e o modelo estrutural (Id, Ego e Superego).",
            verbetesCount = 42,
            audiosCount = 6,
            initial = "SF"
        ),
        Author(
            id = "author_vygotsky",
            name = "Lev Vygotsky",
            dates = "1896 – 1934",
            school = "Psicologia Histórico-Cultural",
            bio = "Pioneiro da mediação semiótica, formulador da Zona de Desenvolvimento Proximal (ZDP), funções psicológicas superiores e internalização cultural.",
            verbetesCount = 28,
            audiosCount = 4,
            initial = "LV"
        ),
        Author(
            id = "author_rogers",
            name = "Carl Rogers",
            dates = "1902 – 1987",
            school = "Abordagem Centrada na Pessoa (ACP)",
            bio = "Estruturou a tendência atualizante, as três atitudes facilitadoras (congruência terapêutica, empatia e consideração positiva incondicional).",
            verbetesCount = 19,
            audiosCount = 3,
            initial = "CR"
        ),
        Author(
            id = "author_skinner",
            name = "B.F. Skinner",
            dates = "1904 – 1990",
            school = "Behaviorismo Radical",
            bio = "Teorizou o condicionamento operante, contingências de três termos (antecedente-resposta-consequência), esquemas de reforçamento e punição.",
            verbetesCount = 34,
            audiosCount = 5,
            initial = "BS"
        ),
        Author(
            id = "author_piaget",
            name = "Jean Piaget",
            dates = "1896 – 1980",
            school = "Epistemologia Genética",
            bio = "Formulou a teoria construtivista dos estágios de desenvolvimento cognitivo (sensório-motor, pré-operatório, operatório concreto e formal), assimilação e acomodação.",
            verbetesCount = 31,
            audiosCount = 4,
            initial = "JP"
        ),
        Author(
            id = "author_jung",
            name = "Carl Gustav Jung",
            dates = "1875 – 1961",
            school = "Psicologia Analítica",
            bio = "Criador dos conceitos de inconsciente coletivo, arquétipos (Sombra, Persona, Anima/Animus, Self), tipos psicológicos (introversão/extroversão) e processo de individuação.",
            verbetesCount = 26,
            audiosCount = 4,
            initial = "CJ"
        ),
        Author(
            id = "author_beck",
            name = "Aaron T. Beck",
            dates = "1921 – 2021",
            school = "Terapia Cognitivo-Comportamental (TCC)",
            bio = "Pioneiro da TCC, formulou a tríade cognitiva da depressão, os pensamentos automáticos distorcidos, crenças intermediárias e crenças nucleares centrais.",
            verbetesCount = 35,
            audiosCount = 5,
            initial = "AB"
        ),
        Author(
            id = "author_frankl",
            name = "Viktor Frankl",
            dates = "1905 – 1997",
            school = "Logoterapia & Análise Existencial",
            bio = "Sobrevivente dos campos de concentração nazistas, fundou a Terceira Escola Vienense de Psicoterapia, focada na busca de sentido para a existência humana e resiliência trágica.",
            verbetesCount = 18,
            audiosCount = 3,
            initial = "VF"
        ),
        Author(
            id = "author_winnicott",
            name = "Donald W. Winnicott",
            dates = "1896 – 1971",
            school = "Teoria das Relações Objetais",
            bio = "Pediatra e psicanalista britânico, cunhou os conceitos de mãe suficientemente boa, objeto transicional, espaço potencial (playing) e desenvolvimento do verdadeiro vs falso self.",
            verbetesCount = 22,
            audiosCount = 3,
            initial = "DW"
        ),
        Author(
            id = "author_bowlby",
            name = "John Bowlby",
            dates = "1907 – 1990",
            school = "Teoria do Apego (Attachment Theory)",
            bio = "Integrou etologia e psicanálise para descrever o apego biológico inato entre bebê e cuidador primário, categorizado em padrões de apego seguro, ansioso e evitativo.",
            verbetesCount = 20,
            audiosCount = 3,
            initial = "JB"
        ),
        Author(
            id = "author_klein",
            name = "Melanie Klein",
            dates = "1882 – 1960",
            school = "Psicanálise Infantil & Relações Objetais",
            bio = "Pioneira na técnica de psicanálise de crianças pelo brincar. Formulou as posições esquizoparanóide e depressiva, a fantasia inconsciente e o conflito amor/ódio primário.",
            verbetesCount = 24,
            audiosCount = 3,
            initial = "MK"
        ),
        Author(
            id = "author_bandura",
            name = "Albert Bandura",
            dates = "1925 – 2021",
            school = "Teoria Social Cognitiva",
            bio = "Demonstrou a aprendizagem observacional e modelagem vicária (experimento do João Bobo), o conceito de crença de autoeficácia e o determinismo recíproco triádico.",
            verbetesCount = 17,
            audiosCount = 2,
            initial = "AB"
        ),
        Author(
            id = "author_maslow",
            name = "Abraham Maslow",
            dates = "1908 – 1970",
            school = "Psicologia Humanista",
            bio = "Pioneiro da Terceira Força da Psicologia, formulador da hierarquia das necessidades humanas e pesquisador da autorrealização e experiências de pico (peak experiences).",
            verbetesCount = 21,
            audiosCount = 3,
            initial = "AM"
        ),
        Author(
            id = "author_perls",
            name = "Fritz Perls",
            dates = "1893 – 1970",
            school = "Gestalt-Terapia (Humanismo)",
            bio = "Cofundador da Gestalt-terapia com Laura Perls. Destacou a tomada de consciência do aqui-e-agora (awareness), a autorregulação organísmica e o fechamento de gestalten abertas.",
            verbetesCount = 19,
            audiosCount = 3,
            initial = "FP"
        ),
        Author(
            id = "author_lacan",
            name = "Jacques Lacan",
            dates = "1901 – 1981",
            school = "Psicanálise Estruturalista",
            bio = "Propôs o 'retorno a Freud', teorizando o inconsciente estruturado como uma linguagem, o estádio do espelho e os três registros psíquicos: o Real, o Simbólico e o Imaginário.",
            verbetesCount = 25,
            audiosCount = 4,
            initial = "JL"
        )
    )

    val verbetes = listOf(
        Verbete(
            id = "verb_defesa",
            title = "Mecanismos de Defesa do Ego",
            subtitle = "Repressão, Sublimação, Projeção, Racionalização e Formação Reativa",
            school = "Psicanálise",
            authors = "Sigmund & Anna Freud",
            summary = "Processos psíquicos inconscientes executados pelo Ego para suprimir conflitos entre as exigências instintuais do Id, os imperativos morais do Superego e as limitações da realidade exterior, reduzindo a angústia psíquica.",
            microPills = listOf(
                "Repressão (Recalque)" to "Barramento primário de representações pulsionais ao inconsciente",
                "Sublimação" to "Canalização de pulsões em alvos socialmente valorizados (arte, trabalho)",
                "Projeção" to "Atribuição a outrem de afetos, desejos ou impulsos próprios inaceitáveis",
                "Formação Reativa" to "Adoção de conduta oposta ao impulso inconsciente reprimido"
            ),
            flashcardsCount = 12,
            audioDuration = "08 min",
            audioTopicKey = "defesa",
            linkedDiscipline = "Psicologia da Personalidade"
        ),
        Verbete(
            id = "verb_zdp",
            title = "Zona de Desenvolvimento Proximal (ZDP)",
            subtitle = "A distância dinâmica entre o nível real consolidado e o potencial mediado",
            school = "Sócio-Histórica",
            authors = "Lev Vygotsky",
            summary = "Espaço dinâmico entre o nível de desenvolvimento real (capacidade de resolver problemas autonomamente) e o nível de desenvolvimento potencial (resolução sob orientação de adulto ou par experiente).",
            microPills = listOf(
                "Mediação Semiótica" to "Signos, linguagem e ferramentas culturais estruturam o pensamento",
                "Andaimento (Scaffolding)" to "Suporte estruturado progressivamente retirado conforme autonomia",
                "Internalização" to "Transição do plano interpsicológico (social) para o intrapsicológico (mental)"
            ),
            flashcardsCount = 16,
            audioDuration = "12 min",
            audioTopicKey = "zdp",
            linkedDiscipline = "Processos de Aprendizagem"
        ),
        Verbete(
            id = "verb_tcc_beck",
            title = "Tríade Cognitiva & Esquemas da TCC",
            subtitle = "Pensamentos automáticos, distorções cognitivas e crenças nucleares",
            school = "Terapia Cognitivo-Comportamental",
            authors = "Aaron T. Beck (1967)",
            summary = "Modelo cognitivo em que as emoções e comportamentos não são causados pelas situações em si, mas pela interpretação cognitiva que o indivíduo faz delas, organizada em esquemas e crenças centrais.",
            microPills = listOf(
                "Tríade Depressógena" to "Visão negativa inflexível de si mesmo, do mundo e do futuro",
                "Distorções Cognitivas" to "Catastrofização, pensamento tudo-ou-nada, abstração seletiva",
                "Reestruturação Cognitiva" to "Exame de evidências empíricas e teste de hipóteses funcionais"
            ),
            flashcardsCount = 18,
            audioDuration = "14 min",
            audioTopicKey = "tcc",
            linkedDiscipline = "Psicologia Cognitiva & Terapias"
        ),
        Verbete(
            id = "verb_jung_arquetipos",
            title = "Inconsciente Coletivo & Arquétipos",
            subtitle = "Estruturas míticas universais herdadas pela psique da espécie humana",
            school = "Psicologia Analítica",
            authors = "Carl Gustav Jung",
            summary = "Camada profunda da psique compartilhada por toda a humanidade contendo padrões herdados de imaginação e experiência instintiva chamados arquétipos, que se manifestam em sonhos, mitos e delírios.",
            microPills = listOf(
                "Sombra" to "Aspectos reprimidos e não reconhecidos da personalidade consciente",
                "Persona" to "Máscara social adaptativa apresentada ao mundo exterior",
                "Anima & Animus" to "Arquétipos do feminino no homem e do masculino na mulher",
                "Self & Individuação" to "Centro integrador da psique e processo de realização integral"
            ),
            flashcardsCount = 14,
            audioDuration = "11 min",
            audioTopicKey = "jung",
            linkedDiscipline = "Teorias da Personalidade"
        ),
        Verbete(
            id = "verb_piaget_estagios",
            title = "Estágios do Desenvolvimento Cognitivo",
            subtitle = "Sensório-motor, pré-operatório, operatório concreto e formal",
            school = "Epistemologia Genética",
            authors = "Jean Piaget",
            summary = "Estruturas universais sucessivas pelas quais a criança constrói ativamente esquemas mentais adaptativos por meio dos processos complementares de assimilação e acomodação em busca da equilibração majorante.",
            microPills = listOf(
                "Sensório-motor (0-2 anos)" to "Construção da permanência do objeto e causalidade prática",
                "Pré-operatório (2-7 anos)" to "Função simbólica, linguagem, egocentrismo e centração",
                "Operatório Concreto (7-11)" to "Reversibilidade de pensamento, conservação de quantidade",
                "Operatório Formal (12+)" to "Pensamento hipotético-dedutivo e raciocínio abstrato proposicional"
            ),
            flashcardsCount = 20,
            audioDuration = "15 min",
            audioTopicKey = "piaget",
            linkedDiscipline = "Psicologia do Desenvolvimento"
        ),
        Verbete(
            id = "verb_teoria_apego",
            title = "Teoria do Apego (Attachment Theory)",
            subtitle = "Padrões seguro, ansioso-ambivalente, evitativo e desorganizado",
            school = "Psicodinâmica Evolutiva",
            authors = "John Bowlby & Mary Ainsworth",
            summary = "Sistema motivacional comportamental inato que impele o bebê a buscar proximidade física e emocional com uma figura de apego primária, servindo como base segura para exploração do mundo exterior.",
            microPills = listOf(
                "Base Segura" to "Referência emocional estável para exploração e retorno seguro",
                "Apego Evitativo" to "Supressão defensiva da expressão de vulnerabilidade e sofrimento",
                "Apego Ambivalente" to "Hiperativação ansiosa e preocupação com abandono iminente",
                "Modelos Operacionais Internos" to "Esquemas cognitivo-afetivos que guiam relações amorosas adultas"
            ),
            flashcardsCount = 15,
            audioDuration = "10 min",
            audioTopicKey = "apego",
            linkedDiscipline = "Psicologia do Desenvolvimento"
        ),
        Verbete(
            id = "verb_winnicott_transicional",
            title = "Objeto Transicional & Espaço Potencial",
            subtitle = "A transição do princípio do prazer para a apreensão da realidade objetiva",
            school = "Psicanálise Winnicottiana",
            authors = "Donald W. Winnicott (1951)",
            summary = "A primeira posse não-eu do bebê (paninho, urso, som) que ocupa uma área intermediária de experiência entre o mundo subjetivo interior e a realidade compartilhada exterior, fundando a capacidade criativa e cultural.",
            microPills = listOf(
                "Mãe Suficientemente Boa" to "Adaptação ativa inicial que declina conforme a criança suporta frustrações",
                "Holding & Handling" to "Sustentação física e psicológica necessária para integração do self",
                "Verdadeiro vs Falso Self" to "Espontaneidade autêntica vs submissão defensiva às demandas externas"
            ),
            flashcardsCount = 13,
            audioDuration = "09 min",
            audioTopicKey = "winnicott",
            linkedDiscipline = "Teorias Psicanalíticas"
        ),
        Verbete(
            id = "verb_skinner_operante",
            title = "Condicionamento Operante & Esquemas de Reforço",
            subtitle = "Tríplice contingência, reforço positivo/negativo e modelagem",
            school = "Behaviorismo Radical",
            authors = "B.F. Skinner",
            summary = "Aprendizagem em que o comportamento voluntário é fortalecido ou enfraquecido em probabilidade pelas consequências que o sucedem no ambiente físico e social.",
            microPills = listOf(
                "Tríplice Contingência" to "Estímulo Discriminativo (Sd) → Resposta (R) → Estímulo Reforçador (Sr)",
                "Reforço vs Punição" to "Reforço aumenta probabilidade de resposta; punição suprime temporariamente",
                "Esquemas Intermitentes" to "Razão Fixa/Variável e Intervalo Fixo/Variável (maior resistência à extinção)"
            ),
            flashcardsCount = 16,
            audioDuration = "13 min",
            audioTopicKey = "skinner",
            linkedDiscipline = "Análise do Comportamento"
        ),
        Verbete(
            id = "verb_dissonancia",
            title = "Dissonância Cognitiva",
            subtitle = "Inconsistência entre crenças e atitudes que gera tensão motivadora",
            school = "Psicologia Social",
            authors = "Leon Festinger (1957)",
            summary = "Estado de desconforto psicológico experimentado quando um indivíduo sustenta duas cognições contraditórias (ideias, valores) ou age em desacordo com sua autoimagem, impulsionando a alteração de crenças ou justificação retroativa.",
            microPills = listOf(
                "Tensão Cognitiva" to "Desconforto gerador de mudança atitudinal",
                "Justificação" to "Racionalização a posteriori do comportamento",
                "Aplicação Clínica" to "Mudança de hábitos aditivos e adesão terapêutica"
            ),
            flashcardsCount = 8,
            audioDuration = "06 min",
            audioTopicKey = "dissonancia",
            linkedDiscipline = "Psicologia Social"
        ),
        Verbete(
            id = "verb_humanis_rogers",
            title = "Abordagem Centrada na Pessoa (ACP)",
            subtitle = "Tendência atualizante, empatia profunda e consideração positiva incondicional",
            school = "Humanismo",
            authors = "Carl Rogers (1951)",
            summary = "Fundada na premissa organísmica de que todo ser humano possui uma motivação inata para desenvolver suas potencialidades construtivas (tendência atualizante). A mudança psicoterápica ocorre não pela interpretação técnica, mas pela qualidade do encontro interpessoal autêntico.",
            microPills = listOf(
                "Tendência Atualizante" to "Força biológica e psíquica inata orientada ao crescimento e autonomia",
                "Empatia Fenomenológica" to "Compreensão precisa do quadro de referências interno do cliente",
                "Consideração Positiva Incondicional" to "Aceitação calorosa sem condições de merecimento ou julgamentos",
                "Congruência Terapêutica" to "Autenticidade do terapeuta: consciência de seus próprios sentimentos"
            ),
            flashcardsCount = 18,
            audioDuration = "14 min",
            audioTopicKey = "humanis_rogers",
            linkedDiscipline = "Psicologia Humanista e Fenomenológica"
        ),
        Verbete(
            id = "verb_humanis_maslow",
            title = "Hierarquia das Necessidades & Autorrealização",
            subtitle = "A pirâmide motivacional e as experiências culminantes (Peak Experiences)",
            school = "Humanismo",
            authors = "Abraham Maslow (1954)",
            summary = "Teoria motivacional que organiza as necessidades humanas em níveis hierárquicos: fisiológicas, segurança, amor/pertencimento, estima e autorrealização. Necessidades deficitárias (D-needs) devem ser razoavelmente supridas para que as metanecessidades do Ser (B-needs) emerjam.",
            microPills = listOf(
                "Necessidades Fisiológicas e Segurança" to "Base biológica indispensável para preservação da vida e estabilidade",
                "Afeto e Pertencimento" to "Vínculos interpessoais, aceitação de grupo e reciprocidade afetiva",
                "Autoestima e Reconhecimento" to "Confiança em si mesmo, competência e prestígio comunitário",
                "Autorrealização & Experiências de Pico" to "Atualização plena dos talentos singulares e momentos de êxtase integrativo"
            ),
            flashcardsCount = 15,
            audioDuration = "11 min",
            audioTopicKey = "humanis_maslow",
            linkedDiscipline = "Psicologia da Personalidade & Humanismo"
        ),
        Verbete(
            id = "verb_humanis_perls",
            title = "Gestalt-Terapia & Tomada de Consciência (Awareness)",
            subtitle = "Aqui-e-agora, ciclo do contato e fechamento de situações inacabadas",
            school = "Humanismo",
            authors = "Fritz & Laura Perls (1951)",
            summary = "Abordagem fenomenológico-existencial focada na experiência do momento presente ('aqui-e-agora'). Investiga como a pessoa organiza seu campo perceptivo (figura/fundo) e como interrompe o ciclo natural de contato através de mecanismos de evitação (introjeção, projeção, retroflexão, confluência e deflexão).",
            microPills = listOf(
                "Awareness (Conscientização)" to "Atenção plena às sensações corporais, afetos e necessidades imediatas",
                "Aqui-e-Agora" to "O passado só existe como memória no presente; o foco é no como e não no porquê",
                "Mecanismos de Interrupção" to "Retroflexão (fazer a si o que queria fazer ao outro), Introjeção e Projeção",
                "Situações Inacabadas" to "Gestalten abertas que drenam energia psíquica até serem expressas e integradas"
            ),
            flashcardsCount = 16,
            audioDuration = "13 min",
            audioTopicKey = "humanis_perls",
            linkedDiscipline = "Teorias e Técnicas Psicoterápicas"
        ),
        Verbete(
            id = "verb_humanis_frankl",
            title = "Logoterapia & Análise Existencial",
            subtitle = "A vontade de sentido, vazio existencial e a tríade trágica",
            school = "Humanismo",
            authors = "Viktor Frankl (1946)",
            summary = "Fundada na convicção de que a motivação primária humana é a vontade de sentido. O ser humano não é determinado unilateralmente por seus impulsos biológicos ou ambiente social, preservando sempre a liberdade da vontade para assumir uma atitude digna mesmo diante do sofrimento inevitável.",
            microPills = listOf(
                "Vontade de Sentido" to "Força propulsora fundamental que supera o princípio do prazer e poder",
                "Vazio Existencial" to "Frustração existencial que gera tédio, apatia e neurose noogênica",
                "Três Vias de Sentido" to "Valores de criação (trabalho), vivência (amor/natureza) e atitude (resiliência)",
                "Tríade Trágica" to "Sofrimento inevitável, culpa e morte ressignificados através da responsabilidade"
            ),
            flashcardsCount = 14,
            audioDuration = "12 min",
            audioTopicKey = "humanis_frankl",
            linkedDiscipline = "Psicologia Existencial-Humanista"
        ),
        Verbete(
            id = "verb_lacan_espelho",
            title = "O Estádio do Espelho & Os Três Registros (RSI)",
            subtitle = "A constituição do Eu pela imagem do outro: Real, Simbólico e Imaginário",
            school = "Psicanálise",
            authors = "Jacques Lacan (1949)",
            summary = "Momento formativo (entre 6 e 18 meses) em que a criança antecipa imaginariamente a unidade corporal ao identificar-se com sua imagem refletida no espelho. Estabelece a alienação constitutiva do Eu no campo do Outro e estrutura os três registros psíquicos: o Real (o que não cessa de não se inscrever), o Simbólico (a ordem da linguagem) e o Imaginário (as identificações visuais).",
            microPills = listOf(
                "Alienação no Espelho" to "Júbilo ao ver a imagem unificada que compensa a prematuração motora",
                "O Inconsciente Estruturado como Linguagem" to "Metáfora e Metonímia como Condensação e Deslocamento",
                "O Objeto a" to "Causa do desejo, resto inassimilável que move a busca pulsional",
                "A Falta e a Castração Simbólica" to "Entrada na lei da linguagem e mediação do Nome-do-Pai"
            ),
            flashcardsCount = 17,
            audioDuration = "15 min",
            audioTopicKey = "lacan_espelho",
            linkedDiscipline = "Teorias Psicanalíticas Contemporâneas"
        )
    )

    val timelineMilestones = listOf(
        TimelineMilestone(
            year = "1879",
            badge = "Marco Fundacional",
            title = "Nascimento da Psicologia Científica",
            description = "Wilhelm Wundt funda o primeiro laboratório de psicologia experimental na Universidade de Leipzig, separando formalmente a investigação psicológica da pura especulação metafísica e filosófica.",
            keyWork = "Princípios de Psicologia Fisiológica (1874)",
            linkedDiscipline = "Epistemologia & História da Psicologia"
        ),
        TimelineMilestone(
            year = "1896",
            badge = "Clínica Psicológica",
            title = "Fundação da Psicologia Clínica",
            description = "Lightner Witmer inaugura a primeira clínica psicológica do mundo na Universidade da Pensilvânia, cunhando o termo 'Psicologia Clínica' e direcionando o conhecimento científico para o alívio do sofrimento infantil e psicopatológico.",
            keyWork = "The Psychological Clinic (Witmer, 1907)",
            linkedDiscipline = "Introdução à Prática Clínica"
        ),
        TimelineMilestone(
            year = "1900",
            badge = "Revolução Inconsciente",
            title = "A Interpretação dos Sonhos",
            description = "Sigmund Freud publica Die Traumdeutung, inaugurando a Psicanálise. Consolida o modelo do aparelho psíquico inconsciente, o conteúdo manifesto vs. latente e o método da livre associação.",
            keyWork = "A Interpretação dos Sonhos (Freud, 1900)",
            linkedDiscipline = "Teorias Psicanalíticas I & II"
        ),
        TimelineMilestone(
            year = "1913",
            badge = "Paradigma Objetivista",
            title = "O Manifesto Behaviorista",
            description = "John B. Watson propõe a psicologia estritamente empírica focada no comportamento observável e mensurável (Estímulo-Resposta), rejeitando a introspecção estruturalista como método científico.",
            keyWork = "Psychology as the Behaviorist Views It (1913)",
            linkedDiscipline = "Análise Experimental do Comportamento"
        ),
        TimelineMilestone(
            year = "1921",
            badge = "Psicologia Analítica",
            title = "Tipos Psicológicos de Carl G. Jung",
            description = "Jung publica sua obra monumental conceituando atitudes fundamentais (introversão e extroversão) e as quatro funções psíquicas (pensamento, sentimento, sensação e intuição), alicerçando os modelos contemporâneos de personalidade.",
            keyWork = "Tipos Psicológicos (C.G. Jung, 1921)",
            linkedDiscipline = "Teorias da Personalidade"
        ),
        TimelineMilestone(
            year = "1936",
            badge = "Epistemologia Construtivista",
            title = "O Nascimento da Inteligência na Criança",
            description = "Jean Piaget publica investigações seminais demonstrando que a lógica e a cognição infantil não são versões miniaturizadas do pensamento adulto, mas estruturas qualitativamente distintas construídas ativamente.",
            keyWork = "O Nascimento da Inteligência na Criança (Piaget, 1936)",
            linkedDiscipline = "Psicologia do Desenvolvimento"
        ),
        TimelineMilestone(
            year = "1946",
            badge = "Terceira Escola Vienense",
            title = "Logoterapia & Em Busca de Sentido",
            description = "Viktor Frankl publica seu relato sobre a sobrevivência aos campos de concentração e formulação da Logoterapia, estabelecendo que a principal força motivadora humana é a busca de um sentido para a vida (vontade de sentido).",
            keyWork = "Em Busca de Sentido (Viktor Frankl, 1946)",
            linkedDiscipline = "Psicologia Existencial-Humanista"
        ),
        TimelineMilestone(
            year = "1953",
            badge = "Behaviorismo Radical",
            title = "Ciência e Comportamento Humano",
            description = "B.F. Skinner sistematiza a teoria do comportamento operante, estendendo os princípios de contingências reforçadoras da análise laboratorial para a cultura, educação, psicoterapia e controle social.",
            keyWork = "Ciência e Comportamento Humano (B.F. Skinner, 1953)",
            linkedDiscipline = "Análise Aplicada do Comportamento"
        ),
        TimelineMilestone(
            year = "1967",
            badge = "Revolução da TCC",
            title = "Fundação da Terapia Cognitiva da Depressão",
            description = "Aaron T. Beck publica seu modelo cognitivo estruturado demonstrando que estados depressivos são alimentados por distorções cognitivas sistemáticas e esquemas negativos disfuncionais de si, do mundo e do futuro.",
            keyWork = "Cognitive Therapy of Depression (Aaron T. Beck, 1967)",
            linkedDiscipline = "Terapias Cognitivo-Comportamentais"
        ),
        TimelineMilestone(
            year = "1969",
            badge = "Psicodinâmica Evolutiva",
            title = "Teoria do Apego de John Bowlby",
            description = "Bowlby publica o primeiro volume de 'Apego e Perda', revolucionando a compreensão do vínculo afetivo mãe-bebê, demonstrando que a necessidade de proximidade segura é uma adaptação biológica primária de sobrevivência.",
            keyWork = "Apego e Perda: Volume 1 (John Bowlby, 1969)",
            linkedDiscipline = "Psicologia do Desenvolvimento & Família"
        ),
        TimelineMilestone(
            year = "2022",
            badge = "Padronização Nosológica",
            title = "Lançamento do DSM-5-TR",
            description = "A Associação Americana de Psiquiatria lança o Manual Diagnóstico e Estatístico de Transtornos Mentais, 5ª edição - Texto Revisado, incorporando critérios atualizados para Transtorno do Luto Prolongado e especificadores socioculturais.",
            keyWork = "DSM-5-TR: Diagnostic and Statistical Manual of Mental Disorders",
            linkedDiscipline = "Psicopatologia & Semiologia Clínica"
        ),
        TimelineMilestone(
            year = "2023",
            badge = "Consolidação Global",
            title = "Implementação Integral da CID-11",
            description = "A Organização Mundial da Saúde (OMS) consolida a Classificação Internacional de Doenças (CID-11) nos sistemas de saúde mundiais, reestruturando a compreensão dimensional do Espectro Autista, TDAH adulto, Burnout como estresse crônico ocupacional e eliminando estigmas diagnósticos.",
            keyWork = "CID-11: Classificação Estatística Internacional de Doenças (OMS, 2023)",
            linkedDiscipline = "Psicopatologia & Saúde Coletiva"
        ),
        TimelineMilestone(
            year = "2024",
            badge = "Regulação & Evidências",
            title = "Marco Regulatório da Psicoterapia Digital no Brasil",
            description = "O Conselho Federal de Psicologia (CFP) publica resolução histórica regulamentando o uso de ferramentas de Inteligência Artificial, Realidade Virtual para dessensibilização sistemática de fobias e TEPT, e atendimentos online com rigor ético e proteção de dados sensíveis (LGPD).",
            keyWork = "Resolução CFP nº 09/2024 & Diretrizes de Práticas Psicoterápicas Digitais",
            linkedDiscipline = "Ética Profissional & Psicoterapia Contemporânea"
        ),
        TimelineMilestone(
            year = "2025",
            badge = "Neurociência Computacional",
            title = "Fenotipagem Digital & Biomarcadores do Humor",
            description = "Pesquisas seminais em neurociência computacional comprovam a eficácia da fenotipagem digital (análise de padrões de sono, digitação, mobilidade e prosódia de voz) como preditores precoces de recaídas em Transtorno Bipolar e Depressão Maior, viabilizando intervenções preventivas oportunas.",
            keyWork = "Digital Phenotyping and Computational Psychiatry (Nature Mental Health, 2025)",
            linkedDiscipline = "Bases Biológicas & Neuropsicologia"
        ),
        TimelineMilestone(
            year = "2026",
            badge = "Psicologia de Precisão",
            title = "Integração Biopsicossocial & Modelos Personalizados",
            description = "Ano corrente: A Psicologia consolida a prática clínica de precisão baseada em evidências. Terapeutas combinam a sensibilidade relacional empática e fenomenológica com Avaliação Ecológica Momentânea (EMA) em tempo real, personalizando protocolos de TCC, Psicanálise e Humanismo para as necessidades singulares de cada indivíduo.",
            keyWork = "Precision Psychotherapy and Ecological Momentary Assessment (2026 Handbook)",
            linkedDiscipline = "Clínica Psicológica Integrativa Contemporânea"
        )
    )

    val disciplines = listOf(
        Discipline(
            id = "disc_psicopatologia1",
            name = "Psicopatologia I",
            code = "PSI-302",
            professor = "Profª. Dra. Helena Siqueira",
            progressPercent = 72,
            currentModule = 3,
            totalModules = 4,
            syllabusSummary = "Semiologia dos transtornos mentais, critérios diagnósticos do DSM-5-TR e CID-11. Avaliação minuciosa das funções psíquicas elementares: Consciência, Humor, Afeto, Atenção e Sensopercepção.",
            learningObjectives = listOf(
                "Diferenciação semiológica: Tristeza patológica vs. Eutimia" to true,
                "Eixos diagnósticos e formulação psicopatológica" to true,
                "Seminário prático: Apresentação de Caso Clínico" to false
            ),
            materialsCount = 8,
            flashcardsCount = 36,
            audioDurationMin = 14,
            semester = 5
        ),
        Discipline(
            id = "disc_aprendizagem",
            name = "Processos de Aprendizagem e Cognição",
            code = "PSI-204",
            professor = "Prof. Dr. Eduardo Camargo",
            progressPercent = 65,
            currentModule = 2,
            totalModules = 4,
            syllabusSummary = "Estudo do condicionamento respondente e operante, equilibração piagetiana e fundamentos neuropsicológicos da memória, neuroplasticidade e fixação de conteúdos cognitivos.",
            learningObjectives = listOf(
                "Modelos de reforçamento contínuo e intermitente" to true,
                "Estágios de desenvolvimento cognitivo de Piaget" to true,
                "Consolidação de memória e sono REM" to false
            ),
            materialsCount = 4,
            flashcardsCount = 28,
            audioDurationMin = 10,
            semester = 5
        ),
        Discipline(
            id = "disc_desenvolvimento",
            name = "Psicologia do Desenvolvimento",
            code = "PSI-201",
            professor = "Prof. Dr. Marcos Vinicius",
            progressPercent = 80,
            currentModule = 4,
            totalModules = 4,
            syllabusSummary = "Fases biopsicossociais do ciclo de vida, crises psicossociais eriksonianas e a psicomotricidade afetiva de Henri Wallon aplicada ao desenvolvimento da infância à senescência.",
            learningObjectives = listOf(
                "8 estágios psicossociais de Erik Erikson" to true,
                "Desenvolvimento afetivo segundo Wallon" to true,
                "Teoria do Apego seguro vs inseguro" to true
            ),
            materialsCount = 6,
            flashcardsCount = 22,
            audioDurationMin = 12,
            semester = 5
        ),
        Discipline(
            id = "disc_personalidade",
            name = "Psicologia da Personalidade",
            code = "PSI-305",
            professor = "Profª. Dra. Beatriz Campos",
            progressPercent = 55,
            currentModule = 2,
            totalModules = 4,
            syllabusSummary = "Mecanismos de defesa, estrutura do aparelho psíquico freudiano, traços de personalidade fatoriais (Big Five / OCEAN) e autorrealização na psicologia humanista.",
            learningObjectives = listOf(
                "Diferenciação entre traço e estado de personalidade" to true,
                "Modelo Big Five (OCEAN) e instrumentos psicométricos" to false,
                "Teoria Fenomenológica de Carl Rogers" to false
            ),
            materialsCount = 5,
            flashcardsCount = 19,
            audioDurationMin = 8,
            semester = 5
        ),
        Discipline(
            id = "disc_biologicas",
            name = "Bases Biológicas do Comportamento",
            code = "PSI-103",
            professor = "Prof. Dr. Ricardo Arantes",
            progressPercent = 90,
            currentModule = 4,
            totalModules = 4,
            syllabusSummary = "Vias dopaminérgicas e serotoninérgicas, regulação neuroendócrina do estresse pelo eixo HPA e controle executivo inibitório córtex pré-frontal.",
            learningObjectives = listOf(
                "Sinapses químicas e receptores de membrana" to true,
                "Estrutura do Sistema Límbico e Eixo HPA" to true,
                "Funções executivas pré-frontais e inibição" to true
            ),
            materialsCount = 7,
            flashcardsCount = 24,
            audioDurationMin = 16,
            semester = 5
        )
    )

    val glossaryTerms = listOf(
        GlossaryTerm(
            id = "gloss_eutimia",
            term = "Eutimia",
            category = "Semiologia Clínica",
            phonetic = "/eu-ti-mi-a/",
            wordClass = "Substantivo feminino",
            definition = "Estado de humor e tônus afetivo basal caracterizado pela oscilação dentro de parâmetros adaptativos e normais. Diferencia-se pela ausência de elação maníaca e ausência de inibição/rebaixamento depressivo patológico.",
            clinicalDifferential = "Não confundir eutimia com apatia ou embotamento afetivo. No paciente eutímico, a capacidade de ressonância emocional e modulação afetiva diante de estímulos permanece preservada.",
            linkedLesson = "Transtornos do Humor • Psicopatologia I",
            polarDiagram = listOf(
                Triple("Pólo Hipomaníaco", "+ Hipertimia", "Expansividade"),
                Triple("Faixa Central", "Eutimia", "Equilíbrio Afetivo"),
                Triple("Pólo Depressivo", "- Hipotimia", "Anedonia")
            )
        ),
        GlossaryTerm(
            id = "gloss_catatonia",
            term = "Catatonia",
            category = "Semiologia Psiquiátrica",
            phonetic = "/ca-ta-to-ni-a/",
            wordClass = "Substantivo feminino",
            definition = "Síndrome neuropsiquiátrica psicomotora caracterizada por catalepsia, flexibilidade cérea, estupor, negativismo, posturas bizarras mantidas ativamente ou agitação psicomotora desprovida de finalidade.",
            clinicalDifferential = "Pode ocorrer tanto na esquizofrenia quanto em transtornos de humor graves (depressão psicótica e mania) e condições neurológicas gerais.",
            linkedLesson = "Transtornos do Espectro da Esquizofrenia"
        ),
        GlossaryTerm(
            id = "gloss_metacognicao",
            term = "Metacognição",
            category = "Psicologia Cognitiva",
            phonetic = "/me-ta-cog-ni-ção/",
            wordClass = "Substantivo feminino",
            definition = "Capacidade do sujeito de monitorar, avaliar e autorregular seus próprios processos de pensamento, aprendizagem e tomada de decisão ('pensar sobre o próprio pensar').",
            clinicalDifferential = "Componente essencial nas terapias cognitivo-comportamentais (TCC) para identificação de distorções cognitivas e pensamentos automáticos disfuncionais.",
            linkedLesson = "Processos de Aprendizagem e Cognição"
        ),
        GlossaryTerm(
            id = "gloss_pulsao",
            term = "Pulsão (Trieb)",
            category = "Metapsicologia",
            phonetic = "/pul-são/",
            wordClass = "Substantivo feminino",
            definition = "Conceito metapsicológico limítrofe entre o somático e o psíquico; representa uma força constante proveniente do corpo biológico que exige trabalho de elaboração psíquica para alcançar satisfação.",
            clinicalDifferential = "Distingue-se do instinto puramente biológico pela extrema plasticidade de seu objeto e de sua meta.",
            linkedLesson = "Teorias Psicanalíticas I"
        ),
        GlossaryTerm(
            id = "gloss_dsm_depressao",
            term = "Transtorno Depressivo Maior (F32/F33)",
            category = "Correlação Diagnóstica DSM-5-TR",
            phonetic = "/de-pres-são-ma-ior/",
            wordClass = "Transtorno do Humor",
            definition = "Quadro sindrômico caracterizado por pelo menos 5 dos 9 critérios do DSM-5-TR presentes por no mínimo 2 semanas consecutivas, representando mudança no funcionamento prévio. Requer obrigatoriamente: (1) Humor deprimido na maior parte do dia e/ou (2) Anedonia marcante, acompanhados de alterações de sono, peso/apetite, fadiga, retardo/agitação psicomotora, sentimentos de desvalia/culpa excessiva, redução da concentração e ideação suicida recorrente.",
            clinicalDifferential = "Diagnóstico Diferencial: Luto normal (predomínio de ondas de tristeza intermitentes com autoestima preservada vs. culpa pervasiva e desvalia global no TDM); Hipotireoidismo; Transtorno Bipolar (investigar histórico de virada maníaca/hipomaníaca prévia).",
            linkedLesson = "Psicopatologia Clínica I • DSM-5-TR & CID-11",
            polarDiagram = listOf(
                Triple("Critério A Obrigatório", "Humor Deprimido", "Anedonia Marcante"),
                Triple("Duração Mínima", "2 Semanas", "Quase Todos os Dias"),
                Triple("Especificador Gravidade", "Leve / Moderado", "Grave com Sintomas Psicóticos")
            )
        ),
        GlossaryTerm(
            id = "gloss_dsm_tag",
            term = "Transtorno de Ansiedade Generalizada (TAG - F41.1)",
            category = "Correlação Diagnóstica DSM-5-TR",
            phonetic = "/an-sie-da-de-ge-ne-ra-li-za-da/",
            wordClass = "Transtorno de Ansiedade",
            definition = "Ansiedade e preocupação excessivas (expectativa apreensiva), de difícil controle pelo indivíduo, ocorrendo na maioria dos dias por pelo menos 6 meses, acerca de diversos eventos ou atividades cotidianas. Associada a 3 ou mais sintomas: inquietude, fatigabilidade fácil, dificuldade de concentração, irritabilidade, tensão muscular e perturbação do sono.",
            clinicalDifferential = "Diferencial com Transtorno de Pânico (ausência de crises agudas paroxísticas e imprevisíveis de terror com sintomas autonômicos intensos) e Fobia Social (ansiedade circunscrita a situações de avaliação de pares).",
            linkedLesson = "Psicopatologia Clínica I • DSM-5-TR",
            polarDiagram = listOf(
                Triple("Temporalidade", ">= 6 Meses", "Crônica & Peremptória"),
                Triple("Sintomas Fisiológicos", "Tensão Muscular", "Insônia Inicial"),
                Triple("Foco Cognitivo", "Expectativa Apreensiva", "Catastrofização")
            )
        ),
        GlossaryTerm(
            id = "gloss_dsm_bipolar",
            term = "Transtorno Bipolar Tipo I e II (F31)",
            category = "Correlação Diagnóstica DSM-5-TR",
            phonetic = "/trans-tor-no-bi-po-lar/",
            wordClass = "Transtorno do Humor",
            definition = "Bipolar Tipo I exige pelo menos um Episódio Maníaco ao longo da vida (humor anormal e persistentemente elevado, expansivo ou irritável e aumento de energia por no mínimo 1 semana com prejuízo funcional acentuado ou hospitalização). Bipolar Tipo II exige pelo menos um Episódio Hipomaníaco (mínimo de 4 dias contínuos sem prejuízo funcional grave) e pelo menos um Episódio Depressivo Maior.",
            clinicalDifferential = "Diferencial crucial com Transtorno de Personalidade Borderline: no TBipolar a oscilação é episódica sustentada (semanas a meses), enquanto no Borderline a instabilidade afetiva é reativa a estressores relacionais interpessoais imediatos (horas a poucos dias).",
            linkedLesson = "Psicopatologia Clínica II • Transtornos do Humor",
            polarDiagram = listOf(
                Triple("Pólo Maníaco (TB I)", ">= 7 Dias", "Grandiosidade & Fuga de Ideias"),
                Triple("Pólo Hipomaníaco (TB II)", ">= 4 Dias", "Produtividade sem Psicose"),
                Triple("Pólo Depressivo", ">= 14 Dias", "Inibição Psicomotora Grave")
            )
        ),
        GlossaryTerm(
            id = "gloss_dsm_luto",
            term = "Transtorno do Luto Prolongado (F43.8)",
            category = "Novidade Nosológica DSM-5-TR",
            phonetic = "/lu-to-pro-lon-ga-do/",
            wordClass = "Transtorno Relacionado a Trauma e Estressores",
            definition = "Entidade nosológica incluída formalmente no DSM-5-TR em 2022. Caracteriza-se pela morte de alguém próximo há pelo menos 12 meses (em adultos, ou 6 meses em crianças/adolescentes), acompanhada de anseio ou saudade intensa e persistente pelo falecido e/ou preocupação mental constante com a pessoa falecida, associada a dor emocional paralisante, descrença e isolamento social crônico.",
            clinicalDifferential = "Diferencia-se do luto culturalmente esperado pelo tempo decorrido e pela gravidade incapacitante; diferencia-se da Depressão Maior porque a dor e as ruminações gravitam especificamente em torno da perda do ente querido e não de sentimentos difusos de inutilidade pessoal.",
            linkedLesson = "Atualizações DSM-5-TR & Psicopatologia",
            polarDiagram = listOf(
                Triple("Janela Temporal", ">= 12 Meses (Adultos)", "Persistência Diária"),
                Triple("Eixo Central", "Anseio Intenso pelo Falecido", "Quebra de Continuidade do Self"),
                Triple("Impacto", "Prejuízo Social & Ocupacional", "Incapacidade de Reengajamento")
            )
        ),
        GlossaryTerm(
            id = "gloss_dsm_borderline",
            term = "Transtorno da Personalidade Borderline (TPB - F60.3)",
            category = "Correlação Diagnóstica DSM-5-TR",
            phonetic = "/bor-der-li-ne/",
            wordClass = "Transtorno de Personalidade do Grupo B",
            definition = "Padrão difuso de instabilidade das relações interpessoais, da autoimagem e dos afetos, com acentuada impulsividade, surgindo no início da vida adulta. Apresenta pelo menos 5 de 9 critérios: esforços desesperados para evitar abandono real ou imaginado; padrão de relações instáveis e intensas (clivagem: idealização vs. desvalorização); perturbação da identidade; impulsividade autodestrutiva; ideação paranoide transitória ou sintomas dissociativos graves.",
            clinicalDifferential = "Diferencial com Transtorno Bipolar, Transtorno de Personalidade Histriônica e TEPT Complexo. Exige avaliação longitudinal da estrutura de personalidade e defesas primitivas (clivagem, identificação projetiva).",
            linkedLesson = "Psicopatologia Clínica II • Transtornos de Personalidade",
            polarDiagram = listOf(
                Triple("Mecanismo de Defesa", "Clivagem (Splitting)", "Idealização / Desvalorização"),
                Triple("Pavor Central", "Abandono & Vazio Crônico", "Desesperança Relacional"),
                Triple("Comportamento", "Automutilação / Parassuicídio", "Impulsividade Reativa")
            )
        ),
        GlossaryTerm(
            id = "gloss_dsm_tdah",
            term = "TDAH: Transtorno de Déficit de Atenção / Hiperatividade (F90)",
            category = "Correlação Diagnóstica DSM-5-TR",
            phonetic = "/t-d-a-h/",
            wordClass = "Transtorno do Neurodesenvolvimento",
            definition = "Padrão persistente de desatenção e/ou hiperatividade-impulsividade que interfere no funcionamento ou no desenvolvimento. Requer pelo menos 6 sintomas (ou 5 em adultos a partir de 17 anos) de desatenção e/ou hiperatividade presentes por no mínimo 6 meses em pelo menos dois ambientes distintos (ex: trabalho/escola e lar). Vários sintomas devem ter surgido antes dos 12 anos de idade.",
            clinicalDifferential = "Diferencial com Ansiedade Generalizada (onde a desatenção decorre de preocupação intrusiva), Transtorno Bipolar (em que a aceleração é episódica e não crônica desde a infância) e Transtornos de Aprendizagem específicos (dislexia/discalculia).",
            linkedLesson = "Psicopatologia do Neurodesenvolvimento • DSM-5-TR",
            polarDiagram = listOf(
                Triple("Apresentação Clínica", "Desatento / Hiperativo", "Combinado (Misto)"),
                Triple("Início dos Sintomas", "Antes dos 12 Anos", "Manifestação em >= 2 Ambientes"),
                Triple("Funções Executivas", "Memória Operacional", "Controle Inibitório Prejudicado")
            )
        ),
        GlossaryTerm(
            id = "gloss_dsm_tea",
            term = "TEA: Transtorno do Espectro Autista (F84.0)",
            category = "Correlação Diagnóstica DSM-5-TR",
            phonetic = "/es-pec-tro-au-tis-ta/",
            wordClass = "Transtorno do Neurodesenvolvimento",
            definition = "Déficits persistentes na comunicação e na interação social em múltiplos contextos (reciprocidade socioemocional, comportamentos comunicativos não verbais e desenvolvimento/manutenção de relações), acompanhados de padrões restritos e repetitivos de comportamento, interesses ou atividades (estereotipias motoras, inflexibilidade a rotinas, interesses altamente restritos e hiper ou hiporreatividade a estímulos sensoriais).",
            clinicalDifferential = "Diferencial com Transtorno da Comunicação Social (Pragmática), em que não há presença de padrões repetitivos/restritos de comportamento; e Deficiência Intelectual (investigar desenvolvimento social proporcional à idade mental).",
            linkedLesson = "Psicopatologia Infantil & Neurodesenvolvimento",
            polarDiagram = listOf(
                Triple("Níveis de Apoio", "Nível 1 (Exige Apoio)", "Nível 3 (Apoio Muito Substancial)"),
                Triple("Domínio A", "Comunicação Social", "Reciprocidade Emocional"),
                Triple("Domínio B", "Padrões Repetitivos", "Reatividade Sensorial Atípica")
            )
        ),
        GlossaryTerm(
            id = "gloss_dsm_tept",
            term = "TEPT: Transtorno de Estresse Pós-Traumático (F43.10)",
            category = "Correlação Diagnóstica DSM-5-TR",
            phonetic = "/t-e-p-t/",
            wordClass = "Transtornos Relacionados a Trauma e Estressores",
            definition = "Desenvolve-se após exposição a evento traumático real ou ameaça de morte, lesão grave ou violência sexual (experiência direta, testemunho presencial ou saber que ocorreu a ente próximo). Caracteriza-se por 4 grupos de sintomas com duração superior a 1 mês: (1) Intrusão (flashbacks, memórias angustiantes, pesadelos); (2) Evitação persistente de estímulos associados; (3) Alterações negativas na cognição e no humor; (4) Alterações marcantes na excitação e reatividade (hipervigilância, sobressalto exagerado).",
            clinicalDifferential = "Diferencial com Transtorno de Estresse Agudo (sintomas duram entre 3 dias e 1 mês imediatamente após o trauma) e Transtorno de Ajustamento (resposta desadaptativa a estressor não-catastrófico).",
            linkedLesson = "Psicopatologia do Trauma • DSM-5-TR",
            polarDiagram = listOf(
                Triple("Intrusão Re-experiência", "Flashbacks Traumáticos", "Pesadelos Conexos"),
                Triple("Comportamento Evitativo", "Evitação de Lugares/Pessoas", "Amnésia Psicogênica do Evento"),
                Triple("Reatividade & Alerta", "Hipervigilância Paranoide", "Insônia & Sobressalto Contínuo")
            )
        ),
        GlossaryTerm(
            id = "gloss_dsm_toc",
            term = "TOC: Transtorno Obsessivo-Compulsivo (F42)",
            category = "Correlação Diagnóstica DSM-5-TR",
            phonetic = "/t-o-c/",
            wordClass = "Transtorno Obsessivo-Compulsivo e Relacionados",
            definition = "Presença de obsessões (pensamentos, impulsos ou imagens mentais recorrentes, intrusivos e indesejados que causam ansiedade acentuada) e/ou compulsões (comportamentos repetitivos ou atos mentais que a pessoa se sente compelida a executar para neutralizar a ansiedade ou prevenir desastre temido). Consomem mais de 1 hora por dia ou causam sofrimento clínico significativo.",
            clinicalDifferential = "Diferencial com Transtorno da Personalidade Obsessivo-Compulsiva (TPOC - caracterizado por perfeccionismo e rigidez egosintônicos, sem obsessões/compulsões verdadeiras); Transtorno Dismórfico Corporal e Transtornos de Ansiedade.",
            linkedLesson = "Psicopatologia Clínica I • DSM-5-TR",
            polarDiagram = listOf(
                Triple("Obsessões (Cognitivo)", "Pensamento Intrusivo Egodistônico", "Dúvida Patológica / Contaminação"),
                Triple("Compulsões (Comportamental)", "Rituais de Neutralização", "Checagem / Limpeza / Simetria"),
                Triple("Insight Diagnóstico", "Bom / Razoável", "Ausente / Convicção Delirante")
            )
        ),
        GlossaryTerm(
            id = "gloss_dsm_esquizofrenia",
            term = "Esquizofrenia e Transtornos Psicóticos (F20)",
            category = "Correlação Diagnóstica DSM-5-TR",
            phonetic = "/es-qui-zo-fre-ni-a/",
            wordClass = "Espectro da Esquizofrenia e Outros Transtornos Psicóticos",
            definition = "Exige pelo menos 2 de 5 sintomas psicóticos presentes na maior parte de 1 mês (com sinais contínuos da perturbação por pelo menos 6 meses). Pelo menos um sintoma deve ser: (1) Delírios (persecutórios, de referência, místicos); (2) Alucinações (predominantemente auditivo-verbais); ou (3) Discurso desorganizado (incoerência, descarrilhamento). Os outros 2 sintomas são comportamento grosseiramente desorganizado/catatônico e sintomas negativos (avolia, embotamento afetivo, anedonia, alogia).",
            clinicalDifferential = "Diferencial com Transtorno Esquizoafetivo (episódios de humor concorrem concomitantemente com a psicose); Transtorno Delirante (apenas delírios não bizarros sem alucinações ou embotamento) e Psicose Induzida por Substâncias.",
            linkedLesson = "Psicopatologia Clínica II • Psicoses & Semiologia",
            polarDiagram = listOf(
                Triple("Sintomas Positivos", "Delírios Persecutórios", "Alucinações Auditivas"),
                Triple("Sintomas Negativos", "Avolia & Embotamento Afetivo", "Isolamento Social Grave"),
                Triple("Eixo Temporal", "Fase Prodrômica", ">= 6 Meses de Declínio Funcional")
            )
        )
    )

    val decks = listOf(
        Deck(
            id = "deck_psico",
            title = "Psicopatologia: Transtornos de Ansiedade e Humor",
            category = "Semiologia & DSM-5-TR",
            totalCards = 36,
            dailyGoal = 12,
            masteredCount = 24,
            isCurrent = true
        ),
        Deck(
            id = "deck_neuro",
            title = "Bases Biológicas: Neurotransmissores (Dopamina, GABA)",
            category = "Neurociência",
            totalCards = 24,
            dailyGoal = 8,
            masteredCount = 18,
            isCurrent = false
        ),
        Deck(
            id = "deck_psicanalise",
            title = "Psicanálise: Tópicas Freudianas e Mecanismos",
            category = "Teorias Psicanalíticas",
            totalCards = 32,
            dailyGoal = 10,
            masteredCount = 20,
            isCurrent = false
        )
    )

    val initialFlashcards = listOf(
        Flashcard(
            id = "card_1",
            deckId = "deck_psico",
            category = "Semiologia Psiquiátrica",
            question = "Qual a diferença clínica fundamental entre Alucinação e Ilusão na Semiologia Psiquiátrica?",
            hint = "Pense na presença ou ausência de estímulo sensorial real no ambiente imediato do paciente.",
            answer = "A Ilusão é a percepção deformada de um objeto real e presente no espaço exterior. Já a Alucinação é a percepção clara e definida sem a presença de um estímulo sensorial real (percepção sem objeto).",
            citation = "Dalgalarrondo, Psicopatologia e Semiologia dos Transtornos Mentais, Cap. 9",
            status = CardMasteryStatus.NOVO
        ),
        Flashcard(
            id = "card_2",
            deckId = "deck_psico",
            category = "Transtornos do Humor",
            question = "Quais são os 2 sintomas cardinais do Episódio Depressivo Maior segundo o DSM-5-TR?",
            hint = "Pelo menos um desses dois sintomas deve estar presente de forma contínua por no mínimo 2 semanas.",
            answer = "1) Humor deprimido na maior parte do dia, quase todos os dias; e 2) Anedonia acentuada (perda acentuada do interesse ou prazer em todas ou quase todas as atividades).",
            citation = "Manual Diagnóstico e Estatístico de Transtornos Mentais DSM-5-TR, p. 182",
            status = CardMasteryStatus.BOM
        ),
        Flashcard(
            id = "card_3",
            deckId = "deck_psico",
            category = "Exame Psíquico",
            question = "O que caracteriza a Fuga de Ideias no exame do curso do pensamento?",
            hint = "Observe a velocidade e as associações que desviam constantemente do foco inicial.",
            answer = "Aceleração extrema do curso do pensamento, onde o indivíduo passa velozmente de um tema a outro estimulado por associações superficiais (sonoridade, rimas) ou estímulos do ambiente, perdendo a ideia diretriz.",
            citation = "Dalgalarrondo, Cap. 12 (Pensamento)",
            status = CardMasteryStatus.DOMINADO
        ),
        Flashcard(
            id = "card_4",
            deckId = "deck_neuro",
            category = "Neurociência",
            question = "Qual o principal neurotransmissor inibitório do SNC e como os benzodiazepínicos atuam nele?",
            hint = "Potencializa a entrada de íons Cloro hiperpolarizando o neurônio.",
            answer = "O GABA (Ácido Gama-Aminobutírico). Os benzodiazepínicos atuam como moduladores alostéricos positivos dos receptores GABA-A, aumentando a frequência de abertura dos canais de Cloro e diminuindo a excitabilidade neuronal.",
            citation = "Bear, Connors & Paradiso - Neurociências: Desvendando o Sistema Nervoso",
            status = CardMasteryStatus.DOMINADO
        )
    )

    val initialNotes = listOf(
        Note(
            id = "note_1",
            title = "Inconsciente em Lacan vs Freud",
            timeAgo = "há 2 horas",
            content = "O inconsciente freudiano como reservatório pulsional e de representações recalcadas. Em Jacques Lacan, a formulação 'O inconsciente é estruturado como uma linguagem': o significante representa o sujeito para outro significante, demarcando a falta fundante do desejo.",
            linkedTopic = "Mecanismos de Defesa & Aparelho Psíquico",
            discipline = "Psicologia da Personalidade",
            tags = listOf("Psicanálise", "Lacan", "Freud", "Linguagem")
        ),
        Note(
            id = "note_2",
            title = "Observações de Campo - Triagem Psiquiátrica",
            timeAgo = "ontem",
            content = "Paciente apresentou preservação da orientação alopsíquica e autopsíquica. Humor hipertímico com logorreia e taquipsiquismo. Hipótese inicial a discutir na supervisão clínica: episódio hipomaníaco vs reação induzida.",
            linkedTopic = "Exame das Funções Psíquicas",
            discipline = "Psicopatologia I",
            tags = listOf("Estágio", "Semiologia", "Supervisão")
        )
    )

    val initialAudioPodcasts = listOf(
        PodcastAudio(
            id = "audio_1",
            episodeTitle = "Ep. 07: O Complexo de Édipo e a Estrutura Psíquica",
            speakerAndDiscipline = "Prof. Dra. Helena S. Guimarães & Síntese IA • Psicanálise I",
            totalDurationSeconds = 1125, // 18:45
            currentPositionSeconds = 372, // 06:12
            isPlaying = false,
            speed = 1.25f,
            tag = "Psicanálise I"
        ),
        PodcastAudio(
            id = "audio_2",
            episodeTitle = "Ep. 04: Condicionamento Operante de Skinner",
            speakerAndDiscipline = "Prof. Dr. Eduardo Camargo • Análise do Comportamento",
            totalDurationSeconds = 1680, // 28:00
            currentPositionSeconds = 860, // 14:20
            isPlaying = false,
            speed = 1.0f,
            tag = "Análise do Comportamento"
        ),
        PodcastAudio(
            id = "audio_3",
            episodeTitle = "Síntese de Aula: Neurotransmissores e Psicofarmacologia",
            speakerAndDiscipline = "Gerado da apostila oficial • Bases Biológicas",
            totalDurationSeconds = 500, // 08:20
            currentPositionSeconds = 0,
            isPlaying = false,
            speed = 1.0f,
            tag = "Síntese de Aula"
        )
    )

    val initialMaterials = listOf(
        StudyMaterial(
            id = "mat_1",
            title = "Manual de Diagnóstico DSM-5-TR (Guia de Bolso).pdf",
            type = MaterialType.PDF,
            size = "8.4 MB",
            updatedAt = "Atualizado há 2 dias",
            highlightsCount = 42,
            linkedNotesCount = 3,
            linkedDiscipline = "Psicopatologia Clínica",
            flashcardsCount = 18,
            verbetesCount = 6,
            aiSummary = "Compêndio de critérios diagnósticos com códigos CID-10 e CID-11 atualizados. Foco em transtornos neurodesenvolvimento, de humor e ansiedade.",
            isAudioTranscribed = false
        ),
        StudyMaterial(
            id = "mat_2",
            title = "Gravação da Aula 08 - Psicologia Social e Identidade Grupal.mp3",
            type = MaterialType.AUDIO,
            size = "52 min",
            updatedAt = "Transcrito e indexado",
            highlightsCount = 14,
            linkedNotesCount = 2,
            linkedDiscipline = "Psicologia Social II",
            flashcardsCount = 14,
            verbetesCount = 2,
            aiSummary = "Transcrição inteligente destacando teoria da identidade social de Tajfel, preconceito intergrupal e conformidade nos experimentos de Asch.",
            isAudioTranscribed = true
        ),
        StudyMaterial(
            id = "mat_3",
            title = "Anotações de Estágio - Hospital Dia.docx",
            type = MaterialType.DOCX,
            size = "1.2 MB",
            updatedAt = "Confidencialidade resguardada",
            highlightsCount = 8,
            linkedNotesCount = 5,
            linkedDiscipline = "Estágio Básico I",
            flashcardsCount = 8,
            verbetesCount = 1,
            aiSummary = "Casos clínicos e diário de campo estruturado conforme o Código de Ética do CFP, preservando sigilo com nomes fictícios.",
            isAudioTranscribed = false
        ),
        StudyMaterial(
            id = "mat_4",
            title = "A Teoria do Apego na Clínica Contemporânea.pdf",
            type = MaterialType.PDF,
            size = "3.8 MB",
            updatedAt = "Processado recentemente",
            highlightsCount = 27,
            linkedNotesCount = 4,
            linkedDiscipline = "Psicologia do Desenvolvimento",
            flashcardsCount = 16,
            verbetesCount = 4,
            aiSummary = "Estrutura assimilada com sucesso: padrões de apego seguro, ansioso-evitativo, desorganizado e implicações no vínculo terapêutico.",
            isAudioTranscribed = true
        )
    )

    val initialClinicalCases = listOf(
        ClinicalCase(
            id = "case_1",
            title = "Caso Clínico: Crises Repentinas de Sufocamento e Medo de Morte",
            patientAge = "Paciente R.S., 28 anos, advogada júnior",
            anamneseVignette = "A paciente procura atendimento queixando-se de episódios súbitos de taquicardia intensa, sensação de falta de ar, sudorese nas mãos e sensação iminente de desmaio ou enfarte. Os episódios iniciaram há 3 meses no transporte público e agora ocorrem até em repouso. Já realizou dois eletrocardiogramas e exames laboratoriais na emergência médica, todos sem alterações orgânicas. Passou a evitar estações de metrô, reuniões presenciais e supermercados lotados por medo de ter uma nova crise e não conseguir socorro rápido.",
            sourceMaterialOrDiscipline = "Baseado em: Manual de Diagnóstico DSM-5-TR & Psicopatologia Clínica",
            mentalStateExam = "Consciência clara, orientada alopsiquicamente e autopsiquicamente. Humor ansioso, afeto congruente com aflição, taquipsiquismo leve durante o relato das crises. Ausência de alterações na sensopercepção. Crítica preservada quanto à desproporção do medo, mas com esquemas de hipervigilância somática.",
            questions = listOf(
                ClinicalQuestion(
                    id = "q_1_1",
                    prompt = "De acordo com os critérios diagnósticos do DSM-5-TR, qual é a hipótese diagnóstica principal para o quadro apresentado por R.S.?",
                    options = listOf(
                        "Transtorno de Ansiedade Generalizada (TAG) isolado",
                        "Transtorno de Pânico com Agorafobia comórbida",
                        "Fobia Social Específica de Desempenho",
                        "Transtorno de Somatização com conversão motora"
                    ),
                    correctOptionIndex = 1,
                    explanation = "O quadro preenche critérios para Transtorno de Pânico devido aos ataques inesperados e recorrentes seguidos de apreensão contínua (ansiedade antecipatória), associado à Agorafobia pela esquiva de situações onde o escape ou auxílio seria difícil (transporte público, multidões)."
                ),
                ClinicalQuestion(
                    id = "q_1_2",
                    prompt = "Na perspectiva da Terapia Cognitivo-Comportamental (TCC) de Aaron Beck e David Clark, qual é o mecanismo cognitivo central que deflagra o círculo vicioso do pânico?",
                    options = listOf(
                        "Interpretação catastrófica de sensações corporais normais ou de ansiedade como sinal de catástrofe iminente (ex: taquicardia = infarto)",
                        "Regressão pulsional ao estágio oral com projeção esquizoparanoide",
                        "Condicionamento meramente operante sem mediação de esquemas de crenças",
                        "Inibição recíproca passiva sem resposta do sistema nervoso autônomo simpático"
                    ),
                    correctOptionIndex = 0,
                    explanation = "O modelo cognitivo do pânico (Clark) demonstra que sensações fisiológicas inócuas são erroneamente interpretadas como ameaça letal (morte, enlouquecimento, desmaio), gerando mais adrenalina e confirmando falsamente a catástrofe."
                ),
                ClinicalQuestion(
                    id = "q_1_3",
                    prompt = "Qual intervenção clínica padrão-ouro baseada em evidências é prioritária para quebrar o ciclo de esquiva agorafóbica?",
                    options = listOf(
                        "Recomendar que a paciente nunca mais use transporte público para evitar desconforto",
                        "Psicoeducação sobre o pânico, reestruturação das cognições catastróficas e exposição interoceptiva e in vivo gradual",
                        "Livre associação sem intervenção comportamental",
                        "Isolamento domiciliar até cessarem completamente as crises"
                    ),
                    correctOptionIndex = 1,
                    explanation = "A intervenção recomendada envolve psicoeducação fisiológica, dessensibilização interoceptiva (expor às sensações de tontura e respiração) e exposição gradual hierarquizada aos locais temidos sem rituais de segurança."
                )
            ),
            dsm5DiagnosticRationale = "F41.0 Transtorno de Pânico e F40.00 Agorafobia. Presença de ataques recorrentes e inesperados com pelo menos 4 dos 13 sintomas autonômicos, preocupação persistente com novos ataques e modificação desadaptativa do comportamento.",
            therapeuticApproach = "TCC (Conceitualização Cognitiva, RPD, treino de respiração diafragmática, exposição interoceptiva e in vivo) com monitoramento interdisciplinar psiquiátrico."
        ),
        ClinicalCase(
            id = "case_2",
            title = "Caso Clínico: Lentificação Psicomotora, Anedonia Severa e Ideação de Ruína",
            patientAge = "Paciente M.A., 52 anos, professor universitário afastado",
            anamneseVignette = "Paciente é trazido pela esposa para avaliação clínica com histórico de perda progressiva de vitalidade há 4 meses após reprovação de projeto acadêmico. Relata incapacidade total de sentir prazer (anedonia), perda de 6 kg sem dieta, insônia terminal (despertares diários às 3h30 da manhã sem conseguir retornar ao sono) e piora matinal expressiva do humor. Afirma convicção de que 'arruinou a família' e que 'nada mais tem concerto'. Abandonou a leitura e recusa contato com colegas de trabalho.",
            sourceMaterialOrDiscipline = "Baseado em: Psicopatologia dos Transtornos do Humor & DSM-5-TR",
            mentalStateExam = "Lentificação psicomotora acentuada, fácies depressiva, voz monocórdica em tom baixo. Orientado no tempo e espaço. Humor hipotímico grave, afeto embotado. Pensamento com curso lentificado (bradipsiquismo), ideação de culpa desproporcional e ruína existencial. Nega planos estruturados de suicídio, mas expressa desesperança e passividade existencial ('se eu não acordasse amanhã seria um alívio').",
            questions = listOf(
                ClinicalQuestion(
                    id = "q_2_1",
                    prompt = "Identifique o especificador clínico do DSM-5-TR mais compatível com os sintomas de insônia terminal, piora matinal, anedonia completa e perda de peso:",
                    options = listOf(
                        "Com características atípicas",
                        "Com características melancólicas",
                        "Com sintomas ansiosos leves",
                        "Com padrão estacional (SAD)"
                    ),
                    correctOptionIndex = 1,
                    explanation = "O especificador 'com características melancólicas' exige anedonia profunda e/ou falta de reatividade a estímulos prazerosos, acompanhado de piora matinal acentuada, despertar matinal precoce (insônia terminal), retardo psicomotor marcante e culpa excessiva."
                ),
                ClinicalQuestion(
                    id = "q_2_2",
                    prompt = "Na avaliação de risco clínico e conduta ética pelo Código de Ética do Psicólogo, qual deve ser a postura prioritária frente à fala de desesperança?",
                    options = listOf(
                        "Desconsiderar a queixa por se tratar de fala passageira",
                        "Investigação ativa de risco de suicídio, pactuação de rede de apoio familiar imediata e encaminhamento interdisciplinar urgente para avaliação psiquiátrica",
                        "Encerrar o atendimento clínico unilateralmente sem orientar a família",
                        "Garantir sigilo absoluto sem permitir qualquer contato com familiares mesmo com risco iminente à vida"
                    ),
                    correctOptionIndex = 1,
                    explanation = "O psicólogo deve investigar ativamente a ideação suicida, acionar a rede de apoio familiar e encaminhar com urgência ao médico psiquiatra para suporte farmacológico, visto o componente biológico e o risco vital da depressão melancólica."
                )
            ),
            dsm5DiagnosticRationale = "F32.2 Transtorno Depressivo Maior, Episódio Único, Grave, Com Características Melancólicas. Humor deprimido e anedonia contínuos por mais de 2 semanas com repercussão somática e psicomotora severa.",
            therapeuticApproach = "Abordagem multidisciplinar: Ativação Comportamental (BA) na TCC aliada a tratamento farmacológico (ISRS/IRSN) e psicoeducação da família."
        ),
        ClinicalCase(
            id = "case_3",
            title = "Caso Clínico: Formulação Psicanalítica - Sintoma Fóbico e Relação Materna",
            patientAge = "Paciente L.T., 21 anos, estudante universitária",
            anamneseVignette = "A paciente busca terapia devido a uma inibição intelectual repentina ao redigir seu artigo de conclusão de curso. Descreve que sua mãe é professora de literatura premiada e que sente 'um bloqueio asfixiante' sempre que senta para escrever, acompanhado de crises de choro e autocrítica mordaz. Relata que sonha repetidamente que suas palavras 'se desmancham em cinzas' diante do olhar da mãe. Na transferência, expressa constante receio de que o terapeuta a considere 'medíocre ou burra'.",
            sourceMaterialOrDiscipline = "Baseado em: Teorias Psicanalíticas & Anotações de Estágio Supervisionado",
            mentalStateExam = "Apresenta-se lúcida, eloquente, com rica elaboração verbal. Humor ansioso com flutuação neurótica. Afeto modulado, com intensa ambivalência afetiva em relação à figura materna (admiração idealizada vs. agressividade recalcada). Crítica preservada.",
            questions = listOf(
                ClinicalQuestion(
                    id = "q_3_1",
                    prompt = "Sob a ótica metapsicológica freudiana, como se compreende a inibição intelectual no caso de L.T.?",
                    options = listOf(
                        "Déficit cognitivo neurogenético irreversível",
                        "Limitação da função do Ego como mecanismo defensivo para evitar conflito com o Superego severo e a erotização/agressividade do ato de superar a mãe",
                        "Simples preguiça ou falta de disciplina acadêmica",
                        "Transtorno do Espectro Autista com hiperfoco em literatura"
                    ),
                    correctOptionIndex = 1,
                    explanation = "Freud em 'Inibições, Sintomas e Ansiedade' (1926) postula que a inibição é a renúncia a uma função do Ego quando essa função foi reinvestida pulsionalmente ou é proibida pelo Superego por representar rivalidade inconsciente."
                ),
                ClinicalQuestion(
                    id = "q_3_2",
                    prompt = "Como o fenômeno transferencial ('medo de ser considerada medíocre pelo terapeuta') deve ser manejado pelo profissional?",
                    options = listOf(
                        "Oferecer elogios constantes para tranquilizar a vaidade da paciente",
                        "Interpretar e assinalar a repetição do padrão relacional com a figura materna idealizada que está sendo projetada na pessoa do terapeuta",
                        "Repreender a paciente por duvidar da neutralidade analítica",
                        "Encaminhar a paciente para outro profissional imediatamente"
                    ),
                    correctOptionIndex = 1,
                    explanation = "A transferência é o motor do tratamento psicanalítico; apontar como a paciente revive a demanda de perfeição materna na relação com o analista permite desconstruir o sintoma e abrir espaço para o desejo próprio da analisanda."
                )
            ),
            dsm5DiagnosticRationale = "Estrutura neurótica com sintoma de inibição funcional do Ego e formação reativa/recalcamento em torno da rivalidade edípica.",
            therapeuticApproach = "Psicanálise / Psicoterapia de Orientação Psicanalítica: escuta da livre associação, manejo da transferência, elaboração da ambivalência edípica e autonomia do desejo."
        )
    )
}

