package com.juan.shopping.sqlitemodel;


public class AverageListItem {

	String upc;
	float quantity;
	float averagePrice;
	
	//TODO: dont think i need the constructors anymore
	
	// constructors
	public AverageListItem(){
	}
	
	public AverageListItem(String upc, float averagePrice, int quantity) {
		this.upc = upc;
		this.quantity = quantity;
		this.averagePrice = averagePrice;
	}
	
	//Getters and Setters

	public String getUPC() {
		return upc;
	}

	public void setUPC(String upc) {
		this.upc = upc;
	}

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}
	
	public float getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(float averagePrice) {
		this.averagePrice = averagePrice;
	}

}
