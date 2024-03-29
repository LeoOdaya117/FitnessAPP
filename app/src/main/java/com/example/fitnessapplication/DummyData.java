package com.example.fitnessapplication;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

public class DummyData {


    public interface MembershipFetchListener {
        void onMembershipFetched(ArrayList<SubscriptionPlan> plans);
        void onFetchError(String errorMessage);
    }

    public static void fetchMembershipPlans(MembershipFetchListener listener) {
        String websiteUrl = URLManager.MY_URL;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url( websiteUrl + "/Gym_Website/user/fetch_membership.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onFetchError(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    ArrayList<SubscriptionPlan> plans = parseMembershipPlans(jsonData);
                    listener.onMembershipFetched(plans);
                } else {
                    listener.onFetchError("Failed to fetch membership plans: " + response.code());
                }
            }
        });
    }

    public static void getDummyPlans(MembershipFetchListener listener) {
        fetchMembershipPlans(listener);
    }

    private static ArrayList<SubscriptionPlan> parseMembershipPlans(String jsonData) {
        ArrayList<SubscriptionPlan> plans = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("subscriptionName");
                String description = jsonObject.getString("subscriptionDescription");
                double price = jsonObject.getDouble("Price");
                plans.add(new SubscriptionPlan(name, description, price));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return plans;
    }
}
