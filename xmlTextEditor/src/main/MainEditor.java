package main;
//Why isnt the stuff in the trunk


/*
 * COPYRIGHTS
 * 
 * Classes:
 *  TextComponentDemo.java
 *  DocumentSizeFilter.java
 *  taken from http://java.sun.com/docs/books/tutorial/uiswing/examples/components/index.html#TextComponentDemo
 *   
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

/*
 * TextComponentDemo.java requires one additional file:
 *   DocumentSizeFilter.java
 *   
 *   Classes:
 *   DemoMain.java
 *   XMLTreeModel.java
 *   XMLTreeNode.java
 *   XMLTreePanel.java
 *   taken from Rob Lybarger's tutorial Displaying XML in a Swing JTree
 *   http://www.developer.com/java/other/article.php/10936_3731356_2/Displaying-XML-in-a-Swing-JTree.htm
 *   
 *   Save File and some other features taken from Turk4n @
 *   http://forum.codecall.net/search.php?searchid=111779
 *   
 *   extended by fabiantheblind @ http://www.the-moron.net as XMLEditor 
 *   for vFFF (Verlag f�r Formforschung)
 */

import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import java.io.File;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import main.XMLTreePanel;

import org.w3c.dom.Document;



public class MainEditor extends JFrame {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 8696036037826998223L;
	
	
	
	JTextPane textPane;
    JTextPane xmlPane;
    JFrame bookPane;
    JTextPane footNotePane;
    JTextArea helpPane;

    
    AbstractDocument doc;
    
//    What does the Max Charackter?
//    static final int MAX_CHARACTERS = 500;
    JTextArea changeLog;
    String newline = "\n";
    HashMap<Object, Action> actions;
	private String currentFile = "myUntitled.xml";
	private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
	private boolean changed = false;



    final String initString[] =
    { "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
	"<!-- Du hast folgende Elemente zur Verfügung:",
      "<h1></h1>","<h2></h2>","<h3></h3>","<it></it>","<bld></bld>",
      "<it> steht für Italic <bld> für Bold",
      "Können innerhalb von <titel>, <body> oder<foot>",
      "<titel></titel>",
      "<body></body>",
      "<foot></foot>",
      "Mit dem element <Root> wird ein neuer Artikel begonnen",
      "Use Ctrl-z to undo changes  Ctrl-c to copy Ctrl-v to paste  -->" };

    //undo helpers
    protected UndoAction undoAction;
    protected RedoAction redoAction;
    protected UndoManager undo = new UndoManager();

    public MainEditor() {
        super("myWindowName");

        //Create the text pane and configure it.
      
        textPane = new JTextPane();
        xmlPane = new JTextPane();
        helpPane = new JTextArea();
        textPane.setCaretPosition(0);
        textPane.setName("myTextPaneName");
        textPane.setMargin(new Insets(5,5,5,5));
        
        final StyledDocument styledDoc = textPane.getStyledDocument();
        if (styledDoc instanceof AbstractDocument) {
            doc = (AbstractDocument)styledDoc;
//            doc.setDocumentFilter(new DocumentSizeFilter(MAX_CHARACTERS));
        } else {
            System.err.println("Text pane's document isn't an AbstractDocument!");
            System.exit(-1);
        }
        final JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(600, 700));

        //Create the text area for the status log and configure it. // or  Set also the footnotepane
        changeLog = new JTextArea(5, 30);
        footNotePane = new JTextPane();
             
        changeLog.setEditable(false);
        footNotePane.setEditable(true);

        final JScrollPane scrollPaneForLog = new JScrollPane(changeLog);
//        final JScrollPane scrollPaneForNotes = new JScrollPane(footNotePane);

        
		//create the tree pane
        Document xmlDocument = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
			xmlDocument = builder.parse(new File("./data/basicTree.xml"));
			xmlDocument.normalize();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
        
        // Create the Tabpane
        XMLTreePanel treePanel = new XMLTreePanel();
        treePanel.setDocument(xmlDocument);
		getContentPane().add(treePanel, "Center");
	
        final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("chapter",scrollPane);
		tabbedPane.addTab("footnotes",footNotePane);
		tabbedPane.addTab("book",treePanel);
        tabbedPane.addTab("xml",xmlPane);
        tabbedPane.addTab("help",helpPane);



		
		

        
//        Create a split pane for the change log and the text area.
//        final JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, tabbedPane, scrollPaneForLog);
//        splitPane.setOneTouchExpandable(true);
//        splitPane.setDividerLocation(600);

        //Create the status area.
        final JPanel statusPane = new JPanel(new GridLayout(1, 1));
        final CaretListenerLabel caretListenerLabel = new CaretListenerLabel("Caret Status");
        statusPane.add(caretListenerLabel);

        //Add the components.
       getContentPane().add(tabbedPane, BorderLayout.CENTER);
//        getContentPane().add(splitPane, BorderLayout.CENTER);
//        getContentPane().add(scrollPaneForLog, BorderLayout.SOUTH);
        getContentPane().add(statusPane, BorderLayout.PAGE_END );

        

        //Set up the menu bar.
        actions=createActionTable(textPane);
        final JMenu fileMenu = createFileMenu();
        final JMenu editMenu = createEditMenu();
        final JMenu uploadMenu = createUploadMenu();
        final JMenu styleMenu = createStyleMenu();
        final JMenuBar mb = new JMenuBar();
        mb.add(fileMenu);
        mb.add(editMenu);
        mb.add(uploadMenu);
        mb.add(styleMenu);
        
        setJMenuBar(mb);

        //Add some key bindings.
        addBindings();

        //Put the initial text into the text pane.
        initDocument();
        textPane.setCaretPosition(0);

        //Start watching for undoable edits and caret changes.
        doc.addUndoableEditListener(new MyUndoableEditListener());
        textPane.addCaretListener(caretListenerLabel);
        doc.addDocumentListener(new MyDocumentListener());
    }




    //This listens for and reports caret movements.
    protected class CaretListenerLabel extends JLabel
                                       implements CaretListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 9167645218151949352L;

		public CaretListenerLabel(final String label) {
            super(label);
        }

        //Might not be invoked from the event dispatch thread.
        public void caretUpdate(final CaretEvent e) {
            displaySelectionInfo(e.getDot(), e.getMark());
        }

        //This method can be invoked from any thread.  It 
        //invokes the setText and modelToView methods, which 
        //must run on the event dispatch thread. We use
        //invokeLater to schedule the code for execution
        //on the event dispatch thread.
        protected void displaySelectionInfo(final int dot,
                                            final int mark) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (dot == mark) {  // no selection
                        try {
                            final Rectangle caretCoords = textPane.modelToView(dot);
                            //Convert it to view coordinates.
                            setText("caret: text position: " + dot
                                    + ", view location = ["
                                    + caretCoords.x + ", "
                                    + caretCoords.y + "]"
                                    + newline);
                        } catch (final BadLocationException ble) {
                            setText("caret: text position: " + dot + newline);
                        }
                    } else if (dot < mark) {
                        setText("selection from: " + dot
                                + " to " + mark + newline);
                    } else {
                        setText("selection from: " + mark
                                + " to " + dot + newline);
                    }
                }
            });
        }
    }

    //This one listens for edits that can be undone.
    protected class MyUndoableEditListener
                    implements UndoableEditListener {
        public void undoableEditHappened(final UndoableEditEvent e) {
            //Remember the edit and update the menus.
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
        }
    }

    //And this one listens for any changes to the document.
    protected class MyDocumentListener
                    implements DocumentListener {
        public void insertUpdate(final DocumentEvent e) {
            displayEditInfo(e);
        }
        public void removeUpdate(final DocumentEvent e) {
            displayEditInfo(e);
        }
        public void changedUpdate(final DocumentEvent e) {
            displayEditInfo(e);
        }
        private void displayEditInfo(final DocumentEvent e) {
            final javax.swing.text.Document document = e.getDocument();
            final int changeLength = e.getLength();
            changeLog.append(e.getType().toString() + ": " +
                changeLength + " character" +
                ((changeLength == 1) ? ". " : "s. ") +
                " Text length = " + document.getLength() +
                "." + newline);
        }
    }

    //Add a couple of emacs key bindings for navigation.
    protected void addBindings() {
        final InputMap inputMap = textPane.getInputMap();

        //Ctrl-b to go backward one character
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.backwardAction);

        //Ctrl-f to go forward one character
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.forwardAction);

        //Ctrl-p to go up one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.upAction);

        //Ctrl-n to go down one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.downAction);
        
        //Ctrl-z to go back
        key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
        inputMap.put(key,undoAction); 
        //Ctrl-v to paste
        key = KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK);
        inputMap.put(key,DefaultEditorKit.pasteAction);
        //Ctrl-x for Cut
        key = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK);
        inputMap.put(key,DefaultEditorKit.cutAction);
        //Ctrl-c for copy
        key = KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK);
        inputMap.put(key,DefaultEditorKit.copyAction);

        Action h1Action = new StyledEditorKit.FontSizeAction("H1",23);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK);
        inputMap.put(key,h1Action);
        
        
        Action h2Action =new StyledEditorKit.FontSizeAction("H2",18);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK);
        inputMap.put(key,h2Action);
        
        Action h3Action = new StyledEditorKit.FontSizeAction("H3",16);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK);
        inputMap.put(key,h3Action);
        
        Action txtAction = new StyledEditorKit.FontSizeAction("TXT",14);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK);
        inputMap.put(key,txtAction);
        
        Action itAction = new StyledEditorKit.ItalicAction();
        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
        inputMap.put(key,itAction);
        
        Action noteAction = new StyledEditorKit.FontSizeAction("NOTE",10);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        inputMap.put(key,noteAction);
        
        
        
    }
   
    
    //Create the File menu.
   
    protected JMenu createFileMenu() {
        final JMenu menu = new JMenu("File");
       
        menu.add(New);
        menu.add(Open);
        menu.add(Save);
        menu.add(SaveAs);
        menu.add(Quit);
        
        
        
        //Undo and redo are actions of our own creation.
 

        //These actions come from the default editor kit.
        //Get the ones we want and stick them in the menu.
        menu.add(getActionByName(DefaultEditorKit.copyAction));
        menu.add(getActionByName(DefaultEditorKit.pasteAction));
        menu.addSeparator();

        menu.add(getActionByName(DefaultEditorKit.selectAllAction));
        return menu;
    }
    
    //Create the edit menu.
    protected JMenu createEditMenu() {
        final JMenu menu = new JMenu("Edit");

        //Undo and redo are actions of our own creation.
        undoAction = new UndoAction();
        menu.add(undoAction);
        

        redoAction = new RedoAction();
        menu.add(redoAction);

        menu.addSeparator();

        //These actions come from the default editor kit.
        //Get the ones we want and stick them in the menu.
        menu.add(getActionByName(DefaultEditorKit.cutAction));
        menu.add(getActionByName(DefaultEditorKit.copyAction));
        menu.add(getActionByName(DefaultEditorKit.pasteAction));
        menu.addSeparator();

        menu.add(getActionByName(DefaultEditorKit.selectAllAction));
        return menu;
    }

    protected JMenu createUploadMenu() {
        final JMenu menu = new JMenu("Upload");
        menu.add(new JLabel("<html>--Sorry<p/>--this doesent work right now--</html>"));

        return menu;
    }
    //Create the style menu.
    protected JMenu createStyleMenu() {
        final JMenu menu = new JMenu("Style");

        Action action = new StyledEditorKit.BoldAction();
//        action.putValue(Action.NAME, "Bold");
//        menu.add(action);

        action = new StyledEditorKit.ItalicAction();
        action.putValue(Action.NAME, "Italic");
        menu.add(action);

//        action = new StyledEditorKit.UnderlineAction();
//        action.putValue(Action.NAME, "Underline");
//        menu.add(action);

        menu.addSeparator();
        menu.add(new JLabel("--Formate"));
        
//        StyledTextAction italic = new StyledEditorKit.ItalicAction();

        
     
        menu.add(new StyledEditorKit.FontSizeAction("H1", 23));
        menu.add(new StyledEditorKit.FontSizeAction("H2", 18));
        menu.add(new StyledEditorKit.FontSizeAction("H3", 15));
        menu.add(new StyledEditorKit.FontSizeAction("Text", 14));
//        menu.add(new StyledEditorKit.FontSizeAction("Footnote", 12));

        
//        menu.add(italic);


//        menu.addSeparator();
//
//        menu.add(new StyledEditorKit.FontFamilyAction("Serif",
//                                                      "Serif"));
//        menu.add(new StyledEditorKit.FontFamilyAction("SansSerif",
//                                                      "SansSerif"));

//        menu.addSeparator();
//
//        menu.add(new StyledEditorKit.ForegroundAction("Red",
//                                                      Color.red));
//        menu.add(new StyledEditorKit.ForegroundAction("Green",
//                                                      Color.green));
//        menu.add(new StyledEditorKit.ForegroundAction("Blue",
//                                                      Color.blue));
//        menu.add(new StyledEditorKit.ForegroundAction("Black",
//                                                      Color.black));

        return menu;
    }

   
	Action New = new AbstractAction("New") {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3477966760179323043L;

		public void actionPerformed(ActionEvent e) {
			saveOld();
			textPane.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Root>\n<titel></titel>\n<text></text>\n<zeile></zeile>\n</Root>");
			currentFile = "Untitled";
			setTitle(currentFile);
			changed = false;
			Save.setEnabled(false);
			SaveAs.setEnabled(false);
		}
	};
	
	Action Open = new AbstractAction("Open") {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8020285724388267648L;

		public void actionPerformed(ActionEvent e) {
			saveOld();
			if(dialog.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				readInFile(dialog.getSelectedFile().getAbsolutePath());
			}
			SaveAs.setEnabled(true);
		}
	};
	Action Save = new AbstractAction("Save") {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1667670692306741779L;

		public void actionPerformed(ActionEvent e) {
			if(!currentFile.equals("myUntitled.xml"))
				saveFile(currentFile);
			else
				saveFileAs();
		}
	};
	
	Action SaveAs = new AbstractAction("Save as...") {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6851303130456893976L;

		public void actionPerformed(ActionEvent e) {
			saveFileAs();
		}
	};
	private void saveFileAs() {
		if(dialog.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
			saveFile(dialog.getSelectedFile().getAbsolutePath());
	}
	
	private void readInFile(String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			textPane.read(r,null);
			r.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
		}
		catch(IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(this,"Editor can't find the file called "+fileName);
		}
	}
	
	Action Quit = new AbstractAction("Quit") {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2979456294561723930L;

		public void actionPerformed(ActionEvent e) {
			saveOld();
			System.exit(0);
		}
	};
	private void saveFile(String fileName) {
		try {
			FileWriter w = new FileWriter(fileName);
			textPane.write(w);
			w.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
			Save.setEnabled(false);
		}
		catch(IOException e) {
		}
	}
	private void saveOld() {
		if( changed == true) {
			if(JOptionPane.showConfirmDialog(this, "Would you like to save "+ currentFile +" ?","Save",JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION)
				saveFile(currentFile);
		}
	}
    protected void initDocument() {


        final SimpleAttributeSet[] attrs = initAttributes(initString.length);

        try {
            for (int i = 0; i < initString.length; i ++) {
                doc.insertString(doc.getLength(), initString[i] + newline,
                        attrs[3]);
            }
        } catch (final BadLocationException ble) {
            System.err.println("Couldn't insert initial text.");
        }
    }

    protected SimpleAttributeSet[] initAttributes(final int length) {
        //Hard-code some attributes.
        final SimpleAttributeSet[] attrs = new SimpleAttributeSet[length];

//H1
        attrs[0] = new SimpleAttributeSet();
//        StyleConstants
        StyleConstants.setFontFamily(attrs[0], "Sans-Serif");
        StyleConstants.setFontSize(attrs[0], 23);

//H2
        attrs[1] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[1], 18);
//      StyleConstants.setBold(attrs[1], true);
//H3
        attrs[2] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[2], 15);
        
        
//Text
        attrs[3] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[3], 14);
//italic
        attrs[4] = new SimpleAttributeSet(attrs[0]);
//        StyleConstants.setFontFamily(attrs[0], "SansSerif");
        StyleConstants.setFontSize(attrs[4], 14);
        StyleConstants.setItalic(attrs[4], true);

//footnotes
        attrs[5] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[5], 14);
//        StyleConstants.setForeground(attrs[5], Color.red);

        return attrs;
    }

    //The following two methods allow us to find an
    //action provided by the editor kit by its name.
    private HashMap<Object, Action> createActionTable(final JTextComponent textComponent) {
        final HashMap<Object, Action> actions = new HashMap<Object, Action>();
        final Action[] actionsArray = textComponent.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            final Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
	return actions;
    }

    
    private Action getActionByName(final String name) {
        return actions.get(name);
    }

    class UndoAction extends AbstractAction {
        /**
		 * 
		 */
		private static final long serialVersionUID = 403466730986623484L;

		public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(final ActionEvent e) {
            try {
                undo.undo();
            } catch (final CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();
        }

        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
                changed=true;
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    class RedoAction extends AbstractAction {
        /**
		 * 
		 */
		private static final long serialVersionUID = -1819348371159106262L;

		public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(final ActionEvent e) {
            try {
                undo.redo();
            } catch (final CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }

        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        final MainEditor frame = new MainEditor();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    //The standard main method.
    public static void main(final String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
	        UIManager.put("swing.boldMetal", Boolean.FALSE);
		createAndShowGUI();
            }
        });
    }
}

