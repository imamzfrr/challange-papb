package ap.mobile.challenge;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.VH> {

  private final Context context;
  private List<ToDo> toDoList;
  private ToDoDAO toDoDAO;

  // Konstruktor untuk ToDoAdapter
  ToDoAdapter(Context context, List<ToDo> toDoList, ToDoDAO toDoDAO) {
    this.context = context;
    this.toDoList = toDoList;
    this.toDoDAO = toDoDAO;
  }

  // Setter untuk mengatur daftar ToDo
  public void setToDoList(List<ToDo> toDoList) {
    this.toDoList = toDoList;
    notifyDataSetChanged();
  }

  public List<ToDo> getToDoList() {
    return toDoList;
  }

  // Metode untuk membuat ViewHolder, yang merepresentasikan setiap item dalam RecyclerView
  @NonNull
  @Override
  public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
    VH viewHolder = new VH(view);

    // Initialize delete button here
    viewHolder.btDel = view.findViewById(R.id.btDel);
    viewHolder.textViewDateValue = view.findViewById(R.id.tvItemDate);

    return viewHolder;
  }

  // Metode untuk menghubungkan data ke ViewHolder
  @Override
  public void onBindViewHolder(@NonNull VH holder, int position) {
    ToDo toDo = toDoList.get(position);

    // Bind data to the ViewHolder
    holder.textViewWhatValue.setText(toDo.what);
    holder.textViewTimeValue.setText(toDo.time);
    holder.textViewDateValue.setText(toDo.date);

    // Add listener for the delete button
    holder.btDel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // Call method to delete data
        deleteItem(position);
      }
    });
  }

  // Metode untuk menghapus item pada posisi tertentu
  private void deleteItem(int position) {
    ToDo toDo = toDoList.get(position);
    new DeleteTask(toDoDAO).execute(toDo);

    // Remove from the list and refresh RecyclerView
    toDoList.remove(position);
    notifyItemRemoved(position);
    notifyItemRangeChanged(position, getItemCount());
  }

  @Override
  public int getItemCount() {
    return toDoList != null ? toDoList.size() : 0;
  }

  // Kelas Inner ViewHolder, merepresentasikan setiap item dalam RecyclerView
  public class VH extends RecyclerView.ViewHolder {
    Button btDel;
    TextView textViewWhatValue;
    TextView textViewTimeValue;
    TextView textViewAdditionalInfoValue;
    TextView textViewDateValue;

    public VH(View itemView) {
      super(itemView);

      // Initialize TextViews
      textViewWhatValue = itemView.findViewById(R.id.tvItemTodo);
      textViewTimeValue = itemView.findViewById(R.id.tvItemTime);
      textViewDateValue = itemView.findViewById(R.id.tvItemDate);
    }
  }

  // Kelas AsyncTask untuk menghapus item dari database sesuai posisi data di dataset
  private static class DeleteTask extends AsyncTask<ToDo, Void, Void> {
    private ToDoDAO toDoDAO;

    DeleteTask(ToDoDAO dao) {
      this.toDoDAO = dao;
    }

    @Override
    protected Void doInBackground(ToDo... toDos) {
      toDoDAO.delete(toDos[0]);
      return null;
    }
  }
}
