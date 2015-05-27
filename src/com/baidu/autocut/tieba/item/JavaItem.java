package com.baidu.autocut.tieba.item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;

import com.baidu.autocut.tieba.util.HashedPoolMananger;
import com.baidu.autocut.tieba.util.UtilHelper;

public class JavaItem extends BaseItem {
    private JavaCommentCleaner mCommentCleaner = new JavaCommentCleaner();

    public JavaItem(String pack) {
        init(pack);
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
        String packageName = "";

        BufferedReader reader = null;
        String path = UtilHelper.getFilePath(key);
        if (path == null) {
            return false;
        }
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = "";
            String temp = "";
            boolean isUse = true;
            while ((temp = reader.readLine()) != null) {
                temp = temp.trim();
                temp = mCommentCleaner.getCleanCode(temp);

                // 统计代码行数 不算import
                if (temp.length() > 1 && !temp.startsWith(UtilHelper.JAVA_IMPORT)) {
                    HashedPoolMananger.line++;
                }
                line += temp;
                if (temp.endsWith(";") || temp.endsWith("{")
                        || temp.endsWith("}")) {
                    // Java Item
//                    System.out.println(line);
                    line = line.trim();
                    if (line.startsWith(UtilHelper.JAVA_PACK)) {// 提取包名
                        packageName = line.substring(line.lastIndexOf(" ") + 1,
                                line.length() - 1) + ".";
                        // System.out.println(packageName);
                    } else if (line.startsWith(UtilHelper.JAVA_IMPORT)) {// 对其它java文件的引用
                        String pack = line.substring(line.lastIndexOf(" ") + 1,
                                line.length() - 1);
                        // System.out.println(pack);
                        JavaItem item = new JavaItem(pack);
                        mImportJavaItem.add(item);

                        if (line.lastIndexOf(".") > 0) {
                            pack = line.substring(line.lastIndexOf(" ") + 1,
                                    line.lastIndexOf("."));
                            // System.out.println(pack);
                            item = new JavaItem(pack);
                            mImportJavaItem.add(item);
                        }
                    } else {

                        // Java Item 处理同一包下文件的引用关系
                        Matcher blockMatcher = UtilHelper.mJavaBlock
                                .matcher(line);
                        while (blockMatcher.find()) {
                            String block = blockMatcher.group();
                            block = packageName + block;

                            if (HashedPoolMananger.checkHashedJavaKey(block) == false
                                    && HashedPoolMananger.checkCodeFile(block) == true) {
                                // System.out.println(block);
                                JavaItem item = new JavaItem(block);
                                mImportJavaItem.add(item);
                            }
                        }

                        Matcher packageMatcher = UtilHelper.mJavaPackBlock
                                .matcher(line);
                        while (packageMatcher.find()) {
                            String block = packageMatcher.group();
                            if (block != null && block.contains(".")) {
                                //System.out.println(block);
                                checkImport(block);
                            }
                        }

                        // Xml Item
                        Matcher xmlMatcher = UtilHelper.mJavaResPattern
                                .matcher(line);
                        while (xmlMatcher.find()) {
                            String group = xmlMatcher.group();
                            String xmlName = group.substring(group
                                    .lastIndexOf(".") + 1);
//                            System.out.println(xmlName);
                            if (HashedPoolMananger.checkCodeFile(xmlName) == true) {
                                XmlItem item = new XmlItem(xmlName);
                                mImportXmlItem.add(item);
                                // System.out.println(xmlName);
                            }
                            if (HashedPoolMananger.checkImageFile(xmlName)) {
                                mImportImageItem.add(xmlName);
                                // System.out.println(xmlName);
                            }
                            if (!xmlName.endsWith("_1")) {
                                xmlName = xmlName + "_1";
                                if (HashedPoolMananger.checkCodeFile(xmlName) == true) {
                                    XmlItem item = new XmlItem(xmlName);
                                    mImportXmlItem.add(item);
                                    // System.out.println(xmlName);
                                }
                                if (HashedPoolMananger.checkImageFile(xmlName)) {
                                    mImportImageItem.add(xmlName);
                                    // System.out.println(xmlName);
                                }
                            }
                        }

                        // String Item
                        Matcher stringMatcher = UtilHelper.mJavaStringPattern
                                .matcher(line);
                        while (stringMatcher.find()) {
                            String group = stringMatcher.group();
                            String stringName = group.substring(group
                                    .lastIndexOf(".") + 1);
                            HashedPoolMananger.putHashedStringKey(stringName);
                        }

                    }

                    line = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void checkImport(String packageName) {
        int start = 0;
        int count = 0;
        while (count >= 0) {
            count = packageName.indexOf(".", start);
            String pack = packageName;
            if (count >= 0) {
                pack = packageName.substring(0, count);
                start = count + 1;
            }
            if (HashedPoolMananger.checkHashedJavaKey(pack) == false
                    && HashedPoolMananger.checkCodeFile(pack) == true) {
                //System.out.println("+"+pack);
                JavaItem item = new JavaItem(pack);
                mImportJavaItem.add(item);
            }

        }
    }

    private static class JavaCommentCleaner {
        private boolean isCommon = false;
        private boolean isString = false;
        private StringBuffer mStringBuffer = new StringBuffer(100);


        public String getCleanCode(String line) {
            mStringBuffer.setLength(0);
            char mPreChar = ' ';
            for (int i = 0; i < line.length(); i++) {

                char currentChar = line.charAt(i);
                if(isString){
                    mStringBuffer.append(currentChar);
                    if(currentChar=='"'){
                        isString=false;
                    }
                }else if(isCommon){
                    if(currentChar=='/'){
                        if(mPreChar=='*'){
                            isCommon = false;
                            currentChar = ' ';
                        }
                    }
                }else{
                    if(currentChar=='"'){
                        isString = true;
                        mStringBuffer.append(currentChar);
                    }else if(currentChar=='/'){
                        if(mPreChar=='/'){
                            break;
                        }else{
//                            mStringBuffer.append(mPreChar);
//                            mStringBuffer.append(currentChar);
                        }

                    }else if(currentChar=='*'){
                        if(mPreChar=='/'){
                            isCommon = true;
                        }else{
                            mStringBuffer.append(currentChar);
                        }
                    }else{
                        if(mPreChar=='/'){
                            mStringBuffer.append(mPreChar);
                        }
                        mStringBuffer.append(currentChar);
                    }
                }
                mPreChar = currentChar;
            }
            return mStringBuffer.toString();
        }
    }

}
