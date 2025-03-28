package files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Scanner;

public class CustomWriteFile extends FileWriter implements ICustomWriteFile{
	
	/**
	 * File-named construction company
	 * @param name
	 * @throws IOException 
	 */
	public CustomWriteFile(String name) throws IOException {
		super(new java.io.File( "." ).getCanonicalPath() + "/src/data/file/" + name);
	}
	
	@Override
	public void writeVector(CustomWriteFile file, String text) {
		try {
			file.write(text + " \n");
		} catch (IOException e) {
			System.out.println("Error - writeVector: " + e.getMessage());
		}
	}
	
	@Override
	public void closeWriteFile(CustomWriteFile file) {
		try {
			file.close();
		} catch (IOException e) {
			System.out.println("Error - closeFileWrite: " + e.getMessage());
		}
	}
}
