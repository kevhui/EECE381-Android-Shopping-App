package com.juan.shopping.sqlitemodel;

public class Item {

	String upc;
	String name;
	String description;
	float price;
	String category;

	
	//TODO: dont think i need the constructors anymore
	
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
	
	//Getters and Setters

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
