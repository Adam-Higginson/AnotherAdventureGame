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
        val tiles : List<TileSetTile>
)

data class TileSetTile (
        val id: Int,
        val properties : List<TileProperty>,
        val type : String
)

data class Tile (
        val tileSetTile : TileSetTile?,
        val x : Int,
        val y : Int,
        val id : Int
)

data class TileProperty (
        val name : String,
        val type : String,
        val value : Any
)