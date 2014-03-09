package com.juan.shopping.sqlitemodel;

public class Shopping_list_item {

	String UPC;
	int quantity;

	// constructors
	public Shopping_list_item() {
	}

	public Shopping_list_item(String UPC, int quantity) {
		this.UPC = UPC;
		this.quantity = quantity;
	}

	// setters
	public void setUPC(String string) {
		this.UPC = string;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	// getters
	public String getUPC(String UPC) {
		return this.UPC;
	}

	public int getQuantity(int quantity) {
		return this.quantity;
	}
	
}
