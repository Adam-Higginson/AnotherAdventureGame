package com.adam.adventure.tilemap

data class TileSet (
        val columns : Int,
        val image : String,
        val imageHeight : Float,
        val imageWidth : Float,
        val margin : Float,
        val name : String,
        val spacing : Float,
        val tileCount : Int,
        val tiledVersion : String,
        val tileHeight : Float,
        val tileWidth : Float,
        val type : String
)