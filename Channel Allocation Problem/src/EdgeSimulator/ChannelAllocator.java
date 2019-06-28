package EdgeSimulator;

import java.io.*;
import java.util.*;

import EdgeSimulator.EdgeNodeManager.TempRequest;

public class ChannelAllocator {
	
	private EdgeNodeManager node_mgr_; // Edge node manager
	private int device_num;
	
	
	public ChannelAllocator (int device_num) {
		node_mgr_ = EdgeNodeManager.GetInstance();
		this.device_num = device_num;
	}
	
	public double[][] GreedyColoringAlg (double[] occupation_time) {
		Set<Integer> source_list = node_mgr_.GetSourceList();
		Map<Integer, Vector<Integer>> conflict_graph = node_mgr_.GetConflictGraph();
		
		double[][] channel_result = new double[device_num][device_num];
		for(int i = 0; i < device_num; i++) {
			Arrays.fill(channel_result[i], 0.0);
		}
		int total_channel_num = node_mgr_.GetTotalChannelNum();
		
		return channel_result;
	}
	
	public double[][] OptimalcoloringAlg (double[] occupation_time) throws IOException {
		Set<Integer> source_list = node_mgr_.GetSourceList();
		Map<Integer, Vector<Integer>> conflict_graph = node_mgr_.GetConflictGraph();
		
		double[][] channel_result = new double[device_num][device_num];
		for(int i = 0; i < device_num; i++) {
			Arrays.fill(channel_result[i], 0.0);
		}
		int total_channel_num = node_mgr_.GetTotalChannelNum();
		
		return channel_result;
	}
	
	public double[][] HeuristicColoringAlg (double[] occupation_time) throws IOException {
		Set<Integer> source_list = node_mgr_.GetSourceList();
		Map<Integer, Vector<Integer>> conflict_graph = node_mgr_.GetConflictGraph();
		int total_channel_num = node_mgr_.GetTotalChannelNum();
		
		double[][] channel_result = new double[device_num][total_channel_num];
		for(int i = 0; i < device_num; i++) {
			Arrays.fill(channel_result[i], 0.0);
		}
		
		Vector<Integer> heuristic_list = new Vector<>();
		
		// sort all pairs according to tau from high to low
		// if have same tau, sort from f(x_i) high to low
		// f(x_i) = the number of pairs that have no conflict with pair i
		while(!source_list.isEmpty()) {	
			double highest_time = Double.MIN_VALUE;
			int select_source_id = -1;
			for(Integer i: source_list) {
				if(occupation_time[i] > highest_time) {
					select_source_id = i;
					highest_time = occupation_time[i];
				}
				if(occupation_time[i] == highest_time) {
					if(conflict_graph.get(i).size() < conflict_graph.get(select_source_id).size()) {
						select_source_id = i;
						highest_time = occupation_time[i];
					}
				}
			}
			if(select_source_id != -1) {
				heuristic_list.add(select_source_id);
				source_list.remove(select_source_id);
			}
		}
		/*
		for(int i = 0; i < heuristic_list.size(); i++) {
			System.out.println(heuristic_list.get(i));
		}
		*/
		
		// assign channels
		int color = 0;
		while(!heuristic_list.isEmpty() && color < total_channel_num) {			
			int select_source_id = heuristic_list.get(0);
			System.out.println(select_source_id);
			channel_result[select_source_id][color] = 1.0;			
			for(int i = 1; i < heuristic_list.size(); i++) {
				if(!conflict_graph.get(select_source_id).contains(heuristic_list.get(i))) {
					channel_result[heuristic_list.get(i)][color] = 1.0;
					// update heuristic list
					heuristic_list.remove(new Integer(heuristic_list.get(i)));
				}
			}
			// update heuristic list
			heuristic_list.remove(new Integer(select_source_id));
			
			color++;
			if(color >= total_channel_num) {
				System.out.println("no enough channels!");
			}
		}
		
		return channel_result;
	}

}
