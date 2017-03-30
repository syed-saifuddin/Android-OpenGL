package com.troden.game.framework;

import java.util.ArrayList;
import java.util.List;

/*___________________________________________________________________
|
| Class: Pool
|
| Description: Manager for music and sound effects.
|__________________________________________________________________*/
public class Pool<T> 
{
	// Returns a new Pool object of data type T
	//  User of this class must override this function to perform object creation for data type T
	public interface PoolObjectFactory<T> {
		public T createObject();
	}
	// array of available data objects
	private final List<T> availObjects;
	// used to generate new instances of the data type Pool holds
	private final PoolObjectFactory<T> factory;
	// max number of instances stored in a Pool
	private final int maxSize;
	
	/*___________________________________________________________________
	|
	| Function: Pool (constructor)
	|__________________________________________________________________*/
	public Pool (PoolObjectFactory<T> factory, int maxSize)
	{
		this.factory = factory;
		this.maxSize = maxSize;
		// Create a new array of objects of type T
		this.availObjects = new ArrayList<T>(maxSize);
	}
	
	/*___________________________________________________________________
	|
	| Function: newObject
	|
	| Description: Create a new object of type T.  Use a free object from
	|	the avail list, if any.  Otherwise create one from scratch.
	|__________________________________________________________________*/
	public T newObject ()
	{
		T object = null;
		// Preferably, use an object from the available list
		if (availObjects.size() > 0)
			object = availObjects.remove(availObjects.size() - 1);
		// Create a new object if necessary
		else
			object = factory.createObject ();
		return (object);
	}
	
	/*___________________________________________________________________
	|
	| Function: freeObject
	|
	| Description: Attempts to add the object back into the Pool.  If pool
	|		is full then we rely on GC to collect the object at some time
	|		in the future.
	|__________________________________________________________________*/
	public void freeObject (T object)
	{
		if (availObjects.size() < maxSize)
			availObjects.add (object);
	}
}
