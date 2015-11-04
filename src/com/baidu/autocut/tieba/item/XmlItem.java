package com.baidu.autocut.tieba.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baidu.autocut.tieba.util.HashedPoolMananger;
import com.baidu.autocut.tieba.util.UtilHelper;

public class XmlItem extends BaseItem {

    public XmlItem(String file) {
        init(file);
    }

    @Override
    public boolean init(String file) {
        key = file;
        return true;
    }

    @Override
    public String getKey() {
        // TODO Auto-generated method stub
        return key;
    }

    @Override
    public LinkedList<JavaItem> getJavaItem() {
        if (mImportJavaItem == null) {
            scanfSelf();
        }
        return mImportJavaItem;
    }

    @Override
    public LinkedList<XmlItem> getXmlItem() {
        if (mImportXmlItem == null) {
            scanfSelf();
        }
        return mImportXmlItem;
    }

    @Override
    public LinkedList<String> getImageItem() {
        if (mImportImageItem == null) {
            scanfSelf();
        }
        return mImportImageItem;
    }

    @Override
    public boolean scanfSelf() {
        mImportJavaItem = new LinkedList<JavaItem>();
        mImportXmlItem = new LinkedList<XmlItem>();
        mImportImageItem = new LinkedList<String>();

        BufferedReader reader = null;
        String path = UtilHelper.getFilePath(key);
        if (path == null) {
            return true;
        }
        try {
            InputStream stream = new FileInputStream(new File(path));

            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();

            Document document = builder.parse(stream);
            Element root = document.getDocumentElement();
            dfs(root);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    //遍历xml树
    private void dfs(Element ele) {
        String tag = ele.getTagName();
        if (HashedPoolMananger.checkCodeFile(tag)) {
            // System.out.println(tag);
            JavaItem java = new JavaItem(tag);
            mImportJavaItem.add(java);
        }

        String value = ele.getFirstChild() != null ? ele.getFirstChild()
                .getNodeValue() : null;
        if (value != null) {
            // System.out.println(value);
            Matcher drawableMatcher = UtilHelper.mXmlDrawablePattern
                    .matcher(value);
            while (drawableMatcher.find()) {
                String group = drawableMatcher.group();
                String imageName = group.substring(group.lastIndexOf("/") + 1);
                if (HashedPoolMananger.checkImageFile(imageName)) {
                    // HashedPoolMananger.putHashedImageKey(imageName);
                    mImportImageItem.add(imageName);
                }
                if (HashedPoolMananger.checkCodeFile(imageName)) {
                    XmlItem item = new XmlItem(imageName);
                    mImportXmlItem.add(item);
                    // System.out.println(imageName);
                }

            }

            Matcher layoutMatcher = UtilHelper.mXmlResPattern.matcher(value);
            while (layoutMatcher.find()) {
                String group = layoutMatcher.group();
                String imageName = group.substring(group.lastIndexOf("/") + 1);
                if (HashedPoolMananger.checkCodeFile(imageName)) {
                    XmlItem item = new XmlItem(imageName);
                    mImportXmlItem.add(item);
                    // System.out.println(imageName);
                }

            }
        }
        NamedNodeMap map = ele.getAttributes();
        for (int i = 0; i < map.getLength(); i++) {
            Node node = map.item(i);
            String content = node.getTextContent();

            if (HashedPoolMananger.checkCodeFile(content)) {
//				 System.out.println(content);
                JavaItem java = new JavaItem(content);
                mImportJavaItem.add(java);
            }
//			 System.out.println(node.getTextContent());
            Matcher drawableMatcher = UtilHelper.mXmlDrawablePattern
                    .matcher(content);
            while (drawableMatcher.find()) {
                String group = drawableMatcher.group();
                String imageName = group.substring(group.lastIndexOf("/") + 1);
                if (HashedPoolMananger.checkImageFile(imageName)) {
                    // HashedPoolMananger.putHashedImageKey(imageName);
                    mImportImageItem.add(imageName);
                    // System.out.println(imageName);
                }
                if (HashedPoolMananger.checkCodeFile(imageName)) {
                    XmlItem item = new XmlItem(imageName);
                    mImportXmlItem.add(item);
                    // System.out.println(imageName);
                }

                if (!imageName.endsWith("_1")) {
                    imageName = imageName + "_1";
                    if (HashedPoolMananger.checkImageFile(imageName)) {
                        // HashedPoolMananger.putHashedImageKey(imageName);
                        mImportImageItem.add(imageName);
                        // System.out.println(imageName);
                    }
                    if (HashedPoolMananger.checkCodeFile(imageName)) {
                        XmlItem item = new XmlItem(imageName);
                        mImportXmlItem.add(item);
                        // System.out.println(imageName);
                    }
                }

            }

            Matcher layoutMatcher = UtilHelper.mXmlResPattern
                    .matcher(content);
            while (layoutMatcher.find()) {
                String group = layoutMatcher.group();
                String imageName = group.substring(group.lastIndexOf("/") + 1);
                if (HashedPoolMananger.checkCodeFile(imageName)) {
                    XmlItem item = new XmlItem(imageName);
                    mImportXmlItem.add(item);
                    // System.out.println(imageName);
                }

            }

            Matcher stringMatcher = UtilHelper.mXmlStringPattern
                    .matcher(content);
            while (stringMatcher.find()) {
                String group = stringMatcher.group();
                String stringName = group.substring(group.lastIndexOf("/") + 1);
                HashedPoolMananger.putHashedStringKey(stringName);
                // System.out.println(stringName);
            }
        }
        NodeList childs = ele.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node item = childs.item(i);
            if (item instanceof Element) {
                dfs((Element) item);
            }
        }
    }

}
