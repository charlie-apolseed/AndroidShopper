package com.example.apolinskyshoppingapp.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDAO {
    @Query("SELECT * FROM shoppingTable")
    fun getAllTodos() : Flow<List<ShoppingItem>>

    @Query("SELECT * from shoppingTable WHERE id = :id")
    fun getTodo(id: Int): Flow<ShoppingItem>

    @Query("SELECT COUNT(*) from shoppingTable")
    suspend fun getTodosNum(): Int


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: ShoppingItem)

    @Update
    suspend fun update(todo: ShoppingItem)

    @Delete
    suspend fun delete(todo: ShoppingItem)

    @Query("DELETE from shoppingTable")
    suspend fun deleteAllTodos()
}