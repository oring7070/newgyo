from fastapi import APIRouter
from scraper import scrap_news_list

router = APIRouter()

@router.post("/articles")
async def scrap_news():
    news_data_list = await scrap_news_list()
    return {"data": news_data_list, "count": len(news_data_list)}