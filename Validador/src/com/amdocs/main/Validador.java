package com.amdocs.main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;
import javax.xml.xquery.XQException;

import org.xml.sax.SAXException;

import net.sf.saxon.s9api.SaxonApiException;

import com.amdocs.dataSource.FileFinder;
import com.amdocs.utils.Utils;

public class Validador {
	

	public static void main(String[] args) throws IOException, XQException, InstantiationException, IllegalAccessException, ClassNotFoundException, XMLStreamException, SaxonApiException, SAXException, URISyntaxException, InterruptedException {
	    File[] files = new File(System.getProperty("user.home") + "/" + "Docs/Validator/FileSystem").listFiles(); 
        Path pFindXsdFiles = Paths.get(System.getProperty("user.home") + "/" + "Docs/Validator/FileSystem/XSDs");
	    
        FileFinder finderXml = new FileFinder("*.xml"); 
        FileFinder finderXQ  = new FileFinder("*.xq");
        FileFinder finderXsd = new FileFinder("*.xsd");
        
        ArrayList<Path> foundXmlFiles;
        ArrayList<Path> foundXQFiles;
        ArrayList<Path> foundXsdFiles;
        Path pInput, pOutput, pQuery;
                
	    Utils.RecorrerDirectorios(files);
	    
	    Files.walkFileTree(pFindXsdFiles , finderXsd);
	    foundXsdFiles = finderXsd.foundPaths;
	    
	    for(Path path: Utils.getDirectoriosAProcesar())	 {
	    	pInput  = Paths.get(path.toString()  + "/input");
	    	pOutput = Paths.get(path.toString()  + "/output/Result.txt");
	    	pQuery  = Paths.get(path.toString()  + "/query");
	        
	    	Files.walkFileTree(pInput , finderXml);
	        Files.walkFileTree(pQuery , finderXQ);
	        
	        foundXmlFiles = finderXml.foundPaths;
	        foundXQFiles  = finderXQ.foundPaths;
	        
	        for(Path pXML : foundXmlFiles) {
	        	System.out.println("* Procesando archivo: " + pXML.toString());
	        	File XsdToProcesar = Utils.DeterminaXSD(pXML.toFile(), foundXsdFiles);
	        	
	        	if (XsdToProcesar != null) {
	        		System.out.println("* Validando XSD con: " + XsdToProcesar.toString());
	        		if(Utils.esXMLValido(pXML.toFile(), XsdToProcesar)) {
	        		}
	        	}

	        	for(Path pXQ : foundXQFiles) {
	        		Utils.EjecutaXQuery(pXML.toFile(), pXQ.toFile(), pOutput.toFile());
	        		System.out.println("* Ejecuta Filtro: " + pXQ.toString());
	        	}

	        	System.out.println("========================================================================================================================================================");
	        }
	    }
	}
}
