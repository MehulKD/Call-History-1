package com.example.callhistory.model;

public class Contact {
	
	//private variables
	int _id;
	String _name, _phone_number, _heading, _comment;
	
	// Empty constructor
	public Contact(String _phoneNumber, String heading, String comment, String addImageClicked){
		
	}
	// constructor
	public Contact(String _phoneNumber, String heading, String comment){
		//this._id = _phoneNumber;
		this._heading = heading;
		this._phone_number = _phoneNumber;
		this._comment = comment;
	}
	
	// constructor
	public Contact(String name, String _phone_number){
		this._name = name;
		this._phone_number = _phone_number;
	}
	// getting ID
	public int getID(){
		return this._id;
	}
	
	// setting id
	public void setID(int id){
		this._id = id;
	}
	
	// getting name
	public String getName(){
		return this._name;
	}
	
	// setting name
	public void setName(String name){
		this._name = name;
	}
	
	// getting phone number
	public String getPhoneNumber(){
		return this._phone_number;
	}
	
	// setting phone number
	public void setPhoneNumber(String phone_number){
		this._phone_number = phone_number;
	}
	
	// getting Heading
	public String getHeading(){
		return this._heading;
	}
		
	// setting Heading
	public void setHeading(String heading){
		this._phone_number = heading;
	}
	// getting Comment
	public String getComment(){
		return this._comment;
	}
			
	// setting Heading
	public void setComment(String comment){
		this._phone_number = comment;
	}
}
