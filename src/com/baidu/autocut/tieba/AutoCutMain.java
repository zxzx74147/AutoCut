package com.baidu.autocut.tieba;

import com.baidu.autocut.tieba.config.Config;
import com.baidu.autocut.tieba.item.JavaItem;
import com.baidu.autocut.tieba.processor.CodeCleaner;
import com.baidu.autocut.tieba.util.HashedPoolMananger;

public class AutoCutMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Config.init();
		HashedPoolMananger.init();
		CodeCleaner mCleaner = new CodeCleaner();
		mCleaner.readConfig();
		mCleaner.scanf();
		mCleaner.clean();
		HashedPoolMananger.printAll();

	}

}
