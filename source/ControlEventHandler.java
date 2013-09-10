import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class ControlEventHandler implements ActionListener
{
	private static JFileChooser selectfile;
	private TextArea textArea=null;
	private JDecaf frame=null;
	
	public ControlEventHandler(JDecaf frame, TextArea textArea)
	{
		this.frame=frame;
		this.textArea=textArea;
	}
	
	static
	{
		initFileChooser();
	}
	
	private static void initFileChooser()
	{
		selectfile = new JFileChooser(".");
		selectfile.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter filter = new ExtensionFileFilter("*.java,*.jdc", new String[] {"JAVA","JDC"});
		selectfile.setFileFilter(filter);
	}
	
	public static File LoadDialog()
	{		
		File f = null;
		int returnVal = selectfile.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			f = selectfile.getSelectedFile();
		}

		return f;
	}

	public static File SaveDialog()
	{
		File f = null;
		int returnVal = selectfile.showSaveDialog(null);

		if (returnVal==JFileChooser.APPROVE_OPTION)
		{
			f = selectfile.getSelectedFile();
		}
		
		String fileName = f.toString();	
		//System.out.println(fileName);
		
		if (!(fileName.endsWith(".java")||fileName.endsWith(".jdc")))
		{
			fileName += ".java";
			f = new File(fileName);
		}		

		return f;
	}
	
	public void actionPerformed(ActionEvent event)
	{	
		JButton button=(JButton)event.getSource();
		
		if(button.getText().equals("Open"))
		{
			if(textArea.getContentChanged())
			{
				if(textArea.fileExists())
				{
					System.out.println("a file is already open!");
	
					int response = JOptionPane.showConfirmDialog(null,"a file is already open!\nwould you like to save and close it before opening the new one?","File Open Warning",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
					
					if(response==JOptionPane.YES_OPTION)
					{
						if(save(textArea.getFile()))
						{
							close();
						}
						else
						{
							return;
						}
					}
					else if(response==JOptionPane.CANCEL_OPTION)
					{
						return;									
					}
				}
				else
				{
					int response = JOptionPane.showConfirmDialog(null,"there are unsaved changes!\nwould you like to save them to a file before opening the new one?","File Open Warning",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);

					if(response==JOptionPane.CANCEL_OPTION)
					{
						return;
					}
					
					if(response==JOptionPane.YES_OPTION)
					{
						File file = SaveDialog();
						
						if(save(file))
						{
							close();
							JOptionPane.showMessageDialog(null,"changes saved!\nnow open the new file","info",JOptionPane.INFORMATION_MESSAGE);
						}
						else
						{
							return;
						}
					}
				}
			}
			
			File file = LoadDialog();
			
			if(file==null)
			{
				JOptionPane.showMessageDialog(null,"no file selected for opening!","warning",JOptionPane.WARNING_MESSAGE);
				System.out.println("no file selected for opening!");
				return;
			}
			
			load(file);
		}
		
		if(button.getText().equals("Save"))
		{
			if(textArea.getContentChanged()==false)
			{
		        JOptionPane.showMessageDialog(null, "There are no changes to be saved: ", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			File file = null;
			
			if(textArea.fileExists())
			{
				file=textArea.getFile();
			}
			else
			{
				file = SaveDialog();
			}
			
			save(file);
		}
		
		if(button.getText().equals("New"))
		{
			//if(textArea.fileExists()==false)
			//{
		    //      JOptionPane.showMessageDialog(null, "The current document does not correspond to a file on disc\nand therefore cannot be closed", "Error", JOptionPane.ERROR_MESSAGE);				
			//}
			
			//close current document
			close();
		}

		if(button.getText().equals("Exit"))
		{
			if(close()==false)
			{
				return;
			}
			
			System.exit(0);
		}
	}

	private boolean load(File file)
	{
		try
		{
		      if (file.isDirectory())
		      { // Clicking on a space character
		          JOptionPane.showMessageDialog(null, file.getAbsolutePath() + " is a directory", "Error", JOptionPane.ERROR_MESSAGE);
		          return false;
		      }
		      else if (!file.isFile())
		      {
		          JOptionPane.showMessageDialog(null, "No such file: " + file.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
		          return false;
		      }

	          BufferedReader r = new BufferedReader(new FileReader(file));
	          textArea.read(r, null);
	          textArea.setFile(file);
	          textArea.setContentChanged(false);
	          r.close();

	          frame.setAppTitle("");
	          frame.repaint();

	          return true;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,"cannot read from selected file","warning",JOptionPane.WARNING_MESSAGE);
			System.out.println("cannot read file");
			return false;
		}		
	}

	private boolean save(File file)
	{
		if(file==null)
		{
			JOptionPane.showMessageDialog(null,"no file selected for saving!","warning",JOptionPane.WARNING_MESSAGE);
			System.out.println("no file selected for saving!");
			return false;
		}
		
		try
		{
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			textArea.write(w);
            w.close();
            
            textArea.setFile(file);
			textArea.setContentChanged(false);
			frame.setAppTitle("");
			return true;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,"cannot write to selected file","warning",JOptionPane.WARNING_MESSAGE);
			System.out.println("cannot write file");
			return false;
		}
	}
	
	private boolean close()
	{
		if(textArea.getContentChanged()==true)
		{
			int response = JOptionPane.showConfirmDialog(null,"there are unsaved changes!\nwould you like to save before you close the document?","File Close Warning",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
			
			if(response==JOptionPane.CANCEL_OPTION)
			{
				return false;
			}

			if(response==JOptionPane.YES_OPTION)
			{
				File file = null;
				
				if(textArea.fileExists())
				{
					file=textArea.getFile();
				}
				else
				{
					file = SaveDialog();
				}
				
				if(save(file)==false)
				{
					return false;
				}
			}
		}

		textArea.setText("");
		textArea.setFile(null);
		textArea.setContentChanged(false);
		frame.setAppTitle("");
		return true;
	}
}

class ExtensionFileFilter extends FileFilter
{
	  String description;

	  String extensions[];

	  public ExtensionFileFilter(String description, String extension)
	  {
	    this(description, new String[] { extension });
	  }

	  public ExtensionFileFilter(String description, String extensions[])
	  {
	    if (description == null)
	    {
	      this.description = extensions[0];
	    }
	    else
	    {
	      this.description = description;
	    }
	    this.extensions = (String[]) extensions.clone();
	    toLower(this.extensions);
	  }

	  private void toLower(String array[])
	  {
	    for (int i = 0, n = array.length; i < n; i++)
	    {
	      array[i] = array[i].toLowerCase();
	    }
	  }

	  public String getDescription()
	  {
	    return description;
	  }

	  public boolean accept(File file)
	  {
	    if (file.isDirectory())
	    {
	      return true;
	    }
	    else
	    {
	      String path = file.getAbsolutePath().toLowerCase();
	      for (int i = 0, n = extensions.length; i < n; i++)
	      {
	        String extension = extensions[i];
	        if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.'))
	        {
	          return true;
	        }
	      }
	    }
	    return false;
	  }
	}