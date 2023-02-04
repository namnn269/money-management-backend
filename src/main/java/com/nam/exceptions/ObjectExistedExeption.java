package com.nam.exceptions;

public class ObjectExistedExeption extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public ObjectExistedExeption(String message){
		super(message);
	}

}
