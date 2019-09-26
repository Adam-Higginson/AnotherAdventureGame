package com.adam.adventure.entity.component.tilemap.data

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
        val type : String,
        val tiles : List<Tile>
)

data class Tile (
        val id : Int,
        val type : String,
        val properties : List<TileProperty>
)

data class TileProperty (
        val name : String,
        val type : String,
        val value : Any
)