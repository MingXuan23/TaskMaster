import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.group_assignment.data.TaskRepository
import com.example.group_assignment.data.local.AppDb

object ServiceLocator {
    @Volatile private var db: AppDb? = null;
    fun repo(context: Context): TaskRepository {
        val database = db ?: Room.databaseBuilder(context.applicationContext, AppDb::class.java, "app.db").build()
            .also { db = it }
        return TaskRepository(database.taskDao())

    }


}