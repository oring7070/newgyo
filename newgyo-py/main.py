from playwright.sync_api import sync_playwright
import time

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

            # print(title, link)

            news_links.append({
                "title": title,
                "url": link
            })

        # 뉴스 본문 가져오기
        for news in news_links:
            url = news["url"]

            # 페이지 접근
            try:
                page.goto(url, timeout=10000)
            except TimeoutError:
                print("페이지 본문 요청 시간 초과")

            page.wait_for_selector("div.story-news")

            content = page.locator("div.story-news").inner_text()
            
            print(content)

if __name__ == "__main__":
    scrap_news_list()
