/*
MixProcessing - Live Mixing of Processing Sketches 
https://github.com/itschleemilch/MixProcessing

Copyright (c) 2014 Sebastian Schleemilch

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.itschleemilch.mixprocessing;

import de.itschleemilch.mixprocessing.channels.GroupChannel;
import de.itschleemilch.mixprocessing.channels.SingleChannel;
import de.itschleemilch.mixprocessing.events.ChannelsChangedListener;
import de.itschleemilch.mixprocessing.sketches.Sketch;
import java.awt.Button;
import java.awt.Desktop;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.Box;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Sebastian Schleemilch
 */
public class ScriptingFrame extends java.awt.Frame 
    implements ActionListener, ChannelsChangedListener {
    private final EventManager eventManager;
    private final ScriptEngine scriptingEngine;
    
    /**
     * Creates a new Scripting Frame
     * @param em Access to event system
     */
    public ScriptingFrame(EventManager em) {
        this.eventManager = em;
        initComponents();
        try {
            buildMenu();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        rebuildSketchesChannelsList();
        /* Init Scripting API */
        ScriptEngineManager factory = new ScriptEngineManager();
        scriptingEngine = factory.getEngineByName("JavaScript");
        scriptingEngine.put("mp", em); // access to EventManager through mp Variable
        
        em.addChannelsChangedListener(this);
    }
    
    /**
     * Loades XML Menu Configuration from Ressource-Files
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    private void buildMenu() 
            throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	       DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	       Document doc = dBuilder.parse( 
                       getClass().getResourceAsStream("res/scripting_templates.xml") );
        NodeList menuList = doc.getElementsByTagName("menu");
        for (int i = 0; i < menuList.getLength(); i++) {
            Node node = menuList.item(i);
            if(node instanceof Element)
            {
                Element menu = (Element) node;
                Menu awtMenu = new Menu(menu.getAttribute("text"));
                NodeList items = menu.getElementsByTagName("item");
                for(int j = 0; j < items.getLength(); j++)
                {
                    Node n = items.item(j);
                    if(n instanceof Element) {
                        String itemText = n.getTextContent();
                        String itemInsertValue = ((Element)n).getAttribute("insert");
                        MenuItem awtItem = new MenuItem(itemText);
                        awtItem.setActionCommand("I:"+itemInsertValue);
                        awtItem.addActionListener(this);
                        awtMenu.add(awtItem);
                    }
                }
                insertMenu.add(awtMenu);
            }
        }
    }
    
    /**
     * Dynamically Rebuilds Sketches from the channel List
     */
    private void rebuildSketchesChannelsList()
    {
        sketchChannelListPanel.removeAll();
        sketchChannelListPanel.add(new Label("Sketches:"));
        Sketch[] sketches = eventManager.getSketches().getAllSketches();
        for (Sketch sketch : sketches) {
            Button b = new Button(sketch.getName());
            b.setActionCommand("I:'" + sketch.getName()+"'");
            b.addActionListener(this);
            sketchChannelListPanel.add(b);
        }
        sketchChannelListPanel.add(new Label("Channels:"));
        SingleChannel[] channels = eventManager.getChannels().getAllChannels();
        for (SingleChannel channel : channels) {
            String text = channel.getChannelName();
            if(channel instanceof GroupChannel)
                text += " (G)";
            Button b = new Button(text);
            b.setActionCommand("I:'" + channel.getChannelName()+"'");
            b.addActionListener(this);
            sketchChannelListPanel.add(b);
        }
        sketchChannelListPanel.add(Box.createGlue());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().startsWith("I:"))
        {
            String insertText = e.getActionCommand().substring(2);
            if(scriptArea.getSelectionStart() == scriptArea.getSelectionEnd())
                scriptArea.insert(insertText, scriptArea.getCaretPosition());
            else { // Replace selected Text
                scriptArea.replaceRange(insertText, 
                        scriptArea.getSelectionStart(), 
                        scriptArea.getSelectionEnd());
            }
        }
    }

    @Override
    public void channelsChanged() {
        rebuildSketchesChannelsList();
        revalidate();
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        centerPanel = new javax.swing.JPanel();
        scriptArea = new java.awt.TextArea();
        panel1 = new java.awt.Panel();
        exec_btn = new java.awt.Button();
        scriptErrorArea = new java.awt.TextArea();
        jPanel1 = new javax.swing.JPanel();
        label1 = new java.awt.Label();
        scrollPane1 = new java.awt.ScrollPane();
        sketchChannelListPanel = new java.awt.Panel();
        menuBar1 = new java.awt.MenuBar();
        menu1 = new java.awt.Menu();
        fileNewItem = new java.awt.MenuItem();
        fileOpenItem = new java.awt.MenuItem();
        fileSaveAsItem = new java.awt.MenuItem();
        fileOpenSketchFolderItem = new java.awt.MenuItem();
        insertMenu = new java.awt.Menu();

        setTitle("Scripting Interface");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        centerPanel.setLayout(new java.awt.BorderLayout());
        centerPanel.add(scriptArea, java.awt.BorderLayout.CENTER);

        panel1.setLayout(new java.awt.BorderLayout());

        exec_btn.setLabel("Execute");
        exec_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exec_btnActionPerformed(evt);
            }
        });
        panel1.add(exec_btn, java.awt.BorderLayout.SOUTH);

        scriptErrorArea.setEditable(false);
        panel1.add(scriptErrorArea, java.awt.BorderLayout.CENTER);

        centerPanel.add(panel1, java.awt.BorderLayout.SOUTH);

        add(centerPanel, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        label1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        label1.setText("Insert ressource names:");
        jPanel1.add(label1, java.awt.BorderLayout.NORTH);

        sketchChannelListPanel.setLayout(new javax.swing.BoxLayout(sketchChannelListPanel, javax.swing.BoxLayout.Y_AXIS));
        scrollPane1.add(sketchChannelListPanel);

        jPanel1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.EAST);

        menu1.setLabel("File");

        fileNewItem.setLabel("New");
        fileNewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNewItemActionPerformed(evt);
            }
        });
        menu1.add(fileNewItem);

        fileOpenItem.setEnabled(false);
        fileOpenItem.setLabel("Open...");
        menu1.add(fileOpenItem);

        fileSaveAsItem.setEnabled(false);
        fileSaveAsItem.setLabel("Save As...");
        menu1.add(fileSaveAsItem);
        menu1.addSeparator();
        fileOpenSketchFolderItem.setLabel("Open Sketch Folder...");
        fileOpenSketchFolderItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileOpenSketchFolderItemActionPerformed(evt);
            }
        });
        menu1.add(fileOpenSketchFolderItem);

        menuBar1.add(menu1);

        insertMenu.setActionCommand("Insert Commands");
        insertMenu.setLabel("Insert");
        menuBar1.add(insertMenu);

        setMenuBar(menuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    private void exec_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exec_btnActionPerformed
        final String script = scriptArea.getText();
        new Thread(new Runnable() {
            @Override
            public void run() {
                scriptErrorArea.setText("");
                try {
                    scriptingEngine.eval(script);
                } catch (ScriptException e) {
                    scriptErrorArea.append(e.getMessage());
                    scriptErrorArea.append(System.getProperty("line.separator"));
                    scriptErrorArea.append(System.getProperty("line.separator"));
                }
            }
        }).start();
    }//GEN-LAST:event_exec_btnActionPerformed

    private void fileOpenSketchFolderItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileOpenSketchFolderItemActionPerformed
        String jarDir = System.getProperty("MixProcessing.SKETCH_DIR");
        try {
            Desktop.getDesktop().open(new File(jarDir));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }//GEN-LAST:event_fileOpenSketchFolderItemActionPerformed

    private void fileNewItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNewItemActionPerformed
        scriptArea.setText("");
    }//GEN-LAST:event_fileNewItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private java.awt.Button exec_btn;
    private java.awt.MenuItem fileNewItem;
    private java.awt.MenuItem fileOpenItem;
    private java.awt.MenuItem fileOpenSketchFolderItem;
    private java.awt.MenuItem fileSaveAsItem;
    private java.awt.Menu insertMenu;
    private javax.swing.JPanel jPanel1;
    private java.awt.Label label1;
    private java.awt.Menu menu1;
    private java.awt.MenuBar menuBar1;
    private java.awt.Panel panel1;
    private java.awt.TextArea scriptArea;
    private java.awt.TextArea scriptErrorArea;
    private java.awt.ScrollPane scrollPane1;
    private java.awt.Panel sketchChannelListPanel;
    // End of variables declaration//GEN-END:variables
}
