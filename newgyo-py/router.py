from fastapi import APIRouter
from scraper import scrap_news_list
from summary import news_summary

router = APIRouter()

@router.get("/health")
def health_check():
    return {"status" : "up"}

@router.post("/articles")
async def scrap_news():
    news_data_list = await scrap_news_list()
    return {"data": news_data_list, "count": len(news_data_list)}

@router.post("/summary")
def summarize_news():
    # news_summary_data = news_summary()
    return {"id": 31, "summary": "hi"}