package com.hlu.ui;

import com.hlu.model.SocialPost;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Date;

public class MainDashboard extends JFrame {

    private DashboardController controller;
    private JPanel mainContent;
    private JProgressBar progressBar;
    private JButton btnData;
    private JButton btnChart1;
    private JButton btnChart2;
    private JButton btnChart4;

    public MainDashboard(DashboardController controller) {
        this.controller = controller;
        
        setTitle("Hệ Thống Phân Tích Bão Lũ - Bão Yagi");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setLayout(new BorderLayout());

        // Thay đổi font chữ mặc định cho ứng dụng
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));

        // ===== SIDEBAR (Panel bên trái) =====
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 32, 40)); // Slate tối hiện đại
        sidebar.setPreferredSize(new Dimension(240, 750));
        sidebar.setLayout(new BorderLayout());

        // Header Sidebar
        JPanel sidebarHeader = new JPanel();
        sidebarHeader.setOpaque(false);
        sidebarHeader.setBorder(new EmptyBorder(25, 15, 25, 15));
        sidebarHeader.setLayout(new BoxLayout(sidebarHeader, BoxLayout.Y_AXIS));
        
        JLabel logoLabel = new JLabel("🌪️ Bão Yagi Analysis");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarHeader.add(logoLabel);

        JLabel subLogoLabel = new JLabel("Hệ thống giám sát cứu trợ");
        subLogoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subLogoLabel.setForeground(new Color(150, 155, 170));
        subLogoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarHeader.add(Box.createVerticalStrut(5));
        sidebarHeader.add(subLogoLabel);

        sidebar.add(sidebarHeader, BorderLayout.NORTH);

        // Menu Buttons
        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        btnData = createMenuButton("📋 Dữ liệu mạng xã hội");
        btnChart1 = createMenuButton("📈 Tâm lý theo ngày");
        btnChart2 = createMenuButton("📊 Phân loại thiệt hại");
        btnChart4 = createMenuButton("🆘 Hàng cứu trợ");

        menuPanel.add(btnData);
        menuPanel.add(btnChart1);
        menuPanel.add(btnChart2);
        menuPanel.add(btnChart4);

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Footer Sidebar
        JLabel footerLabel = new JLabel("Đồ án OOP - HLU © 2026", SwingConstants.CENTER);
        footerLabel.setForeground(new Color(100, 105, 120));
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setBorder(new EmptyBorder(10, 10, 20, 10));
        sidebar.add(footerLabel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        // ===== MAIN CONTENT CONTAINER =====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 246, 250)); // Màu nền xám nhẹ hiện đại

        // Progress Bar cho việc gọi API bất đồng bộ
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(1200, 5));
        centerPanel.add(progressBar, BorderLayout.NORTH);

        mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 25, 20, 25));
        centerPanel.add(mainContent, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Hiển thị Data gốc mặc định
        showDataPanel();
        setActiveButton(btnData);

        // Xử lý sự kiện Menu
        btnData.addActionListener(e -> {
            showDataPanel();
            setActiveButton(btnData);
        });
        btnChart1.addActionListener(e -> {
            showChart1();
            setActiveButton(btnChart1);
        });
        btnChart2.addActionListener(e -> {
            showChart2();
            setActiveButton(btnChart2);
        });
        btnChart4.addActionListener(e -> {
            showChart4();
            setActiveButton(btnChart4);
        });
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(new Color(210, 214, 223));
        btn.setBackground(new Color(42, 45, 56));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return btn;
    }

    private void setActiveButton(JButton activeBtn) {
        JButton[] buttons = {btnData, btnChart1, btnChart2, btnChart4};
        for (JButton btn : buttons) {
            if (btn == activeBtn) {
                btn.setBackground(new Color(0, 122, 255)); // Màu xanh chủ đạo
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(42, 45, 56));
                btn.setForeground(new Color(210, 214, 223));
            }
        }
    }

    private void showDataPanel() {
        mainContent.removeAll();

        // Card Container Panel
        JPanel cardPanel = new JPanel(new BorderLayout(15, 15));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Dữ liệu mạng xã hội thu thập");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(45, 52, 54));
        headerPanel.add(title, BorderLayout.WEST);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // --- INPUT SECTION (Thêm dữ liệu mới) ---
        JPanel inputPanel = new JPanel(new BorderLayout(12, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JTextField txtInput = new JTextField();
        txtInput.putClientProperty("JTextField.placeholderText", "Nhập nội dung bài viết mạng xã hội mới tại đây...");
        txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JButton btnAdd = new JButton("➕ Thêm & Phân tích");
        btnAdd.setBackground(new Color(0, 122, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        inputPanel.add(new JLabel("Đóng góp dữ liệu:"), BorderLayout.WEST);
        inputPanel.add(txtInput, BorderLayout.CENTER);
        inputPanel.add(btnAdd, BorderLayout.EAST);
        
        // --- SETTINGS PANEL (Chọn bộ tiền xử lý & mô hình) ---
        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        settingsPanel.setOpaque(false);
        settingsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        settingsPanel.add(new JLabel("Bộ tiền xử lý:"));
        String[] preprocessors = {
            "Nâng cao (Xóa ký tự đặc biệt)",
            "Cơ bản (Chữ thường & khoảng trắng)",
            "Loại bỏ từ dừng (Stopwords)"
        };
        JComboBox<String> cbPreprocess = new JComboBox<>(preprocessors);
        cbPreprocess.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        settingsPanel.add(cbPreprocess);

        settingsPanel.add(new JLabel("Mô hình phân tích:"));
        String[] analyzers = {
            "Python BERT API (FastAPI)",
            "Java Local Rules (Offline)"
        };
        JComboBox<String> cbAnalyzer = new JComboBox<>(analyzers);
        cbAnalyzer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        settingsPanel.add(cbAnalyzer);

        // Gom nhóm input panel và settings panel
        JPanel inputContainer = new JPanel(new BorderLayout(5, 5));
        inputContainer.setOpaque(false);
        inputContainer.add(inputPanel, BorderLayout.NORTH);
        inputContainer.add(settingsPanel, BorderLayout.SOUTH);

        headerPanel.add(inputContainer, BorderLayout.SOUTH);

        // --- TABLE SECTION ---
        String[] columns = {"ID", "Nguồn", "Nội dung", "Thời gian", "Sắc thái", "Loại thiệt hại", "Hàng cứu trợ"};
        List<SocialPost> posts = controller.getPosts();
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (SocialPost p : posts) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getSource(),
                    p.getContent(),
                    sdf.format(p.getTimestamp()),
                    p.getSentiment(),
                    p.getDamageType(),
                    p.getReliefGood()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(32); // Cho dòng cao, thoáng hơn
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(230, 242, 255));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(79, 86, 97));
        header.setPreferredSize(new Dimension(100, 35));

        // Renderer màu sắc cho từng dòng theo sắc thái
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String sentiment = table.getValueAt(row, 4).toString();
                
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    if ("Tiêu cực".equals(sentiment) || "NEGATIVE".equals(sentiment)) {
                        c.setBackground(new Color(255, 235, 235)); // Màu hồng nhạt đẹp mắt
                        c.setForeground(new Color(192, 57, 43));
                    } else if ("Tích cực".equals(sentiment) || "POSITIVE".equals(sentiment)) {
                        c.setBackground(new Color(235, 255, 235)); // Màu xanh lá nhạt
                        c.setForeground(new Color(39, 174, 96));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.DARK_GRAY);
                    }
                }
                
                if (column == 2) {
                    setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                return c;
            }
        });

        // Xử lý sự kiện Thêm dữ liệu (BẤT ĐỒNG BỘ bằng SwingWorker)
        btnAdd.addActionListener(e -> {
            String text = txtInput.getText().trim();
            if (!text.isEmpty()) {
                // Vô hiệu hóa nút và input để tránh spam click
                txtInput.setEnabled(false);
                btnAdd.setEnabled(false);
                
                // Hiển thị loading bar
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
                progressBar.setString("Đang phân tích dữ liệu...");
                progressBar.setStringPainted(true);

                // Khởi chạy Worker
                SwingWorker<SocialPost, Void> worker = new SwingWorker<>() {
                    @Override
                    protected SocialPost doInBackground() throws Exception {
                        // Chọn bộ tiền xử lý
                        com.hlu.preprocessing.PreprocessStrategy pre = new com.hlu.preprocessing.AdvancedPreprocess();
                        int preIdx = cbPreprocess.getSelectedIndex();
                        if (preIdx == 1) {
                            pre = new com.hlu.preprocessing.BasicPreprocess();
                        } else if (preIdx == 2) {
                            pre = new com.hlu.preprocessing.StopwordsPreprocess();
                        }

                        // Chọn bộ phân tích cảm xúc
                        com.hlu.analyzer.ISentimentAnalyzer anz = new com.hlu.analyzer.PythonApiAnalyzer();
                        int anzIdx = cbAnalyzer.getSelectedIndex();
                        if (anzIdx == 1) {
                            anz = new com.hlu.analyzer.JavaLocalAnalyzer();
                        }

                        return controller.processNewPost(text, pre, anz);
                    }

                    @Override
                    protected void done() {
                        try {
                            SocialPost newPost = get();
                            txtInput.setText("");
                            JOptionPane.showMessageDialog(MainDashboard.this, 
                                    "Đã thêm dữ liệu và phân tích thành công!\nSắc thái: " + newPost.getSentiment(), 
                                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(MainDashboard.this, 
                                    "Lỗi xử lý hoặc kết nối API: " + ex.getMessage(), 
                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            // Khôi phục lại trạng thái
                            txtInput.setEnabled(true);
                            btnAdd.setEnabled(true);
                            progressBar.setVisible(false);
                            // Cập nhật lại giao diện bảng dữ liệu
                            showDataPanel();
                        }
                    }
                };
                worker.execute();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(cardPanel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showChart1() {
        mainContent.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, int[]> stats = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

        List<SocialPost> posts = controller.getPosts();
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

        // Tùy chỉnh màu sắc biểu đồ cho hiện đại
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 249, 250));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        // Tùy chỉnh font chữ hiện đại
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Tùy chỉnh đường vẽ nét dày và có màu sắc tươi sáng hơn
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(46, 204, 113)); // Xanh lá (Tích cực)
        renderer.setSeriesPaint(1, new Color(231, 76, 60));  // Đỏ (Tiêu cực)
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));

        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 240), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);

        mainContent.add(chartContainer, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showChart2() {
        mainContent.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> counts = new HashMap<>();

        List<SocialPost> posts = controller.getPosts();
        for (SocialPost post : posts) {
            String type = post.getDamageType();
            counts.put(type, counts.getOrDefault(type, 0) + 1);
        }

        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (!"Khác".equals(e.getKey()) && !"Chưa phân loại".equals(e.getKey()) && !"Hoạt động cứu trợ".equals(e.getKey())) {
                dataset.addValue(e.getValue(), "Số lượng", e.getKey());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Phân loại thiệt hại do bão lũ",
                "Loại thiệt hại", "Số lượng bài đăng",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 249, 250));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        // Tùy chỉnh font chữ hiện đại
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Tùy chỉnh renderer cột phẳng (Flat Bar) hiện đại
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardBarPainter()); // Xóa bỏ bóng và gradient 3D mặc định
        renderer.setSeriesPaint(0, new Color(52, 152, 219)); // Xanh dương nhẹ hiện đại
        renderer.setMaximumBarWidth(0.08); // Làm cột thanh mảnh hơn

        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 240), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);

        mainContent.add(chartContainer, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showChart4() {
        mainContent.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, int[]> stats = new HashMap<>();

        List<SocialPost> posts = controller.getPosts();
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
                "Tâm lý người dân theo loại nhu cầu hàng cứu trợ",
                "Loại hàng cứu trợ", "Số lượng bài đăng",
                dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 249, 250));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        // Tùy chỉnh font chữ hiện đại
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Tùy chỉnh renderer cột đôi phẳng hiện đại
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(46, 204, 113)); // Xanh lá (Tích cực)
        renderer.setSeriesPaint(1, new Color(231, 76, 60));  // Đỏ (Tiêu cực)
        renderer.setMaximumBarWidth(0.12); // Làm cột gọn gàng hơn

        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 240), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);

        mainContent.add(chartContainer, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }
}