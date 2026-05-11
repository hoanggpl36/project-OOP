package com.hlu.ui;

import com.hlu.model.SocialPost;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Date;

import com.hlu.api.ApiClient;
import com.hlu.preprocessing.DataPreprocessor;
import com.hlu.analyzer.DamageClassificationTask;
import com.hlu.analyzer.ReliefGoodsSentimentTask;

public class MainDashboard extends JFrame {

    private List<SocialPost> posts;
    private JPanel mainContent;

    public MainDashboard(List<SocialPost> posts) {
        this.posts = posts;
        setTitle("Hệ Thống Phân Tích Bão Lũ - Bão Yagi");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setPreferredSize(new Dimension(200, 700));
        sidebar.setLayout(new GridLayout(6, 1, 10, 10));

        JButton btnData = createMenuButton("Dữ liệu gốc");
        JButton btnChart1 = createMenuButton("Tâm lý theo ngày (Bài 1)");
        JButton btnChart2 = createMenuButton("Loại thiệt hại (Bài 2)");
        JButton btnChart4 = createMenuButton("Hàng cứu trợ (Bài 4)");

        sidebar.add(btnData);
        sidebar.add(btnChart1);
        sidebar.add(btnChart2);
        sidebar.add(btnChart4);

        add(sidebar, BorderLayout.WEST);

        // ===== MAIN CONTENT =====
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        add(mainContent, BorderLayout.CENTER);

        // Hiển thị Data gốc mặc định
        showDataPanel();

        // Xử lý sự kiện Menu
        btnData.addActionListener(e -> showDataPanel());
        btnChart1.addActionListener(e -> showChart1());
        btnChart2.addActionListener(e -> showChart2());
        btnChart4.addActionListener(e -> showChart4());
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(52, 58, 64));
        btn.setFocusPainted(false);
        return btn;
    }

    private void showDataPanel() {
        mainContent.removeAll();
        JLabel title = new JLabel("Dữ liệu mạng xã hội", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        
        // --- INPUT PANEL ---
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField txtInput = new JTextField();
        JButton btnAdd = new JButton("Thêm dữ liệu");
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        
        inputPanel.add(new JLabel("Nhập nội dung mới:"), BorderLayout.WEST);
        inputPanel.add(txtInput, BorderLayout.CENTER);
        inputPanel.add(btnAdd, BorderLayout.EAST);
        
        btnAdd.addActionListener(e -> {
            String text = txtInput.getText().trim();
            if (!text.isEmpty()) {
                // Tiền xử lý
                String cleanText = DataPreprocessor.cleanText(text);
                // Tạo post
                SocialPost newPost = new SocialPost(
                        "U" + System.currentTimeMillis(),
                        "Nhập tay",
                        text,
                        new Date(),
                        "Chưa phân tích",
                        "Chưa phân loại",
                        "Chưa phân loại"
                );
                
                // Gọi API
                String res = ApiClient.analyze(cleanText);
                if (!"ERROR".equals(res)) {
                    newPost.setSentiment(ApiClient.getSentiment(res));
                } else {
                    newPost.setSentiment("Lỗi API");
                }
                
                posts.add(newPost);
                
                // Phân loại lại 
                new DamageClassificationTask().execute(posts);
                new ReliefGoodsSentimentTask().execute(posts);
                
                txtInput.setText("");
                JOptionPane.showMessageDialog(mainContent, "Đã thêm dữ liệu và phân tích thành công!");
                showDataPanel(); // Refresh
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.SOUTH);

        mainContent.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Nguồn", "Nội dung", "Ngày", "Sắc thái", "Thiệt hại", "Hàng cứu trợ"};
        Object[][] data = new Object[posts.size()][7];
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (int i = 0; i < posts.size(); i++) {
            SocialPost p = posts.get(i);
            data[i][0] = p.getId();
            data[i][1] = p.getSource();
            data[i][2] = p.getContent();
            data[i][3] = sdf.format(p.getTimestamp());
            data[i][4] = p.getSentiment();
            data[i][5] = p.getDamageType();
            data[i][6] = p.getReliefGood();
        }

        JTable table = new JTable(data, columns);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String sentiment = table.getValueAt(row, 4).toString();
                if ("Tiêu cực".equals(sentiment) || "NEGATIVE".equals(sentiment)) {
                    c.setBackground(new Color(255, 200, 200));
                } else if ("Tích cực".equals(sentiment) || "POSITIVE".equals(sentiment)) {
                    c.setBackground(new Color(200, 255, 200));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        mainContent.add(new JScrollPane(table), BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showChart1() {
        mainContent.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, int[]> stats = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

        for (SocialPost post : posts) {
            String d = sdf.format(post.getTimestamp());
            stats.putIfAbsent(d, new int[]{0, 0});
            if (post.getSentiment().contains("Tích cực") || post.getSentiment().equals("POSITIVE")) stats.get(d)[0]++;
            if (post.getSentiment().contains("Tiêu cực") || post.getSentiment().equals("NEGATIVE")) stats.get(d)[1]++;
        }

        for (Map.Entry<String, int[]> e : stats.entrySet()) {
            dataset.addValue(e.getValue()[0], "Tích cực", e.getKey());
            dataset.addValue(e.getValue()[1], "Tiêu cực", e.getKey());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Sự thay đổi tâm lý công chúng theo thời gian",
                "Ngày", "Số lượng bài đăng",
                dataset, PlotOrientation.VERTICAL, true, true, false);

        mainContent.add(new ChartPanel(chart), BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showChart2() {
        mainContent.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> counts = new HashMap<>();

        for (SocialPost post : posts) {
            String type = post.getDamageType();
            counts.put(type, counts.getOrDefault(type, 0) + 1);
        }

        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (!"Khác".equals(e.getKey()) && !"Chưa phân loại".equals(e.getKey())) {
                dataset.addValue(e.getValue(), "Thiệt hại", e.getKey());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Phân loại thiệt hại phổ biến",
                "Loại thiệt hại", "Số lượng báo cáo",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        mainContent.add(new ChartPanel(chart), BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showChart4() {
        mainContent.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, int[]> stats = new HashMap<>();

        for (SocialPost post : posts) {
            String good = post.getReliefGood();
            if ("Không nhắc đến".equals(good) || "Chưa phân loại".equals(good)) continue;
            
            stats.putIfAbsent(good, new int[]{0, 0});
            if (post.getSentiment().contains("Tích cực") || post.getSentiment().equals("POSITIVE")) stats.get(good)[0]++;
            if (post.getSentiment().contains("Tiêu cực") || post.getSentiment().equals("NEGATIVE")) stats.get(good)[1]++;
        }

        for (Map.Entry<String, int[]> e : stats.entrySet()) {
            dataset.addValue(e.getValue()[0], "Tích cực", e.getKey());
            dataset.addValue(e.getValue()[1], "Tiêu cực", e.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Tâm lý người dân theo loại hàng cứu trợ",
                "Hàng cứu trợ", "Số lượng bài đăng",
                dataset, PlotOrientation.VERTICAL, true, true, false);

        mainContent.add(new ChartPanel(chart), BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }
}