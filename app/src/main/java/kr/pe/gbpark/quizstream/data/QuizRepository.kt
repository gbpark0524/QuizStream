package kr.pe.gbpark.quizstream.data

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

class QuizRepository(private val context: Context) {

    private val progressManager = QuizProgressManager(context)

    private fun getQuizDir(): File {
        val documentDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val targetDir = File(documentDir, "quizstream")

        Log.d("QuizRepository", "Target Dir: ${targetDir.absolutePath}")

        return targetDir
    }

    suspend fun copyAssetsToFiles() = withContext(Dispatchers.IO) {
        try {
            val quizDataDir = getQuizDir()
            if (!quizDataDir.exists()) {
                quizDataDir.mkdirs() // 폴더 없으면 생성
            }

            val assetManager = context.assets
            val assetFiles = assetManager.list("quiz_data") ?: emptyArray()

            assetFiles.filter { it.endsWith(".json") }.forEach { fileName ->
                val targetFile = File(quizDataDir, fileName)

                // 파일이 없을 때만 복사
                if (!targetFile.exists()) {
                    assetManager.open("quiz_data/$fileName").use { inputStream ->
                        targetFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadQuizFiles(): List<QuizFile> = withContext(Dispatchers.IO) {
        val quizFiles = mutableListOf<QuizFile>()

        try {
            // [수정됨] 여기도 반드시 getQuizDir()를 써야 합니다!
            // 예전 코드인 context.filesDir를 쓰면 안 됩니다.
            val quizDataDir = getQuizDir()

            Log.d("QuizRepository", "Loading files from: ${quizDataDir.absolutePath}")

            if (quizDataDir.exists()) {
                val files = quizDataDir.listFiles { _, name -> name.endsWith(".json") }

                Log.d("QuizRepository", "Found files count: ${files?.size ?: 0}")

                files?.forEach { file ->
                    try {
                        val jsonString = file.readText()
                        val jsonObject = JSONObject(jsonString)
                        val title = jsonObject.getString("title")
                        val questionsArray = jsonObject.getJSONArray("questions")
                        val totalQuestions = questionsArray.length()

                        quizFiles.add(
                            QuizFile(
                                id = file.nameWithoutExtension,
                                name = title,
                                questionCount = totalQuestions
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("QuizRepository", "Error parsing file: ${file.name}", e)
                    }
                }
            } else {
                Log.w("QuizRepository", "Directory does not exist")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        quizFiles.sortedBy { it.name }
    }

    suspend fun loadQuiz(quizId: String): Quiz? = withContext(Dispatchers.IO) {
        try {
            val fileName = "$quizId.json"
            // [수정됨] 여기도 getQuizDir() 사용
            val quizFile = File(getQuizDir(), fileName)
            val jsonString = quizFile.readText()

            // ... (아래 파싱 로직은 기존과 동일)
            val jsonObject = JSONObject(jsonString)
            val title = jsonObject.getString("title")
            val questionsArray = jsonObject.getJSONArray("questions")

            val questions = mutableListOf<Question>()
            for (i in 0 until questionsArray.length()) {
                val questionObj = questionsArray.getJSONObject(i)
                val optionsArray = questionObj.getJSONArray("options")
                val answerArray = questionObj.getJSONArray("answer")

                val options = mutableListOf<QuestionOption>()
                for (j in 0 until optionsArray.length()) {
                    val optionObj = optionsArray.getJSONObject(j)
                    options.add(
                        QuestionOption(
                            label = optionObj.getString("label"),
                            text = optionObj.getString("text")
                        )
                    )
                }

                val answers = mutableListOf<String>()
                for (k in 0 until answerArray.length()) {
                    answers.add(answerArray.getString(k))
                }

                val explanation = questionObj.optString("explanation", "")
                val type = questionObj.getString("type")

                questions.add(
                    Question(
                        id = questionObj.getString("id"),
                        question = questionObj.getString("question"),
                        type = type,
                        options = options,
                        answer = answers,
                        explanation = explanation
                    )
                )
            }

            Quiz(
                id = quizId,
                title = title,
                questions = questions
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun startQuiz(quizId: String): Pair<Quiz?, QuizProgressInfo?> =
        withContext(Dispatchers.IO) {
            val quiz = loadQuiz(quizId) ?: return@withContext null to null
            val existingProgress = progressManager.getQuizProgress(quizId)
            val progress = if (existingProgress == null || existingProgress.isCompleted) {
                progressManager.startNewQuiz(quizId, quiz.questions.size)
            } else {
                existingProgress
            }
            val progressInfo = progressManager.getQuizProgressInfo(quizId)
            quiz to progressInfo
        }

    suspend fun getCurrentQuestion(quizId: String): Pair<Question?, Int?> =
        withContext(Dispatchers.IO) {
            val quiz = loadQuiz(quizId) ?: return@withContext null to null
            val currentQuestionIndex =
                progressManager.getCurrentQuestionIndex(quizId) ?: return@withContext null to null
            val question = quiz.questions.getOrNull(currentQuestionIndex)
            val progressInfo = progressManager.getQuizProgressInfo(quizId)
            question to progressInfo?.currentIndex
        }

    suspend fun submitAnswer(
        quizId: String,
        questionId: String,
        userAnswers: List<String>,
        correctAnswers: List<String>
    ): Boolean = withContext(Dispatchers.IO) {
        val isCorrect = userAnswers.sorted() == correctAnswers.sorted()
        progressManager.saveAnswerAndMoveNext(
            quizId,
            questionId,
            userAnswers,
            correctAnswers,
            isCorrect
        )
    }

    suspend fun getQuizProgressInfo(quizId: String): QuizProgressInfo? {
        return progressManager.getQuizProgressInfo(quizId)
    }

    suspend fun resetQuiz(quizId: String): Boolean = withContext(Dispatchers.IO) {
        val quiz = loadQuiz(quizId) ?: return@withContext false
        progressManager.resetQuiz(quizId, quiz.questions.size)
        true
    }
}