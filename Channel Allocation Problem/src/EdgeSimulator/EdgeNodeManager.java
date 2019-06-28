package EdgeSimulator;

import java.io.*;
import java.util.*;

import EdgeSimulator.EdgeNodeManager.TempRequest;


public class EdgeNodeManager {
	
	public class TempRequest {
		int videoId;
		int deviceId;

		TempRequest() {
			videoId = 0;
			deviceId = 0;
		}
		TempRequest(int video_id, int device_id) {
			videoId = video_id;
			deviceId = device_id;
		}
	}

	private static EdgeNodeManager instance_ = new EdgeNodeManager();
	private int device_num_;
	private Map<Integer, DeviceNode> device_list_;
	private Vector<TempRequest>request_list_;
	
	private Set<Integer> source_list_;
	private Set<Integer> receiver_list_;
	private Set<Integer> cellular_list_;
	private Map<Integer, Vector<Integer>> pairing_graph_;
	private Map<Integer, Vector<Integer>> conflict_graph_;
	
	public EdgeNodeManager() {
		device_list_ = new HashMap<Integer, DeviceNode>();
		request_list_ = new Vector<TempRequest>();
		source_list_ = new HashSet<Integer>();
		receiver_list_ = new HashSet<Integer>();
		cellular_list_ = new HashSet<Integer>();
		conflict_graph_ = new HashMap<Integer, Vector<Integer>>();
		pairing_graph_ = new HashMap<Integer, Vector<Integer>>();
	}
	
	public static EdgeNodeManager GetInstance() {
		if(instance_ == null)
			instance_ = new EdgeNodeManager();
		return instance_;
	}
	
/***********************************************************************/	
	
	public void ImportPairingGraph() throws IOException {
		File file = new File("matching.dat");
		BufferedReader matching_in_file = null;
		String temp = null;
		matching_in_file = new BufferedReader(new FileReader(file));
		while((temp = matching_in_file.readLine())!=null) {
			String[] array = temp.split("\t");
			int source_id = Integer.parseInt(array[0]);
			int receiver_id = Integer.parseInt(array[1]);
			source_list_.add(source_id);
			receiver_list_.add(receiver_id);
			if(pairing_graph_.containsKey(source_id)) {
				pairing_graph_.get(source_id).add(receiver_id);
			}
			else {
				pairing_graph_.put(source_id, new Vector<Integer>());
				pairing_graph_.get(source_id).add(receiver_id);
			}
		}
	}
	
	public void InitCellularList () {
		for(int i = 0; i < request_list_.size(); i++) {
			int device_id = request_list_.get(i).deviceId;
			cellular_list_.add(device_id);
		}
		cellular_list_.removeAll(receiver_list_);
		cellular_list_.removeAll(source_list_);
	}
	
	public void InitConflictGraph (double trans_range) {
		for(Integer source_id: source_list_) {
			conflict_graph_.put(source_id, new Vector<Integer>());
		}
		for (Map.Entry<Integer, Vector<Integer>> entry_i : pairing_graph_.entrySet()) { 
			int source_id_i = entry_i.getKey();
			for (Map.Entry<Integer, Vector<Integer>> entry_j : pairing_graph_.entrySet()) {
				if(entry_j.getKey() != source_id_i) {
					int source_id_j = entry_j.getKey();
					Vector<Integer> receiver_list = entry_j.getValue();
					for(int k = 0; k < receiver_list.size(); k++) {
						if(GetDeviceDistance(source_id_i, receiver_list.get(k)) < trans_range) {
							conflict_graph_.get(source_id_i).add(source_id_j);
						}
					}
				}
			}
		}
	}
	
	
	public Set<Integer> GetSourceList () {
		Set<Integer> copy = new HashSet<>();
		copy.addAll(source_list_);
		return copy;
	}
	
	public Set<Integer> GetCellularList () {
		Set<Integer> copy = new HashSet<>();
		copy.addAll(cellular_list_);
		return copy;
	}
	
	public Map<Integer, Vector<Integer>> GetConflictGraph () {
		Map<Integer, Vector<Integer>> copy = new HashMap<>(conflict_graph_);
		return copy;
	}
	
	public int GetTotalChannelNum () {
		return cellular_list_.size();
	}
	
	
/***********************************************************************/
	
	//Import all devices:
	@SuppressWarnings("resource")
	public void ImportAllDevices(int device_num) throws IOException {		
		this.device_num_ = device_num;		
		File file = new File("device.dat");
		BufferedReader device_in_file = null;
		String temp = null;
		device_in_file = new BufferedReader(new FileReader(file));	
		for(int i = 0; i < device_num; ++i) {
			if((temp = device_in_file.readLine())!=null) {
				String[] array = temp.split("\t");
				int device_id = Integer.parseInt(array[0]);
				double x_pos = Double.parseDouble(array[1]);
				double y_pos = Double.parseDouble(array[2]);
				DeviceNode temp_device = new DeviceNode(device_id, x_pos, y_pos);
				device_list_.put(device_id, temp_device);
			}
		}
	}
	
	//Import requests from an existing file:
		public void ImportRequets() throws IOException {
			File file = new File("requestpatterns.dat");
			BufferedReader rp = null;
			String temp=null;
			int temp_video_id, temp_device_id, temp_end;
			try {
				rp = new BufferedReader(new FileReader(file));
				while((temp=rp.readLine())!=null) {
					String[] array = temp.split("\t");
					temp_video_id = Integer.parseInt(array[0]);
					temp_device_id = Integer.parseInt(array[1]);
					temp_end = Integer.parseInt(array[2]); // temp_end = -1 to indicate end of line
					TempRequest req = new TempRequest(temp_video_id, temp_device_id);
					request_list_.add(req);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			rp.close();
		}
	
	//Get distance between two devices:
	public double GetDeviceDistance(int device_id_1, int device_id_2) {
		DeviceNode device_1, device_2;
		if(device_list_.containsKey(device_id_1) != false)
			device_1 = device_list_.get(device_id_1);
		else
			return -1;
		if(device_list_.containsKey(device_id_2) != false)
			device_2 = device_list_.get(device_id_2);
		else
			return -1;
		double x_1, y_1, x_2, y_2;
		x_1 = device_1.GetDeviceLocationX();
		y_1 = device_1.GetDeviceLocationY();
		x_2 = device_2.GetDeviceLocationX();
		y_2 = device_2.GetDeviceLocationY();
		
		return Math.sqrt(Math.pow(x_1 - x_2, 2) + Math.pow(y_1 - y_2, 2));
	}
}
