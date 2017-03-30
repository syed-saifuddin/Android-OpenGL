package com.troden.test_3dmodel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Mesh is a base class for 3D objects making it easier to create and maintain
 * new primitives.
 * 
 * @author Per-Erik Bergman (per-erik.bergman@jayway.com)
 * 
 */
public class Mesh {
	// Our vertex buffer.
	private FloatBuffer mVerticesBuffer = null;
	private int numVertices;

	// Our index buffer.
	private ShortBuffer mIndicesBuffer = null;

	// Our UV texture buffer.
	private FloatBuffer mTextureBuffer = null;

	// Our texture id.
	private int mTextureId = -1; // New variable.

	// The bitmap we want to load as a texture.
	private Bitmap mBitmap; // New variable.

	// Indicates if we need to load the texture.
	private boolean mShouldLoadTexture = false; // New variable.

	// The number of indices.
	private int mNumOfIndices = -1;

	// Flat Color
	private final float[] mRGBA = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	// Smooth Colors
	private FloatBuffer mColorBuffer = null;

	// Translate params.
	public float x = 0;

	public float y = 0;

	public float z = 0;

	// Rotate params.
	public float rx = 0;

	public float ry = 0;

	public float rz = 0;

	int[] bufferObjects = null;
	
	private final int VERTEX_DATA    = 0;
	private final int INDEX_DATA     = 1;
	private final int TEXCOORDS_DATA = 2;
	private final int NORMAL_DATA    = 3;
	
	private static final int SIZEOF_BYTE  = 1;
	private static final int SIZEOF_SHORT = 2;
	private static final int SIZEOF_INT   = 4;
	private static final int SIZEOF_FLOAT = 4;
	
	/**
	 * Render the mesh.
	 * 
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	public void draw(GL10 gl) {
		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticesBuffer);
		// Set flat color
		gl.glColor4f(mRGBA[0], mRGBA[1], mRGBA[2], mRGBA[3]);
		// Smooth color
		if (mColorBuffer != null) {
			// Enable the color array buffer to be used during rendering.
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
		}

		if (mShouldLoadTexture) {
			loadGLTexture(gl);
			mShouldLoadTexture = false;
		}
		if (mTextureId != -1 && mTextureBuffer != null) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// Enable the texture state
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Point to our buffers
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
		}

		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		// Point out the where the color buffer is.
		gl.glDrawElements(GL10.GL_TRIANGLES, mNumOfIndices, GL10.GL_UNSIGNED_SHORT, mIndicesBuffer);
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		if (mTextureId != -1 && mTextureBuffer != null) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}

		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);
	}

	/**
	 * Set the vertices.
	 * 
	 * @param vertices
	 */
	protected void setVertices(float[] vertices) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		numVertices = vertices.length / 3;
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 3 * SIZEOF_FLOAT);
		vbb.order(ByteOrder.nativeOrder());
		mVerticesBuffer = vbb.asFloatBuffer();
		mVerticesBuffer.put(vertices);
		mVerticesBuffer.position(0);
	}

	/**
	 * Set the indices.
	 * 
	 * @param indices
	 */
	protected void setIndices(short[] indices) {
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * SIZEOF_SHORT);
		ibb.order(ByteOrder.nativeOrder());
		mIndicesBuffer = ibb.asShortBuffer();
		mIndicesBuffer.put(indices);
		mIndicesBuffer.position(0);
		mNumOfIndices = indices.length;
	}

	/**
	 * Set the texture coordinates.
	 * 
	 * @param textureCoords
	 */
	protected void setTextureCoordinates(float[] textureCoords) { // New
																	// function.
		// float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoords.length * SIZEOF_FLOAT);
		byteBuf.order(ByteOrder.nativeOrder());
		mTextureBuffer = byteBuf.asFloatBuffer();
		mTextureBuffer.put(textureCoords);
		mTextureBuffer.position(0);
	}

	/**
	 * Set one flat color on the mesh.
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	protected void setColor(float red, float green, float blue, float alpha) {
		mRGBA[0] = red;
		mRGBA[1] = green;
		mRGBA[2] = blue;
		mRGBA[3] = alpha;
	}

	/**
	 * Set the colors
	 * 
	 * @param colors
	 */
	protected void setColors(float[] colors) {
		// float has 4 bytes.
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * SIZEOF_FLOAT);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
	}

	/**
	 * Set the bitmap to load into a texture.
	 * 
	 * @param bitmap
	 */
	public void loadBitmap(Bitmap bitmap) { // New function.
		this.mBitmap = bitmap;
		mShouldLoadTexture = true;
	}

	/**
	 * Loads the texture.
	 * 
	 * @param gl
	 */
	private void loadGLTexture(GL10 gl) { // New function
		// Generate one texture pointer...
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		mTextureId = textures[0];

		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);

		// Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
	}

	// Reads model from a gx3dbin file
	public void readModelFile (int file_resid, Context context)
	{
		InputStream in = null;
		Resources res = context.getResources ();
		byte [] buffer = new byte[48];
		int has_name, has_vertex_normals, has_diffuse, has_specular, has_weights, num_textures;
			
		// open the input file
		in = res.openRawResource (file_resid);
		if (in != null) {
			try {
				// read first part of object header
				in.read (buffer, 0, 45);
				// read has_vertex_normals
				in.read (buffer, 0, 1);	
				has_vertex_normals = (int)(buffer[0]);
				// read has_diffuse
				in.read (buffer, 0, 1);		
				has_diffuse = (int)(buffer[0]);
				// read has_specular
				in.read (buffer, 0, 1);	
				has_specular = (int)(buffer[0]);
				// read has_weights
				in.read (buffer, 0, 1);		
				has_weights = (int)(buffer[0]);
				// skip has_skeleton
				in.read (buffer, 0, 1);
								
				// read layer 1
				in.read (buffer, 0, 4);		// skip id
				in.read (buffer, 0, 4);		// skip parent_id
				in.read (buffer, 0, 1);		// skip has_parent
				in.read (buffer, 0, 1);		// read has_name
				has_name = (int)(buffer[0]);
				in.read (buffer, 0, 12);	// skip pivot
				in.read (buffer, 0, 24);	// skip bound_box
				in.read (buffer, 0, 16);	// skip bound_sphere
				// read num_vertices
				in.read (buffer, 0, 4);
				numVertices =  ((int)(buffer[0]) & 0xFF) 	  		   |
							  (((int)(buffer[1]) & 0xFF) <<  8) & 0xFF00 |
							  (((int)(buffer[2]) & 0xFF) << 16) & 0xFF0000 |
							  (((int)(buffer[3]) & 0xFF) << 24) & 0xFF000000;
				// read num polygons
				in.read (buffer, 0, 4);
				mNumOfIndices =  ((int)(buffer[0]) & 0xFF) 	 |
				  (((int)(buffer[1]) & 0xFF) <<  8) & 0xFF00 |
				  (((int)(buffer[2]) & 0xFF) << 16) & 0xFF0000 |
				  (((int)(buffer[3]) & 0xFF) << 24) & 0xFF000000;
				mNumOfIndices *= 3;
				// read num textures
				in.read (buffer, 0, 4);
				num_textures =  ((int)(buffer[0]) & 0xFF) 	 |
				  (((int)(buffer[1]) & 0xFF) <<  8) & 0xFF00 |
				  (((int)(buffer[2]) & 0xFF) << 16) & 0xFF0000 |
				  (((int)(buffer[3]) & 0xFF) << 24) & 0xFF000000;
				// skip num_morphs
				in.read (buffer, 0, 4);
				// read in vertices
				byte [] buffer_vertices = new byte[numVertices*12];
				in.read (buffer_vertices, 0, numVertices*12);
				
				ByteBuffer ibb = ByteBuffer.allocateDirect(numVertices * 3 * SIZEOF_FLOAT);
				ibb.order(ByteOrder.nativeOrder());
				ibb.put(buffer_vertices);
				ibb.position(0);
				mVerticesBuffer = ibb.asFloatBuffer();
				mVerticesBuffer.position(0);
				
				// read in polygons
				byte [] buffer_polygons = new byte[mNumOfIndices*2];
				in.read (buffer_polygons, 0, mNumOfIndices*2);
				
				ibb = ByteBuffer.allocateDirect(mNumOfIndices * SIZEOF_SHORT);
				ibb.order(ByteOrder.nativeOrder());
				ibb.put(buffer_polygons);
				ibb.position(0);
				mIndicesBuffer = ibb.asShortBuffer();
				mIndicesBuffer.position(0);
				
				// skip layer name?
				if (has_name != 0)
					in.read (buffer, 0, 32);
				// skip vertex normals?
				if (has_vertex_normals != 0)
					in.read (buffer_vertices, 0, numVertices * 3 * SIZEOF_FLOAT);
				
				// skip diffuse?
				if (has_diffuse != 0)
					in.read (buffer_vertices, 0, numVertices * 4);
				// skip specular
				if (has_specular != 0)
					in.read (buffer_vertices, 0, numVertices * 4);
				// skip weights
				if (has_weights != 0) {
					byte [] buffer_weights = new byte [numVertices * 21];
					in.read (buffer_weights, 0, numVertices * 21);
				}
								
				// read first set of texcoords
				if (num_textures == 1) {
					in.read (buffer_vertices, 0, numVertices * 2 * SIZEOF_FLOAT);
										
					ibb = ByteBuffer.allocateDirect(numVertices * 2 * SIZEOF_FLOAT);
					ibb.order(ByteOrder.nativeOrder());
					ibb.put(buffer_vertices, 0, numVertices * 2 * SIZEOF_FLOAT);
					ibb.position(0);
					mTextureBuffer = ibb.asFloatBuffer();
					mTextureBuffer.position(0);

				}
								
				// close file
				in.close ();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void initVBOs (GL10 gl)
	{
		GL11 gl11 = (GL11) gl;
		
		// Create buffer objects (vertex, texture, index)
		bufferObjects = new int[3];
        gl11.glGenBuffers(3, bufferObjects, 0);
        
        // Copy vertex data to object buffer (in VRAM)
        gl11.glBindBuffer (GL11.GL_ARRAY_BUFFER, bufferObjects[VERTEX_DATA]);
        gl11.glBufferData (GL11.GL_ARRAY_BUFFER, numVertices * 3 * SIZEOF_FLOAT, mVerticesBuffer, GL11.GL_STATIC_DRAW);

        // Copy texture coordinate data to object buffer
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, bufferObjects[TEXCOORDS_DATA]);
        gl11.glBufferData(GL11.GL_ARRAY_BUFFER, numVertices * 2 * SIZEOF_FLOAT, mTextureBuffer, GL11.GL_STATIC_DRAW);
     
        // Copy index data to object buffer
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, bufferObjects[INDEX_DATA]);
        gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, mNumOfIndices * SIZEOF_SHORT, mIndicesBuffer, GL11.GL_STATIC_DRAW);

        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void initVBOs_NoTexture (GL10 gl)
	{
		GL11 gl11 = (GL11) gl;
		
		// Create buffer objects (vertex, texture, index)
		bufferObjects = new int[2];
        gl11.glGenBuffers(2, bufferObjects, 0);
        
        // Copy vertex data to object buffer (in VRAM)
        gl11.glBindBuffer (GL11.GL_ARRAY_BUFFER, bufferObjects[VERTEX_DATA]);
        gl11.glBufferData (GL11.GL_ARRAY_BUFFER, numVertices * 3 * SIZEOF_FLOAT, mVerticesBuffer, GL11.GL_STATIC_DRAW);

        // Copy index data to object buffer
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, bufferObjects[INDEX_DATA]);
        gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, mNumOfIndices * SIZEOF_SHORT, mIndicesBuffer, GL11.GL_STATIC_DRAW);

        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void drawVBOs (GL10 gl) 
	{
		GL11 gl11 = (GL11) gl;
		
		// Counter-clockwise winding
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during rendering
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Set flat color
//		gl.glColor4f(mRGBA[0], mRGBA[1], mRGBA[2], mRGBA[3]);
		// Smooth color
//		if (mColorBuffer != null) {
//			// Enable the color array buffer to be used during rendering
//			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
//		}

		// Enable texturing
		if (mShouldLoadTexture) {
			loadGLTexture(gl);
			mShouldLoadTexture = false;
		}
		if (mTextureId != -1 && mTextureBuffer != null) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
		}
		// Enable VBOs for vertices, texture coords and polygon indeces
		gl11.glBindBuffer (GL11.GL_ARRAY_BUFFER, bufferObjects[VERTEX_DATA]);
		gl11.glVertexPointer (3, GL11.GL_FLOAT, 0, 0);
		gl11.glBindBuffer (GL11.GL_ARRAY_BUFFER, bufferObjects[TEXCOORDS_DATA]);
		gl11.glTexCoordPointer (2, GL11.GL_FLOAT, 0, 0);
		gl11.glBindBuffer (GL11.GL_ELEMENT_ARRAY_BUFFER, bufferObjects[INDEX_DATA]);
		
		// Transform the object as needed		
		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		// Render
		gl11.glDrawElements(GL11.GL_TRIANGLES, mNumOfIndices, GL11.GL_UNSIGNED_SHORT, 0);

		// Disable texturing
		if (mTextureId != -1 && mTextureBuffer != null) 
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		// Disable VBOs
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		// Disable face culling
		gl.glDisable(GL10.GL_CULL_FACE);
	}
	
	public void drawVBOs_NoTexture (GL10 gl) 
	{
		GL11 gl11 = (GL11) gl;
		
		// Counter-clockwise winding
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during rendering
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// Enable texturing
//		if (mShouldLoadTexture) {
//			loadGLTexture(gl);
//			mShouldLoadTexture = false;
//		}
//		if (mTextureId != -1 && mTextureBuffer != null) {
//			gl.glEnable(GL10.GL_TEXTURE_2D);
//			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
//		}
		// Enable VBOs for vertices, texture coords and polygon indeces
		gl11.glBindBuffer (GL11.GL_ARRAY_BUFFER, bufferObjects[VERTEX_DATA]);
		gl11.glVertexPointer (3, GL11.GL_FLOAT, 0, 0);
//		gl11.glBindBuffer (GL11.GL_ARRAY_BUFFER, bufferObjects[TEXCOORDS_DATA]);
//		gl11.glTexCoordPointer (2, GL11.GL_FLOAT, 0, 0);
		gl11.glBindBuffer (GL11.GL_ELEMENT_ARRAY_BUFFER, bufferObjects[INDEX_DATA]);
		
		// Transform the object as needed		
		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		// Render
		gl11.glDrawElements(GL11.GL_TRIANGLES, mNumOfIndices, GL11.GL_UNSIGNED_SHORT, 0);

//		// Disable texturing
//		if (mTextureId != -1 && mTextureBuffer != null) 
//			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		// Disable VBOs
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		// Disable face culling
		gl.glDisable(GL10.GL_CULL_FACE);
	}
}
