from playwright.sync_api import sync_playwright
import time
import json
import requests

def scrap_news_list():
    with sync_playwright() as p:
        # 브라우저 띄우기
        browser = p.chromium.launch(headless=False, slow_mo=20)
        page = browser.new_page()

        url = "https://www.yna.co.kr/"
        print("뉴스 페이지 이동", url)

        # 페이지 접근
        try:
            page.goto(url, wait_until="networkidle", timeout=10000)
        except TimeoutError:
            print("페이지 요청 시간 초과")

        for _ in range(5):
            page.keyboard.press("PageDown")
            time.sleep(0.2)

        # 뉴스 목록 링크 가져오기
        items = page.locator("ul.list02 > li")
        news_links = []

        for i in range(items.count()):
            item = items.nth(i)

            a = item.locator("strong.tit-wrap > a.tit-news")
            if a.count() == 0:
                continue

            title_span = a.locator("span.title01")
            if title_span.count() == 0:
                continue

            title = title_span.inner_text()
            link = a.get_attribute("href")

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
                page.goto(url, timeout=10000)
                page.wait_for_selector("div.story-news")
            except TimeoutError:
                print("페이지 본문 요청 시간 초과")

            title = news["title"]

            # 이미지 가져오기
            images = []
            images_elements = page.locator("div.comp-box.photo-group img")
            for i in range(images_elements.count()):
                img_src = images_elements.nth(i).get_attribute("src")
                if(img_src):
                    if img_src.startswith("//"):
                        img_src = "https:" + img_src
                        images.append(img_src)

            # 기자 가져오기
            reporters = []
            reporters_elements = page.locator("div.writer-zone01 strong.tit-name a")
            for i in range(reporters_elements.count()):
                reporter = reporters_elements.nth(i).inner_text().strip()
                reporter_name = reporter.replace("기자", "").strip()
                if(reporter_name):
                    reporters.append(reporter_name)

            # 본문 가져오기
            content = page.locator("div.story-news").inner_text()

            # 날짜 가져오기
            date = ""
            date_element = page.locator("div.update-time p.txt-time01")
            if date_element.count() > 0:
                date = date_element.inner_text().strip()

            # JSON
            news_data = {
                "title": title,
                "images": images,
                "reporters": reporters,
                "content": content,
                "date": date,
                "url": url
            }

            news_data_list.append(news_data)

        print("추출된 뉴스 데이터:")
        print(json.dumps(news_data_list, ensure_ascii=False, indent=2))

if __name__ == "__main__":
    scrap_news_list()
