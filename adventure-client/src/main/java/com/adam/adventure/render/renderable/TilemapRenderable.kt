package com.adam.adventure.render.renderable

import com.adam.adventure.render.Renderer
import com.adam.adventure.render.shader.Program
import com.adam.adventure.render.shader.Uniform1f
import com.adam.adventure.render.shader.Uniform1i
import com.adam.adventure.render.shader.UniformMatrix4f
import com.adam.adventure.render.texture.Texture
import com.adam.adventure.render.vertex.VertexArray
import org.joml.Matrix4f

/**
 * Used to render tilemaps
 * @param transform - The transform of the tilemap
 * @param tileSetTexture - The texture holding the tileset image
 * @param dataTexture - The texture holding the csv values of all the tile data
 * @param tilemapWidth - The number of tiles for the width of the tileMap
 * @param tilemapHeight - The number of tiles for the height of the tileMap
 * @param tilesetColumns - How many columns are in the tileset
 * @param tileSize - The size of the tiles, shader currently only supports square tiles
 * @param firstgid - The first id of the tile
 */
class TilemapRenderable(val transform : Matrix4f,
                        val tileSetTexture: Texture,
                        val dataTexture: Texture,
                        val tilemapWidth: Int,
                        val tilemapHeight: Int,
                        val tilesetColumns : Int,
                        val tileSize : Float,
                        val firstgid : Int) : Renderable {

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

    override fun destroy() {
        tileSetTexture.destroy();
        dataTexture.destroy();
    }

    private fun setUniforms(program: Program) {
        dataTexture.bind(0)
        program.getUniform("tilemapData", Uniform1i::class.java).useUniform(0)

        tileSetTexture.bind(1)
        program.getUniform("tiles", Uniform1i::class.java).useUniform(1)

        program.getUniform("tilemapWidth", Uniform1i::class.java).useUniform(tilemapWidth)
        program.getUniform("tilemapHeight", Uniform1i::class.java).useUniform(tilemapHeight)
        program.getUniform("tilesetWidth", Uniform1i::class.java).useUniform(tileSetTexture.width)
        program.getUniform("tilesetHeight", Uniform1i::class.java).useUniform(tileSetTexture.height)
        program.getUniform("tileSetColumns", Uniform1i::class.java).useUniform(tilesetColumns)
        program.getUniform("tileSize", Uniform1f::class.java).useUniform(tileSize)
        program.getUniform("model", UniformMatrix4f::class.java).useUniform(transform)
        program.getUniform("firstgid", Uniform1i::class.java).useUniform(firstgid)
    }
}