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
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.ui.RectangleEdge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

public class MainDashboard extends JFrame {

    private DashboardController controller;
    private AnimatedContentPanel mainContent;
    private JProgressBar progressBar;
    private SidebarButton btnData;
    private SidebarButton btnChart1;
    private SidebarButton btnChart2;
    private SidebarButton btnChart4;
    private Font faFont;

    public MainDashboard(DashboardController controller) {
        this.controller = controller;

        // Load FontAwesome
        try {
            java.io.InputStream is = MainDashboard.class.getResourceAsStream("fa-solid-900.ttf");
            if (is == null) {
                java.io.File file = new java.io.File("src/com/hlu/ui/fa-solid-900.ttf");
                if (!file.exists()) {
                    file = new java.io.File("open/src/com/hlu/ui/fa-solid-900.ttf");
                }
                if (file.exists()) {
                    is = new java.io.FileInputStream(file);
                }
            }
            if (is != null) {
                faFont = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(faFont);
            } else {
                System.out.println("Không tìm thấy file font fa-solid-900.ttf!");
                faFont = new Font("Segoe UI", Font.PLAIN, 14);
            }
        } catch (Exception e) {
            System.out.println("Lỗi tải FontAwesome: " + e.getMessage());
            faFont = new Font("Segoe UI", Font.PLAIN, 14);
        }

        setTitle("Hệ Thống Phân Tích Bão Lũ - Bão Yagi (Control Center)");
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
        sidebar.setBackground(new Color(15, 17, 23)); // Near Black #0F1117
        sidebar.setPreferredSize(new Dimension(250, 750));
        sidebar.setLayout(new BorderLayout());

        // Header Sidebar
        JPanel sidebarHeader = new JPanel();
        sidebarHeader.setOpaque(false);
        sidebarHeader.setBorder(new EmptyBorder(30, 20, 30, 20));
        sidebarHeader.setLayout(new BoxLayout(sidebarHeader, BoxLayout.Y_AXIS));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoIcon = new JLabel("\uf002"); // Search / Magnifying Glass icon
        logoIcon.setFont(faFont.deriveFont(Font.PLAIN, 20));
        logoIcon.setForeground(new Color(10, 132, 255)); // Electric Blue

        JLabel logoLabel = new JLabel("Bão Yagi Analysis");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);

        logoPanel.add(logoIcon);
        logoPanel.add(logoLabel);
        sidebarHeader.add(logoPanel);

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
        menuPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        btnData = createMenuButton("\uf086", "Dữ liệu mạng xã hội"); // comments icon
        btnChart1 = createMenuButton("\uf201", "Tâm lý theo ngày"); // chart-line icon
        btnChart2 = createMenuButton("\uf071", "Phân loại thiệt hại"); // warning / exclamation-triangle icon
        btnChart4 = createMenuButton("\uf1cd", "Hàng cứu trợ"); // life-ring icon

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
        centerPanel.setBackground(new Color(20, 22, 30)); // Deep dark background

        // Progress Bar cho việc gọi API bất đồng bộ
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(1200, 5));
        progressBar.setForeground(new Color(10, 132, 255)); // Electric blue bar
        centerPanel.add(progressBar, BorderLayout.NORTH);

        mainContent = new AnimatedContentPanel();
        mainContent.setLayout(new BorderLayout());
        mainContent.setBorder(new EmptyBorder(25, 30, 25, 30));
        centerPanel.add(mainContent, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Hiển thị Data gốc mặc định
        showDataPanel();
        setActiveButton(btnData);
        mainContent.startTransition();

        // Xử lý sự kiện Menu
        btnData.addActionListener(e -> {
            showDataPanel();
            setActiveButton(btnData);
            mainContent.startTransition();
        });
        btnChart1.addActionListener(e -> {
            showChart1();
            setActiveButton(btnChart1);
            mainContent.startTransition();
        });
        btnChart2.addActionListener(e -> {
            showChart2();
            setActiveButton(btnChart2);
            mainContent.startTransition();
        });
        btnChart4.addActionListener(e -> {
            showChart4();
            setActiveButton(btnChart4);
            mainContent.startTransition();
        });
    }

    private SidebarButton createMenuButton(String iconChar, String text) {
        return new SidebarButton(iconChar, text, faFont);
    }

    private void setActiveButton(SidebarButton activeBtn) {
        SidebarButton[] buttons = { btnData, btnChart1, btnChart2, btnChart4 };
        for (SidebarButton btn : buttons) {
            btn.setActive(btn == activeBtn);
        }
    }

    private void showDataPanel() {
        mainContent.removeAll();

        // Card Container Panel
        GlassCardPanel cardPanel = new GlassCardPanel(new BorderLayout(15, 15));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24)); // 24px padding

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Dữ liệu mạng xã hội thu thập");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);

        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // --- INPUT SECTION (Thêm dữ liệu mới) ---
        JPanel inputPanel = new JPanel(new BorderLayout(12, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        GlowingTextField txtInput = new GlowingTextField();
        txtInput.putClientProperty("JTextField.placeholderText", "Nhập nội dung bài viết mạng xã hội mới tại đây...");

        PillButton btnAdd = new PillButton("\uf067", "Thêm & Phân tích", true, faFont);

        JLabel lblAdd = new JLabel("Đóng góp dữ liệu:");
        lblAdd.setForeground(new Color(235, 235, 245, 180));
        inputPanel.add(lblAdd, BorderLayout.WEST);
        inputPanel.add(txtInput, BorderLayout.CENTER);
        inputPanel.add(btnAdd, BorderLayout.EAST);

        // --- SETTINGS PANEL (Chọn bộ tiền xử lý & mô hình) ---
        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        settingsPanel.setOpaque(false);
        settingsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblPre = new JLabel("Bộ tiền xử lý:");
        lblPre.setForeground(new Color(235, 235, 245, 180));
        settingsPanel.add(lblPre);

        String[] preprocessors = {
                "Nâng cao (Xóa ký tự đặc biệt)",
                "Cơ bản (Chữ thường & khoảng trắng)",
                "Loại bỏ từ dừng (Stopwords)"
        };
        JComboBox<String> cbPreprocess = new JComboBox<>(preprocessors);
        cbPreprocess.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        settingsPanel.add(cbPreprocess);

        JLabel lblAnz = new JLabel("Mô hình phân tích:");
        lblAnz.setForeground(new Color(235, 235, 245, 180));
        settingsPanel.add(lblAnz);

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

        // --- DATA LIST SECTION (Replaces traditional grid table) ---
        List<SocialPost> posts = controller.getPosts();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        // Header panel for table column names
        JPanel tableHeaderPanel = new JPanel(new BorderLayout(15, 0));
        tableHeaderPanel.setOpaque(false);
        tableHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JPanel leftHeader = new JPanel(new GridLayout(1, 3, 10, 0));
        leftHeader.setOpaque(false);

        JLabel lblIdH = new JLabel("ID");
        lblIdH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblIdH.setForeground(new Color(235, 235, 245, 120));

        JLabel lblSrcH = new JLabel("Nguồn");
        lblSrcH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSrcH.setForeground(new Color(235, 235, 245, 120));

        JLabel lblTimeH = new JLabel("Thời gian");
        lblTimeH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTimeH.setForeground(new Color(235, 235, 245, 120));

        leftHeader.add(lblIdH);
        leftHeader.add(lblSrcH);
        leftHeader.add(lblTimeH);

        JLabel lblContentH = new JLabel("Nội dung");
        lblContentH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContentH.setForeground(new Color(235, 235, 245, 120));

        JPanel rightHeader = new JPanel(new GridLayout(1, 3, 10, 0));
        rightHeader.setOpaque(false);

        JLabel lblSentH = new JLabel("Sắc thái", SwingConstants.CENTER);
        lblSentH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSentH.setForeground(new Color(235, 235, 245, 120));

        JLabel lblDmgH = new JLabel("Loại thiệt hại", SwingConstants.CENTER);
        lblDmgH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDmgH.setForeground(new Color(235, 235, 245, 120));

        JLabel lblGoodH = new JLabel("Hàng cứu trợ", SwingConstants.CENTER);
        lblGoodH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblGoodH.setForeground(new Color(235, 235, 245, 120));

        rightHeader.add(lblSentH);
        rightHeader.add(lblDmgH);
        rightHeader.add(lblGoodH);

        leftHeader.setPreferredSize(new Dimension(220, 20));
        rightHeader.setPreferredSize(new Dimension(320, 20));

        tableHeaderPanel.add(leftHeader, BorderLayout.WEST);
        tableHeaderPanel.add(lblContentH, BorderLayout.CENTER);
        tableHeaderPanel.add(rightHeader, BorderLayout.EAST);

        listPanel.add(tableHeaderPanel);
        listPanel.add(Box.createVerticalStrut(5));

        for (SocialPost p : posts) {
            PostRowPanel rowPanel = new PostRowPanel(p, sdf);
            listPanel.add(rowPanel);
            listPanel.add(Box.createVerticalStrut(8)); // Gaps based on Base Unit 4px (2 * 4px)
        }

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

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(cardPanel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(new Color(0, 0, 0, 0)); // transparent background
        chart.getTitle().setPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));

        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(new Color(0, 0, 0, 0));
            chart.getLegend().setItemPaint(new Color(235, 235, 245, 180));
            chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
            chart.getLegend().setFrame(org.jfree.chart.block.BlockBorder.NONE);
        }

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(25, 27, 35, 150));
        plot.setRangeGridlinePaint(new Color(255, 255, 255, 30));
        plot.setOutlineVisible(false);

        plot.getDomainAxis().setLabelPaint(new Color(235, 235, 245, 150));
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getDomainAxis().setTickLabelPaint(new Color(235, 235, 245, 120));
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        plot.getRangeAxis().setLabelPaint(new Color(235, 235, 245, 150));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelPaint(new Color(235, 235, 245, 120));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
    }

    private void showChart1() {
        mainContent.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, int[]> stats = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

        List<SocialPost> posts = controller.getPosts();
        for (SocialPost post : posts) {
            String d = sdf.format(post.getTimestamp());
            stats.putIfAbsent(d, new int[] { 0, 0 });
            if (post.getSentiment().contains("Tích cực") || post.getSentiment().equals("POSITIVE"))
                stats.get(d)[0]++;
            if (post.getSentiment().contains("Tiêu cực") || post.getSentiment().equals("NEGATIVE"))
                stats.get(d)[1]++;
        }

        for (Map.Entry<String, int[]> e : stats.entrySet()) {
            dataset.addValue(e.getValue()[0], "Tích cực", e.getKey());
            dataset.addValue(e.getValue()[1], "Tiêu cực", e.getKey());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Sự thay đổi tâm lý công chúng theo thời gian",
                "Ngày", "Số lượng bài đăng",
                dataset, PlotOrientation.VERTICAL, true, true, false);

        styleChart(chart);
        CategoryPlot plot = chart.getCategoryPlot();

        // Sử dụng BezierLineRenderer cho đường cong Bezier và chấm tròn phát sáng
        BezierLineRenderer renderer = new BezierLineRenderer();
        renderer.setSeriesPaint(0, new Color(40, 200, 64)); // Xanh lá (Tích cực)
        renderer.setSeriesPaint(1, new Color(255, 69, 58)); // Đỏ (Tiêu cực)
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        plot.setRenderer(renderer);

        GlassCardPanel chartContainer = new GlassCardPanel(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(0, 0, 0, 0));
        chartContainer.add(chartPanel, BorderLayout.CENTER);

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
            if (!"Khác".equals(e.getKey()) && !"Chưa phân loại".equals(e.getKey())
                    && !"Hoạt động cứu trợ".equals(e.getKey())) {
                dataset.addValue(e.getValue(), "Số lượng", e.getKey());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Phân loại thiệt hại do bão lũ",
                "Loại thiệt hại", "Số lượng bài đăng",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        styleChart(chart);
        CategoryPlot plot = chart.getCategoryPlot();

        // Tùy chỉnh renderer cột phẳng bo tròn đỉnh và Gradient Sky Blue -> Electric
        // Blue
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new RoundedBarPainter());
        renderer.setSeriesPaint(0, new Color(10, 132, 255)); // Kích hoạt gradient
        renderer.setMaximumBarWidth(0.08); // Làm cột thanh mảnh hơn

        GlassCardPanel chartContainer = new GlassCardPanel(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(0, 0, 0, 0));
        chartContainer.add(chartPanel, BorderLayout.CENTER);

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
            if ("Không nhắc đến".equals(good) || "Chưa phân loại".equals(good))
                continue;

            stats.putIfAbsent(good, new int[] { 0, 0 });
            if (post.getSentiment().contains("Tích cực") || post.getSentiment().equals("POSITIVE"))
                stats.get(good)[0]++;
            if (post.getSentiment().contains("Tiêu cực") || post.getSentiment().equals("NEGATIVE"))
                stats.get(good)[1]++;
        }

        for (Map.Entry<String, int[]> e : stats.entrySet()) {
            dataset.addValue(e.getValue()[0], "Tích cực", e.getKey());
            dataset.addValue(e.getValue()[1], "Tiêu cực", e.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Tâm lý người dân theo loại nhu cầu hàng cứu trợ",
                "Loại hàng cứu trợ", "Số lượng bài đăng",
                dataset, PlotOrientation.VERTICAL, true, true, false);

        styleChart(chart);
        CategoryPlot plot = chart.getCategoryPlot();

        // Tùy chỉnh renderer cột đôi bo tròn với Gradient
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new RoundedBarPainter());
        renderer.setSeriesPaint(0, new Color(40, 200, 64)); // Xanh lá (Tích cực)
        renderer.setSeriesPaint(1, new Color(255, 69, 58)); // Đỏ (Tiêu cực)
        renderer.setMaximumBarWidth(0.12);

        GlassCardPanel chartContainer = new GlassCardPanel(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(0, 0, 0, 0));
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        mainContent.add(chartContainer, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }
}

// Custom components and renderers for the Hi-Tech Control Center UI
class GlassCardPanel extends JPanel {
    private int borderRadius = 24;

    public GlassCardPanel() {
        setOpaque(false);
    }

    public GlassCardPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Semi-transparent background (Glassmorphism)
        g2.setColor(new Color(255, 255, 255, 20)); // rgba(255, 255, 255, 0.08)
        g2.fillRoundRect(0, 0, w - 1, h - 1, borderRadius, borderRadius);

        // Border
        g2.setColor(new Color(255, 255, 255, 25)); // rgba(255, 255, 255, 0.1)
        g2.drawRoundRect(0, 0, w - 1, h - 1, borderRadius, borderRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}

class PillButton extends JButton {
    private Color bgNormal = new Color(10, 132, 255); // Electric Blue #0A84FF
    private Color bgHover = new Color(0, 119, 237); // Deep Blue #0077ED
    private Color bgPressed = new Color(0, 85, 187);
    private Color fgNormal = Color.WHITE;
    private boolean isPrimary = true;
    private JLabel lblIcon;
    private JLabel lblText;

    public PillButton(String iconChar, String text, boolean isPrimary, Font faFont) {
        super();
        this.isPrimary = isPrimary;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));

        if (iconChar != null && faFont != null) {
            lblIcon = new JLabel(iconChar);
            lblIcon.setFont(faFont.deriveFont(Font.PLAIN, 13));
            lblIcon.setForeground(isPrimary ? fgNormal : new Color(210, 214, 223));
            add(lblIcon);
        }

        lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblText.setForeground(isPrimary ? fgNormal : new Color(210, 214, 223));
        add(lblText);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (lblIcon != null) {
            lblIcon.setEnabled(enabled);
        }
        if (lblText != null) {
            lblText.setEnabled(enabled);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color bg = isPrimary ? bgNormal : new Color(42, 45, 56);
        if (getModel().isPressed()) {
            bg = isPrimary ? bgPressed : new Color(25, 27, 34);
        } else if (getModel().isRollover()) {
            bg = isPrimary ? bgHover : new Color(55, 59, 73);
        }

        if (isPrimary) {
            // Neon glow effect (rgba(10, 132, 255, 0.15) 0px 0px 20px 0px)
            g2.setColor(new Color(10, 132, 255, 38));
            for (int i = 1; i <= 5; i++) {
                g2.drawRoundRect(i, i, w - 1 - 2 * i, h - 1 - 2 * i, h, h);
            }
        }

        g2.setColor(bg);
        g2.fillRoundRect(2, 2, w - 5, h - 5, h - 5, h - 5);

        g2.dispose();
        super.paintComponent(g);
    }
}

class SidebarButton extends JButton {
    private boolean active = false;
    private JLabel lblIcon;
    private JLabel lblText;

    public SidebarButton(String iconChar, String text, Font faFont) {
        super();
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setLayout(new BorderLayout(15, 0));

        lblIcon = new JLabel(iconChar);
        lblIcon.setFont(faFont.deriveFont(Font.PLAIN, 14));
        lblIcon.setForeground(new Color(10, 132, 255)); // Electric blue by default

        lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblText.setForeground(new Color(210, 214, 223));

        add(lblIcon, BorderLayout.WEST);
        add(lblText, BorderLayout.CENTER);
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            lblIcon.setForeground(Color.WHITE);
            lblText.setForeground(Color.WHITE);
        } else {
            lblIcon.setForeground(new Color(10, 132, 255)); // Electric blue for icon
            lblText.setForeground(new Color(210, 214, 223));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (active) {
            g2.setColor(new Color(10, 132, 255));
            g2.fillRoundRect(2, 2, w - 5, h - 5, h - 5, h - 5);

            g2.setColor(new Color(10, 132, 255, 40));
            for (int i = 1; i <= 3; i++) {
                g2.drawRoundRect(2 - i, 2 - i, w - 5 + 2 * i, h - 5 + 2 * i, h - 5 + i, h - 5 + i);
            }
        } else if (getModel().isRollover()) {
            g2.setColor(new Color(255, 255, 255, 20)); // rgba(255, 255, 255, 0.08)
            g2.fillRoundRect(2, 2, w - 5, h - 5, h - 5, h - 5);
            g2.setColor(new Color(255, 255, 255, 30));
            g2.drawRoundRect(2, 2, w - 5, h - 5, h - 5, h - 5);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

class GlowingTextField extends JTextField {
    private float focusProgress = 0f;
    private javax.swing.Timer focusTimer;

    public GlowingTextField() {
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        setFont(new Font("Segoe UI", Font.PLAIN, 13));

        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                startFocusTransition(true);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                startFocusTransition(false);
            }
        });
    }

    private void startFocusTransition(boolean focusIn) {
        if (focusTimer != null && focusTimer.isRunning()) {
            focusTimer.stop();
        }
        focusTimer = new javax.swing.Timer(16, e -> {
            if (focusIn) {
                focusProgress += 0.1f;
                if (focusProgress >= 1f) {
                    focusProgress = 1f;
                    focusTimer.stop();
                }
            } else {
                focusProgress -= 0.1f;
                if (focusProgress <= 0f) {
                    focusProgress = 0f;
                    focusTimer.stop();
                }
            }
            repaint();
        });
        focusTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(new Color(20, 20, 25, 200));
        g2.fillRoundRect(2, 2, w - 5, h - 5, 8, 8);

        Color borderColor = new Color(
                (int) (255 * (1 - focusProgress) + 10 * focusProgress),
                (int) (255 * (1 - focusProgress) + 132 * focusProgress),
                (int) (255 * (1 - focusProgress) + 255 * focusProgress),
                (int) (25 + focusProgress * 150));
        g2.setColor(borderColor);
        g2.drawRoundRect(2, 2, w - 5, h - 5, 8, 8);

        if (focusProgress > 0) {
            g2.setColor(new Color(10, 132, 255, (int) (focusProgress * 30)));
            for (int i = 1; i <= 3; i++) {
                g2.drawRoundRect(2 - i, 2 - i, w - 5 + 2 * i, h - 5 + 2 * i, 8 + i, 8 + i);
            }
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

class AnimatedContentPanel extends JPanel {
    private float animationProgress = 0f;
    private javax.swing.Timer animTimer;
    private int slideDistance = 15;

    public AnimatedContentPanel() {
        setOpaque(false);
    }

    public void startTransition() {
        animationProgress = 0f;
        if (animTimer != null && animTimer.isRunning()) {
            animTimer.stop();
        }

        animTimer = new javax.swing.Timer(16, e -> {
            animationProgress += 0.08f;
            if (animationProgress >= 1f) {
                animationProgress = 1f;
                animTimer.stop();
            }
            repaint();
        });
        animTimer.start();
    }

    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animationProgress));

        int offsetY = (int) ((1f - animationProgress) * slideDistance);
        g2.translate(0, offsetY);

        super.paintChildren(g2);
        g2.dispose();
    }
}

class PostRowPanel extends JPanel {
    private float hoverProgress = 0f;
    private javax.swing.Timer fadeTimer;
    private int translateY = 0;

    public PostRowPanel(SocialPost post, SimpleDateFormat sdf) {
        setOpaque(false);
        setLayout(new BorderLayout(15, 0));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        Color sentimentColor;
        String sentiment = post.getSentiment();
        if (sentiment.contains("Tích cực") || sentiment.contains("POSITIVE")) {
            sentimentColor = new Color(40, 200, 64);
        } else if (sentiment.contains("Tiêu cực") || sentiment.contains("NEGATIVE")) {
            sentimentColor = new Color(255, 69, 58);
        } else {
            sentimentColor = new Color(235, 235, 245, 150);
        }

        JPanel leftPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        leftPanel.setOpaque(false);

        JLabel lblId = new JLabel(post.getId());
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblId.setForeground(new Color(235, 235, 245, 180));

        JLabel lblSource = new JLabel(post.getSource());
        lblSource.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSource.setForeground(new Color(10, 132, 255));

        JLabel lblDate = new JLabel(sdf.format(post.getTimestamp()));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(new Color(235, 235, 245, 120));

        leftPanel.add(lblId);
        leftPanel.add(lblSource);
        leftPanel.add(lblDate);

        JLabel lblContent = new JLabel(post.getContent());
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblContent.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        rightPanel.setOpaque(false);

        JLabel lblSentiment = new JLabel(post.getSentiment(), SwingConstants.CENTER);
        lblSentiment.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSentiment.setForeground(sentimentColor);

        JLabel lblDamage = new JLabel(post.getDamageType(), SwingConstants.CENTER);
        lblDamage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDamage.setForeground(new Color(235, 235, 245, 180));

        JLabel lblRelief = new JLabel(post.getReliefGood(), SwingConstants.CENTER);
        lblRelief.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRelief.setForeground(new Color(100, 210, 255));

        rightPanel.add(lblSentiment);
        rightPanel.add(lblDamage);
        rightPanel.add(lblRelief);

        leftPanel.setPreferredSize(new Dimension(220, 40));
        rightPanel.setPreferredSize(new Dimension(320, 40));

        add(leftPanel, BorderLayout.WEST);
        add(lblContent, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                startFade(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                startFade(false);
            }
        });
    }

    private void startFade(boolean fadeIn) {
        if (fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
        }

        fadeTimer = new javax.swing.Timer(16, e -> {
            if (fadeIn) {
                hoverProgress += 0.08f;
                if (hoverProgress >= 1f) {
                    hoverProgress = 1f;
                    fadeTimer.stop();
                }
            } else {
                hoverProgress -= 0.08f;
                if (hoverProgress <= 0f) {
                    hoverProgress = 0f;
                    fadeTimer.stop();
                }
            }
            translateY = (int) (hoverProgress * -4);
            repaint();
        });
        fadeTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int alpha = (int) (10 + hoverProgress * 25); // rgba(255,255,255,0.04) to rgba(255,255,255,0.14)
        g2.setColor(new Color(255, 255, 255, alpha));

        g2.fillRoundRect(2, 2 + translateY, w - 5, h - 5, 16, 16);

        int borderAlpha = (int) (15 + hoverProgress * 30);
        g2.setColor(new Color(255, 255, 255, borderAlpha));
        g2.drawRoundRect(2, 2 + translateY, w - 5, h - 5, 16, 16);

        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(0, translateY);
        super.paintChildren(g2);
        g2.dispose();
    }
}

class BezierLineRenderer extends LineAndShapeRenderer {
    public BezierLineRenderer() {
        super(true, true);
    }

    @Override
    public boolean getItemLineVisible(int row, int column) {
        return false; // Tell superclass not to draw straight lines
    }

    @Override
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {

        super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column, pass);

        if (pass == 0 && column > 0) {
            Number currentValue = dataset.getValue(row, column);
            if (currentValue != null) {
                Number previousValue = dataset.getValue(row, column - 1);
                if (previousValue != null) {
                    double x0 = domainAxis.getCategoryMiddle(column - 1, dataset.getColumnCount(), dataArea,
                            plot.getDomainAxisEdge());
                    double y0 = rangeAxis.valueToJava2D(previousValue.doubleValue(), dataArea, plot.getRangeAxisEdge());

                    double x1 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea,
                            plot.getDomainAxisEdge());
                    double y1 = rangeAxis.valueToJava2D(currentValue.doubleValue(), dataArea, plot.getRangeAxisEdge());

                    g2.setPaint(getItemPaint(row, column));
                    g2.setStroke(getItemStroke(row, column));

                    double ctrlX1 = x0 + (x1 - x0) / 2.0;
                    double ctrlY1 = y0;
                    double ctrlX2 = x0 + (x1 - x0) / 2.0;
                    double ctrlY2 = y1;

                    CubicCurve2D.Double curve = new CubicCurve2D.Double(
                            x0, y0, ctrlX1, ctrlY1, ctrlX2, ctrlY2, x1, y1);
                    g2.draw(curve);
                }
            }
        }

        if (pass == 1) {
            Number currentValue = dataset.getValue(row, column);
            if (currentValue != null) {
                double x1 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea,
                        plot.getDomainAxisEdge());
                double y1 = rangeAxis.valueToJava2D(currentValue.doubleValue(), dataArea, plot.getRangeAxisEdge());

                Color paint = (Color) getItemPaint(row, column);

                g2.setStroke(new BasicStroke(1.0f));
                g2.setColor(new Color(paint.getRed(), paint.getGreen(), paint.getBlue(), 30));
                g2.fill(new Ellipse2D.Double(x1 - 8, y1 - 8, 16, 16));
                g2.setColor(new Color(paint.getRed(), paint.getGreen(), paint.getBlue(), 60));
                g2.fill(new Ellipse2D.Double(x1 - 6, y1 - 6, 12, 12));

                g2.setColor(paint);
                g2.fill(new Ellipse2D.Double(x1 - 4, y1 - 4, 8, 8));

                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4));
            }
        }
    }
}

class RoundedBarPainter extends StandardBarPainter {
    @Override
    public void paintBar(Graphics2D g2, BarRenderer renderer, int row, int column, RectangularShape bar,
            RectangleEdge base) {
        double x = bar.getX();
        double y = bar.getY();
        double width = bar.getWidth();
        double height = bar.getHeight();

        RoundRectangle2D rounded = new RoundRectangle2D.Double(x, y, width, height, 12, 12);

        Paint itemPaint = renderer.getItemPaint(row, column);
        Color baseColor = Color.BLUE;
        if (itemPaint instanceof Color) {
            baseColor = (Color) itemPaint;
        }

        GradientPaint gp;
        if (baseColor.equals(new Color(52, 152, 219)) || baseColor.equals(new Color(10, 132, 255))) {
            gp = new GradientPaint(
                    0, (float) y, new Color(100, 210, 255), // Sky Blue #64D2FF
                    0, (float) (y + height), new Color(10, 132, 255) // Electric Blue #0A84FF
            );
        } else {
            Color lightColor = getLighterColor(baseColor);
            gp = new GradientPaint(
                    0, (float) y, lightColor,
                    0, (float) (y + height), baseColor);
        }

        g2.setPaint(gp);
        g2.fill(rounded);

        if (renderer.isDrawBarOutline()) {
            g2.setStroke(renderer.getItemOutlineStroke(row, column));
            g2.setPaint(renderer.getItemOutlinePaint(row, column));
            g2.draw(rounded);
        }
    }

    private Color getLighterColor(Color c) {
        int r = Math.min(255, c.getRed() + 60);
        int g = Math.min(255, c.getGreen() + 60);
        int b = Math.min(255, c.getBlue() + 60);
        return new Color(r, g, b, c.getAlpha());
    }
}