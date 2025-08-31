package kr.pe.gbpark.quizstream.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizProgressDao {
    
    @Query("SELECT * FROM quiz_progress WHERE quizId = :quizId")
    suspend fun getQuizProgress(quizId: String): QuizProgress?
    
    @Query("SELECT * FROM quiz_progress ORDER BY lastAccessTime DESC")
    fun getAllQuizProgress(): Flow<List<QuizProgress>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateQuizProgress(progress: QuizProgress)
    
    @Query("UPDATE quiz_progress SET currentIndex = :currentIndex, lastAccessTime = :timestamp WHERE quizId = :quizId")
    suspend fun updateCurrentIndex(quizId: String, currentIndex: Int, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE quiz_progress SET isCompleted = :isCompleted, lastAccessTime = :timestamp WHERE quizId = :quizId")
    suspend fun updateCompletionStatus(quizId: String, isCompleted: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM quiz_progress WHERE quizId = :quizId")
    suspend fun deleteQuizProgress(quizId: String)
    
    @Query("DELETE FROM quiz_progress")
    suspend fun deleteAllProgress()
}

@Dao
interface QuestionResultDao {
    
    @Query("SELECT * FROM question_results WHERE quizId = :quizId ORDER BY questionIndex ASC")
    suspend fun getQuestionResults(quizId: String): List<QuestionResult>
    
    @Query("SELECT * FROM question_results WHERE quizId = :quizId AND questionId = :questionId")
    suspend fun getQuestionResult(quizId: String, questionId: String): QuestionResult?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateQuestionResult(result: QuestionResult)
    
    @Query("DELETE FROM question_results WHERE quizId = :quizId")
    suspend fun deleteQuestionResults(quizId: String)
    
    @Query("SELECT COUNT(*) FROM question_results WHERE quizId = :quizId AND isCorrect = 1")
    suspend fun getCorrectAnswerCount(quizId: String): Int
    
    @Query("SELECT COUNT(*) FROM question_results WHERE quizId = :quizId")
    suspend fun getTotalAnsweredCount(quizId: String): Int
}
