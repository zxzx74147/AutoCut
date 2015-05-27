package com.baidu.autocut.tieba.item;

import java.util.LinkedList;

public abstract class BaseItem {

	protected String key = null;
	protected LinkedList<JavaItem> mImportJavaItem = null;
	protected LinkedList<XmlItem> mImportXmlItem = null;
	protected LinkedList<String> mImportImageItem = null;

	public abstract boolean init(String file);

	public abstract String getKey();

	public abstract LinkedList<JavaItem> getJavaItem();

	public abstract LinkedList<XmlItem> getXmlItem();

	public abstract LinkedList<String> getImageItem();

	protected abstract boolean scanfSelf();
}
