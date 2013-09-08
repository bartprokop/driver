/*
 * DOMUtil.java
 *
 * Created on December 6, 2007, 1:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.w3c.dom.Element;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Sample Utility class to work with DOM document
 */
public class DOMUtil {

    /** Prints the specified node, then prints all of its children. */
    public static String printDOM(Node node) {
        StringBuilder sb = new StringBuilder();

        int type = node.getNodeType();
        switch (type) {
            // print the document element
            case Node.DOCUMENT_NODE: {
                sb.append("<?xml version=\"1.0\" ?>\n");
                sb.append(printDOM(((Document) node).getDocumentElement()));
                break;
            }

            // print element with attributes
            case Node.ELEMENT_NODE: {
                sb.append("<");
                sb.append(node.getNodeName());
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    Node attr = attrs.item(i);
                    sb.append(" " + attr.getNodeName().trim() + "=\"" + attr.getNodeValue().trim() + "\"");
                }
                sb.append(">\n");

                NodeList children = node.getChildNodes();
                if (children != null) {
                    int len = children.getLength();
                    for (int i = 0; i < len; i++) {
                        sb.append(printDOM(children.item(i)));
                    }
                }

                break;
            }

            // handle entity reference nodes
            case Node.ENTITY_REFERENCE_NODE: {
                sb.append("&");
                sb.append(node.getNodeName().trim());
                sb.append(";");
                break;
            }

            // print cdata sections
            case Node.CDATA_SECTION_NODE: {
                sb.append("<![CDATA[");
                sb.append(node.getNodeValue().trim());
                sb.append("]]>");
                break;
            }

// print text
            case Node.TEXT_NODE: {
                sb.append(node.getNodeValue().trim());
                break;
            }

// print processing instruction
            case Node.PROCESSING_INSTRUCTION_NODE: {
                sb.append("<?");
                sb.append(node.getNodeName().trim());
                String data = node.getNodeValue().trim();
                {
                    sb.append(" ");
                    sb.append(data);
                }
                sb.append("?>");
                break;
            }
        }

        if (type == Node.ELEMENT_NODE) {
            sb.append("\n");
            sb.append("</");
            sb.append(node.getNodeName().trim());
            sb.append('>');
        }

        return sb.toString();
    }

    /**
     * Create a new empty Document
     * @param fileName
     * @return Document
     */
    public static Document createNew() {
        Document document = null;
        // Initiate DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // To get a validating parser
        factory.setValidating(false);
        // To get one that understands namespaces
        factory.setNamespaceAware(true);

        try {
            // Get DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Parse and load into memory the Document
            document = builder.newDocument();
            return document;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Document createNew(Schema schema) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setSchema(schema);

        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    public static void removeAllNodeChildren(Node n) {
        NodeList nl = n.getChildNodes();
        while (nl.getLength() > 0) {
            n.removeChild(nl.item(0));
        }
    }

    /**
     * Parse the XML file and create Document
     * @param fileName
     * @return Document
     */
    public static Document parse(File file) {
        Document document = null;
        // Initiate DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // To get a validating parser
        factory.setValidating(false);
        // To get one that understands namespaces
        factory.setNamespaceAware(true);

        try {
            // Get DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Parse and load into memory the Document
            document = builder.parse(file);
            return document;
        } catch (SAXParseException spe) {
            // Error generated by the parser
            System.out.println("\n** Parsing error , line " + spe.getLineNumber() + ", uri " + spe.getSystemId());
            System.out.println(" " + spe.getMessage());
            // Use the contained exception, if any
            Exception x = spe;
            if (spe.getException() != null) {
                x = spe.getException();
            }
            x.printStackTrace();
        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            x.printStackTrace();
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
        }

        return null;
    }

    /**
     * Parse the XML InputStream and create Document
     * @param fileName
     * @return Document
     */
    public static Document parse(InputStream is) {
        Document document = null;
        // Initiate DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // To get a validating parser
        factory.setValidating(false);
        // To get one that understands namespaces
        factory.setNamespaceAware(true);

        try {
            // Get DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Parse and load into memory the Document
            document = builder.parse(is);
            return document;
        } catch (SAXParseException spe) {
            // Error generated by the parser
            System.out.println("\n** Parsing error , line " + spe.getLineNumber() + ", uri " + spe.getSystemId());
            System.out.println(" " + spe.getMessage());
            // Use the contained exception, if any
            Exception x = spe;
            if (spe.getException() != null) {
                x = spe.getException();
            }
            x.printStackTrace();
        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            x.printStackTrace();
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
        }

        return null;
    }

    public static Document parse(Schema schema, InputStream is) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setSchema(schema);

        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    public static Status validate(Schema schema, Document document) {
        Validator validator = schema.newValidator();
        Source source = new DOMSource(document);
        try {
            validator.validate(source);
            return Status.ok();
        } catch (Exception e) {
            return new Status(e);
        }
    }

    /**
     * Parse the XML file and create Document
     * @param fileName
     * @return Document
     */
    public static Document parse(String xml) {
        Document document = null;
        // Initiate DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // To get a validating parser
        factory.setValidating(false);
        // To get one that understands namespaces
        factory.setNamespaceAware(true);

        try {
            // Get DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Parse and load into memory the Document

            document = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            return document;
        } catch (SAXParseException spe) {
            // Error generated by the parser
            System.out.println("\n** Parsing error , line " + spe.getLineNumber() + ", uri " + spe.getSystemId());
            System.out.println(" " + spe.getMessage());
            // Use the contained exception, if any
            Exception x = spe;
            if (spe.getException() != null) {
                x = spe.getException();
            }
            x.printStackTrace();
        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            x.printStackTrace();
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
        }

        return null;
    }

    /**
     * This method writes a DOM document to a file
     * @param filename
     * @param document
     */
    public static void writeXmlToFile(File file, Document document) {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(document);

            // Prepare the output file
            Result result = new StreamResult(file.getPath());

            // Write the DOM document to the file
            // Get Transformer
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            // Write to a file
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            System.out.println("TransformerConfigurationException: " + e);
        } catch (TransformerException e) {
            System.out.println("TransformerException: " + e);
        }
    }

    public static String toString(Document document) {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(baos);

            transformer.transform(source, result);
            return baos.toString("UTF-8");
        } catch (TransformerConfigurationException e) {
            System.out.println("TransformerConfigurationException: " + e);
        } catch (TransformerException e) {
            System.out.println("TransformerException: " + e);
        } catch (UnsupportedEncodingException e) {
            System.out.println("TransformerException: " + e);
        }
        return "";
    }

    /**
     * Count Elements in Document by Tag Name
     * @param tag
     * @param document
     * @return number elements by Tag Name
     */
    public static int countByTagName(String tag, Document document) {
        NodeList list = document.getElementsByTagName(tag);
        return list.getLength();
    }

    public static Element getChildElement(Element element, String tag) {
        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() != n.ELEMENT_NODE) {
                continue;
            }
            Element e = (Element) n;
            if (e.getLocalName().equals(tag)) {
                return e;
            }
        }
        return null;
    }

    public static String getChildElementText(Element element, String tag) {
        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() != n.ELEMENT_NODE) {
                continue;
            }
            Element e = (Element) n;
            if (e.getLocalName().equals(tag)) {
                return e.getTextContent();
            }
        }
        return null;
    }

    public static List<Element> getChildElements(Element element) {
        ArrayList<Element> retVal = new ArrayList<Element>();

        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() != n.ELEMENT_NODE) {
                continue;
            }
            retVal.add((Element) n);
        }
        return retVal;
    }

    public static List<Element> getChildElements(Element element, String tag) {
        if (element == null) {
            throw new IllegalArgumentException("element==null");
        }
        if (tag == null) {
            throw new IllegalArgumentException("tag==null");
        }

        ArrayList<Element> retVal = new ArrayList<Element>();

        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() != n.ELEMENT_NODE) {
                continue;
            }
            Element e = (Element) n;
            //System.err.println("********* " + e + " /// " + e.getTagName());
            if (e.getTagName().equals(tag)) {
                retVal.add(e);
            }
        }
        return retVal;
    }
}
