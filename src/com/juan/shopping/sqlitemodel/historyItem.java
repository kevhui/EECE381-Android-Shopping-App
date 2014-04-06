package com.juan.shopping.sqlitemodel;


public class HistoryItem {


	String upc;
	float price;
	int quantity;
	String date;
	int rid;

	
	//TODO: dont think i need the constructors anymore
	
	// constructors
	public HistoryItem(){
	}
	
	public HistoryItem(String upc, float price, String date, int quantity) {
		this.upc = upc;
		this.price = price;
		this.date = date;
		this.quantity = quantity;
	}
	
	public HistoryItem(String upc, float price, String date, int quantity, int rid) {
		this.upc = upc;
		this.price = price;
		this.date = date;
		this.quantity = quantity;
		this.rid = rid;
	}
	
	//Getters and Setters

	public String getUPC() {
		return upc;
	}

	public void setUPC(String upc) {
		this.upc = upc;
	}

	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void addQuantity(int quantity) {
		this.quantity += quantity;
	}
	
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

}
