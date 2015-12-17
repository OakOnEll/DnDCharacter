package com.oakonell.dndcharacter.utils;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Rob on 11/9/2015.
 */
public class XmlUtils {
    public static String getElementText(Element parent, String childElemName) {
        Element element = getElement(parent, childElemName);
        if (element == null) return null;
        return element.getTextContent();
    }

    public static Element getElement(Element parent, String childElemName) {
        List<Element> elements = getChildElements(parent, childElemName);
        if (elements.isEmpty()) return null;
        if (elements.size() > 1) {
            throw new RuntimeException("Too many elements named '" + childElemName + "' off element named " +
                    "'" + parent.getTagName() + "'- expected 1, but there were " + elements.size());
        }
        return elements.get(0);

    }

    public static List<Element> getChildElements(Element parent) {
        Node child = parent.getFirstChild();
        List<Element> elements = new ArrayList<>();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElem = (Element) child;
                elements.add(childElem);
            }
            child = child.getNextSibling();
        }
        return elements;
    }

    public static List<Element> getChildElements(Element parent, String childElemName) {
        Node child = parent.getFirstChild();
        List<Element> elements = new ArrayList<>();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElem = (Element) child;
                if (childElem.getTagName().equals(childElemName)) elements.add(childElem);
            }
            child = child.getNextSibling();
        }
        return elements;
    }

    public static Document createDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            return db.newDocument();
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
    }

    public static Document getDocument(InputStream inputStream) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(inputStream);
            document = db.parse(inputSource);
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        return document;
    }

    public static String prettyPrint(Node xml) {
        try {
            Source xmlInput = new DOMSource(xml);
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            //transformerFactory.setAttribute(OutputKeys.INDENT, 3);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error pretty printing xml", e);
        }
    }

    public static Document getDocument(String s) {
        try {
            InputStream stream = new ByteArrayInputStream(s.getBytes("UTF-8"));
            return getDocument(stream);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error", e);
        }
    }


}
