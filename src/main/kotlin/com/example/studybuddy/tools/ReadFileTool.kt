package com.example.studybuddy.tools

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
