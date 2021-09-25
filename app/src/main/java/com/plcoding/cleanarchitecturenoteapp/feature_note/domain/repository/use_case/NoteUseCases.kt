package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.use_case

data class NoteUseCases(
    val getNotes: GetNotesUseCase,
    val deleteNote: DeleteNoteUseCase,
    val addNote: AddNoteUseCase
)