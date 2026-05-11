import streamlit as st
import pandas as pd
st.set_page_config(page_title="Disaster Dáhboard", layout="wide")

st.title("🚨 Disaster Analysis Dashboard")

df = pd.read_csv("final_data.csv")
keyword = st.text_input("🔍 Search content")

if keyword:
    df = df[df["content"].str.contains(keyword, case=False)]
st.write("## 🔍 Filter Data")

sentiment_filter = st.selectbox(
    "Chọn sentiment",
    ["ALL", "POSITIVE", "NEGATIVE"]
)
if sentiment_filter != "ALL":
    df = df[df["sentiment"] == sentiment_filter]
st.write("### 📋 Data Preview")
st.dataframe(df)

st.write("### 📊 Sentiment Distribution")
import matplotlib.pyplot as plt

fig, ax = plt.subplots()
df["sentiment"].value_counts().plot(kind="bar", ax=ax)
st.pyplot(fig)
st.write("### 🆘 Need Help Count")
col1, col2 = st.columns(2)
with col1:
    st.write("### Sentiment")
    st.bar_char(df["sentiment"].value_counts())

with col2:
    st.write("### Need Help")
    st.bar_chart(df["need_help"].value_counts())