package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM video_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): VideoProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(project: VideoProjectEntity)

    @Update
    suspend fun update(project: VideoProjectEntity)

    @Query("DELETE FROM video_projects WHERE id = :id")
    suspend fun deleteById(id: String)
}
