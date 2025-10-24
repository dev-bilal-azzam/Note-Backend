package com.devbilalazzam.note.api

import com.devbilalazzam.note.api.model.NoteRequest
import com.devbilalazzam.note.api.model.NoteResponse
import com.devbilalazzam.note.database.model.Note
import com.devbilalazzam.note.database.repository.NoteRepository
import jakarta.validation.Valid
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/notes")
class NoteController(
    private val noteRepository: NoteRepository,

) {

    @PostMapping
    fun saveNote(
        @Valid @RequestBody requestBody: NoteRequest
    ): NoteResponse {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val note = noteRepository.save(
            Note(
                id = requestBody.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = requestBody.title,
                content = requestBody.content,
                color = requestBody.color,
                createdAt = Instant.now(),
                ownerId = ObjectId(ownerId)
            )
        )

        return NoteResponse.fromNote(note)
    }

    @GetMapping
    fun findByOwnerId(): List<NoteResponse> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        return noteRepository.findByOwnerId(ObjectId(ownerId)).map { NoteResponse.fromNote(it) }
    }

    // Delete /notes/note id
    @DeleteMapping(path = ["/{id}"])
    fun deleteById(
        @PathVariable id: String
    ) {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String

        val note = noteRepository.findById(ObjectId(id)).orElseThrow {
            throw IllegalArgumentException("Note not found!")
        }

        if (note.ownerId != ObjectId(ownerId)) {
            throw IllegalArgumentException("You are not authorized to delete this note!")
        }

        noteRepository.deleteById(ObjectId(id))
    }
}
