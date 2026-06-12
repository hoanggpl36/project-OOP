package com.hlu.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    public static String analyze(String text) {
        try {
            // Sửa lỗi cảnh báo deprecation bằng cách dùng URI
            URL url = java.net.URI.create("http://127.0.0.1:8000/predict").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Dùng Gson để tạo JSON an toàn (tránh lỗi nếu text có dấu ngoặc kép)
            com.google.gson.JsonObject json = new com.google.gson.JsonObject();
            json.addProperty("text", text);
            String jsonInput = json.toString();

            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes(java.nio.charset.StandardCharsets.UTF_8));
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