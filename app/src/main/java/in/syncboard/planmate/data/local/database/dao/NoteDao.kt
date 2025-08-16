// Path: app/src/main/java/in/syncboard/planmate/data/local/database/dao/NoteDao.kt

package `in`.syncboard.planmate.data.local.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import `in`.syncboard.planmate.data.local.database.entities.NoteEntity

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY isPinned DESC, updatedAt DESC")
    suspend fun getNotesByUser(userId: String): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE userId = :userId AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY isPinned DESC, updatedAt DESC")
    suspend fun searchNotes(userId: String, query: String): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity): Int

    @Delete
    suspend fun deleteNote(note: NoteEntity): Int

    @Query("UPDATE notes SET isPinned = :isPinned WHERE id = :noteId")
    suspend fun updatePinnedStatus(noteId: String, isPinned: Boolean): Int

    @Query("UPDATE notes SET isArchived = :isArchived WHERE id = :noteId")
    suspend fun updateArchivedStatus(noteId: String, isArchived: Boolean): Int

    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY isPinned DESC, updatedAt DESC")
    fun observeNotes(userId: String): Flow<List<NoteEntity>>
}
