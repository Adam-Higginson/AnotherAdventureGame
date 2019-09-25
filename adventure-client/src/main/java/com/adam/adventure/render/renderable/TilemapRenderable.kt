package com.adam.adventure.render.renderable

import com.adam.adventure.render.Renderer
import com.adam.adventure.render.shader.Program
import com.adam.adventure.render.shader.Uniform1i
import com.adam.adventure.render.shader.UniformMatrix4f
import com.adam.adventure.render.texture.Texture
import com.adam.adventure.render.vertex.VertexArray
import org.joml.Matrix4f

/**
 * Used to render tilemaps
 * @param tileSetTexture - The texture holding the tileset image
 * @param dataTexture - The texture holding the csv values of all the tile data
 * @param tilemapWidth - The number of tiles for the width of the tilemap
 * @param tilemapHeight - The number of tiles for the height of the tilemap
 */
class TilemapRenderable(val tileSetTexture: Texture,
                        val dataTexture: Texture,
                        val tilemapWidth: Int,
                        val tilemapHeight: Int) : Renderable {

    var vertexArray: VertexArray? = null

    override fun getZIndex() = 0

    override fun initialise(renderer: Renderer) {
        val numberOfVerticesPerTile = 6
        vertexArray = renderer.vertexArrayFactory
                .newEmptyVertexArray(tilemapWidth * tilemapHeight * numberOfVerticesPerTile)
    }

    override fun render(renderer: Renderer) {
        val program = renderer.getProgram("TilemapProgram")
        program.useProgram()
        setUniforms(program)
        renderer.applyProjectionMatrix(program)

        vertexArray!!.enableVertexArray()
        vertexArray!!.drawArrays()
        vertexArray!!.unbind()

        program.disableProgram()
        tileSetTexture.unbind()
        dataTexture.unbind()
    }


    private fun setUniforms(program: Program) {
        dataTexture.bind(0)
        program.getUniform("tilemapData", Uniform1i::class.java).useUniform(0)

        tileSetTexture.bind(1)
        program.getUniform("tiles", Uniform1i::class.java).useUniform(1)

        program.getUniform("tilemapWidth", Uniform1i::class.java).useUniform(tilemapWidth)
        program.getUniform("tilemapHeight", Uniform1i::class.java).useUniform(tilemapHeight)

        program.getUniform("model", UniformMatrix4f::class.java).useUniform(Matrix4f());
    }
}