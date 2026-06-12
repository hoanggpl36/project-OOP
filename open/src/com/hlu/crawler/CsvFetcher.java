package com.hlu.crawler;

import com.hlu.model.SocialPost;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvFetcher implements IDataFetcher {

    private String csvFilePath;

    public CsvFetcher(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    @Override
    public List<SocialPost> fetchData(String keyword, Date from, Date to) {
        List<SocialPost> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            int idCounter = 1;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Bỏ qua header
                    continue;
                }

                // Split by comma. Lưu ý: nếu nội dung có dấu phẩy thì split này sẽ lỗi,
                // Nhưng với file CSV đơn giản thì dùng cách này tạm thời.
                // data.csv có dạng: date,content,clean,sentiment,need_help
                // Nhưng có thể có quote ""
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (values.length >= 2) {
                    String dateStr = values[0].replace("\"", "").trim();
                    String content = values[1].replace("\"", "").trim();
                    
                    Date date = new Date(); // default
                    try {
                        if (!dateStr.isEmpty()) {
                            date = sdf.parse(dateStr);
                        }
                    } catch (Exception e) {
                        System.out.println("Lỗi parse ngày: " + dateStr);
                    }

                    list.add(new SocialPost(
                            String.valueOf(idCounter++),
                            "File CSV",
                            content,
                            date,
                            "Chưa phân tích",
                            "Chưa phân loại",
                            "Chưa phân loại"
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Không đọc được file CSV: " + e.getMessage());
        }

        return list;
    }

    public void savePostToCsv(SocialPost post, boolean needHelp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(post.getTimestamp());
        String contentEscaped = post.getContent().replace("\"", "\"\"");
        String cleanText = com.hlu.preprocessing.DataPreprocessor.cleanText(post.getContent());
        String sentiment = post.getSentiment();
        String needHelpStr = needHelp ? "YES" : "NO";

        String csvLine = String.format("%s,\"%s\",\"%s\",%s,%s", dateStr, contentEscaped, cleanText, sentiment, needHelpStr);

        try (java.io.FileWriter fw = new java.io.FileWriter(csvFilePath, true);
             java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
             java.io.PrintWriter out = new java.io.PrintWriter(bw)) {
            out.println();
            out.print(csvLine);
        } catch (Exception e) {
            System.out.println("Không ghi được vào file CSV: " + e.getMessage());
        }
    }
}
