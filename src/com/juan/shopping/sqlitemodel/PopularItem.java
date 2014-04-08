package com.juan.shopping.sqlitemodel;


public class PopularItem {

	String upc;
	int quantity;
	
	//TODO: dont think i need the constructors anymore
	
	// constructors
	public PopularItem(){
	}
	
	public PopularItem(String upc, int quantity) {
		this.upc = upc;
		this.quantity = quantity;
	}
	
	//Getters and Setters

	public String getUPC() {
		return upc;
	}

	public void setUPC(String upc) {
		this.upc = upc;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
