# 🧞‍♂️ Genieary_Backend 🧞
홍익대학교 졸업프로젝트 - 얼굴 분석을 통한 본인 이해 다이어리 선물 추천 서비스

## 👶🏼 Backend Members 👶🏼
<img width="160px" alt="스크린샷 2025-09-17 오전 1 49 40" src="https://github.com/user-attachments/assets/928b8400-0ff1-40d9-9383-f03c92fc1b0f" /> | <img width="160px" alt="image" src="https://github.com/user-attachments/assets/0e6bc9fe-8c18-453e-b97f-934f92efa433" />| <img width="160px" src="https://github.com/user-attachments/assets/0a2d857c-7ca2-425c-a4af-770b88527c51" />| 
|:-----:|:-----:|:-----:|
|[정원희](https://github.com/oneeee822)|[원동희](https://github.com/ddhi7)|[권아림](https://github.com/dkfla)|
| 정원희 👩🏻‍💻| 원동희 👩🏻‍💻| 권아림 👩🏻‍💻|
</div>
<br/>
<br>

## 🌟 프로젝트 소개

**Genieary_Backend**는 사용자에게 감정 상태 분석과 선물 추천 기능을 통한 다이어리 서비스를 제공합니다. 사용자 친화적인 인터페이스를 통해 일기 관리와 선물 추천 시스템을 활용하여 감정상태를 인식할 수 있도록 도와줍니다.

### **주요 기능**  

1. **회원가입 및 로그인**  
   - 이메일 기반 회원가입 (이메일 중복 확인 포함)  
   - Access Token 기반 인증 (JWT 사용)  
   - 로그인 및 로그아웃 기능 제공  
   - 비밀번호 변경 및 계정 탈퇴 지원  
   - 액세스 토큰 만료 시 Refresh Token을 이용한 자동 갱신
   - **성격 및 취미 저장** → 개인화 선물 추천 데이터로 활용 

2. **감정 분석 시스템**  
   - 사용자의 **얼굴 사진**을 촬영하면 7가지 감정 분석
   - 당일 일기를 고려하여 현재 유저의 **감정 상태 요약**  


3. **맞춤 선물 추천**  
   - **사용자 데이터 분석 기반 선물 추천 메뉴 4개 제공**  
   - **기념일 반영 추천 기능** → 기념일기반 선물 추천 기능 추가
   - 선호도 조사를 통한 맞춤형 서비스 강화

4. **일기 작성 및 분석**  
   - **일기 작성 기록을 날짜순으로 관리** 기능 제공  
   - **일기 공유 기능** 
   - **기념일 및 일정 저장** → 개인화 추천 데이터로 활용  
   - 특정 기록 삭제 기능 지원  
   - **사용자의 일기를 통한 생활 패턴 분석 기능 추가** 

5. **보안 및 HTTPS 적용**  
   - **모든 API 요청을 HTTPS를 통해 암호화하여 전송** (보안 강화)  
   - **AWS Route 53 및 SSL 인증서(ACM) 적용**을 통한 HTTPS 지원  
   - **로그인 및 비밀번호 변경 시 데이터 암호화 적용**  
   - **클라이언트-서버 간 데이터 암호화**를 통해 안전한 정보 전송  

6. **CI/CD 및 배포 자동화**  
   - **Jenkins를 활용한 CI/CD 파이프라인 구축**  
   - **테스트 자동화 및 빌드, 배포 자동화 적용**  
   - **AWS EC2 및 DockerCompose를 활용한 무중단 배포**  
   - **Docker & Nginx를 활용한 컨테이너 기반 배포 환경 구성**  
   - **배포 단계에서 보안 강화를 위한 환경 변수 관리 및 접근 제한 적용**  
<br>
<br>

## 🛠️ 기술 스택

- **Backend**: Spring Boot(Java)
- **Database**: RDS(MySQL), Redis
- **Cloud**: AWS (EC2, VPC)
- **API Documentation**: Swagger, Notion
- **Version Control**: GitHub

<br>
<br>

## 🖥️ 프로젝트 구조

### ERD
<img width="1880" height="1082" alt="image" src="https://github.com/user-attachments/assets/66bc9026-a5fe-4469-af07-557f03d0ed74" />



### 인프라 구성도

<br>

## 🌟 프로젝트 배경 및 아이디어

### 프로젝트 기본 아이디어

1. **문제**: 많은 현대인들은 자신의 감정과 상태를 제대로 인지하지 못한 채 무기력하게 생활
2. **목표**: 얼굴과 일기를 기반으로 사용자의 감정을 분석하여, 개인에게 맞는 해결책과 맞춤형 선물을 제안
3. **해결책**: 얼굴 사진을 통한 감정 인식과 일기 분석을 결합하여, 사용자가 자신의 상태를 이해하고 적절한 대응을 할 수 있도록 도움
<br>
<br>

## 🤖 자체 AI모델 구축
- 얼굴이미지를 분석하여 angry, disgust, fear, happy, neutral, sad, surprise 감정 분류를 수행하며, 각 클래스의 데이터 비율과 약 50% 정확도를 가진 감정 인식 모델 구축
- 사용한 데이터셋 : [FER-2013](https://www.kaggle.com/datasets/msambare/fer2013)

<br>
<br>

## 🌐 API 명세서
[Swagger](https://genieary.site/swagger-ui/index.html)

