package kr.pe.gbpark.quizstream.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_progress")
data class QuizProgress(
    @PrimaryKey
    val quizId: String,
    val shuffledQuestionOrder: String, // JSON 배열 "[3,2,4,5,1]" 형태
    val currentIndex: Int = 0,
    val totalQuestions: Int,
    val isCompleted: Boolean = false,
    val lastAccessTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "question_results")
data class QuestionResult(
    @PrimaryKey
    val id: String, // "${quizId}_${questionId}"
    val quizId: String,
    val questionId: String,
    val questionIndex: Int, // 섞인 순서에서의 인덱스
    val userAnswers: String, // JSON 배열 형태로 저장 ["A", "B"]
    val correctAnswers: String, // JSON 배열 형태로 저장 ["B", "C"]
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
