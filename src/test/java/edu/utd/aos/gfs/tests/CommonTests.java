package edu.utd.aos.gfs.tests;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class CommonTests {

	public static void main(String[] args) throws IOException {
//		list();
//		file();
//		stringIndex();
//		Integer a = null;
//		a++;
//		String s = "rtrt,ddd,ddsd,";
//		String[] split = s.split(",");
//		System.out.println(split.length);
//		for(int i = 0; i < split.length; i++) {
//			System.out.println(split[i]);
//		}
//		String filename = "/home/pankaj/Desktop/UTD-Semesters/19Fall/ML/Assignment/Final_Project/submission/readme.txt";
		String filename = "/home/pankaj/Desktop/UTD-Semesters/baljeet.txt";
		
		
//		byte [] bytes = ByteBuffer.allocate(4).putInt(0).array();		
		FileOutputStream out = new FileOutputStream(filename);
		char c = '1';
//		Integer a = 0;
		byte b = (byte) c;
		out.write(b);		 
		out.close();
		
		
//		File file = new File(filename);	
//		
//		File filePad = new File(filename);
//		byte[] bArrayPad = new byte[(int) filePad.length()];
//		FileInputStream fisPad = new FileInputStream(filePad);
//		fisPad.read(bArrayPad);
//		
//		byte[] writeArray = new byte[4096];
//		int i = 0;
//		for(; i < bArrayPad.length; i++) {
//			writeArray[i] = bArrayPad[i];
//		}
//		char c = '\0';
//		for(; i < 4096 - bArrayPad.length; i++) {
//			writeArray[i] = (byte) c;
//		}
//		String filename2 = "/home/pankaj/Desktop/UTD-Semesters/baljeet2.txt";
//		
//		OutputStream os = new FileOutputStream(filename2); 
//		
//		os.write(writeArray);
//		os.close();
//		
//		File fileR = new File(filename2);
//		System.out.println(fileR.length());
//		byte[] bArray = new byte[(int) fileR.length()];
//		FileInputStream fis = new FileInputStream(fileR);
//		fis.read(bArray);
//		fis.close();
//		
//		FileUtils.writeByteArrayToFile(file, writeArray);
//		
//		String content = "";
//		for (int k = 1; k < bArray.length; k++) {
//			content = content + (char) bArray[k];
//		}	
//		System.out.println(content);
		
//		fisPad.close();
//		System.out.println(file.canRead());
//		System.out.println(file.getTotalSpace());
//		
//		
//		RandomAccessFile raf = new RandomAccessFile(file, "rw");
//		raf.writeChar('c');
//		raf.close();
//		System.out.println("" + raf.readUnsignedShort());
//		System.out.println(raf.length());
//		raf.seek(0);
//		System.out.println("------------");
//		String content = raf.readUTF();
//		
//		System.out.println("" + content);
//		raf.close();
//		FileInputStream fis = null;
//		byte[] bs = new byte[15];
//		int i = 0;
//	    char c;
//		fis = new FileInputStream(filename);
//		i = fis.read(bs, 1, 5);
//		System.out.println("Number of bytes read: "+i);
//         for(byte b:bs) {
//            c = (char)b;
//            System.out.print(c);
//         } 
		
		
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
