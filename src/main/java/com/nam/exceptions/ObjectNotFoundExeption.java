package com.nam.exceptions;

public class ObjectNotFoundExeption extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public ObjectNotFoundExeption(String message){
		super(message);
	}

}
