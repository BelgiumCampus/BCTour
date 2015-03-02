package com.metaio.Example;

public class Activity {

	private int id;
	private String name;
	private String activity;
	
	public Activity(int id, String name, String activity) 
	{
		this.id = id;
		this.name = name;
		this.activity = activity;
	
	}
	
	 public String getName(){
	        return this.name;
	    }
	     
	    public void setName(String name){
	        this.name = name;
	    }
	     
	    public int getID(){
	        return this.id;
	    }
	     
	    public void setId(int id){
	        this.id = id;
	    }
	    
	    public String getActivity(){
	        return this.activity;
	    }
	     
	    public void setActivity(String activity){
	        this.activity = activity;
	    }
	    
	    @Override
	    public String toString() {
	    	
	    	return "Venue :" + name+"\n"
	    			+"Activity :" + activity ;
	    }
}