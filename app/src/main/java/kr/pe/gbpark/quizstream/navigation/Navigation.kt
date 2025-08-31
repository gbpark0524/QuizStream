package kr.pe.gbpark.quizstream.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
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
                                    // 다음 문제 버튼을 눌렀을 때는 단순히 다음 문제로 이동
                                    val (nextQuestion, nextDisplayIndex) = repository.getCurrentQuestion(quiz.id)
                                    if (nextQuestion != null && nextDisplayIndex != null) {
                                        currentQuestion = nextQuestion
                                        currentQuestionIndex = nextDisplayIndex
                                        userAnswers = emptyList()
                                        navController.popBackStack()
                                        navController.navigate("quiz_screen")
                                    } else {
                                        // 더 이상 문제가 없으면 퀴즈 완료
                                        navController.popBackStack("quiz_file_list", false)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
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
