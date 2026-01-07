from fastapi import APIRouter
from scraper import scrap_news_list
from typing import List
from schemas import SummaryRequest, SummaryResponse
from summary import news_summary

router = APIRouter()

@router.get("/health")
def health_check():
    return {"status" : "up"}

@router.post("/articles")
async def scrap_news():
    news_data_list = await scrap_news_list()
    return {"data": news_data_list, "count": len(news_data_list)}

@router.post("/summary", response_model=List[SummaryResponse])
async def summarize_news(requests: List[SummaryRequest]):
    return await news_summary(requests)