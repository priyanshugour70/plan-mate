// Path: app/src/main/java/in/syncboard/planmate/data/repository/NoteRepositoryImpl.kt

package `in`.syncboard.planmate.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import `in`.syncboard.planmate.data.local.database.dao.NoteDao
import `in`.syncboard.planmate.data.local.database.entities.NoteEntity
import `in`.syncboard.planmate.domain.repository.NoteRepository
import `in`.syncboard.planmate.domain.entity.Note
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override suspend fun createNote(note: Note): Result<Note> {
        return try {
            val noteEntity = note.toEntity()
            noteDao.insertNote(noteEntity)
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNoteById(noteId: String): Result<Note?> {
        return try {
            val noteEntity = noteDao.getNoteById(noteId)
            Result.success(noteEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNotesByUser(userId: String): Result<List<Note>> {
        return try {
            val notes = noteDao.getNotesByUser(userId)
            Result.success(notes.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchNotes(userId: String, query: String): Result<List<Note>> {
        return try {
            val notes = noteDao.searchNotes(userId, query)
            Result.success(notes.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(note: Note): Result<Note> {
        return try {
            val noteEntity = note.toEntity()
            noteDao.updateNote(noteEntity)
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            val noteEntity = noteDao.getNoteById(noteId)
            if (noteEntity != null) {
                noteDao.deleteNote(noteEntity)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Note not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pinNote(noteId: String): Result<Note> {
        return try {
            noteDao.updatePinnedStatus(noteId, true)
            val updatedNote = noteDao.getNoteById(noteId)
            if (updatedNote != null) {
                Result.success(updatedNote.toDomainModel())
            } else {
                Result.failure(Exception("Note not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun archiveNote(noteId: String): Result<Note> {
        return try {
            noteDao.updateArchivedStatus(noteId, true)
            val updatedNote = noteDao.getNoteById(noteId)
            if (updatedNote != null) {
                Result.success(updatedNote.toDomainModel())
            } else {
                Result.failure(Exception("Note not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeNotes(userId: String): Flow<List<Note>> {
        return noteDao.observeNotes(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}

// Extension functions
private fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        userId = userId,
        title = title,
        content = content,
        color = color,
        tags = tags.joinToString(","),
        isPinned = isPinned,
        isArchived = isArchived,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun NoteEntity.toDomainModel(): Note {
    return Note(
        id = id,
        userId = userId,
        title = title,
        content = content,
        color = color,
        tags = if (tags.isNotEmpty()) tags.split(",") else emptyList(),
        isPinned = isPinned,
        isArchived = isArchived,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}