package com.mnn.society.md3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.mnn.society.server.utils.Logger;

public final class TextFile {
	private TextFile() {
    
	}
    
	public final static BufferedReader openFile(String fileName) {
		BufferedReader reader = null;
		File fp = new File(fileName);
		Logger.log(Logger.LOG_CLIENT, "CLIENT : TextFile : openFile : " + fp.getAbsolutePath());
		
		if (fp.exists()) {
			try {
				reader = new BufferedReader(new FileReader(fp));
			} catch(FileNotFoundException f) {
				f.getMessage();
			}
		}
		return reader;
	}
}