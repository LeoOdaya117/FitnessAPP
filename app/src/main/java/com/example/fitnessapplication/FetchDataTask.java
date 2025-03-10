package com.example.fitnessapplication;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchDataTask extends AsyncTask<String, Void, List<Exercise>> {

    private static final String TAG = "FetchDataTask";
    private OnDataFetchedListener listener;

    public FetchDataTask(OnDataFetchedListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Exercise> doInBackground(String... params) {
        String search = params[0];
        List<Exercise> exerciseList = new ArrayList<>();

        try {
            String category = Discover.getCategory(); // Get the category from Discover class

            String apiLink = ""; // Initialize API link based on category
            if (category.equals("exercise")) {
                apiLink = "/User/api/fetch_exercise.php?search=";
            } else if (category.equals("food")) {
                apiLink = "/User/api/fetch_food.php?search=";
            } else if (category.equals("equipment")) {
                apiLink = "/User/api/fetch_equipment.php?search=";
            }else if (category.equals("warmup")) {
                apiLink = "/User/api/fetch_warmup.php?search=";
            }

            // Construct the URL
            URL url = new URL(URLManager.MY_URL + apiLink + search);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Read response
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String response = stringBuilder.toString();
            Log.d("SERVER RESPONSE",response);
            // Parse JSON response based on category
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Exercise exercise;
                if (category.equals("exercise")) {
                    exercise = new Exercise(
                            jsonObject.getString("ExerciseID"),
                            jsonObject.getString("ExerciseName"),
                            jsonObject.getString("Description"),
                            jsonObject.getString("EquipmentID"),
                            jsonObject.getString("Difficulty"),
                            jsonObject.getString("ImageURL")
                    );
                } else if (category.equals("food")) {
                    exercise = new Exercise(
                            jsonObject.getString("id"),
                            jsonObject.getString("name"),
                            jsonObject.getString("serving"),
                            jsonObject.getString("photo")
                    );
                }else if (category.equals("warmup")) {
                    exercise = new Exercise(
                            jsonObject.getString("ExerciseID"),
                            jsonObject.getString("ExerciseName"),
                            jsonObject.getString("Description"),
                            jsonObject.getString("EquipmentID"),
                            jsonObject.getString("Difficulty"),
                            jsonObject.getString("ImageURL")
                    );
                } else { // equipment
                    exercise = new Exercise(
                            jsonObject.getString("equipmentID"),
                            jsonObject.getString("equipmentName"),
                            jsonObject.getString("image")
                    );
                }

                exerciseList.add(exercise);
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error fetching data: " + e.getMessage());
        }

        return exerciseList;
    }


    @Override
    protected void onPostExecute(List<Exercise> exerciseList) {
        if (listener != null) {
            listener.onDataFetched(exerciseList);
        }
    }

    public interface OnDataFetchedListener {
        void onDataFetched(List<Exercise> exerciseList);
    }
}

