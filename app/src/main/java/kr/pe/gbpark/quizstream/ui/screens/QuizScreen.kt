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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.pe.gbpark.quizstream.data.Question
import kr.pe.gbpark.quizstream.data.QuestionOption
import kr.pe.gbpark.quizstream.ui.theme.BackgroundLight
import kr.pe.gbpark.quizstream.ui.theme.Blue100
import kr.pe.gbpark.quizstream.ui.theme.Blue500
import kr.pe.gbpark.quizstream.ui.theme.Blue600
import kr.pe.gbpark.quizstream.ui.theme.QuizStreamTheme
import kr.pe.gbpark.quizstream.ui.theme.SurfaceWhite
import kr.pe.gbpark.quizstream.ui.theme.TextOnBlue
import kr.pe.gbpark.quizstream.ui.theme.TextPrimary
import kr.pe.gbpark.quizstream.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    question: Question,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onAnswerSelected: (List<String>) -> Unit = {},
    onConfirmAnswer: () -> Unit = {},
    onCloseQuiz: () -> Unit = {}
) {
    var selectedAnswers by remember(question.id) { 
        mutableStateOf<Set<String>>(emptySet()) 
    }
    
    val isMultipleChoice = question.type == "multiple_choice"
    
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
        
        // Question Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text(
                    text = question.question,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
                
                // 다중선택 안내
                if (isMultipleChoice) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "※ 복수 정답을 선택하세요",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Blue600,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        // Answer Section - Optimized for one-handed use
        Column(
            modifier = Modifier.padding(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            question.options.forEach { option ->
                AnswerButton(
                    option = option,
                    isSelected = selectedAnswers.contains(option.label),
                    isMultipleChoice = isMultipleChoice,
                    onClick = { 
                        selectedAnswers = if (isMultipleChoice) {
                            // 다중선택: 토글 방식
                            if (selectedAnswers.contains(option.label)) {
                                selectedAnswers - option.label
                            } else {
                                selectedAnswers + option.label
                            }
                        } else {
                            // 단일선택: 하나만 선택
                            setOf(option.label)
                        }
                        onAnswerSelected(selectedAnswers.toList())
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // Confirm Button
            Button(
                onClick = {
                    if (selectedAnswers.isNotEmpty()) { 
                        onConfirmAnswer()
                    }
                },
                enabled = selectedAnswers.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue600,
                    disabledContainerColor = Color(0xFFE0E0E0)
                )
            ) {
                Text(
                    text = "Confirm",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedAnswers.isNotEmpty()) TextOnBlue else Color.Gray
                )
            }
        }
    }
}

@Composable
fun AnswerButton(
    option: QuestionOption,
    isSelected: Boolean,
    isMultipleChoice: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp) // Large touch target for one-handed use
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Blue100 else SurfaceWhite
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, Blue500)
        } else {
            BorderStroke(1.dp, Color(0xFFE0E0E0))
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState()) // 가로 스크롤 추가
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 라벨 표시 (A, B, C, D)
            Text(
                text = option.label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Blue600 else TextSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .wrapContentSize(Alignment.Center)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = option.text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Blue600 else TextPrimary,
                maxLines = 1 // 한 줄로 제한
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    QuizStreamTheme {
        QuizScreen(
            question = Question(
                id = "preview",
                question = "회사는 독점 애플리케이션의 로그 파일을 분석할 수 있는 능력이 필요합니다. 로그는 Amazon S3 버킷에 JSON 형식으로 저장됩니다. 쿼리는 간단하고 주문형으로 실행됩니다. 솔루션 설계자는 기존 아키텍처에 대한 최소한의 변경으로 분석을 수행해야 합니다. 솔루션 설계자는 최소한의 운영 오버헤드로 이러한 요구 사항을 충족하기 위해 무엇을 해야 합니까?",
                type = "multiple_choice",
                options = listOf(
                    QuestionOption("A", "Berlin"),
                    QuestionOption("B", "Paris"),
                    QuestionOption("C", "Madrid"),
                    QuestionOption("D", "Rome")
                ),
                answer = listOf("B"),
                explanation = "Paris is the capital of France."
            ),
            currentQuestionIndex = 0,
            totalQuestions = 5
        )
    }
}