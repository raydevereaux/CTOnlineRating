package com.bc.ct.util;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.springframework.xml.transform.StringSource;

public class XmlStringTransformer {
	
	public static String prettifyXmlString(String xmlString) throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		//initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		StringSource source = new StringSource(xmlString);
		transformer.transform(source, result);
		//Add a new-line after the <?xml version="1.0" encoding="UTF-8"?> declaration
		return result.getWriter().toString().replaceFirst("\\?>", "?>\n");
	}
}
