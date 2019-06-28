package DeviceGenerator;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.math3.distribution.UniformRealDistribution;

public class RandomLocate {
	public static void main(String[] args)throws IOException {
		int avg_per_node = 500;
		int device_id = 0;
		int area_radius = 150;
		UniformRealDistribution uniform_real_distribution = new UniformRealDistribution(0.0, 1.0);
		
		@SuppressWarnings("resource")
		FileOutputStream device_out_file = new FileOutputStream("device.dat"); //output file
		String str = null;
		
		for(int j = 0; j < avg_per_node; j++)
		{
			double radius_rand = uniform_real_distribution.sample();
			double angle_rand = uniform_real_distribution.sample();
			double x_pos;
			double y_pos;
			
			x_pos = area_radius * Math.sqrt(radius_rand) * Math.cos(2 * Math.acos(-1) * angle_rand);
			y_pos = area_radius * Math.sqrt(radius_rand) * Math.sin(2 * Math.acos(-1) * angle_rand);
			
			str = device_id + "\t" + x_pos + "\t" + y_pos + "\n";
			device_out_file.write(str.getBytes());
			device_id++;
		}
		
		System.out.println("Devices have been created!");
	}
}
