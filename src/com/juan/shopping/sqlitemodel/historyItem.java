package com.juan.shopping.sqlitemodel;

import java.sql.Date;

public class historyItem {


	String name;
	float price;
	int quantity;
	Date date;

	
	//TODO: dont think i need the constructors anymore
	
	// constructors

	public historyItem(String name, float price, Date date) {
		this.name = name;
		this.price = price;
		this.date = date;
	}
	
	//Getters and Setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

}
