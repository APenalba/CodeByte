package edu.pis.codebyte.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAiManager {

    public interface OnResultLoadedListener {
        void onResultLoaded(String result);
    }

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
    OnResultLoadedListener listener;

    public OpenAiManager() {

    }

    public OnResultLoadedListener getListener() {
        return listener;
    }

    public void setListener(OnResultLoadedListener listener) {
        this.listener = listener;
    }

    public void sendPromptTask () {
        String apiKey = "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";
        String model = "davinci";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("model", model)
                .add("prompt", "Generate a test question with 4 answers.\\n- The answer should be interpreted by a program. Write the question and answers on the first line separated by ';'.\\n- Write the correct answer on the next line.\\n\\n- Difficulty: Hard\\n")
                .add("temperature", "0.5")
                .build();

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/engines/" + model + "/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject jsonObject = new JSONObject(response.body().string());
            String generatedText = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text");
            System.out.println(generatedText);



        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
