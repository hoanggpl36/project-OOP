package com.hlu.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    public static String analyze(String text) {
        try {
            URL url = new URL("http://127.0.0.1:8000/predict");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // JSON gửi đi
            String jsonInput = "{\"text\":\"" + text + "\"}";

            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes());
            os.flush();
            os.close();

            // đọc response
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            br.close();

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
    public static String getSentiment(String json) {
    if (json.contains("NEGATIVE")) return "Tiêu cực";
    if (json.contains("POSITIVE")) return "Tích cực";
    return "Trung lập";
}
    public static boolean needHelp(String json) {
    return json.contains("\"need_help\":1");
}

}