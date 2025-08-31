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
    
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedQuiz by remember { mutableStateOf<Quiz?>(null) }
    var userAnswers by remember { mutableStateOf<List<String>>(emptyList()) }
    
    NavHost(
        navController = navController,
        startDestination = "quiz_file_list"
    ) {
        composable("quiz_file_list") {
            QuizFileListScreen(
                onQuizSelected = { quizId ->
                    scope.launch {
                        try {
                            val quiz = repository.loadQuiz(quizId)
                            if (quiz != null) {
                                currentQuestionIndex = 0
                                selectedQuiz = quiz
                                userAnswers = emptyList()
                                navController.navigate("quiz_screen")
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
            selectedQuiz?.let { quiz ->
                if (currentQuestionIndex < quiz.questions.size) {
                    QuizScreen(
                        question = quiz.questions[currentQuestionIndex],
                        currentQuestionIndex = currentQuestionIndex,
                        totalQuestions = quiz.questions.size,
                        onAnswerSelected = { answers ->
                            userAnswers = answers
                        },
                        onConfirmAnswer = {
                            // 결과 화면으로 이동
                            navController.navigate("quiz_result")
                        },
                        onCloseQuiz = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
        
        composable("quiz_result") {
            selectedQuiz?.let { quiz ->
                if (currentQuestionIndex < quiz.questions.size) {
                    QuizResultScreen(
                        question = quiz.questions[currentQuestionIndex],
                        userAnswers = userAnswers,
                        currentQuestionIndex = currentQuestionIndex,
                        totalQuestions = quiz.questions.size,
                        onNextQuestion = {
                            if (currentQuestionIndex < quiz.questions.size - 1) {
                                currentQuestionIndex++
                                userAnswers = emptyList()
                                navController.popBackStack()
                                navController.navigate("quiz_screen")
                            } else {
                                // 퀴즈 완료 - 메인으로 돌아가기
                                navController.popBackStack("quiz_file_list", false)
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