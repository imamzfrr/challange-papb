package ap.mobile.challenge;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ToDo {

  @PrimaryKey(autoGenerate = true)
  public int id;

  public String what;
  public String time;

  // Additional String members
  public String date;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getWhat() {
    return what;
  }

  public void setWhat(String what) {
    this.what = what;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  // Constructor
  public ToDo(String what, String time, String date) {
    this.what = what;
    this.time = time;
    this.date = date;
  }
}
