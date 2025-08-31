package kr.pe.gbpark.quizstream.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [QuizProgress::class, QuestionResult::class],
    version = 1,
    exportSchema = false
)
abstract class QuizStreamDatabase : RoomDatabase() {
    
    abstract fun quizProgressDao(): QuizProgressDao
    abstract fun questionResultDao(): QuestionResultDao
    
    companion object {
        @Volatile
        private var INSTANCE: QuizStreamDatabase? = null
        
        fun getDatabase(context: Context): QuizStreamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizStreamDatabase::class.java,
                    "quiz_stream_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
