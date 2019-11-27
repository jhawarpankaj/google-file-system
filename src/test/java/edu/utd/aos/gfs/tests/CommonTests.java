package edu.utd.aos.gfs.tests;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class CommonTests {

	public static void main(String[] args) {
//		list();
//		file();
//		stringIndex();
//		Integer a = null;
//		a++;
	}

	private static void stringIndex() {
		String s = "chunk1";
		System.out.println(s.substring(s.length() - 1));
		
	}

	private static void list() {
		List<String> l = Arrays.asList("A", "B");
		System.out.println(l);
		
	}

	private static void file() {
		String dir = "/home/pankaj/Desktop/UTD-Semesters/19Fall/AOS/Assignment/A3"
				+ "/code/google-file-system/src/main/java/edu/utd/aos/gfs";
		
		ArrayList<File> directories = new ArrayList<File>(
		    Arrays.asList(
		        new File(dir).listFiles(File::isDirectory)
		    )
		);
		
		for(File f: directories) {
			f.getAbsolutePath();
//			System.out.println(f.getName());
		
		
		
		ArrayList<File> files = new ArrayList<File>(
			    Arrays.asList(
			        new File(dir + "/" + f.getName()).listFiles(File::isFile)
			    )
			);
		
		System.out.println("Dir:" + f.getName());
		for(File f1: files) {
			System.out.println("File:" + f1.getName());
		}
		break;
		}
		
	}

}
