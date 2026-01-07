import asyncio
from openai import OpenAI, RateLimitError
from config import OPENAI_API_KEY
from typing import List
from schemas import SummaryRequest, SummaryResponse

client = OpenAI(api_key=OPENAI_API_KEY)

async def news_summary(requests: List[SummaryRequest]) -> List[SummaryResponse]:
    results = []

    for req in requests:
        try:
            response = client.chat.completions.create(
                model="gpt-4o-mini", 
                messages=[
                    {"role": "system", "content": "너는 뉴스 요약을 잘하는 한국어 AI야. 주어진 기사의 핵심 내용만 간결하게 요약해줘."},
                    {"role": "user", "content": f"다음 뉴스 기사를 100자 이내로 요약해줘:\n\n{req.content}"},
                ],
                max_tokens=50
            )
        
            summary_text = response.choices[0].message.content.strip()

            print(summary_text)

            results.append(SummaryResponse(
                    id=req.id,
                    summary=summary_text
                )
            )

            await asyncio.sleep(0.5)
        
        except RateLimitError as e:
            print(f"Rate limit for ID {req.id}: {e}")
            results.append(SummaryResponse(id=req.id, summary="한도 초과로 요약 실패"))
        except Exception as e:
            print(f"Error for ID {req.id}: {e}")
            results.append(SummaryResponse(id=req.id, summary="요약 생성 실패"))

    return results
