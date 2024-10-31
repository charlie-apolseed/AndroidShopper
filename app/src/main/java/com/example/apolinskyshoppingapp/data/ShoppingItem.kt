package com.example.apolinskyshoppingapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.apolinskyshoppingapp.R
import java.io.Serializable


@Entity(tableName = "shoppingTable")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "description") val description:String,
    @ColumnInfo(name = "price") var price: String,
    @ColumnInfo(name = "category") var category:ShoppingCategory,
    @ColumnInfo(name = "purchased") var purchased: Boolean
) : Serializable

enum class ShoppingCategory {
    Electronic, Food, Clothing, Book, Luxury, Other;

    fun getIcon(): Int {
        if (this == Electronic) {
            return R.drawable.electronics_icon
        } else if (this == Food) {
            return R.drawable.food_icon }
        else if (this == Clothing) {
            return R.drawable.clothes_icon }
        else if (this == Book) {
            return R.drawable.book_icon }
        else if (this == Luxury) {
            return R.drawable.luxury_icon }
        else {
            return R.drawable.other_icon
        }
    }
}