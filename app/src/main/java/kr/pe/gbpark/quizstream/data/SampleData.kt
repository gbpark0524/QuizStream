package kr.pe.gbpark.quizstream.data

object SampleData {
    val quizFiles = listOf(
        QuizFile("history_quiz", "History Quiz", 10),
        QuizFile("science_quiz", "Science Quiz", 8),
        QuizFile("geography_quiz", "Geography Quiz", 7),
        QuizFile("math_quiz", "Math Quiz", 8),
        QuizFile("SAA2", "SAA", 2)
    )
    
    val sampleQuiz = Quiz(
        id = "sample",
        title = "Sample Quiz",
        questions = listOf(
            Question(
                id = "q1",
                question = "What is the capital of France?",
                type = "single_choice",
                options = listOf(
                    QuestionOption("A", "Berlin"),
                    QuestionOption("B", "Paris"),
                    QuestionOption("C", "Madrid"),
                    QuestionOption("D", "Rome")
                ),
                answer = listOf("B"),
                explanation = "Paris has been the capital of France since 508 AD."
            ),
            Question(
                id = "q2", 
                question = "Who painted the Mona Lisa?",
                type = "single_choice",
                options = listOf(
                    QuestionOption("A", "Van Gogh"),
                    QuestionOption("B", "Da Vinci"),
                    QuestionOption("C", "Picasso"),
                    QuestionOption("D", "Monet")
                ),
                answer = listOf("B"),
                explanation = "Leonardo da Vinci painted the Mona Lisa between 1503-1519."
            ),
            Question(
                id = "q3",
                question = "Which of the following are programming languages? (Multiple answers)",
                type = "multiple_choice",
                options = listOf(
                    QuestionOption("A", "Python"),
                    QuestionOption("B", "HTML"),
                    QuestionOption("C", "JavaScript"),
                    QuestionOption("D", "CSS")
                ),
                answer = listOf("A", "C"),
                explanation = "Python and JavaScript are programming languages. HTML and CSS are markup and styling languages respectively."
            )
        )
    )
}