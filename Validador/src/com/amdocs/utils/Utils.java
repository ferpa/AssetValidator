package com.amdocs.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQSequence;

import net.sf.saxon.Configuration;
import net.sf.saxon.Controller;
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SchemaManager;
import net.sf.saxon.s9api.SchemaValidator;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XdmNode;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import oracle.xquery.exec.Document;
import oracle.xquery.xqj.OXQDataSource;

public class Utils {
	
	static ArrayList<Path> DirectoriosAProcesar = new ArrayList<>();
	
	public static ArrayList<Path> getDirectoriosAProcesar() {
		return DirectoriosAProcesar;
	}

	public void setDirectoriosAProcesar(ArrayList<Path> directoriosAProcesar) {
		DirectoriosAProcesar = directoriosAProcesar;
	}

	public static Path SiHoja(File[] files_s) {
		boolean bInput = false, bOutput = false, bQuery = false;
		
	    for (File file : files_s) {
	    	
	        if (file.isDirectory()) {   	
	        	if(file.getName().equalsIgnoreCase("input")) bInput = true;
	        	else if(file.getName().equalsIgnoreCase("output")) bOutput = true;
	        	else if(file.getName().equalsIgnoreCase("query")) bQuery = true;
	        }
	        
	        if (bInput && bOutput && bQuery)
	        	return Paths.get(file.toURI()).getParent();
	    }
	    
	    return null;
	}
	
	public static void EjecutaFiltro(File xmlFile, File xQueryFile, File OutputFile) throws XQException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, XMLStreamException {
		Properties serializationProps = new java.util.Properties();
		XQDataSource xqjd =  new OXQDataSource();
		XQConnection xqjc = xqjd.getConnection();
		String xQuery = readFile(xQueryFile.getPath(), StandardCharsets.UTF_8);

		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader XMLStream = factory.createXMLStreamReader(new FileReader(xmlFile));
		
		
		XQPreparedExpression xQueryExp 	= xqjc.prepareExpression(xQuery);
		//Document domDocument = new Document();
		//xQueryExp.bindNode(XQConstants.CONTEXT_ITEM, domDocument, null);

		//xQueryExp.bindString(new QName("jXMLFile"), xQueryFile.getPath(), null);
		xQueryExp.bindDocument(new QName("doc"), XMLStream, null);
		XQSequence xQueryRes 			= xQueryExp.executeQuery();
		
		
		serializationProps.setProperty("method", "xml");
		serializationProps.setProperty("encoding", "ASCII");

		xQueryRes.writeSequence(new FileOutputStream(OutputFile), serializationProps);

		xqjc.close();
	}

	public static void EjecutaXQuery(File xmlFile, File xQueryFile, File OutputFile) throws SaxonApiException, IOException {
		Processor proc 		= new Processor(false);
		XQueryCompiler comp = proc.newXQueryCompiler();
		XdmNode contextItem = proc.newDocumentBuilder().build(xmlFile);
		Serializer outFile	= proc.newSerializer(OutputFile);;
		
		String xQuery 		= readFile(xQueryFile.getPath(), StandardCharsets.UTF_8);
		
		outFile.setOutputProperty(Serializer.Property.METHOD, "text");
				 //outFile.setOutputProperty(Serializer.Property.INDENT, "yes");
				 //outFile.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
				 //outFile.setOutputStream(System.out);
        XQueryExecutable xQueryExp = comp.compile(xQuery);
		XQueryEvaluator xQeuryEval = xQueryExp.load();

		xQeuryEval.setContextItem(contextItem);
		xQeuryEval.run(outFile);		
	}
	
	public static boolean esXMLValido(File Name, File XSD) throws SAXException, IOException, InterruptedException {
		Source schemaFile = new StreamSource(XSD);
		Source xmlFile = new StreamSource(Name);
		SchemaFactory schemaFactory = SchemaFactory
		    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);
		Validator validator = schema.newValidator();
		XmlErrorHandler xmlErrorH = new XmlErrorHandler();
		boolean isValid = false;
		
		try {
			validator.setErrorHandler(xmlErrorH);
			validator.validate(xmlFile);
					
			if((xmlErrorH).isStatusError()) {
				for(String e: xmlErrorH.getErrors())
					System.err.println(e);
				System.out.println(xmlFile.getSystemId() + " is NOT valid");
				isValid = false;
			} else {
				System.out.println(xmlFile.getSystemId() + " is valid");
				isValid = true;
			}
			
			return  isValid;
		} catch (SAXException e) {
			System.out.println(xmlFile.getSystemId() + " is NOT valid");
			System.out.println("Reason: " + e.getLocalizedMessage());
			return false;
		}
	}
	
	public static void ValidaXSD(File Name, File XSD) throws SaxonApiException {
		Processor processor = new Processor(false);
        SchemaManager manager = processor.getSchemaManager();
        
        System.out.println(processor.getSaxonEdition());

        if(!processor.isSchemaAware())
        {
        	System.out.println("Processor is not schemaAware");
        }
        
        if (manager == null)
        {
        	System.out.println("Manager NULL");
        }
        
        // No resolver here, there isn't one.
        DocumentBuilder builder = processor.newDocumentBuilder();
        SAXSource sourceXml = new SAXSource(new InputSource(Name.toURI().toString()));
        XdmNode document = builder.build(sourceXml);

        SAXSource sourceXsd = new SAXSource(new InputSource(XSD.toURI().toString()));
        XdmNode schema = builder.build(sourceXsd);
        manager.load(schema.asSource());

        XdmDestination destination = new XdmDestination();
        Controller controller = new Controller(processor.getUnderlyingConfiguration());
        Receiver receiver = destination.getReceiver(controller.getConfiguration());
        PipelineConfiguration pipe = controller.makePipelineConfiguration();
        pipe.setRecoverFromValidationErrors(false);
        receiver.setPipelineConfiguration(pipe);

        SchemaValidator validator = manager.newSchemaValidator();
        validator.setDestination(destination);

        validator.validate(document.asSource());
	}
	
	
	public static void RecorrerDirectorios(File[] files) {
		Path Hoja;
		
		if ((Hoja = SiHoja(files)) != null) { 
			getDirectoriosAProcesar().add(Hoja);
		}
		
	    for (File file : files) {
	        if (file.isDirectory()) {
	            //System.out.println("Directory: " + file.getName());
	            if (file.listFiles() != null)
	            	RecorrerDirectorios(file.listFiles()); // Calls same method again.
	        } 
	    }
	}

	static String readFile(String path, Charset encoding)  throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	

	public static File DeterminaXSD(File XML, ArrayList<Path> foundXsdFiles)
	{
		String delims = "\\\\";
		String[] tokens = XML.getPath().split(delims);
		
		for (String token: tokens) {
			
	        for(Path pXsd : foundXsdFiles) {
				if (token.equalsIgnoreCase( FilenameUtils.removeExtension(pXsd.toFile().getName()))) {
					return pXsd.toFile();
				}
	        }
		}			
		
		return null;
	}
}
 