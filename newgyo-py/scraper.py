from playwright.async_api import async_playwright
import json
import asyncio

async def scrap_news_list():
    async with async_playwright() as p:
        # 브라우저 띄우기
        browser = await p.chromium.launch(headless=False, slow_mo=20)
        page = await browser.new_page()

        url = "https://www.yna.co.kr/"
        print("뉴스 페이지 이동", url)

        # 페이지 접근
        try:
            await page.goto(url, wait_until="networkidle", timeout=30000)
        except TimeoutError:
            print("페이지 요청 시간 초과")

        for _ in range(5):
            await page.keyboard.press("PageDown")
            await asyncio.sleep(0.2)

        # 뉴스 목록 링크 가져오기
        items = page.locator("ul.list02 > li")
        news_links = []
        
        count = await items.count()
        for i in range(count):
            item = items.nth(i)

            a = item.locator("strong.tit-wrap > a.tit-news")
            if await a.count() == 0:
                continue

            title_span = a.locator("span.title01")
            if await title_span.count() == 0:
                continue

            title = await title_span.inner_text()
            link = await a.get_attribute("href")

            news_links.append({
                "title": title,
                "url": link
            })

        # 뉴스 본문 가져오기
        news_data_list = []
        for news in news_links:
            url = news["url"]

            # 페이지 접근
            try:
                await page.goto(url, timeout=10000)
                await page.wait_for_selector("div.story-news")
            except TimeoutError:
                print("페이지 본문 요청 시간 초과")

            title = news["title"]

            # 카테고리 가져오기
            category = await page.locator("ul.nav-path01 li").first.locator("a").inner_text()

            # 이미지 가져오기
            images = []
            images_elements = page.locator("div.comp-box.photo-group img")
            img_count = await images_elements.count()

            for i in range(img_count):
                img_src = await images_elements.nth(i).get_attribute("src")
                if(img_src):
                    if img_src.startswith("//"):
                        img_src = "https:" + img_src
                        images.append(img_src)

            # 기자 가져오기
            reporter = None
            reporter_elements = page.locator("#newsWriterCarousel01 .swiper-slide-active")

            if await reporter_elements.count() > 0:
                text = await reporter_elements.first.inner_text()
                reporter = text.replace("기자", "").replace("구독", "").strip()

            # 본문 가져오기
            content = await page.locator("div.story-news").inner_text()

            # 날짜 가져오기
            date = ""
            date_element = page.locator("div.update-time p.txt-time01")
            if await date_element.count() > 0:
                date_text = await date_element.inner_text()
                date = date_text.replace("송고", "").strip()

            # JSON
            news_data = {
                "title": title,
                "category": category,
                "images": images,
                "reporter": reporter,
                "content": content,
                "date": date,
                "url": url
            }

            news_data_list.append(news_data)

        print("추출된 뉴스 데이터:")
        print(json.dumps(news_data_list, ensure_ascii=False, indent=2))

        await browser.close()
        return news_data_list