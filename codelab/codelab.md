id: koog-study-buddy-agent
summary: Koog 프레임워크를 활용한 대학생 과제 도우미 AI 에이전트 개발
authors: GDG Android Korea
categories: AI, Kotlin
environments: Web
status: Published
feedback_link: https://github.com/l2hyunwoo/2026-Build-With-AI-GDG-On-Campus-Koog/issues
analytics_account:

# 과제 끝내기 에이전트 — Kotlin + Koog로 나만의 학습 도우미 만들기

## 소개
Duration: 2:00

> "매주 5분 투자한 게 시험 전날 치트시트가 되어 돌아온다"

이 코드랩에서는 JetBrains의 **Koog** 프레임워크를 사용해 실제로 동작하는 AI 학습 도우미 에이전트를 단계적으로 만들어봅니다.

강의자료를 던져놓으면 복습 노트가 자동으로 만들어지고, 과제 때는 축적된 노트를 참고해서 풀이 가이드를 생성하고, 시험 때는 한 학기 노트를 종합해서 치트시트와 예상문제를 자동으로 만들어주는 에이전트입니다.

```
매주: 강의자료 → 에이전트가 복습 노트 생성 → notes/에 축적
        ↓
과제 때: notes/ 참고해서 C++ 과제 풀이 가이드 생성
        ↓
시험 때: notes/ 전체를 종합 → 치트시트 + 예상문제 + 개념맵 자동 생성
```

### 무엇을 배우나요?

- **Koog AIAgent**: `AIAgent`, `simpleGoogleAIExecutor`, `systemPrompt`의 구성 방법
- **Custom Tool 개발**: `@Tool`, `@LLMDescription` 어노테이션으로 에이전트가 사용할 함수 만들기
- **ToolRegistry**: 여러 Tool을 에이전트에 연결하는 방법
- **ChatMemory Feature**: 대화 맥락을 유지하는 메모리 시스템 구현
- **Multi-Agent Orchestration**: 여러 전문 에이전트를 조합해서 복잡한 작업 분업
- **CLI 앱 배포**: Banner, Command 패턴, Gradle distribution으로 배포 가능한 앱 만들기

### 에이전트란?

AI 에이전트는 단순히 질문에 답하는 챗봇이 아닙니다. **스스로 판단하고, 도구를 사용하고, 목표를 달성하는 프로그램**입니다.

```
챗봇:       사용자 → 질문 → LLM → 답변 (끝)
에이전트:   사용자 → 목표 → LLM → 도구 호출 → 결과 확인 → 다시 판단 → ... → 목표 달성
```

이 반복 루프를 **ReAct 패턴** (Reasoning + Acting)이라고 합니다. 에이전트는 "생각하고(Reasoning) → 행동하고(Acting) → 결과를 관찰하고(Observing)" 를 반복합니다.

### 사전 준비

시작 전에 다음 항목을 준비하세요.

- **IntelliJ IDEA 2026.1.1** Community Edition(무료) 또는 Ultimate 권장, 또는 Android Studio
- **JDK 21 이상**
- **Google AI Studio API 키** ([aistudio.google.com](https://aistudio.google.com) 에서 무료 발급)
- **Kotlin 기초 문법** 이해 (기본 문법을 알고 있으면 충분)

### 기술 스택

- Kotlin 2.3.21
- Koog 0.8.0
- Google Gemini API
- Gradle

## 환경 설정
Duration: 7:00

### 1. JDK 21 설치 확인

터미널에서 확인합니다.

```bash
java -version
# openjdk version "21.x.x" 이상이면 OK
```

Negative
: JDK 버전이 21 미만이면 [Adoptium](https://adoptium.net/)에서 JDK 21을 다운로드하세요. Koog 프레임워크는 JDK 21의 기능을 사용합니다.

### 2. Google Gemini API Key 발급

1. [Google AI Studio](https://aistudio.google.com)에 접속
2. "Get API Key" 클릭 → "Create API key" 클릭
3. 발급된 키를 복사해두기

Positive
: Gemini API는 무료 티어가 있어서 이 코드랩을 완료하는 데 비용이 들지 않습니다.

### 3. 환경변수 설정

Mac/Linux:

```bash
export GOOGLE_API_KEY=여기에_발급받은_키_붙여넣기
```

Windows (PowerShell):

```powershell
$env:GOOGLE_API_KEY="여기에_발급받은_키_붙여넣기"
```

Positive
: IntelliJ에서 실행할 때는 Run > Edit Configurations > Environment variables에서 설정해도 됩니다. 터미널에서 설정한 환경변수는 IntelliJ에서 인식하지 못할 수 있으므로, IntelliJ 내에서 직접 설정하는 것을 권장합니다.

### 4. 프로젝트 클론

```bash
git clone https://github.com/l2hyunwoo/2026-Build-With-AI-GDG-On-Campus-Koog.git
cd 2026-Build-With-AI-GDG-On-Campus-Koog
```

기본 브랜치(`initial`)에는 빌드 설정과 데이터 파일만 들어있는 빈 프로젝트입니다. 코드랩을 따라가며 직접 코드를 작성하세요.

#### 브랜치 구조

각 브랜치는 해당 단계까지의 **완성된 코드**를 포함합니다. 막히면 해당 브랜치로 전환해서 정답을 확인할 수 있습니다.

| 브랜치 | 내용 |
|--------|------|
| `initial` (기본) | 프로젝트 스켈레톤 — 빌드 설정 + 데이터 파일 |
| `page3` | 첫 에이전트 + 역할 부여 |
| `page4` | 강의자료 읽기 + 복습 노트 생성 |
| `page5` | 과제 분석 + 노트 활용 |
| `page6` | ChatMemory 대화형 과제 도움 |
| `page7` | 시험 대비 자료 자동 생성 |
| `page8` | Multi-Agent 학습 전문가 팀 |
| `page9` | CLI 디자인 + 배포 설정 |
| `complete` | 최종 완성본 |

```bash
# 예: 5단계에서 막혔을 때 정답 확인
git checkout page5

# 다시 처음부터 직접 작성하고 싶을 때
git checkout initial
```

### 5. 프로젝트 열기

프로젝트 폴더를 IntelliJ로 열고 Gradle sync가 완료될 때까지 기다립니다. 처음에는 라이브러리 다운로드로 시간이 걸릴 수 있습니다.

### 6. 프로젝트 구조

```
2026-Build-With-AI-GDG-On-Campus-Koog/
├── build.gradle.kts                    # Gradle 빌드 설정
├── src/main/kotlin/dev/community/gdg/campus/korea/koog/
│   ├── Main.kt                         # 진입점 (여기에 코드를 작성합니다)
│   └── tools/                          # 에이전트가 사용할 도구들
│       ├── ReadFileTool.kt             # 파일 읽기 도구
│       ├── SaveNoteTool.kt             # 노트 저장 도구
│       ├── ListFilesTool.kt            # 파일 목록 도구
│       └── GenerateExamPrepTool.kt     # 시험 대비 자료 생성 도구
├── lecture-notes/                      # 강의 자료 (예제 데이터)
│   ├── week07-bst-basics.md            # BST 강의 노트
│   └── week08-avl-tree.md              # AVL Tree 강의 노트
├── assignments/                        # 과제 요구사항
│   └── hw-avl-tree.md                  # AVL Tree 과제
├── student-code/                       # 학생 코드 (버그 포함)
│   └── avl_tree.cpp                    # 버그가 있는 AVL Tree 구현
├── notes/                              # 에이전트가 자동 생성하는 복습 노트
└── exam-prep/                          # 에이전트가 생성하는 시험 대비 자료
```

`initial` 브랜치에서는 `Main.kt`에 빈 스켈레톤만 있고, `tools/` 폴더 안의 파일들은 아직 없습니다. 코드랩을 진행하면서 하나씩 만들어갑니다.

### 트러블슈팅

| 오류 | 해결법 |
|------|--------|
| Gradle sync 실패 | JDK 21 설치 확인, File > Project Structure > SDK |
| `GOOGLE_API_KEY` 미인식 | IntelliJ 재시작, 또는 Run Configuration에서 직접 설정 |
| 의존성 다운로드 실패 | 네트워크 확인, `./gradlew --refresh-dependencies` 실행 |

`./gradlew build` 명령이 성공하면 이 단계 완료입니다.

Positive
: 막히면 `git checkout page3`으로 다음 단계의 완성 코드를 확인할 수 있습니다.

## 첫 에이전트 + 역할 부여
Duration: 7:00

### 이 단계에서 배우는 것

LLM에게 **역할**을 부여하면 답변의 품질이 완전히 달라집니다. 같은 모델이라도 "너는 교수야"와 "너는 조교야"로 지시하면 전혀 다른 스타일의 답변이 나옵니다. 이것이 System Prompt의 핵심입니다.

### Koog 핵심 컴포넌트

| 클래스/함수 | 역할 | 비유 |
|---|---|---|
| `AIAgent` | Koog의 핵심 클래스. LLM 모델과 System Prompt, Tool을 묶어서 하나의 에이전트를 만든다 | 직원 한 명을 고용하는 것 |
| `simpleGoogleAIExecutor` | Google Gemini API와 연결하는 실행기 | API 클라이언트 (Retrofit과 비슷한 역할) |
| `systemPrompt` | 에이전트의 역할과 행동 규칙을 정의하는 텍스트 | 직원에게 주는 업무 매뉴얼 |
| `GoogleModels` | 사용할 Gemini 모델을 지정한다 | 어떤 등급의 직원을 고용할지 |

### 왜 System Prompt가 중요한가?

System Prompt는 에이전트의 **성격, 전문성, 행동 규칙**을 정의합니다. 에이전트가 매 요청마다 "나는 누구이고, 어떻게 행동해야 하는가"를 확인하는 기준점입니다.

```
System Prompt 없이:  "AVL Tree 설명해줘" → 길고 일반적인 위키피디아식 답변
System Prompt 있으면: "AVL Tree 설명해줘" → "한 줄 요약: ..." 으로 시작하는 간결한 조교식 답변
```

### Main.kt 구현

`Main.kt`를 열고 아래 코드를 작성합니다.

```kotlin
package dev.community.gdg.campus.korea.koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.clients.google.GoogleModels
import kotlinx.coroutines.runBlocking
```

먼저 필요한 클래스들을 임포트합니다. `AIAgent`가 에이전트의 핵심이고, `simpleGoogleAIExecutor`가 Gemini API 연결을 담당합니다. `runBlocking`은 코루틴을 메인 함수에서 실행하기 위한 것입니다.

```kotlin
fun main() = runBlocking {
    val apiKey = System.getenv("GOOGLE_API_KEY")
        ?: error("GOOGLE_API_KEY 환경변수를 설정해주세요!")

    val agent = AIAgent(
        promptExecutor = simpleGoogleAIExecutor(apiKey),
        systemPrompt = """
            너는 컴퓨터공학과 학생의 과제와 시험 준비를 도와주는 조교야.

            규칙:
            - 핵심만 간결하게 설명해. 대학생은 바쁘니까.
            - 과제에 바로 쓸 수 있는 실용적인 답변을 해.
            - 코드 관련 질문에는 C++로 예시를 들어줘.
            - 개념 설명은 "한 줄 요약 → 상세 설명" 순서로 해.
        """.trimIndent(),
        llmModel = GoogleModels.Gemini2_5Flash
    )

    val response = agent.run("AVL Tree가 뭐야? BST랑 뭐가 달라?")
    println(response)
}
```

코드를 하나씩 살펴봅시다:

- **`simpleGoogleAIExecutor(apiKey)`**: API Key로 Gemini에 연결합니다. Retrofit으로 API 클라이언트를 만드는 것과 비슷합니다.
- **`systemPrompt`**: 이 에이전트의 페르소나를 정의합니다. "조교"라는 역할과 구체적인 행동 규칙 4가지를 지정했습니다.
- **`GoogleModels.Gemini2_5Flash`**: 빠르고 효율적인 Gemini 2.5 Flash 모델을 사용합니다.
- **`agent.run("...")`**: 에이전트에게 질문을 던지고 답변을 받습니다.

### 실행 확인

IntelliJ에서 `Main.kt`의 ▶ 버튼을 클릭하거나, 터미널에서 `./gradlew run`을 실행합니다.

Positive
: `systemPrompt`를 "교수님처럼 설명해줘"로 바꾸면 어떻게 달라지는지 직접 바꿔보세요. 같은 질문이라도 System Prompt에 따라 "교수님 스타일" vs "친구 스타일" 답변이 나옵니다. 에이전트를 만드는 핵심은 **역할 정의**입니다. 역할이 명확할수록 답변 품질이 올라갑니다.

### 트러블슈팅

| 오류 | 해결법 |
|------|--------|
| `error("GOOGLE_API_KEY ...")` 발생 | 환경변수 설정 재확인, IntelliJ Run Config에서 직접 설정 |
| 401/403 API 오류 | Google AI Studio에서 Key 재발급 |
| 응답이 영어로 나옴 | System Prompt에 "항상 한국어로 답변해" 추가 |

에이전트가 AVL Tree와 BST의 차이를 한국어로 설명하면 이 단계 완료입니다.

Positive
: 막히면 `git checkout page3`으로 완성 코드를 확인하세요.

## 강의자료 읽기 + 복습 노트 생성
Duration: 10:00

### 이 단계에서 배우는 것

지금까지 만든 에이전트는 질문에 답만 할 수 있습니다. **파일을 읽거나 저장하는 것은 못합니다.** LLM은 텍스트만 다루기 때문입니다. 에이전트가 실제 파일 시스템과 상호작용하려면 **Tool(도구)**을 만들어서 연결해야 합니다.

```
Tool 없이:   "강의자료 정리해줘" → "네, 어떤 내용인가요?" (파일을 못 읽음)
Tool 있으면: "강의자료 정리해줘" → 파일 읽기 → 내용 분석 → 노트 저장 (자동)
```

### 왜 Tool이 필요한가?

LLM은 텍스트를 입력받아 텍스트를 출력하는 모델입니다. 파일 시스템에 접근하거나, 데이터베이스를 조회하거나, API를 호출하는 것은 LLM 자체로는 불가능합니다. 그래서 **개발자가 함수(Tool)를 만들어서 에이전트에게 제공**하면, 에이전트가 필요할 때 스스로 호출합니다.

이것이 바로 **Function Calling** 패턴입니다:

```
1. 에이전트가 "파일을 읽어야겠다"고 판단 (Reasoning)
2. readFile 도구를 호출 (Acting)
3. 파일 내용을 받아서 분석 (Observing)
4. 결과를 정리해서 saveNote 도구로 저장 (Acting)
```

에이전트는 **어떤 도구를 언제 쓸지 스스로 판단**합니다. 개발자는 도구만 만들어주면 됩니다.

### Koog 핵심 컴포넌트

| 어노테이션/클래스 | 역할 |
|---|---|
| `@Tool` | 함수를 에이전트가 사용할 수 있는 도구로 선언하는 어노테이션 |
| `@LLMDescription` | 에이전트(LLM)에게 이 도구와 파라미터가 무엇인지 설명하는 어노테이션. **설명이 부정확하면 도구를 제대로 못 쓴다** |
| `ToolRegistry` | 에이전트가 사용할 Tool들을 모아두는 저장소 |

### 예제 시나리오

1. 에이전트가 `readFile` 도구로 강의 노트(`week07-bst-basics.md`)를 읽는다
2. 핵심 내용을 정리해서 `saveNote` 도구로 `notes/` 폴더에 저장한다
3. 학생은 "Week 7 강의자료 복습 정리해줘" 한 마디만 하면 된다

### ReadFileTool.kt 구현

`tools/ReadFileTool.kt` 파일을 새로 만듭니다.

```kotlin
package dev.community.gdg.campus.korea.koog.tools

import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

@Tool
@LLMDescription("강의 노트, 과제, 소스 코드 등 파일을 읽어서 내용을 반환합니다")
fun readFile(
    @LLMDescription("읽을 파일 경로 (예: lecture-notes/week07-bst-basics.md)")
    filePath: String
): String {
    val path = Path(filePath)
    if (!path.exists()) return "파일을 찾을 수 없습니다: $filePath"
    return path.readText()
}
```

핵심 포인트:

- **`@Tool`**: 이 함수를 에이전트가 호출할 수 있는 도구로 등록합니다.
- **`@LLMDescription`**: LLM에게 "이 도구가 뭘 하는지" 설명합니다. 이 설명이 정확할수록 에이전트가 도구를 올바르게 사용합니다.
- **에러 메시지를 문자열로 반환**: 예외를 던지지 않습니다. LLM은 예외를 이해 못하지만, "파일을 찾을 수 없습니다"라는 텍스트는 이해하고 다른 경로를 시도할 수 있습니다.

### SaveNoteTool.kt 구현

`tools/SaveNoteTool.kt` 파일을 새로 만듭니다.

```kotlin
package dev.community.gdg.campus.korea.koog.tools

import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlin.io.path.Path
import kotlin.io.path.writeText
import kotlin.io.path.createDirectories

@Tool
@LLMDescription("복습 노트나 정리 내용을 파일로 저장합니다")
fun saveNote(
    @LLMDescription("저장할 파일명 (예: week07-bst-summary.md)")
    fileName: String,
    @LLMDescription("저장할 내용")
    content: String
): String {
    val path = Path("notes/$fileName")
    path.parent?.createDirectories()
    path.writeText(content)
    return "노트가 notes/$fileName 에 저장되었습니다!"
}
```

`@LLMDescription`의 파라미터 설명에 **예시를 포함**한 것에 주목하세요. "저장할 파일명 (예: week07-bst-summary.md)"처럼 예시가 있으면 에이전트가 적절한 파일명을 생성하는 데 도움이 됩니다.

### ToolRegistry 연결

`Main.kt`를 수정해서 Tool들을 에이전트에 연결합니다. `studyBuddyPrompt`를 별도 변수로 분리하고, ToolRegistry를 추가합니다.

```kotlin
import dev.community.gdg.campus.korea.koog.tools.readFile
import dev.community.gdg.campus.korea.koog.tools.saveNote
import ai.koog.agents.core.tools.ToolRegistry

val studyBuddyPrompt = """
    너는 컴퓨터공학과 학생의 과제와 시험 준비를 도와주는 조교야.

    규칙:
    - 핵심만 간결하게 설명해. 대학생은 바쁘니까.
    - 과제에 바로 쓸 수 있는 실용적인 답변을 해.
    - 코드 관련 질문에는 C++로 예시를 들어줘.
    - 개념 설명은 "한 줄 요약 → 상세 설명" 순서로 해.
    - 반드시 도구를 사용해서 파일을 읽고 저장해.
    - 항상 한국어로 답변해.
""".trimIndent()

val toolRegistry = ToolRegistry {
    tool(::readFile)
    tool(::saveNote)
}

val agent = AIAgent(
    promptExecutor = simpleGoogleAIExecutor(apiKey),
    systemPrompt = studyBuddyPrompt,
    llmModel = GoogleModels.Gemini2_5Flash,
    toolRegistry = toolRegistry
)

val response = agent.run(
    "lecture-notes/week07-bst-basics.md 강의자료를 읽고, 핵심만 정리해서 복습 노트로 저장해줘"
)
println(response)
```

`ToolRegistry`에 `tool(::readFile)` 형태로 등록하면, 에이전트가 필요할 때 이 함수를 호출할 수 있게 됩니다. Kotlin의 함수 레퍼런스(`::`)를 사용합니다.

### 실행 확인

실행하면 에이전트가 다음 순서로 동작합니다:

```
1. "강의자료를 읽어야겠다" → readFile("lecture-notes/week07-bst-basics.md") 호출
2. 파일 내용을 분석하고 핵심을 추출
3. "정리한 내용을 저장해야겠다" → saveNote("week07-bst-summary.md", "...") 호출
4. 사용자에게 완료 보고
```

이 순서를 개발자가 직접 프로그래밍하지 않았습니다. **에이전트가 스스로 판단**한 것입니다. 이것이 바로 **ReAct 패턴** (Reasoning + Acting)입니다.

Positive
: `@LLMDescription`이 에이전트가 Tool을 이해하는 핵심입니다. 설명이 모호하면 도구를 엉뚱하게 쓰거나 아예 안 씁니다. "파일을 읽습니다"보다 "강의 노트, 과제, 소스 코드 등 파일을 읽어서 내용을 반환합니다"가 훨씬 효과적입니다.

### 트러블슈팅

| 오류 | 해결법 |
|------|--------|
| "파일을 찾을 수 없습니다" | Working directory 확인. IntelliJ Run Config에서 Working directory를 프로젝트 루트로 설정 |
| Tool이 호출되지 않음 | System Prompt에 "반드시 도구를 사용해서 작업해" 지시 추가 |
| 노트 내용이 너무 짧음 | System Prompt에 "키워드, 공식, 예시를 포함해서 정리해" 추가 |

`notes/` 폴더에 복습 노트 파일이 자동으로 생성되면 이 단계 완료입니다.

Positive
: 막히면 `git checkout page4`로 완성 코드를 확인하세요.

## 과제 분석 + 노트 활용
Duration: 10:00

### 이 단계에서 배우는 것

이전 단계에서 에이전트에게 도구 2개(읽기, 저장)를 줬습니다. 이번에는 도구를 **3개**로 늘려서 에이전트가 **여러 도구를 자율적으로 조합**하는 것을 확인합니다. 에이전트에게 도구를 많이 줄수록 할 수 있는 일이 늘어납니다.

### Koog 핵심 컴포넌트

- **Multi Tool 조합**: 에이전트가 여러 도구를 자율적으로 선택하고 순서를 결정해서 사용한다
- **Tool 간 연계 동작**: 한 Tool의 결과가 다음 Tool의 입력으로 이어지는 파이프라인

### 에이전트의 동작 흐름

```
"과제 풀이 가이드 만들어줘"
    ↓
① listFiles("notes/") → 기존 복습 노트 목록 확인
    ↓
② readFile("assignments/hw-avl-tree.md") → 과제 요구사항 파악
    ↓
③ readFile("notes/week07-bst-summary.md") → 기존 복습 노트 참고
    ↓
④ readFile("student-code/avl_tree.cpp") → 학생 코드 분석
    ↓
⑤ saveNote("hw-avl-guide.md", "...") → 풀이 가이드 저장
```

이 5단계를 **에이전트가 스스로 결정**합니다. 개발자는 도구만 제공했을 뿐입니다.

### ListFilesTool.kt 구현

`tools/ListFilesTool.kt` 파일을 새로 만듭니다.

```kotlin
package dev.community.gdg.campus.korea.koog.tools

import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

@Tool
@LLMDescription("디렉토리 내 파일 목록을 조회합니다")
fun listFiles(
    @LLMDescription("조회할 디렉토리 경로 (예: notes/)")
    directory: String
): String {
    val dir = Path(directory)
    if (!dir.exists() || !dir.isDirectory()) return "디렉토리를 찾을 수 없습니다: $directory"
    return dir.listDirectoryEntries()
        .joinToString("\n") { "- ${it.name}" }
        .ifEmpty { "(빈 디렉토리)" }
}
```

왜 `listFiles`가 필요할까요? 에이전트는 파일 시스템의 구조를 모릅니다. `notes/` 폴더에 어떤 파일이 있는지 알려면 먼저 목록을 확인해야 합니다. 이 도구 없이는 "기존 노트를 참고해줘"라는 지시를 수행할 수 없습니다.

### ToolRegistry 업데이트

`Main.kt`에서 ToolRegistry에 `listFiles`를 추가합니다.

```kotlin
import dev.community.gdg.campus.korea.koog.tools.listFiles

val toolRegistry = ToolRegistry {
    tool(::readFile)
    tool(::saveNote)
    tool(::listFiles)
}
```

### 에이전트 실행

```kotlin
val response = agent.run("""
    1. assignments/hw-avl-tree.md 과제 요구사항을 읽어줘
    2. notes/ 폴더에 있는 기존 복습 노트도 참고해줘
    3. student-code/avl_tree.cpp 학생 코드를 분석해줘
    4. 과제 풀이 가이드를 만들어서 notes/hw-avl-guide.md로 저장해줘
""")
```

### 학생 코드 버그 확인

에이전트가 `student-code/avl_tree.cpp`를 분석할 때 버그 2개를 발견하는지 확인해보세요.

- **`getBalance()`**: `right - left`로 계산 (정확한 것은 `left - right`)
- **`rightRotate()`**: 회전 후 높이 업데이트 누락

Positive
: 에이전트가 `listFiles` → `readFile`(여러 번) → `saveNote` 순서를 **스스로 결정**합니다. 순서를 직접 프로그래밍하지 않았습니다. 이전 단계에서 쌓아둔 BST 복습 노트를 참고해서 AVL 과제를 분석합니다. **지식이 축적될수록 에이전트가 더 잘합니다**.

### 트러블슈팅

| 오류 | 해결법 |
|------|--------|
| 에이전트가 Tool을 순서대로 안 씀 | 정상입니다. 에이전트가 최적이라고 판단한 순서로 실행합니다 |
| 코드 분석이 부정확 | System Prompt에 "문법 오류보다 로직 오류 위주로 분석해" 지시 추가 |
| 기존 노트를 참고 안 함 | System Prompt에 "작업 전에 항상 notes/ 폴더를 먼저 확인해" 추가 |

에이전트가 과제 요구사항, 기존 노트, 학생 코드를 모두 읽고 풀이 가이드를 생성하면 이 단계 완료입니다.

Positive
: 막히면 `git checkout page5`로 완성 코드를 확인하세요.

## ChatMemory로 대화형 과제 도움
Duration: 8:00

### 이 단계에서 배우는 것

지금까지 에이전트는 `agent.run()` 한 번 호출하면 끝이었습니다. 하지만 실제 과제 도움은 **여러 번의 대화**가 필요합니다. "LL 회전이 뭐야?" → "그러면 LR은?" → "내 코드에서 그 부분 봐줘" 같은 연속 대화에서 이전 맥락을 기억해야 합니다.

### Koog 핵심 컴포넌트

| 클래스/함수 | 역할 |
|---|---|
| `ChatMemory` | 에이전트가 이전 대화를 기억하게 해주는 Feature |
| `InMemoryChatHistoryProvider` | 메모리에 대화 히스토리를 저장하는 구현체 |
| `windowSize(20)` | 최근 20개 메시지까지만 유지. 오래된 것은 자동으로 잊는다 (토큰 절약) |

### ChatMemory가 필요한 이유

`agent.run()`은 기본적으로 매 호출마다 **독립적인 대화**로 취급합니다. 이전 대화를 기억하려면 `ChatMemory` Feature를 설치해야 합니다.

```
ChatMemory 없을 때:
학생 > AVL Tree에서 LL 회전이 뭐야?
에이전트 > (LL 회전 설명)
학생 > 그러면 LR 회전은 그거랑 뭐가 달라?
에이전트 > ??? (이전 대화를 모름 — "그거"가 뭔지 모른다)

ChatMemory 있을 때:
학생 > AVL Tree에서 LL 회전이 뭐야?
에이전트 > (LL 회전 설명)
학생 > 그러면 LR 회전은 그거랑 뭐가 달라?
에이전트 > LL과 비교하면, LR은 ... (이전 맥락을 기억)
```

### 왜 windowSize가 필요한가?

LLM에는 **토큰 제한**이 있습니다. 대화가 길어질수록 이전 메시지가 쌓이면서 토큰을 소비합니다. `windowSize(20)`으로 설정하면 최근 20개 메시지만 유지하고 오래된 것은 자동으로 제거합니다. 실제 서비스에서도 이런 **슬라이딩 윈도우** 전략을 사용합니다.

### ChatMemory 설치

```kotlin
import ai.koog.agents.chatMemory.feature.ChatMemory
import ai.koog.agents.chatMemory.feature.InMemoryChatHistoryProvider

val agent = AIAgent(
    promptExecutor = simpleGoogleAIExecutor(apiKey),
    systemPrompt = studyBuddyPrompt,
    llmModel = GoogleModels.Gemini2_5Flash,
    toolRegistry = toolRegistry
) {
    // ChatMemory 설치: 이전 대화를 기억하게 만듦
    install(ChatMemory) {
        chatHistoryProvider = InMemoryChatHistoryProvider()
        windowSize(20) // 최근 20개 메시지까지 기억
    }
}
```

`install(ChatMemory)`는 AIAgent의 **후행 람다** 안에 작성합니다. Koog에서는 이 람다를 통해 에이전트에 Feature를 설치합니다.

### 대화형 과제 도움 세션 구현

이제 `main()` 함수를 대화형 루프로 변경합니다. `runStudySession` 함수를 만듭니다.

```kotlin
// 대화형 과제 도움 세션
suspend fun runStudySession(apiKey: String) {
    val toolRegistry = ToolRegistry {
        tool(::readFile)
        tool(::saveNote)
        tool(::listFiles)
    }

    val agent = AIAgent(
        promptExecutor = simpleGoogleAIExecutor(apiKey),
        systemPrompt = studyBuddyPrompt,
        llmModel = GoogleModels.Gemini2_5Flash,
        toolRegistry = toolRegistry
    ) {
        install(ChatMemory) {
            chatHistoryProvider = InMemoryChatHistoryProvider()
            windowSize(20)
        }
    }

    println("=== 과제 도우미 시작 ===")
    println("질문을 입력하세요 (종료: exit)\n")

    while (true) {
        print("학생 > ")
        val input = readLine() ?: break
        if (input == "exit") break

        val response = agent.run(input)
        println("\n조교 > $response\n")
    }
}
```

`main()` 함수에서 `runStudySession(apiKey)`을 호출하도록 변경합니다.

### 직접 해보기

프로그램을 실행하고 이 순서로 질문해봅니다.

```
학생 > AVL Tree에서 LL 회전이 뭐야?
학생 > 그러면 LR 회전은 그거랑 뭐가 달라?
학생 > 내 코드에서 회전 부분 다시 봐줘
```

마지막 질문에서 "내 코드"와 "회전"을 이전 맥락으로 이해하는지 확인해보세요.

Positive
: `install(ChatMemory)` 한 줄이 "맥락 없는 봇"과 "대화가 되는 에이전트"의 차이입니다. `windowSize(20)`는 토큰 제한 내에서 최근 대화만 유지하는 현실적인 전략입니다. 실제 서비스에서도 이렇게 합니다.

Negative
: `install(...)` 블록이 `AIAgent(...)` 람다 **안에** 있어야 합니다. 밖에 있으면 동작하지 않습니다. Koog의 Feature 시스템은 에이전트 생성 시점에 설치되어야 합니다.

### 트러블슈팅

| 오류 | 해결법 |
|------|--------|
| 이전 대화를 기억 못함 | `install(ChatMemory)` 블록이 AIAgent 람다 안에 있는지 확인 |
| 답변이 느려짐 | `windowSize`를 10으로 줄여서 컨텍스트 크기 조절 |
| `readLine()`이 동작 안 함 | IntelliJ Run Config에서 "Emulate terminal in output console" 체크, 또는 터미널에서 `./gradlew run` 실행 |

"아까 설명한 LL 회전"처럼 이전 맥락 참조가 동작하면 이 단계 완료입니다.

Positive
: 막히면 `git checkout page6`으로 완성 코드를 확인하세요.

## 시험 대비 자료 자동 생성
Duration: 10:00

### 이 단계에서 배우는 것

이전 단계들에서 복습 노트와 과제 분석 결과가 `notes/`에 쌓였습니다. 이번에는 이 **축적된 지식을 종합**해서 시험 대비 자료 3종(치트시트, 예상문제, 개념맵)을 자동 생성합니다. 노트가 많을수록 생성되는 자료의 품질이 높아지는 **지식 복리 효과**를 체험합니다.

### Koog 핵심 컴포넌트

- **Tool 확장**: 기존 ToolRegistry에 새 Tool을 추가하는 패턴
- **여러 파일 종합 처리**: 에이전트가 여러 노트를 읽고 하나의 결과물로 합치는 패턴

### 핵심 스토리

```
notes/week07-bst-summary.md  ─┐
notes/hw-avl-guide.md        ─┼→ 에이전트가 종합 → exam-prep/cheatsheet.md
notes/week08-avl-summary.md  ─┘                  → exam-prep/practice-exam.md
                                                  → exam-prep/concept-map.md
```

이전 단계에서 생성한 복습 노트들과 과제 분석 결과가 이미 `notes/`에 쌓여 있습니다. 이 축적된 지식을 종합해서 **3가지 시험 대비 자료**를 자동으로 만듭니다.

### GenerateExamPrepTool.kt 구현

`tools/GenerateExamPrepTool.kt` 파일을 새로 만듭니다.

```kotlin
package dev.community.gdg.campus.korea.koog.tools

import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlin.io.path.Path
import kotlin.io.path.writeText
import kotlin.io.path.createDirectories

@Tool
@LLMDescription("축적된 복습 노트를 종합하여 시험 대비 자료(치트시트, 예상문제, 개념맵)를 생성합니다")
fun generateExamPrep(
    @LLMDescription("생성할 자료 유형: cheatsheet, practice-exam, concept-map")
    type: String,
    @LLMDescription("생성된 자료 내용")
    content: String
): String {
    val fileName = when (type) {
        "cheatsheet" -> "cheatsheet.md"
        "practice-exam" -> "practice-exam.md"
        "concept-map" -> "concept-map.md"
        else -> "$type.md"
    }
    val path = Path("exam-prep/$fileName")
    path.parent?.createDirectories()
    path.writeText(content)
    return "시험 대비 자료가 exam-prep/$fileName 에 저장되었습니다!"
}
```

`saveNote`와 다른 점: `saveNote`는 `notes/`에 저장하고, `generateExamPrep`은 `exam-prep/`에 저장합니다. **도구의 용도를 분리**하면 에이전트가 상황에 맞는 도구를 더 정확하게 선택합니다.

### ToolRegistry 업데이트

`Main.kt`에서 ToolRegistry에 `generateExamPrep`을 추가합니다.

```kotlin
import dev.community.gdg.campus.korea.koog.tools.generateExamPrep

val toolRegistry = ToolRegistry {
    tool(::readFile)
    tool(::saveNote)
    tool(::listFiles)
    tool(::generateExamPrep)
}
```

### 에이전트 실행

```kotlin
val response = agent.run("""
    notes/ 폴더에 있는 모든 복습 노트를 읽고,
    시험 대비 자료 3종을 생성해줘:
    1. 원페이저 치트시트 (A4 1장 분량, 핵심 키워드 + 공식만)
    2. 예상 문제 3개 + 풀이 (과제 패턴 기반)
    3. 개념 연결 맵 (개념 간 관계)
""")
```

에이전트는 이 요청을 받고 다음과 같이 동작합니다:

```
① listFiles("notes/") → 어떤 노트가 있는지 확인
② readFile("notes/week07-bst-summary.md") → BST 노트 읽기
③ readFile("notes/hw-avl-guide.md") → 과제 가이드 읽기
④ generateExamPrep("cheatsheet", "...") → 치트시트 생성
⑤ generateExamPrep("practice-exam", "...") → 예상문제 생성
⑥ generateExamPrep("concept-map", "...") → 개념맵 생성
```

Positive
: **"매주 5분 투자한 복습 노트가 시험 자료로 변환되는 순간"** — 이것이 이 코드랩의 하이라이트입니다. `notes/`에 노트가 많을수록 생성되는 자료의 품질이 높아집니다. 지식 복리 효과입니다.

### 트러블슈팅

| 오류 | 해결법 |
|------|--------|
| notes/에 파일이 없음 | 이전 단계를 먼저 실행했는지 확인 |
| 자료 품질이 낮음 | System Prompt에 "시험에 나올 확률이 높은 내용 위주로" 지시 추가 |
| exam-prep/ 폴더가 생성 안 됨 | `GenerateExamPrepTool.kt`의 `createDirectories()` 호출 확인 |

`exam-prep/` 폴더에 `cheatsheet.md`, `practice-exam.md`, `concept-map.md` 3개 파일이 자동으로 생성되면 이 단계 완료입니다.

Positive
: 막히면 `git checkout page7`로 완성 코드를 확인하세요.

## Multi-Agent — 학습 전문가 팀
Duration: 8:00

### 이 단계에서 배우는 것

지금까지 하나의 에이전트가 모든 작업을 했습니다. 하지만 실제로는 **전문 분야별로 에이전트를 나누면 더 좋은 결과**가 나옵니다. 왜? 하나의 System Prompt에 모든 역할을 넣으면 지시가 모호해지고, 에이전트가 어떤 역할로 행동할지 혼란스러워하기 때문입니다.

```
범용 에이전트 하나:  "복습도 하고, 과제도 분석하고, 시험 자료도 만들어"
                    → 역할이 모호해서 품질이 고르지 않음

전문 에이전트 셋:   복습 전문가 → 과제 전문가 → 시험 전문가
                    → 각자 하나의 역할에 집중해서 품질이 높음
```

### Koog 핵심 컴포넌트

- **복수 AIAgent 인스턴스**: 각자 다른 System Prompt와 Tool 조합을 가진 전문가 에이전트들
- **에이전트 간 결과 전달**: 파일 시스템을 공유 메모리로 활용해서 에이전트 간에 결과를 전달
- **순차 오케스트레이션**: 코틀린 함수로 에이전트 실행 순서를 제어

### 파이프라인 아키텍처

```
┌─────────────┐    notes/    ┌─────────────┐    notes/    ┌─────────────┐
│  복습 정리가  │ ─────────→ │  과제 도우미  │ ─────────→ │  시험 대비가  │
│             │             │             │             │             │
│ lecture-notes/ │           │ assignments/ │            │   notes/    │
│  → notes/   │             │ + notes/     │            │ → exam-prep/│
│             │             │ + student-code/│           │             │
└─────────────┘             └─────────────┘             └─────────────┘
   readFile                    readFile                    readFile
   saveNote                    listFiles                   listFiles
                               saveNote                    generateExamPrep
```

각 에이전트는 **다른 System Prompt + 다른 Tool 조합**을 가집니다. 그리고 **파일 시스템**이 에이전트 간 공유 메모리 역할을 합니다. 복습 정리가가 `notes/`에 저장한 파일을 과제 도우미가 읽고, 과제 도우미가 저장한 결과를 시험 대비가가 읽습니다.

| 전문가 에이전트 | 역할 | 입력 → 출력 |
|---|---|---|
| **복습 정리가** | 강의자료 → 핵심 요약 노트 | `lecture-notes/` → `notes/` |
| **과제 도우미** | 과제 + 노트 + 코드 → 풀이 가이드 | `assignments/` + `notes/` + `student-code/` → `notes/` |
| **시험 대비가** | 축적된 노트 → 치트시트 + 예상문제 | `notes/` → `exam-prep/` |

### 멀티에이전트 구현

`runStudyTeam` 함수를 만듭니다. 각 에이전트는 독립적인 `AIAgent` 인스턴스입니다.

```kotlin
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.clients.google.GoogleModels
import dev.community.gdg.campus.korea.koog.tools.*

// 순차 오케스트레이션 — 각 전문가 에이전트를 역할별 System Prompt로 생성
suspend fun runStudyTeam(apiKey: String) {
    val reviewerAgent = AIAgent(
        promptExecutor = simpleGoogleAIExecutor(apiKey),
        systemPrompt = """
            너는 강의자료 복습 전문가야.
            강의 노트를 읽고 핵심 개념만 추출해서 간결한 복습 노트를 만들어.
            키워드 중심, 시험에 나올 내용 위주로 정리해.
            반드시 도구를 사용해서 파일을 읽고 저장해.
        """.trimIndent(),
        llmModel = GoogleModels.Gemini2_5Flash,
        toolRegistry = ToolRegistry {
            tool(::readFile)
            tool(::saveNote)
        }
    )

    val assignmentAgent = AIAgent(
        promptExecutor = simpleGoogleAIExecutor(apiKey),
        systemPrompt = """
            너는 과제 분석 전문가야.
            과제 요구사항을 분석하고, 기존 복습 노트를 참고해서 풀이 전략을 세워.
            학생 코드가 있으면 버그도 찾아줘.
            반드시 도구를 사용해서 파일을 읽고 저장해.
        """.trimIndent(),
        llmModel = GoogleModels.Gemini2_5Flash,
        toolRegistry = ToolRegistry {
            tool(::readFile)
            tool(::listFiles)
            tool(::saveNote)
        }
    )

    val examPrepAgent = AIAgent(
        promptExecutor = simpleGoogleAIExecutor(apiKey),
        systemPrompt = """
            너는 시험 대비 자료 전문가야.
            축적된 복습 노트를 종합해서 치트시트와 예상문제를 만들어.
            치트시트는 A4 1장 이내, 예상문제는 과제 패턴 기반으로 3문제.
            반드시 도구를 사용해서 파일을 읽고 저장해.
        """.trimIndent(),
        llmModel = GoogleModels.Gemini2_5Flash,
        toolRegistry = ToolRegistry {
            tool(::readFile)
            tool(::listFiles)
            tool(::generateExamPrep)
        }
    )

    println("=== 학습 전문가 팀 가동! ===\n")

    // 1단계: 복습 정리
    println("[1/3] 복습 정리가가 강의자료를 분석합니다...")
    val reviewResult = reviewerAgent.run(
        "lecture-notes/week08-avl-tree.md 강의자료를 읽고 복습 노트를 만들어줘"
    )
    println("복습 완료: $reviewResult\n")

    // 2단계: 과제 분석 (복습 노트를 참조)
    println("[2/3] 과제 도우미가 과제를 분석합니다...")
    val assignmentResult = assignmentAgent.run(
        "assignments/hw-avl-tree.md 과제를 분석하고, notes/ 폴더의 복습 노트를 참고해서 풀이 가이드를 만들어줘. student-code/avl_tree.cpp도 확인해줘."
    )
    println("과제 분석 완료: $assignmentResult\n")

    // 3단계: 시험 대비 자료 생성 (모든 노트를 종합)
    println("[3/3] 시험 대비가가 자료를 생성합니다...")
    val examPrepResult = examPrepAgent.run(
        "notes/ 폴더의 모든 노트를 읽고 치트시트와 예상문제를 만들어줘"
    )
    println("시험 대비 자료 완료: $examPrepResult")

    println("\n=== 전문가 팀 작업 완료! ===")
}
```

`main()` 함수에서 `runStudyTeam(apiKey)`을 호출하도록 변경합니다.

각 에이전트의 차이점에 주목하세요:

- **복습 정리가**: `readFile` + `saveNote`만 가지고 있습니다. 파일 목록은 알 필요 없습니다 — 읽을 파일이 명시되어 있으니까요.
- **과제 도우미**: `listFiles`도 가지고 있습니다. `notes/`에 뭐가 있는지 먼저 확인해야 하니까요.
- **시험 대비가**: `saveNote` 대신 `generateExamPrep`을 가지고 있습니다. 결과물을 `exam-prep/`에 저장해야 하니까요.

Positive
: 결과가 **파이프라인처럼 흐릅니다**: 복습 노트(파일) → 과제 가이드(파일) → 시험 자료(파일). 파일 시스템이 에이전트 간 공유 메모리 역할을 합니다. 이런 패턴을 **파일 기반 오케스트레이션**이라고 합니다.

### 트러블슈팅

| 오류 | 해결법 |
|------|--------|
| 에이전트가 중간에 멈춤 | Rate Limit(429) 가능성 — 호출 사이에 `delay(1000)` 추가 |
| 결과물이 비어있음 | 각 에이전트의 System Prompt에 "반드시 도구를 사용해" 추가 |
| 2단계에서 1단계 결과를 못 읽음 | 1단계 완료 후 `notes/` 폴더에 파일이 생성되었는지 확인 |

3개 에이전트가 순차 실행되어 `notes/`와 `exam-prep/` 폴더에 결과물이 생성되면 이 단계 완료입니다.

Positive
: 막히면 `git checkout page8`로 완성 코드를 확인하세요.

## CLI 앱 디자인 + 배포
Duration: 10:00

### 이 단계에서 배우는 것

지금까지 만든 에이전트를 **실제 CLI 앱**으로 다듬습니다. 환영 배너, 슬래시 명령어 시스템, 도구 호출 시각화를 추가하고, Gradle distribution으로 배포 가능한 패키지를 만듭니다.

### 핵심 컴포넌트

| 클래스/패턴 | 역할 | 왜 필요한가 |
|---|---|---|
| `Banner` | CLI 실행 시 출력되는 환영/종료 화면 | 앱의 첫인상. 사용 가능한 명령어를 안내한다 |
| `Command` 인터페이스 | 슬래시 명령어를 구조적으로 처리하는 패턴 | 새 명령어를 추가할 때 기존 코드를 수정하지 않아도 된다 (OCP) |
| `CommandRegistry` | 명령어를 등록하고 입력에 맞는 명령어를 찾아 실행하는 저장소 | 명령어 매칭 로직을 중앙화한다 |
| `handleEvents` | 에이전트의 도구 호출 상태를 실시간으로 보여주는 이벤트 핸들러 | 에이전트가 뭘 하고 있는지 사용자에게 피드백한다 |

### Banner.kt 구현

`ui/Banner.kt` 파일을 생성합니다. ASCII 아트로 앱의 첫인상을 만듭니다.

```kotlin
package dev.community.gdg.campus.korea.koog.ui

object Banner {
    private const val VERSION = "1.0.0"

    fun printWelcome() {
        println("""
            |
            |  ███████╗████████╗██╗   ██╗██████╗ ██╗   ██╗
            |  ██╔════╝╚══██╔══╝██║   ██║██╔══██╗╚██╗ ██╔╝
            |  ███████╗   ██║   ██║   ██║██║  ██║ ╚████╔╝
            |  ╚════██║   ██║   ██║   ██║██║  ██║  ╚██╔╝
            |  ███████║   ██║   ╚██████╔╝██████╔╝   ██║
            |  ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝    ╚═╝
            |
            |  ██████╗ ██╗   ██╗██████╗ ██████╗ ██╗   ██╗
            |  ██╔══██╗██║   ██║██╔══██╗██╔══██╗╚██╗ ██╔╝
            |  ██████╔╝██║   ██║██║  ██║██║  ██║ ╚████╔╝
            |  ██╔══██╗██║   ██║██║  ██║██║  ██║  ╚██╔╝
            |  ██████╔╝╚██████╔╝██████╔╝██████╔╝   ██║
            |  ╚═════╝  ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝
            |
            |  AI 학습 도우미 v${'$'}VERSION
            |  강의 복습 · 과제 분석 · 시험 대비
            |
            |  ─────────────────────────────────────────
            |  /help   사용 가능한 명령어 보기
            |  /clear  대화 초기화
            |  /exit   종료
            |  ─────────────────────────────────────────
            |
            |  또는 바로 질문을 입력하세요!
            |
        """.trimMargin())
    }

    fun printGoodbye() {
        println("""
            |
            |  ─────────────────────────────────────────
            |  공부 화이팅! 다음에 또 만나요!
            |  ─────────────────────────────────────────
            |
        """.trimMargin())
    }
}
```

Kotlin의 `trimMargin()`은 각 줄의 `|` 앞부분을 제거해서 깔끔한 들여쓰기를 유지합니다.

### Command 패턴 구현

`command/Command.kt` — 명령어 인터페이스와 레지스트리를 정의합니다.

```kotlin
package dev.community.gdg.campus.korea.koog.command

interface Command {
    val name: String
    val aliases: List<String> get() = emptyList()
    val description: String
    val usage: String get() = "/$name"
    suspend fun execute(args: List<String> = emptyList()): CommandResult
    fun matches(input: String): Boolean {
        val commandName = input.removePrefix("/").split(" ").firstOrNull() ?: return false
        return commandName == name || commandName in aliases
    }
}

sealed class CommandResult {
    data class Success(val message: String = "") : CommandResult()
    data class Error(val message: String) : CommandResult()
    data object Exit : CommandResult()
    data object ClearSession : CommandResult()
}

class CommandRegistry {
    private val commands = mutableListOf<Command>()

    fun register(command: Command) { commands.add(command) }
    fun registerAll(vararg cmds: Command) { commands.addAll(cmds) }
    fun getAllCommands(): List<Command> = commands.toList()

    suspend fun execute(input: String): CommandResult? {
        val command = commands.find { it.matches(input) } ?: return null
        val args = input.removePrefix("/").split(" ").drop(1).filter { it.isNotBlank() }
        return command.execute(args)
    }
}
```

왜 `sealed class`를 사용할까요? 명령어 실행 결과가 **Success, Error, Exit, ClearSession** 4가지뿐이라는 것을 타입 시스템으로 보장합니다. `when` 분기에서 모든 경우를 처리하도록 컴파일러가 강제합니다.

`command/HelpCommand.kt`, `command/ExitCommand.kt`, `command/ClearCommand.kt`도 각각 생성합니다.

```kotlin
// HelpCommand.kt
class HelpCommand(private val registry: CommandRegistry) : Command {
    override val name = "help"
    override val aliases = listOf("h", "?")
    override val description = "사용 가능한 명령어를 보여줍니다"
    override suspend fun execute(args: List<String>): CommandResult {
        println("\n📋 사용 가능한 명령어:\n")
        registry.getAllCommands().sortedBy { it.name }.forEach { cmd ->
            val aliasText = if (cmd.aliases.isNotEmpty()) {
                " (${cmd.aliases.joinToString(", ") { "/$it" }})"
            } else ""
            println("  ${cmd.usage}$aliasText")
            println("    ${cmd.description}")
        }
        println()
        return CommandResult.Success()
    }
}

// ExitCommand.kt
class ExitCommand : Command {
    override val name = "exit"
    override val aliases = listOf("quit", "q")
    override val description = "프로그램을 종료합니다"
    override suspend fun execute(args: List<String>) = CommandResult.Exit
}

// ClearCommand.kt
class ClearCommand : Command {
    override val name = "clear"
    override val aliases = listOf("reset")
    override val description = "대화를 초기화하고 새로 시작합니다"
    override suspend fun execute(args: List<String>): CommandResult {
        println("🔄 세션이 초기화되었습니다. 새로 시작합니다!")
        return CommandResult.ClearSession
    }
}
```

### EventHandler로 도구 호출 시각화

에이전트가 내부적으로 도구를 호출할 때 사용자에게 아무런 피드백이 없으면 "멈춘 건가?" 하고 불안해집니다. `handleEvents`를 설치하면 도구 호출 과정이 실시간으로 표시됩니다.

```kotlin
import ai.koog.agents.features.eventHandler.feature.handleEvents

val agent = AIAgent(
    promptExecutor = simpleGoogleAIExecutor(apiKey),
    systemPrompt = studyBuddyPrompt,
    llmModel = GoogleModels.Gemini2_5Flash,
    toolRegistry = toolRegistry
) {
    install(ChatMemory) {
        chatHistoryProvider = InMemoryChatHistoryProvider()
        windowSize(20)
    }
    handleEvents {
        onToolCallStarting { println("  🔧 [도구 호출] ${it.toolName}...") }
        onToolCallCompleted { println("  ✅ [도구 완료] ${it.toolName}") }
    }
}
```

이제 에이전트가 도구를 호출하면 다음과 같이 표시됩니다:

```
학생 > 강의자료 정리해줘
  🔧 [도구 호출] readFile...
  ✅ [도구 완료] readFile
  🔧 [도구 호출] saveNote...
  ✅ [도구 완료] saveNote

조교 > 복습 노트를 notes/week07-bst-summary.md에 저장했습니다!
```

### Main.kt에 Command 패턴 통합

기존 `runStudySession` 함수를 Banner + Command 패턴으로 업그레이드합니다. 에이전트 생성을 `createAgent` 함수로 분리해서 `/clear` 명령 시 새 에이전트를 만들 수 있게 합니다.

```kotlin
// CLI 대화형 세션 (Banner + Command 패턴 적용)
suspend fun runStudySession(apiKey: String) {
    val commandRegistry = CommandRegistry()
    commandRegistry.registerAll(
        HelpCommand(commandRegistry),
        ExitCommand(),
        ClearCommand()
    )

    Banner.printWelcome()
    var agent = createAgent(apiKey)

    while (true) {
        print("학생 > ")
        val input = readLine()?.trim() ?: break
        if (input.isBlank()) continue

        if (input.startsWith("/")) {
            when (val result = commandRegistry.execute(input)) {
                is CommandResult.Exit -> { Banner.printGoodbye(); return }
                is CommandResult.ClearSession -> { agent = createAgent(apiKey); continue }
                is CommandResult.Success -> continue
                is CommandResult.Error -> { println("  ❌ ${result.message}"); continue }
                null -> { println("  알 수 없는 명령어입니다. /help를 입력해보세요."); continue }
            }
        }

        val response = agent.run(input)
        println("\n조교 > $response\n")
    }
}
```

### CLI 앱으로 배포하기

Gradle `application` 플러그인이 이미 설정되어 있으므로, 배포 가능한 CLI 앱을 바로 만들 수 있습니다.

`build.gradle.kts`에 다음을 추가합니다.

```kotlin
application {
    mainClass.set("dev.community.gdg.campus.korea.koog.MainKt")
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
```

- **`applicationDefaultJvmArgs`**: Koog 프레임워크가 필요로 하는 JVM 옵션입니다.
- **`standardInput = System.in`**: `./gradlew run`으로 실행할 때 키보드 입력을 받을 수 있게 합니다. 이 설정이 없으면 `readLine()`이 즉시 `null`을 반환합니다.

배포용 패키지 생성:

```bash
# ZIP 배포 패키지 생성
./gradlew distZip

# 또는 로컬 설치
./gradlew installDist

# 실행
./build/install/study-buddy-agent-codelab/bin/study-buddy-agent-codelab
```

`distZip`으로 생성된 `build/distributions/study-buddy-agent-codelab-1.0.0.zip`을 배포하면, 상대방은 압축을 풀고 `bin/study-buddy-agent-codelab` 스크립트로 바로 실행할 수 있습니다.

Positive
: Gradle `application` 플러그인은 **시작 스크립트를 자동 생성**합니다. `bin/` 폴더에 OS별 실행 파일(Linux/Mac용 sh, Windows용 bat)이 들어있어서 Java를 직접 호출할 필요가 없습니다. `GOOGLE_API_KEY` 환경변수만 설정하면 바로 실행됩니다.

### 트러블슈팅

| 오류 | 해결법 |
|------|--------|
| `standardInput`이 동작하지 않음 | `tasks.named<JavaExec>("run")` 블록 확인 |
| 배너가 깨짐 | IntelliJ 터미널의 인코딩을 UTF-8로 설정 |
| `distZip` 후 실행 권한 없음 | `chmod +x bin/study-buddy-agent-codelab` 실행 |

배너가 출력되고 `/help` 명령이 동작하면 이 단계 완료입니다.

Positive
: 막히면 `git checkout page9` 또는 `git checkout complete`로 최종 완성 코드를 확인하세요.

## 마무리
Duration: 0:00

축하합니다! Koog 프레임워크로 완전한 AI 학습 도우미 에이전트를 완성했습니다.

### 구현한 내용 정리

| 단계 | Koog 개념 | 무엇을 만들었나 |
|------|-----------|----------------|
| 환경 설정 | 환경 설정 | 개발 환경 + API Key + 프로젝트 클론 |
| 첫 에이전트 | AIAgent, systemPrompt | 역할을 가진 첫 에이전트 |
| 강의자료 읽기 | @Tool, @LLMDescription, ToolRegistry | 파일 읽기 + 노트 저장 도구 |
| 과제 분석 | Multi Tool 조합 | 과제 분석 + 코드 리뷰 에이전트 |
| ChatMemory | ChatMemory Feature | 대화 맥락을 기억하는 에이전트 |
| 시험 대비 | Tool 확장, 지식 종합 | 시험 대비 자료 자동 생성기 |
| Multi-Agent | Multi-Agent Orchestration | 학습 전문가 팀 (3명) |
| CLI 디자인 + 배포 | Banner, Command, EventHandler, distZip | 배포 가능한 CLI 앱 |

### 배운 핵심 개념

- **ReAct 패턴**: LLM이 "생각 → 행동 → 관찰"을 반복하며 목표를 달성하는 에이전트 루프
- **도구 설계**: `@LLMDescription`이 명확할수록 에이전트가 올바른 도구를 선택. 에러는 예외 대신 문자열로 반환
- **지식 축적**: 매주 복습 노트가 쌓일수록 시험 자료의 품질이 올라가는 복리 효과
- **멀티에이전트**: 전문 에이전트를 역할별로 분리하면 범용 에이전트보다 더 좋은 결과
- **파일 기반 오케스트레이션**: 파일 시스템을 공유 메모리로 활용한 에이전트 간 통신

### 더 해볼 것들

- 다른 과목 강의자료도 추가해서 에이전트에게 던져보기
- 플래시카드 생성 Tool 추가하기
- 학점 계산기 Tool 추가하기
- Koog A2A 프로토콜로 에이전트 간 HTTP 통신 도전 (심화)

### 전체 트러블슈팅 가이드

| 오류 | 원인 | 해결법 |
|------|------|--------|
| `GOOGLE_API_KEY 환경변수를 설정해주세요!` | 환경변수 미설정 | IntelliJ Run Config > Environment variables |
| `401 Unauthorized` | API Key 무효 | Google AI Studio에서 재발급 |
| `Gradle sync failed` | JDK 불일치 | JDK 21 설치 후 Project Structure에서 선택 |
| `Unresolved reference: simpleGoogleAIExecutor` | 의존성 미설치 | build.gradle.kts에서 `ai.koog:koog-agents:0.8.0` 확인 후 Gradle sync 재실행 |
| `파일을 찾을 수 없습니다` | Working directory 불일치 | Run Config > Working directory를 프로젝트 루트로 설정 |
| Tool이 호출되지 않음 | LLM이 Tool 불필요로 판단 | System Prompt에 "반드시 도구를 사용해" 지시 추가 |
| 대화 맥락 기억 못함 | ChatMemory 미설치 | ChatMemory 단계의 `install(ChatMemory)` 위치 확인 |
| Rate Limit (429) | API 빈도 초과 | 호출 사이 `delay(1000)` 추가 |

### 참고 자료

- [Koog 공식 문서](https://github.com/JetBrains/koog)

감사합니다!
