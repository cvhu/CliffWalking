// CS394R Assignment 3
// Example 6.6, Sutton & Barto

import java.util.*;
import java.io.*;

class CliffState{
	int x;
	int y;
	int length;
	int width;
	int rew;
	int ret;
	public CliffState(int w, int l){
		reset();
		width = w;
		length = l;
		
	}

	public void reset(){
		x = 0;
		y = 0;
		rew = 0;
		ret = 0;
	}

	public void action(String a){
		if(a.equals("up")){
			up();
		}else if(a.equals("down")){
			down();
		}else if(a.equals("left")){
			left();
		}else if(a.equals("right")){
			right();
		}
		reward();
	}

	public void up(){
		if(y < (length-1)){
			y++;
		}		
	}

	public void down(){
		if(y > 0){
			y--;
		}
	}

	public void right(){
		if(x < (width-1)){
			x++;
		}		
	}

	public void left(){
		if(x > 0){
			x--;
		}		
	}

	public void reward(){
		if((y==0) && (x>0) && (x<(width-1))){
			reset();
			rew = -100;
		}else{
			rew = -1;
		}
		ret += rew;
	}

	public int getReward(){
		return rew;
	}

	public boolean terminate(){
		return (x > 0 && (y==0));
	}

	public String getState(){
		return String.format("(%d, %d)", x, y);
	}
}

public class CliffWalking{	
	int w;
	int l;

	public CliffWalking(int width, int length){
		w = width;
		l = length;
	}

	public String etaGreedy(HashMap<String, Double> qa, double eta){
		Random rand = new Random();
		Double maxValue = Double.NEGATIVE_INFINITY;
		int maxCount = 0;
		int totalCount = 0;
		LinkedList<String> actions = new LinkedList<String>();
		LinkedList<Double> probs = new LinkedList<Double>();
		for(String action : qa.keySet()){
			totalCount++;
			Double currValue = qa.get(action);
			// System.out.printf("%f vs. %f\n", currValue, maxValue);
			int compare = currValue.compareTo(maxValue);
			if(compare > 0){
				maxValue = currValue;
				maxCount = 1;
			}else if(compare == 0){
				// System.out.println("duplicate max");
				maxCount++;
			}
		}
		Double exploreProb = eta/totalCount;
		Double greedyProb = (1.0 - eta)/maxCount + exploreProb;
		Double oldProb = 0.0;
		// System.out.printf("eV: %f\n gV: %f\nmaxCount: %d\n max: %f\n", exploreValue, greedyValue, maxCount, max);
		for(String action : qa.keySet()){
			Double currValue = qa.get(action);
			if(currValue.compareTo(maxValue)==0){
				oldProb += greedyProb;
			}else{
				oldProb += exploreProb;
			}
			// System.out.printf("%f\n", oldValue);
			probs.add(oldProb);
			actions.add(action);
		}
		double r = rand.nextDouble();
		for(int i = 0; i < totalCount; i++){
			if(r < probs.get(i)){
				return actions.get(i);
			}
		}
		return actions.get(totalCount-1);
	}

	public Double getMaxQAV(HashMap<String, Double> qa){
		Double maxValue = Double.NEGATIVE_INFINITY;
		String maxAction = null;
		for(String action : qa.keySet()){
			Double currValue = qa.get(action);
			int compare = currValue.compareTo(maxValue);
			if(compare > 0){
				maxValue = currValue;
				maxAction = action;
			}
		}
		return maxValue;
	}

	public void QLearning(double eta, double alpha, double gamma, int n){		
		HashMap<String, HashMap<String, Double>> q = new HashMap<String, HashMap<String, Double>>();
		CliffState cs = new CliffState(w, l);

		// initialize Q arbitrarily
		for(int i = 0; i < w; i++){
			for(int j = 0; j < l; j++){
				HashMap<String, Double> qa = new HashMap<String, Double>();
				qa.put("up", 0.5);
				qa.put("down", 0.5);
				qa.put("right", 0.5);
				qa.put("left", 0.5);
				q.put(String.format("(%d, %d)", i, j), qa);
			}
		}

		for(int i = 0; i < n; i++){
			cs.reset();
			String s = cs.getState();			
			int rs = 0;
			while(!cs.terminate()){
				String a = etaGreedy(q.get(s), eta);
				cs.action(a);
				int r = cs.getReward();
				String s_n = cs.getState();				
				HashMap<String, Double> qa = q.get(s);
				Double qav = qa.get(a) + alpha*(r + gamma*(getMaxQAV(q.get(s_n))) - qa.get(a));
				qa.put(a, qav);
				q.put(s, qa);
				s = s_n;				
				rs += r;
				// System.out.printf("%d: %s-%s %f\n", i, s, a, qav);
			}			
			// System.out.println(rs);
		}
		printPolicy(q);
	}

	public void Sarsa(double eta, double alpha, double gamma, int n){		
		HashMap<String, HashMap<String, Double>> q = new HashMap<String, HashMap<String, Double>>();
		CliffState cs = new CliffState(w, l);

		// initialize Q arbitrarily
		for(int i = 0; i < w; i++){
			for(int j = 0; j < l; j++){
				HashMap<String, Double> qa = new HashMap<String, Double>();
				qa.put("up", 0.5);
				qa.put("down", 0.5);
				qa.put("right", 0.5);
				qa.put("left", 0.5);
				q.put(String.format("(%d, %d)", i, j), qa);
			}
		}

		for(int i = 0; i < n; i++){
			cs.reset();
			String s = cs.getState();
			String a = etaGreedy(q.get(s), eta);
			int rs = 0;
			while(!cs.terminate()){
				cs.action(a);
				int r = cs.getReward();
				String s_n = cs.getState();
				String a_n = etaGreedy(q.get(s_n), eta);
				HashMap<String, Double> qa = q.get(s);
				Double qav = qa.get(a) + alpha*(r + gamma*(q.get(s_n).get(a_n)) - qa.get(a));
				qa.put(a, qav);
				q.put(s, qa);
				s = s_n;
				a = a_n;
				rs += r;
				// System.out.printf("%d: %s-%s %f\n", i, s, a, qav);
			}			
			// System.out.println(rs);
		}
		printPolicy(q);
	}

	public void printPolicy(HashMap<String, HashMap<String, Double>> q){
		CliffState cs = new CliffState(w,l);
		cs.reset();
		while(!cs.terminate()){
			String s = cs.getState();
			String a = etaGreedy(q.get(s), 0.0);
			cs.action(a);
			System.out.printf("%s-%s", s, a);
		}
		System.out.printf("\n");
	}

	public static void main(String[] args){		
		CliffWalking cw = new CliffWalking(12, 4);
		cw.Sarsa(0.1, 0.5, 1.0, 500);
		cw.QLearning(0.1, 0.5, 1.0, 500);
	}
}