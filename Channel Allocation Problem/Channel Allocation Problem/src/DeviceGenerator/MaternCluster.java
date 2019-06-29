package DeviceGenerator;

import java.io.*;
import org.apache.commons.math3.distribution.*;

public class MaternCluster {

	public static void main(String[] args)throws IOException {
		@SuppressWarnings("resource")
		FileOutputStream device_out_file = new FileOutputStream("device.dat"); //output file
		String str = null;
		
		// Window parameters
		double r = 150;  // radius of disk
		double xx0 = 0; 
		double yy0 = 0; // centre of disk
		double areaTotal = Math.PI * Math.sqrt(r); // area of disk
		
		// Parameters for the parent and daughter point processes 
		int lambdaParent = 10; // density of parent Poisson point process
		int lambdaDaughter = 50; // mean number of points in each cluster
		double radiusCluster = 30; // radius of cluster disk (for daughter points)
		
		// Simulate Poisson point process for the parents
		UniformRealDistribution uniform_real_distribution = new UniformRealDistribution(0.0, 1.0);
		PoissonDistribution poisson_distribution1 = new PoissonDistribution(lambdaParent); 
		// Poisson number of points
		int numbPointsParent = poisson_distribution1.sample();
		System.out.println("numParent = " + numbPointsParent);
		int numbPoints = 0;
		int device_id = 0;
		for(int i = 0; i < numbPointsParent; i++) {
			double radius_rand = uniform_real_distribution.sample();
			double angle_rand = uniform_real_distribution.sample();
			double xxParent = r * Math.sqrt(radius_rand) * Math.cos(2 * Math.acos(-1) * angle_rand);
			double yyParent = r * Math.sqrt(radius_rand) * Math.sin(2 * Math.acos(-1) * angle_rand);
			// Simulate Poisson point process for the daughters
			PoissonDistribution poisson_distribution2 = new PoissonDistribution(lambdaDaughter, numbPointsParent);
			int numbPointsDaughter = poisson_distribution2.sample();
			numbPoints += numbPointsDaughter;
			for(int k = 0; k < numbPointsDaughter; k++) {
				radius_rand = uniform_real_distribution.sample();
				angle_rand = uniform_real_distribution.sample();
				double xxDaughter = xxParent + radiusCluster * Math.sqrt(radius_rand) * Math.cos(2 * Math.acos(-1) * angle_rand);
				double yyDaughter = yyParent + radiusCluster * Math.sqrt(radius_rand) * Math.sin(2 * Math.acos(-1) * angle_rand);
				
				// Write file
				str = device_id + "\t" + xxDaughter + "\t" + yyDaughter + "\n";
				device_out_file.write(str.getBytes());
				device_id++;
			}
		}
		
		System.out.println("Devices have been created!");
	}
}
