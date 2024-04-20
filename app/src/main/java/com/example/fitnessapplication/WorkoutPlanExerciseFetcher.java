package com.example.fitnessapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WorkoutPlanExerciseFetcher {

    public interface WorkoutPlanExerciseFetchListener {
        void onExercisesFetched(List<WorkoutPlanExercise> exercises);
    }

    public interface WorkoutPlanExerciseFetchErrorListener {
        void onFetchFailed(IOException e);
    }

    private OkHttpClient client;
    private Context context;

    public WorkoutPlanExerciseFetcher(Context context) {
        this.client = new OkHttpClient();
        this.context = context;
    }

    public void fetchExercises(final WorkoutPlanExerciseFetchListener listener,
                               final WorkoutPlanExerciseFetchErrorListener errorListener) {
        String baseUrl = URLManager.MY_URL;
        String username = UserDataManager.getInstance(context).getEmail();
        String workoutPlanId = UserDataManager.getInstance(context).getWorkoutPlanId();
        String day = UserDataManager.getInstance(context).getWorkoutPlanDay();
        String url = baseUrl + "/User/api/fetch_workout_data.php?";
        url += "IdNum=" + username + "&";  // Separate parameters with "&"
        url += "WorkoutPlanID=" + workoutPlanId + "&";
        url += "day=" + day;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errorListener.onFetchFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String jsonData = response.body().string();
                    List<WorkoutPlanExercise> exercises = parseJsonData(jsonData);

                    // Assuming you have a context available, you can use it to get the main looper
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onExercisesFetched(exercises);
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorListener.onFetchFailed((IOException) e);
                        }
                    });
                }
            }

        });
    }

    private List<WorkoutPlanExercise> parseJsonData(String jsonData) {
        List<WorkoutPlanExercise> exercises = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                WorkoutPlanExercise exercise = new WorkoutPlanExercise(
                        jsonObject.getInt("workoutExerciseID"),
                        jsonObject.getString("Status"),
                        jsonObject.getString("ExerciseID"),
                        jsonObject.getString("image"),
                        jsonObject.getString("name"),
                        jsonObject.getString("set"),
                        jsonObject.getString("reps"), // Here's the potential issue
                        jsonObject.getString("duration"),
                        jsonObject.getString("Difficulty")
                );
                exercises.add(exercise);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exercises;
    }

}
