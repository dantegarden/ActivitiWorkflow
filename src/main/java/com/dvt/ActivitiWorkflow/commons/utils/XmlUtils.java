package com.dvt.ActivitiWorkflow.commons.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.Node;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlUtils {
	
	private static final String ENCODE = "UTF-8";
	
	/**格式化HTML*/
	public static String formatHtml(String str) throws Exception {  
	    Document document = null;  
	    document = DocumentHelper.parseText(str);  
	  
	    OutputFormat format = OutputFormat.createPrettyPrint();  
	    format.setEncoding(ENCODE); 
	    format.setExpandEmptyElements(true);
	    StringWriter writer = new StringWriter();  
	  
	    HTMLWriter htmlWriter = new HTMLWriter(writer, format);  
	    htmlWriter.write(document);  
	    htmlWriter.close();  
	    return writer.toString();  
	}  
	/** 
     * 返回格式化的XML字段串 
     *  
     * @param document 
     *            要格式化的文档 
     * @param encoding 
     *            使用的编码,如果为null刚使用默认编码(utf-8) 
     * @return 格式化的XML字段串 
     */  
    public static String toXMLString(Document document, String encoding) {  
        if (encoding == null) {  
            encoding = ENCODE;  
        }  
        StringWriter writer = new StringWriter();  
        OutputFormat format = OutputFormat.createPrettyPrint();  
        format.setEncoding(ENCODE);  
        XMLWriter xmlwriter = new XMLWriter(writer, format);  
        try {  
            xmlwriter.write(document);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return writer.toString();  
    }  
    
    /** 
     * 返回格式化的XML字段串 
     *  
     * @param element 
     *            要格式化的节点元素 
     * @param encoding 
     *            使用的编码,如果为null刚使用默认编码(utf-8) 
     * @return 格式化的XML字段串 
     */  
    public static String toXMLString(ElementHandler element, String encoding) {  
        if (encoding == null) {  
            encoding = ENCODE;  
        }  
        StringWriter writer = new StringWriter();  
        OutputFormat format = OutputFormat.createPrettyPrint();  
        format.setEncoding(encoding);  
        XMLWriter xmlwriter = new XMLWriter(writer, format);  
        try {  
            xmlwriter.write(element);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return writer.toString();  
    }
    
    /** 
     * 格式化文档并输出到文件 
     *  
     * @param document 
     *            要输出的文档 
     * @param filename 
     *            XML文件名 
     * @param encoding 
     *            使用的编码,如果为null刚使用默认编码(utf-8) 
     * @return true or false 
     */  
    public static boolean toXMLFile(Document document, String filename,  
            String encoding) {  
        if (encoding == null) {  
            encoding = ENCODE;  
        }  
        boolean returnValue = false;  
        XMLWriter output = null;
        OutputStream out = null;
        Writer wr = null;
        try {  
            /** 格式化输出,类型IE浏览一样 */  
            OutputFormat format = OutputFormat.createPrettyPrint();  
            out = new FileOutputStream(filename);
            wr = new OutputStreamWriter(out, ENCODE);   
            output = new XMLWriter(wr, format);  
            output.write(document);  
            /** 执行成功,需返回1 */  
            returnValue = true;  
        } catch (Exception ex) {  
            ex.printStackTrace();  
            returnValue = false;  
        } finally {
        	try {
				output.close();
				wr.close();
		        out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
           
        }
        return returnValue;  
    }
    
    /** 
     * 格式化XML文件并保存 
     *  
     * @param srcFileName 
     *            源XML文件 
     * @param desFileName 
     *            格式化后的XML文件,如果为null,则使用srcFileName 
     * @param encoding 
     *            使用的编码,如果为null刚使用默认编码(utf-8) 
     * @return true or false 
     */  
    public static boolean toXMLFile(String srcFileName, String desFileName,  
            String encoding) {  
        if (encoding == null) {  
            encoding = ENCODE;  
        }  
        if (desFileName == null) {  
            desFileName = srcFileName;  
        }  
        boolean returnValue = false;  
        try {  
            SAXReader saxReader = new SAXReader();  
            Document document = saxReader.read(new File(srcFileName));  
            XMLWriter output = null;  
            /** 格式化输出,类型IE浏览一样 */  
            OutputFormat format = OutputFormat.createPrettyPrint();  
            /** 指定XML字符集编码 */  
            format.setEncoding(encoding);  
            output = new XMLWriter(new FileWriter(new File(desFileName)),  
                    format);  
            output.write(document);  
            output.close();  
            /** 执行成功,需返回1 */  
            returnValue = true;  
        } catch (Exception ex) {  
            ex.printStackTrace();  
            returnValue = false;  
        }  
        return returnValue;  
    }  
    
    
    /** 
     * 从读取XML文件 
     *  
     * @param fileName 
     * @return Document对象 
     */  
    public static Document read(String fileName) {  
        SAXReader reader = new SAXReader();  
        reader.setEncoding(ENCODE);
        Document document = null;  
        try {  
            document = reader.read(new File(fileName));  
        } catch (DocumentException e) {  
            e.printStackTrace();  
        }  
        return document;  
    }
    
    /** 
     * 从XML字符串转换到document 
     *  
     * @param xmlStr 
     *            XML字符串 
     * @return Document 
     */  
    public static Document parseText(String xmlStr) {  
        Document document = null;  
        try {  
            document = DocumentHelper.parseText(xmlStr);  
        } catch (DocumentException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return document;  
    }
    
    /**
     * 使用xpath表达式查询节点
     * @param xpathSelector
     * @param document
     * */
    public static List<Node> XpathQuery(Document document,String xpathSelector){
    	List<Node> nodes = document.selectNodes(xpathSelector);
    	return nodes;
    }
}
