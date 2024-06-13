package ap.mobile.challenge;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ToDo.class}, version = 1)
public abstract class ToDoDatabase extends RoomDatabase {

  private static final String DBNAME = "todolist";

  public static ToDoDatabase instance;

  public static ToDoDatabase getDb(Context context) {
    if (ToDoDatabase.instance == null) {
      ToDoDatabase.instance =
              Room.databaseBuilder(context.getApplicationContext(),
                              ToDoDatabase.class, ToDoDatabase.DBNAME)
                      .fallbackToDestructiveMigration()
                      .build();
    }
    return ToDoDatabase.instance;
  }
  public abstract ToDoDAO toDoDAO();

}
