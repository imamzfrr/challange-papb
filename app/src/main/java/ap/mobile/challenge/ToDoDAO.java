package ap.mobile.challenge;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ToDoDAO {

    @Query("SELECT * FROM ToDo")
    List<ToDo> getAll();

    @Query("SELECT * FROM ToDo WHERE  strftime('%H:%M', time) >= 6 AND time < 18")
    List<ToDo> getAllDay(); // 6AM to 6PM

    @Query("SELECT * FROM ToDo WHERE strftime('%H:%M', time) < 6 OR time >= 18")
    List<ToDo> getAllNight(); // 6PM to 6AM

    @Query("SELECT * FROM ToDo WHERE what LIKE :keyword LIKE :keyword")
    List<ToDo> search(String keyword);

    @Insert
    void insert(ToDo toDo);

    @Insert
    void insertAll(ToDo... toDos);

    @Delete
    void delete(ToDo toDo);

    @Query("DELETE FROM ToDo")
    void clear();

    // Additional SELECT operation
    @Query("SELECT * FROM ToDo WHERE id = :taskId")
    ToDo getTaskById(int taskId);

    // Additional INSERT operation
    @Insert
    void insertTask(ToDo toDo);

    // Additional DELETE operation
    @Query("DELETE FROM ToDo WHERE id = :taskId")
    void deleteTaskById(int taskId);
}
