package com.ay.whatstheword;

import java.util.Date;
import java.util.List;



public class Category {

    private Integer id = 1;


    private String name = "";

    
    private Date updateDate;


	private int imageID;


	private List<Word> words;


	private String color;

    public Category() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdateDate() {
        return updateDate;
    }
    
    
    public List<Word> getWords() {
        return words;
    }
    
    public int getImageID() {
        return imageID;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        Category cc = (Category)o;
        if (cc == null) {
            return false;
        }
        return this.getName().equals(cc.getName());
    }

	public void setImage(int resID) {
		this.imageID = resID;
		
	}

	public void setWords(List<Word> words) {
		this.words = words;		
	}

	public void setColor(String color) {
		this.color = color;
		
	}
	
	public String getColor() {
		return color;
		
	}

}
