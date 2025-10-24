package com.devbilalazzam.note.api.model

import com.devbilalazzam.note.database.model.Note
import java.time.Instant


data class NoteResponse(
    val id: String,
    val title: String,
    val content: String,
    val color: Long,
    val createdAt: Instant,
    val ownerId: String
) {
    companion object {
        fun fromNote(note: Note): NoteResponse {
            return NoteResponse(
                id = note.id.toString(),
                title = note.title,
                content = note.content,
                color = note.color,
                createdAt = note.createdAt,
                ownerId = note.ownerId.toString()
            )
        }
    }
}