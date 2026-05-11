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
}
