package kr.pe.gbpark.quizstream.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kr.pe.gbpark.quizstream.data.Quiz
import kr.pe.gbpark.quizstream.data.QuizRepository
import kr.pe.gbpark.quizstream.data.Question
import kr.pe.gbpark.quizstream.data.QuizProgressInfo
import kr.pe.gbpark.quizstream.ui.screens.QuizFileListScreen
import kr.pe.gbpark.quizstream.ui.screens.QuizScreen
import kr.pe.gbpark.quizstream.ui.screens.QuizResultScreen

@Composable
fun QuizStreamNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { QuizRepository(context) }
    
    var currentQuiz by remember { mutableStateOf<Quiz?>(null) }
    var currentQuestion by remember { mutableStateOf<Question?>(null) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(0) }
    var userAnswers by remember { mutableStateOf<List<String>>(emptyList()) }
    var progressInfo by remember { mutableStateOf<QuizProgressInfo?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = "quiz_file_list"
    ) {
        composable("quiz_file_list") {
            QuizFileListScreen(
                onQuizSelected = { quizId ->
                    scope.launch {
                        try {
                            // 새로운 Room 기반 시스템 사용
                            val (quiz, progress) = repository.startQuiz(quizId)
                            if (quiz != null && progress != null) {
                                currentQuiz = quiz
                                progressInfo = progress
                                totalQuestions = quiz.questions.size
                                
                                // 현재 문제 가져오기
                                val (question, displayIndex) = repository.getCurrentQuestion(quizId)
                                if (question != null && displayIndex != null) {
                                    currentQuestion = question
                                    currentQuestionIndex = displayIndex
                                    userAnswers = emptyList()
                                    navController.navigate("quiz_screen")
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onSettingsClick = {
                    // Settings 화면으로 네비게이션 (나중에 구현)
                }
            )
        }
        
        composable("quiz_screen") {
            currentQuestion?.let { question ->
                currentQuiz?.let { quiz ->
                    QuizScreen(
                        question = question,
                        currentQuestionIndex = currentQuestionIndex,
                        totalQuestions = totalQuestions,
                        onAnswerSelected = { answers ->
                            userAnswers = answers
                        },
                        onConfirmAnswer = {
                            scope.launch {
                                try {
                                    // Confirm 버튼을 눌렀을 때 답안 제출 및 상태 저장
                                    repository.submitAnswer(
                                        quizId = quiz.id,
                                        questionId = question.id,
                                        userAnswers = userAnswers,
                                        correctAnswers = question.answer
                                    )
                                    
                                    // 결과 화면으로 이동
                                    navController.navigate("quiz_result")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        onCloseQuiz = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }

        composable("quiz_result") {
            // [추가] 다이얼로그 표시 여부와 점수 텍스트를 저장할 상태 변수
            var showCompletionDialog by remember { mutableStateOf(false) }
            var completionMessage by remember { mutableStateOf("") }

            // [추가] 퀴즈 완료 알림창 (점수 표시)
            if (showCompletionDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { /* 밖을 눌러도 안 닫히게 하려면 비워둠 */ },
                    title = {
                        androidx.compose.material3.Text(
                            text = "퀴즈 완료!",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    },
                    text = {
                        androidx.compose.material3.Text(
                            text = completionMessage,
                            fontSize = 16.sp
                        )
                    },
                    confirmButton = {
                        androidx.compose.material3.Button(
                            onClick = {
                                scope.launch {
                                    currentQuiz?.let { quiz ->
                                        // [여기서 리셋 및 이동 수행]
                                        repository.resetQuiz(quiz.id)
                                        showCompletionDialog = false
                                        navController.popBackStack("quiz_file_list", false)
                                    }
                                }
                            }
                        ) {
                            androidx.compose.material3.Text("확인")
                        }
                    }
                )
            }

            currentQuestion?.let { question ->
                currentQuiz?.let { quiz ->
                    QuizResultScreen(
                        question = question,
                        userAnswers = userAnswers,
                        currentQuestionIndex = currentQuestionIndex,
                        totalQuestions = totalQuestions,
                        onNextQuestion = {
                            scope.launch {
                                try {
                                    if (currentQuestionIndex < totalQuestions - 1) {
                                        // 1. 다음 문제가 있을 때 (기존 로직 동일)
                                        val (nextQuestion, nextDisplayIndex) = repository.getCurrentQuestion(quiz.id)
                                        if (nextQuestion != null && nextDisplayIndex != null) {
                                            currentQuestion = nextQuestion
                                            currentQuestionIndex = nextDisplayIndex
                                            userAnswers = emptyList()

                                            navController.popBackStack()
                                            navController.navigate("quiz_screen")
                                        }
                                    } else {
                                        // 2. 마지막 문제일 때 -> 점수 계산 후 다이얼로그 띄우기
                                        val info = repository.getQuizProgressInfo(quiz.id)
                                        if (info != null) {
                                            val percent = (info.correctCount.toFloat() / info.totalQuestions.toFloat()) * 100

                                            // 메시지 생성 (예: "총 10문제 중 8문제 정답 (80%)")
                                            completionMessage = "수고하셨습니다!\n\n" +
                                                    "총 ${info.totalQuestions}문제 중 ${info.correctCount}문제 정답\n" +
                                                    "정답률: ${String.format("%.1f", percent)}%"

                                            // 다이얼로그 표시 (리셋은 다이얼로그 버튼에서 함)
                                            showCompletionDialog = true
                                        } else {
                                            // 정보 로드 실패 시 그냥 나감
                                            repository.resetQuiz(quiz.id)
                                            navController.popBackStack("quiz_file_list", false)
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    navController.popBackStack("quiz_file_list", false)
                                }
                            }
                        },
                        onCloseQuiz = {
                            navController.popBackStack("quiz_file_list", false)
                        }
                    )
                }
            }
        }
    }
}
