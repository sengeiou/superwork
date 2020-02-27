package com.superwork.apcosplatform.app.common;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 解析XML文件
 * @author Administrator
 * @date 2014-1-23
 */
public class ParserXmlUtil {
	/**
	 * 解析XML
	 *
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Iterator parserXml(String fileName) {
		Iterator it = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document document = db.parse(new ByteArrayInputStream(fileName.getBytes("UTF-8")));
			NodeList employees = document.getChildNodes();
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < employees.getLength(); i++) {
				Node employee = employees.item(i);

				NodeList employeeInfo = employee.getChildNodes();
				for (int j = 0; j < employeeInfo.getLength(); j++) {
					Node node = employeeInfo.item(j);
					if (!node.getNodeName().equals("report")) {
						map.put(node.getNodeName(), node.getTextContent());
					}
					NodeList employeeMeta = node.getChildNodes();
					for (int k = 0; k < employeeMeta.getLength(); k++) {
						if (!employeeMeta.item(k).getNodeName().equals("#text")) {
							map.put(employeeMeta.item(k).getNodeName(), employeeMeta.item(k).getTextContent());
						}
					}
				}
			}
			it = map.entrySet().iterator();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (SAXException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return it;
	}

	/**
	 * 解析发送xml
	 *
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static SmsResult parserSendXml(String fileName) throws Exception {
		Iterator it = parserXml(fileName);
		SmsResult result = new SmsResult();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			if (entry.getKey().equals("blacklist")) {//黑名单 暂时不处理

			} else if (entry.getKey().equals("msgid")) {
				result.setMsgid(entry.getValue().toString());
			} else if (entry.getKey().equals("result")) {
				result.setResultCode(Integer.valueOf(entry.getValue().toString()));
			} else if (entry.getKey().equals("desc")) {
				result.setResultMsg(entry.getValue().toString());
			}
		}
		return result;
	}

	/**
	 * 解析短信状态xml
	 *
	 * @param fileName
	 */
	@SuppressWarnings("unchecked")
	public static SmsResult parserReportXml(String fileName) throws Exception {
		Iterator it = parserXml(fileName);
		SmsResult result = new SmsResult();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			if (entry.getKey().equals("result")) {
				result.setResultCode(Integer.valueOf(entry.getValue().toString()));
				System.out.println(entry.getValue().toString() + "result=========");
			} else if (entry.getKey().equals("desc")) {
				result.setResultMsg(entry.getValue().toString());
				System.out.println(entry.getValue().toString() + "desc=========");
			} else if (entry.getKey().equals("msgid")) {
				result.setMsgid(entry.getValue().toString());
				System.out.println(entry.getValue().toString() + "msgid=========");
			} else if (entry.getKey().equals("phone")) {
				result.setPhones(entry.getValue().toString());
				System.out.println(entry.getValue().toString() + "phone=========");
			} else if (entry.getKey().equals("status")) {
				result.setStatus(Integer.valueOf(entry.getValue().toString()));
				System.out.println(entry.getValue().toString() + "status=========");
			} else if (entry.getKey().equals("wgcode")) {
				result.setWgcode(entry.getValue().toString());
				System.out.println(entry.getValue().toString() + "wgcode=========");
			}
		}
		return result;
	}

	/**
	 * 解析余额查询接口 (单独解析因为比较特殊，返回的字段有重复)
	 *
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static SmsResult parserBalanceXml(String fileName) throws Exception {
		SmsResult result = new SmsResult();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document document = db.parse(new ByteArrayInputStream(fileName.getBytes("UTF-8")));
		NodeList employees = document.getChildNodes();
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < employees.getLength(); i++) {
			Node employee = employees.item(i);
			NodeList employeeInfo = employee.getChildNodes();
			for (int j = 0; j < employeeInfo.getLength(); j++) {
				Node node = employeeInfo.item(j);
				if (!node.getNodeName().equals("sms") && !node.getNodeName().equals("mms")) {
					map.put(node.getNodeName(), node.getTextContent());
					if (node.getNodeName().equals("result")) {
						result.setResultCode(Integer.valueOf(node.getTextContent().toString()));
					} else if (node.getNodeName().equals("desc")) {
						result.setResultMsg(node.getTextContent().toString());
					}

				}
				NodeList employeeMeta = node.getChildNodes();
				for (int k = 0; k < employeeMeta.getLength(); k++) {
					if (!employeeMeta.item(k).getNodeName().equals("#text")) {
						if (node.getNodeName().equals("sms")) {
							if (employeeMeta.item(k).getNodeName().equals("amount")) {
								result.setAmount(employeeMeta.item(k).getTextContent().toString());
							} else if (employeeMeta.item(k).getNodeName().equals("number")) {
								result.setNumber(employeeMeta.item(k).getTextContent().toString());
							} else if (employeeMeta.item(k).getNodeName().equals("freeze")) {
								result.setFreeze(employeeMeta.item(k).getTextContent().toString());
							}
						} else if (node.getNodeName().equals("mms")) {
							if (employeeMeta.item(k).getNodeName().equals("amount")) {
								result.setCaiAmount(employeeMeta.item(k).getTextContent().toString());
							} else if (employeeMeta.item(k).getNodeName().equals("number")) {
								result.setCaiNumber(employeeMeta.item(k).getTextContent().toString());
							} else if (employeeMeta.item(k).getNodeName().equals("freeze")) {
								result.setCaiFreeze(employeeMeta.item(k).getTextContent().toString());
							}

						}
					}
				}
			}
		}
		return result;
	}
}