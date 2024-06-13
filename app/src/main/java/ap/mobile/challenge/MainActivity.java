package ap.mobile.challenge;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import ap.mobile.challenge.ToDo;

import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private ToDoDatabase toDoDatabase;
  private ToDoDAO toDoDAO;
  private ToDoAdapter toDoAdapter;
  private EditText etActivity;

  private SimpleDateFormat timeFormat;
  private SimpleDateFormat dateFormat;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize your ToDoDatabase and ToDoDAO instances
    toDoDatabase = ToDoDatabase.getDb(this);
    toDoDAO = toDoDatabase.toDoDAO();

    // Initialize RecyclerView and ToDoAdapter
    RecyclerView recyclerView = findViewById(R.id.rvData);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    toDoAdapter = new ToDoAdapter(this, new ArrayList<>(), toDoDAO);
    recyclerView.setAdapter(toDoAdapter);

    // Set click listeners for buttons
    Button btAdd = findViewById(R.id.btAdd);
    Button btReset = findViewById(R.id.btReset);
    Button btRemoveRandom = findViewById(R.id.btRemoveRandom);
    Button btCustom = findViewById(R.id.btCustom);
    etActivity = findViewById(R.id.etActivity);
    btAdd.setOnClickListener(this);
    btReset.setOnClickListener(this);
    btRemoveRandom.setOnClickListener(this);
    btCustom.setOnClickListener(this);

    // Initialize date and time formats
    timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Initially, load data from the database and update the list view
    new UpdateListViewTask().execute();

    // Initially, load data from the database and update the list view
    new UpdateListViewTask().execute();
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btAdd) {
      // Tambahkan data yang diketik oleh pengguna
      String userInput = etActivity.getText().toString();

      // Pastikan input tidak kosong sebelum menambahkan
      if (!userInput.isEmpty()) {
        // Get current local time
        Calendar calendar = Calendar.getInstance();
        String currentTime = timeFormat.format(calendar.getTime());
        String currentDate = dateFormat.format(calendar.getTime());

        // Update the list view
        new AddTask().execute(new ToDo(userInput, currentTime, currentDate));
        showToast("Task added");
      } else {
        showToast("Input cannot be empty");
      }
    } else if (v.getId() == R.id.btReset) {
      // Get data from the online API and insert into the database
      new ResetDatabaseTask().execute();
    } else if (v.getId() == R.id.btRemoveRandom) {
      // Remove a random ToDo item from the database
      new RemoveRandomTask().execute();
      showToast("Data Removed");
    } else if (v.getId() == R.id.btCustom) {
      // Menjalankan operasi khusus (Custom Process)
      new CustomTask().execute();
      showToast("Custom command executed");
    }

  }

  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  // AsyncTask untuk memperbarui tampilan daftar
  private class UpdateListViewTask extends AsyncTask<Void, Void, List<ToDo>> {

    @Override
    protected List<ToDo> doInBackground(Void... voids) {
      // Fetch all tasks from the database in the background
      return toDoDAO.getAll();
    }

    @Override
    protected void onPostExecute(List<ToDo> toDos) {
      // Update the adapter on the main thread
      toDoAdapter.setToDoList(toDos);
      toDoAdapter.notifyDataSetChanged();
    }
  }

  // AsyncTask untuk menambahkan item ke database
  private class AddTask extends AsyncTask<ToDo, Void, Void> {
    @Override
    protected Void doInBackground(ToDo... toDos) {
      // Menambahkan item ToDo ke database di background
      toDoDAO.insert(toDos[0]);
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      // Setelah menambahkan item, perbarui adapter
      new UpdateListViewTask().execute();
    }
  }

  // AsyncTask untuk mereset database dari API
  private class ResetDatabaseTask extends AsyncTask<Void, Void, List<ToDo>> {

    @Override
    protected List<ToDo> doInBackground(Void... voids) {
      // Fetch data dari API di background
      List<ToDo> todos = fetchDataFromApi();

      // Mendapatkan tanggal lokal hari ini
      Calendar calendar = Calendar.getInstance();
      String currentDate = dateFormat.format(calendar.getTime());

      // Menetapkan tanggal lokal hari ini ke setiap item dari API
      for (ToDo toDo : todos) {
        toDo.date = currentDate;
      }

      // Menghapus semua item dari database
      toDoDAO.clear();

      // Memasukkan data yang diambil dari API ke dalam database
      for (ToDo toDo : todos) {
        toDoDAO.insert(toDo);
      }
      // Mengembalikan list yang telah di-update dari database
      return toDoDAO.getAll();
    }

    @Override
    protected void onPostExecute(List<ToDo> toDos) {
      // Update the adapter on the main thread
      toDoAdapter.setToDoList(toDos);
      toDoAdapter.notifyDataSetChanged();
      showToast("List reset");
    }

    private List<ToDo> fetchDataFromApi() {
      // Implement your logic to fetch data from the API
      List<ToDo> todos = new ArrayList<>();

      try {
        // Create a URL object with the API endpoint
        URL url = new URL("https://mgm.ub.ac.id/todo.php");

        // Open a connection to the specified URL
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
          // Create an input stream to read data from the API response
          InputStream in = new BufferedInputStream(urlConnection.getInputStream());

          // Use Gson to parse the JSON response
          Gson gson = new Gson();

          // Define the type for Gson to correctly parse into a list of ToDo objects
          Type type = new TypeToken<List<ToDo>>(){}.getType();

          // Parse the JSON data into a list of ToDo objects
          todos = gson.fromJson(new InputStreamReader(in), type);
        } finally {
          // Disconnect the URL connection to release resources
          urlConnection.disconnect();
        }
      } catch (IOException e) {
        // Handle IOException by printing the stack trace for debugging purposes
        e.printStackTrace();
      }

      // Return the list of ToDo objects (it might be empty or contain data from the API)
      return todos;
    }
  }




  // AsyncTask untuk menghapus item acak dari database
  private class RemoveRandomTask extends AsyncTask<Void, Void, List<ToDo>> {

    @Override
    protected List<ToDo> doInBackground(Void... voids) {
      // Menghapus item ToDo secara acak dari database di background
      ToDo randomToDo = getRandomToDoFromDatabase();
      if (randomToDo != null) {
        // Hapus dari database
        toDoDAO.delete(randomToDo);
      }

      // Mengembalikan list setelah penghapusan item ToDo
      return toDoDAO.getAll();
    }

    @Override
    protected void onPostExecute(List<ToDo> toDos) {
      // Update adapter di thread utama setelah menghapus item ToDo
      toDoAdapter.setToDoList(toDos);
      toDoAdapter.notifyDataSetChanged();
      showToast("Random task removed");
    }

    private ToDo getRandomToDoFromDatabase() {
      List<ToDo> toDos = toDoDAO.getAll();
      if (!toDos.isEmpty()) {
        int randomIndex = (int) (Math.random() * toDos.size());
        return toDos.get(randomIndex);
      }
      return null;
    }
  }

  private class CustomTask extends AsyncTask<Void, Void, List<ToDo>> {

    @Override
    protected List<ToDo> doInBackground(Void... voids) {
      // Implementasi logika menampilkan ToDo yang melakukan disiang hari
      return toDoDAO.getAllNight();
    }

    @Override
    protected void onPostExecute(List<ToDo> toDos) {
      // Update adapter di thread utama setelah menjalankan operasi khusus
      toDoAdapter.setToDoList(toDos);
      toDoAdapter.notifyDataSetChanged();
      showToast("Custom process executed");
    }
  }
}
