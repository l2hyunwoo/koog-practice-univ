package com.example.studybuddy.tools

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
