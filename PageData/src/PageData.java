
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class PageData extends JFrame implements ActionListener, Runnable {

    Thread runner;
    String[] headers = {"Content-Length", "Content-Type", "Date",
        "Public", "Expires", "Last-Modified", "Server"};
    URL page;
    JTextField url;
    JLabel[] headerLabel = new JLabel[7];
    JTextField[] header = new JTextField[7];
    JButton readPage, clearPage, quitLoading;
    JLabel status;

    public PageData() {
        super("Page Data");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLookAndFeel();
        setLayout(new GridLayout(10, 1));
        
        /* First row.  */
        JPanel first = new JPanel();
        first.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel urlLabel = new JLabel("URL:");
        url = new JTextField(22);
        urlLabel.setLabelFor(url);
        first.add(urlLabel);
        first.add(url);
        add(first);
        
        JPanel second = new JPanel();
        second.setLayout(new FlowLayout());
        readPage = new JButton("Read Page");
        clearPage = new JButton("Clear Fields");
        quitLoading = new JButton("Quit Loading");
        readPage.setMnemonic('r');
        clearPage.setMnemonic('c');
        quitLoading.setMnemonic('q');
        // Displays text when hover over one of the three buttons in first row.  
        readPage.setToolTipText("Begin loading the Web Page");
        clearPage.setToolTipText("Clear All Header Fields Below");
        quitLoading.setToolTipText("Clear All Header Fields Below");
        /* At the start, only the readPage option is available because nothing 
        is loaded yet.  */
        readPage.setEnabled(true);
        clearPage.setEnabled(false);
        quitLoading.setEnabled(false);
        readPage.addActionListener(this);
        clearPage.addActionListener(this);
        quitLoading.addActionListener(this);
        second.add(readPage);
        second.add(clearPage);
        second.add(quitLoading);
        add(second);
        
        /* Row #3 to row #9.  */
        JPanel[] row = new JPanel[7];
        for (int i = 0; i < 7; i++) {
            row[i] = new JPanel();
            // Components in the row panel align right.  
            row[i].setLayout(new FlowLayout(FlowLayout.RIGHT));
            /* headerLabel is JLabel, header is String.  This means I set each JLabel 
            element's display text equal to the String element of the corresponding 
            index, then add a ":" at the end.  For example, headerLabel[0] have its 
            display text set to "Content:  Length.  "*/
            headerLabel[i] = new JLabel(headers[i] + ":");
            header[i] = new JTextField(headers[i] + ":");
            /* Set 7 empty JTextField that with each has maximum input of 22 characters.  */
            header[i] = new JTextField(22);
            headerLabel[i].setLabelFor(header[i]);
            /* Row #3 to row #9 each has a JLabel (headerLabel) and a JTextField (header).  */
            row[i].add(headerLabel[i]);
            row[i].add(header[i]);
            /* I added this line below so the text fields aren't editable.  */
            header[i].setEnabled(false);
            add(row[i]);
        }
        
        JPanel last = new JPanel();
        last.setLayout(new FlowLayout(FlowLayout.LEFT));
        status = new JLabel("Enter a URL address to check.");
        last.add(status);
        add(last);
        pack();
        setVisible(true);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == readPage) {
            try {
                // Set the page (URL) equals to whatever is in url (JTextField).  
                page = new URL(url.getText());
                /* If thread currently isn't active (null), then start a new 
                thread and call the start() method.  By calling the start() 
                method, the run() method is called.  */
                if (runner == null) {
                    runner = new Thread(this);
                    runner.start();
                }
                /* Only quitLoading is set to true after this.  */
                quitLoading.setEnabled(true);
                readPage.setEnabled(false);
            }
            /* If URL that I put in isn't valid, status (JLabel) will print an error message.  */
            catch (MalformedURLException mue) {
                status.setText("Bad URL:" + page);
                /* I added these lines below, without these lines, if I have an 
                invalid address, all buttons will be disabled.  */
                readPage.setEnabled(false);
                quitLoading.setEnabled(true);
            }
        }
        // Reset every header array element to empty.  Only setPage set to enable.  
        else if (source == clearPage) {
            for (int i = 0; i < 7; i++) {
                header[i].setText("");
            }
            quitLoading.setEnabled(false);
            readPage.setEnabled(true);
            clearPage.setEnabled(false);
        }
        // Reset every url elemnet to empty.  Only readPage set to enable.  
        else if (source == quitLoading) {
            // Stops the running thread.  
            runner = null;
            // Sets the url (JTextField) to empty.  
            url.setText("");
            quitLoading.setEnabled(false);
            readPage.setEnabled(true);
            clearPage.setEnabled(false);
        }
    }
    
    
    @Override
    public void run() {
        URLConnection conn;
        try {
            conn = this.page.openConnection();
            conn.connect();
            status.setText("Connection opened ...");
            /* Retrieves information from the Internet and writes them down 
            as text for header array ().  */
            for (int i = 0; i < 7; i++) {
                header[i].setText(conn.getHeaderField(headers[i]));
            }
            /* While the thread is running, quitLoading is disabled and clearPage 
            is enabled.  */
            quitLoading.setEnabled(false);
            clearPage.setEnabled(true);
            status.setText("Done");
            runner = null;
        }
        catch (IOException ie) {
            status.setText("IO Error:" + ie.getMessage());
        }
        
    }
    
    
    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } 
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    
    public static void main(String[] args) {
        new PageData();
    }

}
