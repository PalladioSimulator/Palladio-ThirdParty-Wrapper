/* Generated by Together */

package org.oslo.ocl20.standard.lib;

public interface OclBag 
    extends OclCollection
{
	public OclBoolean equalTo(OclBag bag2);
	public OclBoolean notEqualTo(OclBag bag2);
	
    public OclBag union(OclBag bag2);
    public OclBag union(OclSet set);
	public OclBag union(OclOrderedSet set);
    public OclBag intersection(OclBag bag2);
    public OclSet intersection(OclSet set);
	public OclOrderedSet intersection(OclOrderedSet set);
    public OclBag including(OclAny object);
    public OclBag excluding(OclAny object);

    public OclInteger count(OclAny object);
    public OclBag flatten();

}
