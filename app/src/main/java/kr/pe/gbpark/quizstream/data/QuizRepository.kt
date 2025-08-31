package kr.pe.gbpark.quizstream.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

class QuizRepository(private val context: Context) {
    
    // 앱 실행 시 assets의 퀴즈 파일들을 files 디렉토리로 복사(나중에 제거 예정)
    suspend fun copyAssetsToFiles() = withContext(Dispatchers.IO) {
        try {
            val quizDataDir = File(context.filesDir, "quiz_data")
            if (!quizDataDir.exists()) {
                quizDataDir.mkdirs()
            }
            
            val assetManager = context.assets
            val assetFiles = assetManager.list("quiz_data") ?: emptyArray()
            
            assetFiles.filter { it.endsWith(".json") }.forEach { fileName ->
                val targetFile = File(quizDataDir, fileName)
                
                // 파일이 이미 존재하지 않을 때만 복사 (사용자가 추가한 파일 보호)
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
            // app/files/quiz_data 폴더의 모든 JSON 파일 목록 가져오기
            val quizDataDir = File(context.filesDir, "quiz_data")

            if (quizDataDir.exists()) {
                val files = quizDataDir.listFiles { _, name -> name.endsWith(".json") }

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
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        quizFiles.sortedBy { it.name }
    }
    
    suspend fun loadQuiz(quizId: String): Quiz? = withContext(Dispatchers.IO) {
        try {
            val fileName = "$quizId.json"
            val quizFile = File(File(context.filesDir, "quiz_data"), fileName)
            val jsonString = quizFile.readText()
            
            val jsonObject = JSONObject(jsonString)
            val title = jsonObject.getString("title")
            val questionsArray = jsonObject.getJSONArray("questions")
            
            val questions = mutableListOf<Question>()
            for (i in 0 until questionsArray.length()) {
                val questionObj = questionsArray.getJSONObject(i)
                val optionsArray = questionObj.getJSONArray("options")
                val answerArray = questionObj.getJSONArray("answer")
                
                // 선택지를 QuestionOption 객체로 변환
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
                
                // 정답 라벨들을 문자열 리스트로 변환
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
}