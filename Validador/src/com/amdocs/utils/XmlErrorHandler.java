package com.amdocs.utils;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlErrorHandler implements ErrorHandler {
	private boolean StatusError = false;
	private List<String> Errors = new ArrayList<String>();
	private List<String> Warnings = new ArrayList<String>();
    
	public List<String> getWarning() {
		return Warnings;
	}

	public void setWarning(List<String> warning) {
		Warnings = warning;
	}

	public List<String> getErrors() {
		return Errors;
	}

	public void setErrors(List<String> errors) {
		Errors = errors;
	}

	public boolean isStatusError() {
		return StatusError;
	}

	public void setStatusError(boolean statusError) {
		StatusError = statusError;
	}

	public void warning(SAXParseException ex) {
		this.getWarning().add(ex.getMessage());
    }

    public void error(SAXParseException ex) {
    	this.getErrors().add(ex.getMessage());
        this.setStatusError(true);
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }
}
