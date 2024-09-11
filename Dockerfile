FROM openjdk:17-slim

# Bash 셸 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    bash \
    && rm -rf /var/lib/apt/lists/*

# JAR 파일을 복사합니다.
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# wait-for-it.sh 스크립트를 복사하고 실행 권한을 부여합니다.
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

# 환경 변수 설정
ENV OPENAI_API_KEY=your_openai_api_key_here

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
