package dev.qingzhou.pushserver.manager.wecom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class WecomMessageParser {

    public static WecomMessagePayload parse(String xml) {
        WecomMessagePayload payload = new WecomMessagePayload();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);

            Element root = document.getDocumentElement();

            payload.setToUserName(getElementValue(root, "ToUserName"));
            payload.setFromUserName(getElementValue(root, "FromUserName"));
            payload.setReceiveMsgType(getElementValue(root, "MsgType")); // 使用 receiveMsgType
            payload.setContent(getElementValue(root, "Content"));
            payload.setReceiveAgentId(getElementValue(root, "AgentID")); // 使用 receiveAgentId
            payload.setEvent(getElementValue(root, "Event"));
            payload.setEventKey(getElementValue(root, "EventKey"));
            
            // 图片消息字段
            payload.setPicUrl(getElementValue(root, "PicUrl"));
            payload.setMediaId(getElementValue(root, "MediaId"));

            String createTime = getElementValue(root, "CreateTime");
            if (createTime != null && !createTime.isEmpty()) {
                payload.setCreateTime(Long.parseLong(createTime));
            }

            String msgId = getElementValue(root, "MsgId");
            if (msgId != null && !msgId.isEmpty()) {
                payload.setMsgId(Long.parseLong(msgId));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 解析失败返回空对象或部分对象
        }
        return payload;
    }

    private static String getElementValue(Element root, String tagName) {
        NodeList nodeList = root.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}