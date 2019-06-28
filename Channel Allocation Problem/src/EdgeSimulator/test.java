package EdgeSimulator;

import java.io.*;

public class test {

	public static void main(String[] args) throws IOException {
		
		int device_num = 493;
		double trans_range = 50;
		
		double[] occupation_time = new double[device_num];
		for(int i = 0; i < device_num; i++) {
			int num = 1 + (int)(Math.random() * (10 - 1 + 1));
			occupation_time[i] = num;
		}
		
		EdgeNodeManager manager = EdgeNodeManager.GetInstance();
		manager.ImportAllDevices(device_num);
		manager.ImportRequets();
		manager.ImportPairingGraph();
		manager.InitCellularList();
		manager.InitConflictGraph(trans_range);
		
		
		// Create file to record results
		FileOutputStream util_output = null;
		util_output = new FileOutputStream("output_.dat");
		
		ChannelAllocator channelAllocator = new ChannelAllocator(device_num);
		double[][] result = channelAllocator.HeuristicColoringAlg(occupation_time);
		for(int i = 0; i < device_num; i++) {
			for(int j = 0; j < result[i].length; j++) {
				if(result[i][j] > 0) {
					String str = i + "\t" + j + "\n";
					util_output.write(str.getBytes());
				}
			}
		}
	}

}
