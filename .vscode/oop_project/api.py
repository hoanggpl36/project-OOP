from fastapi import FastAPI
from pydantic import BaseModel
from bert_model import predict

app = FastAPI()

class TextInput(BaseModel):
    text: str


def analyze(text):
    if "cứu" in text or "help" in text:
        return {"sentiment": "NEGATIVE", "need_help": 1}
    else:
        return {"sentiment": "POSITIVE", "need_help": 0}

@app.get("/")
def home():
    return {"message": "API is running"}

@app.post("/predict")
def predict_api(input: TextInput):
    
    return predict(input.text)