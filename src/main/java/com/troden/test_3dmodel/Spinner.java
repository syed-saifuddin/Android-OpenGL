package com.troden.test_3dmodel;

// 4-sided cube
public class Spinner extends Mesh {
	public Spinner(float width, float height, float depth) {
        width  /= 2;
        height /= 2;
        depth  /= 2;
 
        float vertices[] = {
        		-width, -height, -depth, // [0]  point 0  back
                 width, -height, -depth, // [1]  point 1
                 width,  height, -depth, // [2]  point 2
                -width,  height, -depth, // [3]  point 3
                             
                 width,  height, -depth, // [4]  point 2 top
                -width,  height, -depth, // [5]  point 3
                 width,  height,  depth, // [6]  point 6
                -width,  height,  depth, // [7]  point 7
                 
                -width, -height,  depth, // [8]  point 4 front
                 width, -height,  depth, // [9]  point 5
                 width,  height,  depth, // [10] point 6
                -width,  height,  depth, // [11] point 7
                             
                -width, -height, -depth, // [12] point 0 bottom
                 width, -height, -depth, // [13] point 1
                -width, -height,  depth, // [14] point 4 
                 width, -height,  depth, // [15] point 5        
        };

        short indices[] = { 
        		0,2,1, 		// back
        		0,3,2,
        		7,4,5,		// top
        		7,6,4,
        		8,10,11,	// front
        		8,9,10,
        		14,12,13,	// bottom
        		14,13,15
        };
     
        // Mapping coordinates for the vertices - this array needs to be same size as vertices array
		float textureCoordinates[] = { 
				0.5f, 0.5f,	// vertex [0] back
				1.0f, 0.5f,	// vertex [1]
				1.0f, 1.0f,	// vertex [2]
				0.5f, 1.0f,	// vertex [3]
				
				1.0f, 0.0f,	// vertex [4] top
				0.5f, 0.0f, // vertex [5]
				1.0f, 0.5f, // vertex [6]
				0.5f, 0.5f, // vertex [7]
				
				0.0f, 0.5f, // vertex [8] front
				0.5f, 0.5f, // vertex [9]
				0.5f, 0.0f, // vertex [10]
				0.0f, 0.0f, // vertex [11]
				
				0.0f, 1.0f, // vertex [12] bottom
				0.5f, 1.0f, // vertex [13]
				0.0f, 0.5f, // vertex [14]
				0.5f, 0.5f, // vertex [15]
		};   
        
	    setIndices(indices);
        setVertices(vertices);
		setTextureCoordinates(textureCoordinates);
    }
}
