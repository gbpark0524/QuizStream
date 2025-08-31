package kr.pe.gbpark.quizstream.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.pe.gbpark.quizstream.data.database.QuizProgress
import kr.pe.gbpark.quizstream.data.database.QuizStreamDatabase
import kr.pe.gbpark.quizstream.data.database.QuestionResult
import org.json.JSONArray

class QuizProgressManager(private val context: Context) {
    
    private val database = QuizStreamDatabase.getDatabase(context)
    private val progressDao = database.quizProgressDao()
    private val resultDao = database.questionResultDao()
    
    /**
     * 새 퀴즈 시작 - 문제 순서 섞기
     */
    suspend fun startNewQuiz(quizId: String, totalQuestions: Int): QuizProgress = withContext(Dispatchers.IO) {
        // 문제 인덱스를 섞어서 저장
        val shuffledOrder = (0 until totalQuestions).shuffled()
        val shuffledOrderJson = JSONArray(shuffledOrder).toString()
        
        val progress = QuizProgress(
            quizId = quizId,
            shuffledQuestionOrder = shuffledOrderJson,
            currentIndex = 0,
            totalQuestions = totalQuestions,
            isCompleted = false
        )
        
        // 기존 진행 상태와 결과 삭제
        progressDao.deleteQuizProgress(quizId)
        resultDao.deleteQuestionResults(quizId)
        
        // 새 진행 상태 저장
        progressDao.insertOrUpdateQuizProgress(progress)
        progress
    }
    
    /**
     * 기존 퀴즈 진행 상태 가져오기
     */
    suspend fun getQuizProgress(quizId: String): QuizProgress? = withContext(Dispatchers.IO) {
        progressDao.getQuizProgress(quizId)
    }
    
    /**
     * 섞인 순서대로 문제 인덱스 가져오기
     */
    suspend fun getShuffledQuestionIndices(quizId: String): List<Int>? = withContext(Dispatchers.IO) {
        val progress = progressDao.getQuizProgress(quizId) ?: return@withContext null
        
        val jsonArray = JSONArray(progress.shuffledQuestionOrder)
        val indices = mutableListOf<Int>()
        for (i in 0 until jsonArray.length()) {
            indices.add(jsonArray.getInt(i))
        }
        indices
    }
    
    /**
     * 현재 풀어야 할 문제 인덱스 (섞인 순서 기준)
     */
    suspend fun getCurrentQuestionIndex(quizId: String): Int? = withContext(Dispatchers.IO) {
        val progress = progressDao.getQuizProgress(quizId) ?: return@withContext null
        if (progress.currentIndex >= progress.totalQuestions) return@withContext null
        
        val shuffledIndices = getShuffledQuestionIndices(quizId) ?: return@withContext null
        shuffledIndices[progress.currentIndex]
    }
    
    /**
     * 문제 답안 저장 및 다음 문제로 이동
     */
    suspend fun saveAnswerAndMoveNext(
        quizId: String,
        questionId: String,
        userAnswers: List<String>,
        correctAnswers: List<String>,
        isCorrect: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        val progress = progressDao.getQuizProgress(quizId) ?: return@withContext false
        
        // 답안 저장
        val result = QuestionResult(
            id = "${quizId}_${questionId}",
            quizId = quizId,
            questionId = questionId,
            questionIndex = progress.currentIndex,
            userAnswers = JSONArray(userAnswers).toString(),
            correctAnswers = JSONArray(correctAnswers).toString(),
            isCorrect = isCorrect
        )
        resultDao.insertOrUpdateQuestionResult(result)
        
        // 다음 문제로 이동
        val nextIndex = progress.currentIndex + 1
        if (nextIndex >= progress.totalQuestions) {
            // 퀴즈 완료
            progressDao.updateCompletionStatus(quizId, true)
            return@withContext true // 퀴즈 완료
        } else {
            // 다음 문제로 이동
            progressDao.updateCurrentIndex(quizId, nextIndex)
            return@withContext false // 계속 진행
        }
    }
    
    /**
     * 퀴즈 진행률 가져오기 (UI용)
     */
    suspend fun getQuizProgressInfo(quizId: String): QuizProgressInfo? = withContext(Dispatchers.IO) {
        val progress = progressDao.getQuizProgress(quizId) ?: return@withContext null
        val correctCount = resultDao.getCorrectAnswerCount(quizId)
        val answeredCount = resultDao.getTotalAnsweredCount(quizId)
        
        QuizProgressInfo(
            currentIndex = progress.currentIndex,
            totalQuestions = progress.totalQuestions,
            correctCount = correctCount,
            answeredCount = answeredCount,
            isCompleted = progress.isCompleted
        )
    }
    
    /**
     * 퀴즈 초기화 (다시 처음부터)
     */
    suspend fun resetQuiz(quizId: String, totalQuestions: Int): QuizProgress = withContext(Dispatchers.IO) {
        startNewQuiz(quizId, totalQuestions)
    }
}

/**
 * UI에서 사용할 진행률 정보
 */
data class QuizProgressInfo(
    val currentIndex: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val answeredCount: Int,
    val isCompleted: Boolean
) {
    val progressPercentage: Float
        get() = if (totalQuestions > 0) currentIndex.toFloat() / totalQuestions else 0f
    
    val accuracyPercentage: Float
        get() = if (answeredCount > 0) correctCount.toFloat() / answeredCount else 0f
}
