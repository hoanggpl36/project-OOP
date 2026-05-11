import pandas as pd
import re
from transformers import pipeline
df = pd.read_csv("data.csv", encoding="utf-8")
def clean_text(text):
    text = text.lower()
    text = re.sub(r"http\S+", "", text)
    text = re.sub(r"@\w+", "", text)
    text = re.sub(r"#\w+", "", text)
    return text
df["clean"] = df["content"].apply(clean_text)
model = pipeline("sentiment-analysis")

def get_sentiment(text):
    try:
        return model(text[:512])[0]["label"]
    except:
        return "NEUTRAL"
df["sentiment"] = df["clean"].apply(get_sentiment)

def detect_need(text):
    keywords = ["cần", "thiếu", "cứu trợ", "khẩn cấp"]
    for k in keywords:
        if k in text:
            return "YES"
        return "NO"
df["need_help"] = df["clean"].apply(detect_need)

df.to_csv("final_data.csv", index=False)

print("DONE!")
print(df.head())
