package com.hlu.model;

import java.util.Arrays;
import java.util.List;

public class DisasterConfig {
    public static final String DISASTER_NAME = "Bão Yagi";
    
    // Từ khóa để thu thập
    public static final List<String> KEYWORDS = Arrays.asList(
        "bão yagi", "siêu bão yagi", "lụt", "ngập", "mất điện", "sạt lở", "cứu trợ"
    );
    
    // Phân loại thiệt hại (Bài toán 2)
    public static final List<String> DAMAGE_TYPES = Arrays.asList(
        "Người bị ảnh hưởng",
        "Gián đoạn kinh tế",
        "Nhà cửa bị hư hỏng",
        "Tài sản bị mất",
        "Cơ sở hạ tầng",
        "Khác"
    );
    
    // Phân loại hàng cứu trợ (Bài toán 4)
    public static final List<String> RELIEF_GOODS = Arrays.asList(
        "Nhà ở",
        "Giao thông",
        "Thực phẩm",
        "Y tế",
        "Tiền mặt"
    );
}
