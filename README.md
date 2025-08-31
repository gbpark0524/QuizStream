# QuizStream v1.0 📱

간편하게 문제은행을 한손으로 풀어보고 기록하는 안드로이드 앱

## ✨ 주요 기능

- **📚 문제 파일 관리**: JSON 형식의 퀴즈 파일 지원
- **🎯 한손 조작 최적화**: 큰 터치 영역과 직관적인 UI
- **✅ 실시간 결과 확인**: 정답/오답 즉시 표시 및 해설 제공
- **🔄 단일/다중 선택 지원**: 다양한 문제 유형 대응
- **📊 진행률 표시**: 현재 문제 위치 및 전체 진행 상황

## 🎨 주요 화면

### 1. 문제 목록
- 사용 가능한 퀴즈 파일 목록 표시
- 문제 개수 및 제목 정보 제공

### 2. 문제 풀이
- 깔끔한 문제 표시
- 직관적인 선택지 버튼
- 진행률 바 및 문제 번호 표시

### 3. 결과 확인
- 정답/오답 상태 표시
- 접을 수 있는 문제/선택지 다시보기
- 상세한 해설 제공
- 하단 고정 다음 문제 버튼

## 🛠️ 기술 스택

- **언어**: Kotlin
- **UI**: Jetpack Compose
- **아키텍처**: MVVM Pattern
- **네비게이션**: Navigation Compose
- **최소 SDK**: Android 7.0 (API 24)
- **대상 SDK**: Android 14 (API 36)

## 📁 프로젝트 구조

```
app/src/main/java/kr/pe/gbpark/quizstream/
├── data/                   # 데이터 모델 및 Repository
│   ├── Models.kt
│   ├── QuizRepository.kt
│   └── SampleData.kt
├── navigation/             # 네비게이션 설정
│   └── Navigation.kt
├── ui/
│   ├── screens/           # 화면 Composable들
│   │   ├── QuizFileListScreen.kt
│   │   ├── QuizScreen.kt
│   │   └── QuizResultScreen.kt
│   └── theme/             # 앱 테마 설정
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

## 📄 퀴즈 데이터 형식

```json
{
  "title": "퀴즈 제목",
  "questions": [
    {
      "id": "Q1",
      "question": "문제 내용",
      "type": "single_choice", // 또는 "multiple_choice"
      "options": [
        {
          "label": "A",
          "text": "선택지 1"
        },
        {
          "label": "B", 
          "text": "선택지 2"
        }
      ],
      "answer": ["A"], // 정답 라벨들
      "explanation": "해설 내용 (선택사항)"
    }
  ]
}
```

## 🚀 설치 및 실행

### 개발 환경 설정
1. Android Studio 설치
2. JDK 21 설치
3. 프로젝트 클론: `git clone [repository-url]`
4. Android Studio에서 프로젝트 열기
5. Sync Project with Gradle Files

### APK 빌드
```bash
# Debug APK
./gradlew assembleDebug

# Release APK  
./gradlew assembleRelease
```

### 디바이스 설치
```bash
# USB 디버깅으로 설치
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📱 시스템 요구사항

- **최소 버전**: Android 7.0 (API 24)
- **권장 버전**: Android 10+ (API 29+)
- **저장공간**: 약 15MB
- **RAM**: 최소 2GB 권장

## 🎯 사용법

1. **퀴즈 선택**: 메인 화면에서 원하는 퀴즈 파일 선택
2. **문제 풀이**: 선택지를 터치하고 Confirm 버튼 클릭
3. **결과 확인**: 정답 여부 확인 및 해설 읽기
4. **다음 문제**: 하단 버튼으로 다음 문제로 진행

## 🔧 향후 개선 계획

- [ ] 사용자 통계 및 진행률 저장
- [ ] 즐겨찾기 문제 기능
- [ ] 오답노트 기능
- [ ] 다크 테마 지원
- [ ] 문제 검색 기능
- [ ] 온라인 문제 동기화

## 📝 라이선스

이 프로젝트는 개인 학습 목적으로 제작되었습니다.

## 👨‍💻 개발자

**gbpark** - 개인 프로젝트

---

📧 문의사항이나 버그 리포트는 Issues 탭을 이용해주세요.
