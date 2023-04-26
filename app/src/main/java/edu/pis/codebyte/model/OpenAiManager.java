package edu.pis.codebyte.model;


import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAiManager {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public OpenAiManager() {

    }

    public void callAPI(String prompt) {
        // TODO: okhttp setup

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","text-davinei-903");
            jsonBody.put("prompt",prompt);
            jsonBody.put("max_tokens",4900) ;
            jsonBody.put("temperature",0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //TODO
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else {
                    //TODO
                }

            }
        });
    }
}
