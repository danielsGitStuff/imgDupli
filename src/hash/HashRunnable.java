package hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import data.ConcurrentResultMap;
import interfaces.IDuplicateSearchListener;
import tools.Hash;

public class HashRunnable implements Runnable {
	private final ConcurrentResultMap resultMap;
	private final List<File> files;

	public HashRunnable(List<File> files, ConcurrentResultMap resultMap) {
		this.files = files;
		this.resultMap = resultMap;
	}

	@Override
	public void run() {
		try {
			for (File file : files) {
				String hash = Hash.md5(file);
				// System.out.println("put");
				resultMap.put(hash, file);
				// System.out.println("put.done");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("hashrunnable.finish :) " + files.size());
	}

}
