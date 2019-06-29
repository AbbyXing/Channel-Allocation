package EdgeSimulator;

public class DeviceNode {

	private int deviceId;
	private double xPos;
	private double yPos;
	
	public DeviceNode(int dev_id, double x_val, double y_val)
	{
		deviceId = dev_id;
		xPos = x_val;
		yPos = y_val;
	}
	
	public int GetDeviceId ()
	{
		return deviceId;
	}
	
	public double GetDeviceLocationX()
	{
		return xPos;
	}
	
	public double GetDeviceLocationY()
	{
		return yPos;
	}
}