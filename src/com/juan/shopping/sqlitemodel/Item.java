package com.juan.shopping.sqlitemodel;

public class Item {

	String upc;
	String name;
	String category;

	// constructors
	public Item() {
	}

	public Item(String name, String category) {
		this.name = name;
		this.category = category;
	}

	public Item(String upc, String name, String category) {
		this.upc = upc;
		this.name = name;
		this.category = category;
	}

	// setters
	public void setUpc(String upc) {
		this.upc = upc;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	// getters
	public String getUpc() {
		return this.upc;
	}

	public String getName() {
		return this.name;
	}

	public String getCategory() {
		return this.category;
	}
}
