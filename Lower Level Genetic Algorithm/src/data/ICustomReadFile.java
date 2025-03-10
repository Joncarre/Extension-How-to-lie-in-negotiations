package data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public interface ICustomReadFile {
	public abstract void closeReadFile(CustomReadFile file);
	public abstract Vector<Double> readVector(Scanner in) throws IOException;
	public abstract int readDouble(Scanner in);
	public abstract int[] readArray(Scanner in, int sizeArray);
	int readInt(Scanner in);
}
