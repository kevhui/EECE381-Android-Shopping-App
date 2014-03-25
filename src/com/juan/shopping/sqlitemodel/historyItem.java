package com.juan.shopping.sqlitemodel;

import java.sql.Date;

public class historyItem {


	String upc;
	float price;
	int quantity;
	String date;

	
	//TODO: dont think i need the constructors anymore
	
	// constructors
	public historyItem(){
	}
	
	public historyItem(String upc, float price, String date) {
		this.upc = upc;
		this.price = price;
		this.date = date;
	}
	
	public historyItem(String upc, float price, String date, int quantity) {
		this.upc = upc;
		this.price = price;
		this.date = date;
		this.quantity = quantity;
	}
	
	//Getters and Setters

	public String getUPC() {
		return upc;
	}

	public void setName(String upc) {
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

}
