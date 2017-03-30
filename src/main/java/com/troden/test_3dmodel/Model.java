package com.troden.test_3dmodel;

import com.troden.test_3dmodel.Mesh;
import android.content.Context;

// 3D model
public class Model extends Mesh {
	public Model(int resid, Context context) {
		// Read all data from the file
		readModelFile (resid, context);
    }
}
