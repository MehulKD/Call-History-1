package com.example.callhistory;

import java.io.Serializable;

public class CallData implements Serializable{
	
	private String calltype, contactname;
	 private String callnumber;
	 private String calldatetime;
	 private String callduration;
	 private String satatus;
	 
	 public CallData()
	 {
	   
	 }
	  
	 public CallData(String calltype, String callnumber, String contactName, String callDateTime, String callDuration)
	 {
	  this.calldatetime=callDateTime;
	  this.callduration=callDuration;
	  this.callnumber=callnumber;
	  this.calltype=calltype;
	  this.contactname = contactName;
	 }
	 
	 public String getCalltype() {
	  return calltype;
	 }
	 
	 public void setCalltype(String calltype) {
	  this.calltype = calltype;
	 }
	 
	 public String getCallnumber() {
	  return callnumber;
	 }
	 
	 public void setCallnumber(String callnumber) {
	  this.callnumber = callnumber;
	 }
	 
	 public String getContactName(){
		 return contactname;
	 }
	 
	 public void setContactname(String conName){
		 this.contactname = conName;
	 }
	 
	 public String getCalldatetime() {
	  return calldatetime;
	 }
	 
	 public void setCalldatetime(String calldatetime) {
	  this.calldatetime = calldatetime;
	 }
	 
	 public String getCallduration() {
	  return callduration;
	 }
	 
	 public void setCallduration(String callduration) {
	  this.callduration = callduration;
	 }
	 
}