package com.example.studybuddy.tools

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
