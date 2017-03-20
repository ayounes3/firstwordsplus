package com.ay.whatstheword;

public class Word {
	private Integer id;
	private String title;
	private int imageID;
	private String category;
	private int sound;
	private int soundReal;
	private String color;

	public Word() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		if (title == null) {
			return "";
		}

		return title;
	}

	public void setTitle(String title) {
		if (title == null) {
			this.title = "";
		} else {
			this.title = title;
		}
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public boolean equals(Object c) {
		Word card2 = (Word) c;
		return this.getId().equals(card2.getId());
	}

	@Override
	public int hashCode() {
		return getId();
	}

	public int getImageId() {
		return imageID;
	}

	public void setImageID(int image) {
		this.imageID = image;
	}

	public int getSoundId() {
		return sound;
	}
	
	public String getColor() {
		return color;
	}

	public void setSound(int sound) {
		this.sound = sound;
	}

	public void setColor(String color) {
		this.color = color;
		
	}

	public int getSoundReal() {
		return soundReal;
	}

	public void setSoundReal(int soundReal) {
		this.soundReal = soundReal;
	}
}
