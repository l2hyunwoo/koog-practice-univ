package com.example.studybuddy

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.chatMemory.feature.ChatMemory
import ai.koog.agents.chatMemory.feature.InMemoryChatHistoryProvider
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.clients.google.GoogleModels
import com.example.studybuddy.tools.readFile
import com.example.studybuddy.tools.saveNote
import com.example.studybuddy.tools.listFiles
import com.example.studybuddy.tools.generateExamPrep
import kotlinx.coroutines.runBlocking

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

fun main() = runBlocking {
    val apiKey = System.getenv("GOOGLE_API_KEY")
        ?: error("GOOGLE_API_KEY 환경변수를 설정해주세요!")

    runStudyTeam(apiKey)
}

// Step 6: Multi-Agent 순차 오케스트레이션
suspend fun runStudyTeam(apiKey: String) {
    val reviewerAgent = AIAgent(
        promptExecutor = simpleGoogleAIExecutor(apiKey),
        systemPrompt = """
            너는 강의자료 복습 전문가야.
            강의 노트를 읽고 핵심 개념만 추출해서 간결한 복습 노트를 만들어.
            키워드 중심, 시험에 나올 내용 위주로 정리해.
            반드시 도구를 사용해서 파일을 읽고 저장해.
            항상 한국어로 답변해.
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
            항상 한국어로 답변해.
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
            항상 한국어로 답변해.
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

// Step 4: ChatMemory 대화형 세션
suspend fun runStudySession(apiKey: String) {
    val toolRegistry = ToolRegistry {
        tool(::readFile)
        tool(::saveNote)
        tool(::listFiles)
        tool(::generateExamPrep)
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
        handleEvents {
            onToolCallStarting { println("  [도구 호출] ${it.toolName}...") }
            onToolCallCompleted { println("  [도구 완료] ${it.toolName}") }
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
