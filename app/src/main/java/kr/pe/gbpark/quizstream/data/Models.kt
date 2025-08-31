package kr.pe.gbpark.quizstream.data

data class QuizFile(
    val id: String,
    val name: String,
    val questionCount: Int,
    val isSelected: Boolean = false
)

data class QuestionOption(
    val label: String,
    val text: String
)

data class Question(
    val id: String,
    val question: String,
    val type: String, // "single_choice" 또는 "multiple_choice"
    val options: List<QuestionOption>,
    val answer: List<String>, // 정답 라벨들 (예: ["A", "B"])
    val explanation: String = ""
)

data class Quiz(
    val id: String,
    val title: String, // name -> title로 변경
    val questions: List<Question>
)