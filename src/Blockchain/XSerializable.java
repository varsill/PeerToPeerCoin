package Blockchain;

public interface XSerializable {

	public String[] getListOfObjectNames();
	public void addChildToList(Object object);
	public XSerializable[] getObjectList();
}
