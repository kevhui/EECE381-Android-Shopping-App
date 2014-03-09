package com.juan.shopping.sqlitemodel;

public class Item {

	String UPC;
	String name;
	String category;

	// constructors
	public Item() {
	}

	public Item(String name, String category) {
		this.name = name;
		this.category = category;
	}

	public Item(String UPC, String name, String category) {
		this.UPC = UPC;
		this.name = name;
		this.category = category;
	}

	// setters
	public void setUPC(String UPC) {
		this.UPC = UPC;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	// getters
	public String getUPC() {
		return this.UPC;
	}

	public String getName() {
		return this.name;
	}

	public String getCategory() {
		return this.category;
	}
}
