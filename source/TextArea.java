import java.io.File;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


public class TextArea extends RSyntaxTextArea
{
	private static final long serialVersionUID = 1L;
	private File file=null;
	private boolean contentChanged;
	
	public TextArea(int rows,int cols)
	{
		super(rows,cols);
		contentChanged=false;
	}

	public void setContentChanged(boolean contentChanged)
	{
		this.contentChanged=contentChanged;
	}

	public boolean getContentChanged()
	{
		return contentChanged;
	}

	public void setFile(File file)
	{
		this.file=file;
	}
	
	public File getFile()
	{
		return file;
	}
	
	public boolean fileExists()
	{
		return file!=null;
	}
}
