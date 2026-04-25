package com.example.studybuddy.tools

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
