package EdgeSimulator;

import java.io.*;
import java.util.*;
import org.omg.IOP.TAG_ORB_TYPE;
import org.apache.commons.lang3.ArrayUtils;

import EdgeSimulator.EdgeNodeManager.TempRequest;

public class ChannelAllocator {
	
	private EdgeNodeManager node_mgr_; // Edge node manager
	private int device_num;
	
	
	public ChannelAllocator (int device_num) {
		node_mgr_ = EdgeNodeManager.GetInstance();
		this.device_num = device_num;
	}
	
	public double[][] GreedyColoringAlg (double[] occupation_time) {
		Set<Integer> temp = node_mgr_.GetSourceList();
		int[] source_list = new int[temp.size()];
		int k = 0;
		for(Integer it: temp) {
			source_list[k++] = it;
		}
		if(source_list.length == 0) {
			System.out.println("no D2D pair found.");
		}
		Map<Integer, Vector<Integer>> conflict_graph = node_mgr_.GetConflictGraph();
		int total_channel_num = node_mgr_.GetTotalChannelNum();
		
		int[] channel_result = new int[device_num];
		Arrays.fill(channel_result, -1);
		double[][] final_result = new double[device_num][total_channel_num];
		for(int i = 0; i < device_num; i++) {
			Arrays.fill(final_result[i], 0.0);
		}
				
		/*1. Color first vertex with first color.
		2. Do following for remaining V-1 vertices.
		….. a) Consider the currently picked vertex and color it with the
		lowest numbered color that has not been used on any previously
		colored vertices adjacent to it. If all previously used colors
		appear on vertices adjacent to v, assign a new color to it.*/
		int sel_source_id = source_list[0];
		channel_result[sel_source_id] = 0;
		// A temporary array to store the available colors. False 
        // value of available[cr] would mean that the color cr is 
        // assigned to one of its adjacent vertices 
		boolean available[] = new boolean[total_channel_num];
		// Initially, all colors are available 
		Arrays.fill(available, true);
		for(int i = 1; i < source_list.length; i++) {
			sel_source_id = source_list[i];
			// Process all adjacent vertices and flag their colors as unavailable
			for(Integer j: conflict_graph.get(sel_source_id)) {
				//System.out.println(j);
				if(channel_result[j] != -1)
                    available[channel_result[j]] = false; 
			}
			// Find the first available color 
            int cr; 
            for (cr = 0; cr < total_channel_num; cr++){ 
                if (available[cr]) {
                	break;
                }
            }
            if(cr == total_channel_num) {
            	System.out.println("no enough channels.");
            }
            channel_result[sel_source_id] = cr; // Assign the found color 
        	// Reset the values back to true for the next iteration 
            Arrays.fill(available, true); 
		}
		
		for(int i = 0; i < channel_result.length; i++) {
			int color_id = channel_result[i];
			if(color_id != -1) {
				final_result[i][color_id] = 1.0;
			}
		}
		
		return final_result;
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
		
		// assign channels
		int color = 0;
		while(!heuristic_list.isEmpty() && color < total_channel_num) {			
			int select_source_id = heuristic_list.get(0);
			//System.out.println(select_source_id);
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
