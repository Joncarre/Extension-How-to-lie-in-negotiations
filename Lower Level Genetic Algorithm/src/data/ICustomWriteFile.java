package data;

public interface ICustomWriteFile {
	public abstract void closeWriteFile(CustomWriteFile file);
	public abstract void writeVector(CustomWriteFile file, String text);
}
