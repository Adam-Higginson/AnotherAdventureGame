package com.adam.adventure.render.renderable

import com.adam.adventure.render.Renderer
import com.adam.adventure.render.shader.Program
import com.adam.adventure.render.shader.Uniform1i
import com.adam.adventure.render.texture.Texture

/**
 * Used to render tilemaps
 * @param tileSetTexture - The texture holding the tileset image
 * @param dataTexture - The texture holding the csv values of all the tile data
 * @param tilemapWidth - The number of tiles for the width of the tilemap
 * @param tilemapHeight - The number of tiles for the height of the tilemap
 */
class TilemapRenderable(val tileSetTexture: Texture,
                        val dataTexture: Texture,
                        val tilemapWidth : Int,
                        val tilemapHeight : Int) : Renderable {
    override fun getZIndex() = 0

    override fun render(renderer: Renderer?) {
        val program = renderer!!.getProgram("TilemapProgram")
        program.useProgram()
        setUniforms(program)
    }


    private fun setUniforms(program: Program) {
        tileSetTexture.bindTexture(0)
        program.getUniform("tilemapData", Uniform1i::class.java).useUniform(0)

        dataTexture.bindTexture(1)
        program.getUniform("tileset", Uniform1i::class.java).useUniform(1)

        program.getUniform("tilemapWidth", Uniform1i::class.java).useUniform(tilemapWidth)
        program.getUniform("tilemapHeight", Uniform1i::class.java).useUniform(tilemapHeight)
    }
}