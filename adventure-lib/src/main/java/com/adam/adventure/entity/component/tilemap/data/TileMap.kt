package com.adam.adventure.entity.component.tilemap.data

data class TileMap (
        val height : Int,
        val infinite : Boolean,
        val layers : List<TileMapLayer>,
        val nextLayerId : Int,
        val nextObjectId : Int,
        val orientation : String,
        val renderOrder : String,
        val tiledVersion : String,
        val tileHeight : Float,
        val tileWidth : Float,
        val tileSets : List<TileMapTileSet>,
        val width : Int
)

data class TileMapLayer (
        val data : IntArray,
        val height : Int,
        val name : String,
        val opacity : Int,
        val type : String,
        val visible : Boolean,
        val width : Int,
        val x : Int,
        val y : Int
)

data class TileMapTileSet (
        val firstgid : Int,
        val source : String
)

