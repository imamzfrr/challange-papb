package ap.mobile.challenge;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ap.mobile.challenge.ToDo;

public class API {

  public interface DataListener {
    void onResponse(List<ToDo> toDos);
    void onError(String errorMessage);
  }

  public static void getAllData(Context context, DataListener listener) {
    String url = "https://mgm.ub.ac.id/todo.php";

    RequestQueue requestQueue = Volley.newRequestQueue(context);

    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
              @Override
              public void onResponse(JSONArray response) {
                List<ToDo> toDos = parseResponse(response);
                listener.onResponse(toDos);
              }
            }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        // Handle Volley error response
        String errorMessage = "Unknown error";
        if (error.networkResponse != null && error.networkResponse.data != null) {
          errorMessage = new String(error.networkResponse.data);
        }
        listener.onError(errorMessage);
      }
    });

    requestQueue.add(jsonArrayRequest);
  }

  private static List<ToDo> parseResponse(JSONArray response) {
    List<ToDo> toDos = new ArrayList<>();
    try {
      for (int i = 0; i < response.length(); i++) {
        JSONObject obj = response.getJSONObject(i);
        ToDo toDo = new ToDo(
                obj.getString("what"),
                obj.getString("time"),
                obj.getString("date")
        );
        toDo.id = obj.getInt("id");
        toDos.add(toDo);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return toDos;
  }
}
