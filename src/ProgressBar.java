import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

public class ProgressBar extends JFrame implements ActionListener
{
  private JProgressBar jp = new JProgressBar();
  private JButton jb = new JButton("Copy");
  private JButton jc = new JButton("Cancel");
  private JTextField fromFile = new JTextField();
  private JTextField toFile = new JTextField();
  private ProgressBarThread thread;

  public static void main(String[] args)  // creating the frame application
  {
    ProgressBar application = new ProgressBar();
    application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    application.setTitle("Copy file");
    application.setSize(400, 180);
    application.setVisible(true); 
  }

  public ProgressBar()                    // class constructor
  {
    Container pane = getContentPane();    // building the GUI   
    pane.setLayout(new BorderLayout());
    jp.setStringPainted(true);            // display style of the progress bar
    pane.add(jp, BorderLayout.NORTH);

    JPanel panel1 = new JPanel(new BorderLayout());
    panel1.setBorder(new TitledBorder("From:"));
    panel1.add(fromFile, BorderLayout.CENTER);
 
    JPanel panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(new TitledBorder("To:"));
    panel2.add(toFile, BorderLayout.CENTER);
 
    JPanel panel3 = new JPanel(new GridLayout(2,1));
    panel3.add(panel1);
    panel3.add(panel2);
    pane.add(panel3, BorderLayout.CENTER);

    JPanel panel4 = new JPanel();
    panel4.add(jb);
    panel4.add(jc);
    pane.add(panel4, BorderLayout.SOUTH);

    jb.addActionListener(this);
    jc.addActionListener(this);
    jc.setEnabled(false);
  }

  public void actionPerformed(ActionEvent e) // buttons events processing
  {
    if (e.getSource() == jb)
    {
      thread = new ProgressBarThread(this, jp, fromFile, toFile,jb,jc);
      thread.start();
      jb.setEnabled(false);
      jc.setEnabled(true);
    }
    else if (e.getSource() == jc)
    {
      thread.cancel = true;
      jc.setEnabled(false);
    } 
  }
}

class ProgressBarThread extends Thread
{
  private JFrame frame;
  private JProgressBar jp;
  private JTextField fromFile;
  private JTextField toFile;
  public boolean cancel = false;
  JButton jb,jc;

  public ProgressBarThread(JFrame f, JProgressBar jp, JTextField from, JTextField to,JButton jb,JButton jc)
  {
    frame = f;                                // thread constructor
    this.jp = jp;
    fromFile = from;
    toFile = to;
    this.jb = jb;
    this.jc = jc;
  }

  public void run()
  {
    BufferedInputStream in = null;
    BufferedOutputStream out = null;
    try
    {
      File inFile = new File(fromFile.getText().trim());  // input stream
      in = new BufferedInputStream(new FileInputStream(inFile));

      File outFile = new File(toFile.getText().trim());   // output stream
      out =  new BufferedOutputStream(new FileOutputStream(outFile));

      long fileSize = in.available();        // input file size in bytes
      jp.setValue(0);                        // set up the progress bar
      jp.setMaximum(100);                    // indicator

      int r = 0;
      long bytesRead = 0;
      byte[] buffer = new byte[20];           // read/write buffer
      while ((r = in.read(buffer, 0, buffer.length)) != -1)
      {
        out.write(buffer, 0, r);             // write to the file     
        bytesRead += r;
        int copyProgress = (int) (bytesRead*100.0/fileSize);
        jp.setValue(copyProgress);           // update the indicator
        if (cancel) return;                  // kill the thread
      }
    }
    catch(FileNotFoundException e)
    {
      JOptionPane.showMessageDialog(frame, "File not found",
      "Error message", JOptionPane.ERROR_MESSAGE);
    }
    catch(IOException e)
    {
      JOptionPane.showMessageDialog(frame, "Cannot write to file",
      "Error message", JOptionPane.ERROR_MESSAGE);
    }
    finally                                  // close the files
    {
      try
      {
        if (in != null) in.close();
        if (out != null) out.close();
        jb.setEnabled(true);
        jc.setEnabled(false);
		JOptionPane.showMessageDialog(frame, "File Copied Successfully",
      "Error message", JOptionPane.INFORMATION_MESSAGE);
		
      }
      catch(Exception e)
      {
        JOptionPane.showMessageDialog(frame, "I/O error",
        "Error message", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
