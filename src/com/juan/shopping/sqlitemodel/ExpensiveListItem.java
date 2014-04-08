package com.juan.shopping.sqlitemodel;


public class ExpensiveListItem {

	int rid;
	float totalPrice;
	String date;
	
	//TODO: dont think i need the constructors anymore
	
	// constructors
	public ExpensiveListItem(){
	}
	
	public ExpensiveListItem(int rid, float totalPrice) {
		this.rid = rid;
		this.totalPrice = totalPrice;
	}
	
	//Getters and Setters

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}

}
