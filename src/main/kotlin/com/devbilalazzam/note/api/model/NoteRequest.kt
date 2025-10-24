package com.devbilalazzam.note.api.model

import jakarta.validation.constraints.NotBlank


data class NoteRequest(
    val id: String?,
    @NotBlank(message = "Title can't be blank!")
    val title: String,
    val content: String,
    val color: Long,
)
