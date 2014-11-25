package org.skynet.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
//import java.util.LinkedList;
import java.util.List;

import org.skynet.reseau.Reseau;
//import org.skynet.utils.SkynetImage;

public class NetworkStateManager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2108905564843297711L;
	public class NetworkState implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4127222854555731607L;
		private String name;
		private byte[] buffer;
		public NetworkState(String name,byte[] buffer){
			this.buffer = buffer;
			this.name = name;
		}
		public String getName(){
			return name;
		}
		public byte[] getBuffer(){
			return buffer;
		}
	}
	//private List<NetworkStateManagerListener> listeners = new LinkedList<NetworkStateManagerListener>();
	private boolean stateChanged = false;
	private ArrayList<NetworkState> states = new ArrayList<NetworkState>();
	private int currentState = 0;//-1;
	
	public NetworkStateManager(Reseau reseau){
		pushState("Creation initial",reseau);
	}
	/*
	 * It will push the state to the end of the list, aswell as will set the current state pointer to the last
	 */
	public void pushState(String name,Reseau reseau){
			NetworkState newState = createState(name,reseau);
			states.add(newState);
	        currentState = states.size()-1; // Set the current active state to the last element
	        stateChanged = true;
	}
	protected NetworkState createState(String name,Reseau reseau){
		try {
	        java.io.ByteArrayOutputStream fileOut = new java.io.ByteArrayOutputStream();
	        java.io.ObjectOutputStream out;
			out = new java.io.ObjectOutputStream(fileOut);
	        //out.writeObject(SkynetImage.cachedImages);
	        out.writeObject(reseau);
	        out.close();
	        NetworkState ret = new NetworkState(name,fileOut.toByteArray());
	        fileOut.close();
	        return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void replaceState(int index,String name,Reseau reseau){
		NetworkState newState = createState(name,reseau);
		states.set(index, newState);
	}
	public void addState(String name, Reseau reseau){
		if(currentState!=-1){// state is not at final state, we have to remove all the following states
			for(int i=states.size()-1; i>currentState; i--)
				states.remove(i);
		}
		// We push the state
		pushState(name,reseau);
	}
	public boolean hasPreviousState(){
		return states.size()>0 && (currentState>0 || currentState==-1);
	}
	public boolean hasNextState(){
		return currentState!=-1 && currentState+1<states.size();
	}
	protected Reseau loadState(int index){
		if(index<0)
			index+=states.size();
		index = index<states.size() ? index : states.size()-1;
		byte []buffer = states.get(index).getBuffer();
	      try {
			java.io.ByteArrayInputStream fileIn = new java.io.ByteArrayInputStream(buffer);
			java.io.ObjectInputStream in = new java.io.ObjectInputStream(fileIn);
			
			Reseau reseau = (Reseau)in.readObject();
			
			in.close();
			fileIn.close();
			return reseau;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	      stateChanged=true;
	      return null;
	}
	public Reseau changeState(int index){
		if(index<0)
			index+=states.size();
		index = index<states.size() ? index : states.size()-1;
		
		currentState = index;
		return loadState(index);
	}
	public Reseau prevState(){
		if(states.size()==0 || currentState==0)
			return null;
		if(currentState==-1)
			currentState = states.size();
		currentState--;
		return loadState(currentState);
	}
	public Reseau nextState(){
		if(states.size()==0 || currentState==-1 || currentState==states.size()-1)
			return null;
		currentState++;
		if(currentState==states.size())
			currentState = -1;
		return loadState(currentState);
	}
	public List<NetworkState> getStates(){
		return states;
	}
	public int getActiveState(){
		return currentState;
	}
	public boolean isChanged(){
		if(stateChanged){
			stateChanged=false;
			return true;
		}
		//return stateChanged;
		return false;
	}
	public void setChanged(){
		stateChanged=true;
	}
}
