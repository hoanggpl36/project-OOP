from transformers import pipeline

# model đa ngôn ngữ (có tiếng Việt)
classifier = pipeline(
    "sentiment-analysis",
    model="nlptown/bert-base-multilingual-uncased-sentiment"
)

# def predict(text):
#     result = classifier(text)[0]
    
#     sentiment = result["label"]  # ví dụ: '4 stars'
    
#     # convert về POSITIVE / NEGATIVE
#     if "1" in sentiment or "2" in sentiment:
#         final_sentiment = "NEGATIVE"
#     elif "4" in sentiment or "5" in sentiment:
#         final_sentiment = "POSITIVE"
#     else:
#         final_sentiment = "NEUTRAL"

#     need_help = 1 if final_sentiment == "NEGATIVE" else 0

#     return {
#         "sentiment": final_sentiment,
#         "need_help": need_help
#     }
def predict(text):
    text_lower = text.lower()

    # 🛑 BƯỚC 0: Kiểm tra xem câu có ngữ cảnh bão lũ, cứu trợ không (Context Filter)
    context_keywords = [
        "bão", "lũ", "ngập", "cứu", "hỗ trợ", "thuốc", "y tế", "thực phẩm", 
        "sạt lở", "thiệt hại", "quyên góp", "ủng hộ", "đồ ăn", "nước uống", 
        "áo phao", "cô lập", "mưa lớn", "chìm", "mất tích", "cứu trợ"
    ]
    
    # Nếu không có từ khóa liên quan đến bão lũ, cho về Trung lập
    if not any(word in text_lower for word in context_keywords):
        return {
            "sentiment": "NEUTRAL",
            "need_help": 0
        }

    # 🚨 BƯỚC 1: check từ khóa nguy hiểm (THÊM ĐOẠN NÀY)
    if any(word in text_lower for word in ["cứu", "khẩn", "help", "emergency"]):
        return {
            "sentiment": "NEGATIVE",
            "need_help": 1
        }
        
    # 🔥 BƯỚC 2: check từ khóa tích cực
    if any(word in text_lower for word in ["vui", "cảm ơn", "tốt", "tuyệt", "hỗ trợ", "thuốc", "y tế", "cứu trợ"]):
        return {
            "sentiment": "POSITIVE",
            "need_help": 0
        }

    # 🤖 BƯỚC 2: nếu không có từ nguy hiểm thì mới dùng AI
    result = classifier(text)[0]
    sentiment = result["label"]

    if "1" in sentiment or "2" in sentiment:
        final_sentiment = "NEGATIVE"
    elif "4" in sentiment or "5" in sentiment:
        final_sentiment = "POSITIVE"
    else:
        final_sentiment = "NEUTRAL"

    need_help = 1 if final_sentiment == "NEGATIVE" else 0

    return {
        "sentiment": final_sentiment,
        "need_help": need_help
    }