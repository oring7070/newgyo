# 1. Playwright 공식 이미지 (Python 및 브라우저 실행 환경 포함)
FROM python:3.11-slim-bookworm

# 1. uv 바이너리만 쏙 가져오기 (가장 빠르고 가벼움)
COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /usr/local/bin/

WORKDIR /app

# 3. 브라우저 실행에 필요한 최소한의 시스템 라이브러리 설치
# (slim 이미지에는 라이브러리가 없어서 이 과정이 필요합니다)
RUN apt-get update && apt-get install -y --no-install-recommends \
    libglib2.0-0 \
    libnss3 \
    libnspr4 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libdbus-1-3 \
    libxcb1 \
    libxkbcommon0 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    libgbm1 \
    libpango-1.0-0 \
    libcairo2 \
    libasound2 \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# 설정 파일 먼저 복사 (캐시 활용)
COPY pyproject.toml uv.lock ./

# 2. uv로 광속 설치
RUN uv pip install --system --no-cache -r pyproject.toml

# 5. [핵심] Chromium만 설치 (다른 브라우저 제외)
RUN playwright install --with-deps chromium

COPY . .

# FastAPI가 사용할 8000번 포트 열기 (문서화 용도)
EXPOSE 8000

# uvicorn을 사용하여 서버 실행
# --host 0.0.0.0: 모든 네트워크 인터페이스에서 접속 허용 (컨테이너 외부 접속 필수 설정)
# main:app: main.py 파일의 app 객체를 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]