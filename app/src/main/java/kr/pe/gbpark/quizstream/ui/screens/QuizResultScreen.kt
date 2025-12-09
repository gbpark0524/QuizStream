package kr.pe.gbpark.quizstream.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.pe.gbpark.quizstream.data.Question
import kr.pe.gbpark.quizstream.data.QuestionOption
import kr.pe.gbpark.quizstream.ui.theme.BackgroundLight
import kr.pe.gbpark.quizstream.ui.theme.Blue500
import kr.pe.gbpark.quizstream.ui.theme.Blue600
import kr.pe.gbpark.quizstream.ui.theme.QuizStreamTheme
import kr.pe.gbpark.quizstream.ui.theme.SurfaceWhite
import kr.pe.gbpark.quizstream.ui.theme.TextOnBlue
import kr.pe.gbpark.quizstream.ui.theme.TextPrimary
import kr.pe.gbpark.quizstream.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    question: Question,
    userAnswers: List<String>,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onNextQuestion: () -> Unit = {},
    onCloseQuiz: () -> Unit = {}
) {
    val frozenQuestion = remember { question }
    val frozenUserAnswers = remember { userAnswers }

    // 기존의 isCorrect 계산 등도 frozen 변수를 사용해야 합니다.
    val isCorrect = frozenUserAnswers.sorted() == frozenQuestion.answer.sorted()
    val scrollState = rememberScrollState()

    var showQuestion by remember(question.id) { mutableStateOf(false) }
    var showOptions by remember(question.id) { mutableStateOf(false) }

    LaunchedEffect(question.id) { scrollState.scrollTo(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Bar with Progress
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onCloseQuiz) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Quiz",
                        tint = TextSecondary
                    )
                }
            },
            actions = {
                Text(
                    text = "${currentQuestionIndex + 1} / $totalQuestions",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    modifier = Modifier.padding(end = 16.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundLight
            )
        )
        
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1).toFloat() / totalQuestions },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Blue500,
                trackColor = Color(0xFFE0E0E0)
            )
        }
        
        // Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Result Status
            ResultStatusCard(isCorrect = isCorrect)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Collapsible Question Section
            CollapsibleSection(
                title = "문제 다시보기",
                isExpanded = showQuestion,
                onToggle = { showQuestion = !showQuestion }
            ) {
                Text(
                    text = frozenQuestion.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    lineHeight = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Collapsible Answer Options Section
            CollapsibleSection(
                title = "선택지 다시보기",
                isExpanded = showOptions,
                onToggle = { showOptions = !showOptions }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    frozenQuestion.options.forEach { option ->
                        ResultAnswerOption(
                            option = option,
                            isUserAnswer = frozenUserAnswers.contains(option.label),
                            isCorrectAnswer = frozenQuestion.answer.contains(option.label)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Explanation Section (if available)
            if (frozenQuestion.explanation.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "해설",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Blue600,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = frozenQuestion.explanation,
                            fontSize = 16.sp,
                            color = TextPrimary,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
            
            // Bottom padding to ensure content doesn't get hidden behind the button
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Fixed Bottom Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundLight)
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            Button(
                onClick = onNextQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue600
                )
            ) {
                Text(
                    text = if (currentQuestionIndex < totalQuestions - 1) "다음 문제" else "완료",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnBlue
                )
            }
        }
    }
}

@Composable
fun ResultStatusCard(isCorrect: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                contentDescription = if (isCorrect) "Correct" else "Incorrect",
                tint = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.size(28.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = if (isCorrect) "정답입니다" else "오답입니다",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}

@Composable
fun ResultAnswerOption(
    option: QuestionOption,
    isUserAnswer: Boolean,
    isCorrectAnswer: Boolean
) {
    val backgroundColor = when {
        isCorrectAnswer && isUserAnswer -> Color(0xFFE8F5E8) // 정답이면서 사용자가 선택한 것
        isCorrectAnswer -> Color(0xFFE3F2FD) // 정답이지만 사용자가 선택하지 않은 것
        isUserAnswer -> Color(0xFFFFEBEE) // 사용자가 선택했지만 오답인 것
        else -> SurfaceWhite // 선택하지 않은 일반 선택지
    }
    
    val borderColor = when {
        isCorrectAnswer && isUserAnswer -> Color(0xFF4CAF50) // 정답이면서 사용자가 선택한 것
        isCorrectAnswer -> Blue500 // 정답이지만 사용자가 선택하지 않은 것
        isUserAnswer -> Color(0xFFF44336) // 사용자가 선택했지만 오답인 것
        else -> Color(0xFFE0E0E0) // 일반 선택지
    }
    
    val textColor = when {
        isCorrectAnswer -> Color(0xFF4CAF50)
        isUserAnswer -> Color(0xFFF44336)
        else -> TextPrimary
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()) // 가로 스크롤 추가
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 라벨 표시 (A, B, C, D)
            Text(
                text = option.label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier
                    .size(24.dp)
                    .wrapContentSize(Alignment.Center)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = option.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                maxLines = 1 // 한 줄로 제한
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 정답/오답 표시 아이콘
            if (isCorrectAnswer) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Correct Answer",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
            } else if (isUserAnswer) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Wrong Answer",
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizResultScreenCorrectPreview() {
    QuizStreamTheme {
        QuizResultScreen(
            question = Question(
                id = "preview",
                quizFileId = "preview",
                question = "회사는 독점 애플리케이션의 로그 파일을 분석할 수 있는 능력이 필요합니다. 로그는 Amazon S3 버킷에 JSON 형식으로 저장됩니다. 쿼리는 간단하고 주문형으로 실행됩니다. 솔루션 설계자는 기존 아키텍처에 대한 최소한의 변경으로 분석을 수행해야 합니다. 솔루션 설계자는 최소한의 운영 오버헤드로 이러한 요구 사항을 충족하기 위해 무엇을 해야 합니까?",
                type = "single_choice",
                options = listOf(
                    QuestionOption("A", "Amazon Elasticsearch Service 클러스터를 설정하고 Logstash를 사용하여 로그를 가져옵니다."),
                    QuestionOption("B", "Amazon Athena를 사용하여 S3의 로그에 대해 SQL 쿼리를 실행합니다."),
                    QuestionOption("C", "Amazon EMR 클러스터를 설정하고 Apache Spark를 사용하여 로그를 처리합니다."),
                    QuestionOption("D", "Amazon Redshift 클러스터를 설정하고 로그를 데이터 웨어하우스로 가져옵니다.")
                ),
                answer = listOf("B"),
                explanation = "Amazon Athena는 S3에 저장된 데이터에 대해 표준 SQL을 사용하여 쿼리를 실행할 수 있는 서버리스 대화형 쿼리 서비스입니다. JSON 형식의 로그 파일을 분석하는 데 이상적이며, 기존 아키텍처에 대한 변경을 최소화하면서 운영 오버헤드가 거의 없습니다."
            ),
            userAnswers = listOf("B"),
            currentQuestionIndex = 0,
            totalQuestions = 5
        )
    }
}

@Composable
fun CollapsibleSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header with toggle button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue600,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "접기" else "펼치기",
                    tint = Blue600,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Expandable content
            if (isExpanded) {
                Box(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    )
                ) {
                    content()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizResultScreenIncorrectPreview() {
    QuizStreamTheme {
        QuizResultScreen(
            question = Question(
                id = "preview",
                quizFileId = "preview",
                question = "다음 중 Amazon S3의 스토리지 클래스가 아닌 것은?",
                type = "multiple_choice",
                options = listOf(
                    QuestionOption("A", "Standard"),
                    QuestionOption("B", "Intelligent-Tiering"),
                    QuestionOption("C", "Glacier"),
                    QuestionOption("D", "EFS")
                ),
                answer = listOf("D"),
                explanation = "Amazon EFS(Elastic File System)는 S3의 스토리지 클래스가 아니라 별도의 파일 시스템 서비스입니다. S3의 스토리지 클래스에는 Standard, Intelligent-Tiering, Glacier 등이 포함됩니다."
            ),
            userAnswers = listOf("A", "B"),
            currentQuestionIndex = 1,
            totalQuestions = 5
        )
    }
}
