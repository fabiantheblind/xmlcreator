//package main;
//
//import javax.swing.JTree;
//import javax.swing.event.TreeSelectionEvent;
//import javax.swing.event.TreeSelectionListener;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.TreePath;
//import java.io.File;
//import java.io.IOException;
//
//import javax.xml.parsers.*;
//
//import org.w3c.dom.Document;
//import org.xml.sax.SAXException;
//
//public class BookTree extends JTree {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -2467274909375394184L;
//	
//	public static Document document;
//	public BookTree() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	private static void readTree(){
//		
//	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//	    DocumentBuilder builder = null;
//		try {
//			builder = factory.newDocumentBuilder();
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    document = null;
//		try {
//			document = builder.parse( new File("./data/basicTree.xml") );
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		System.out.println("test");
//	    System.out.println( document.getFirstChild().getTextContent() );
//	    
//	}
//	
//	public static JTree buildTree(){
//		readTree();
//		 DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Book" );
//
////		 XmlElement [] chapters =  document.getElementsByTagName("chapter");
//	        for ( int nodeCnt = 0; nodeCnt < 4; nodeCnt++ )
//	        {
//	          DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode( "Knoten " + nodeCnt );
//	          root.add( dmtn );
//	        
//		          for ( int leafCnt = 1; leafCnt < 4; leafCnt++ ){
//		            dmtn.add( new DefaultMutableTreeNode( "Blatt " + (nodeCnt * 3 + leafCnt) ) );
//		          }
//	        }
//		
//	        JTree tree = new JTree( root );
//	        tree.getSelectionModel().addTreeSelectionListener( new TreeSelectionListener()
//	        {
//	          @Override
//	          public void valueChanged( TreeSelectionEvent e )
//	          {
//	            TreePath path = e.getNewLeadSelectionPath();
//	            System.out.println( path );
//	          }
//	        } );
//	        return tree;
//	        
//	}
//	
//
//}
