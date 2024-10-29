package com.example.apolinskyshoppingapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "shoppingTable")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "description") val description:String,
    @ColumnInfo(name = "price") var price: String,
    @ColumnInfo(name = "category") var category:ShoppingCategory,
    @ColumnInfo(name = "status") var status: Boolean
) : Serializable

enum class ShoppingCategory {
    Electronic, Food, Clothing, Book, Luxury, Other;

    fun getIcon(): Int {
        //TODO implement correct image return
        return 0;
    }
}