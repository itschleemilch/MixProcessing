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
import de.itschleemilch.mixprocessing.events.SketchesChangedListener;
import de.itschleemilch.mixprocessing.script.ScriptRunner;
import de.itschleemilch.mixprocessing.sketches.Sketch;
import de.itschleemilch.mixprocessing.util.InternetShortcuts;
import java.awt.Button;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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
 * Editor for the Skripting Interface
 *
 * @author Sebastian Schleemilch
 */
public class ScriptingFrame extends java.awt.Frame 
    implements ActionListener, ChannelsChangedListener, SketchesChangedListener {
    private final EventManager eventManager;
    private final ScriptRunner scrRunner;

    /**
     * Creates a new Scripting Frame
     * @param eventManager Access to event system
     * @param scrRunner 
     */
    public ScriptingFrame(EventManager eventManager, ScriptRunner scrRunner) {
        this.eventManager = eventManager;
        this.scrRunner = scrRunner;
        
        initComponents();
        try {
            buildMenu();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        rebuildSketchesChannelsList();
        
        eventManager.addChannelsChangedListener(this);
        eventManager.addSketchesChangedListener(this);
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
                        if( ((Element)n).hasAttribute("separator") ) {
                            awtMenu.addSeparator();
                        }
                        else if( ((Element)n).hasAttribute("label") ) {
                            String label = ((Element)n).getAttribute("label");
                            MenuItem awtItem = new MenuItem(label);
                            awtItem.setEnabled(false);
                            awtMenu.add(awtItem);
                            
                        }
                        else {
                            String itemInsertValue = ((Element)n).getAttribute("insert");
                            MenuItem awtItem = new MenuItem(itemText);
                            awtItem.setActionCommand("I:"+itemInsertValue);
                            awtItem.addActionListener(this);
                            awtMenu.add(awtItem);
                        }
                    }
                }
                insertMenu.add(awtMenu);
            }
        }
    }
    
    private void rebuildSketchList() {
        sketchChoice.removeAll();
        Sketch[] sketches = eventManager.getSketches().getAllSketches();
        for (Sketch sketch : sketches) {
            sketchChoice.add(sketch.getName());
        }
    }
    
    private void rebuildChannelList() {
        channelChoice.removeAll();
        SingleChannel[] channels = eventManager.getChannels().getAllChannels();
        for (SingleChannel channel : channels) {
            String text = channel.getChannelName();
            if(channel instanceof GroupChannel)
                text += " (G)";
            channelChoice.add(text);
        }
    }
    
    /**
     * Dynamically Rebuilds Sketches from the channel List
     */
    private void rebuildSketchesChannelsList()
    {
        rebuildSketchList();
        rebuildChannelList();
    }
    
    private void insertText(String text) {
        if(scriptArea.getSelectionStart() == scriptArea.getSelectionEnd())
            scriptArea.insert(text, scriptArea.getCaretPosition());
        else { // Replace selected Text
            scriptArea.replaceRange(text, 
                    scriptArea.getSelectionStart(), 
                    scriptArea.getSelectionEnd());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().startsWith("I:"))
        {
            String insertText = e.getActionCommand().substring(2);
            insertText(insertText);
        }
    }

    @Override
    public void channelsChanged() {
        rebuildChannelList();
        revalidate();
    }

    @Override
    public void sketchesChanged() {
        rebuildSketchList();
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
        label2 = new java.awt.Label();
        sketchChoice = new java.awt.Choice();
        sketchInsertBtn = new java.awt.Button();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        label3 = new java.awt.Label();
        channelChoice = new java.awt.Choice();
        channelInsertBtn = new java.awt.Button();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        menuBar1 = new java.awt.MenuBar();
        fileManu = new java.awt.Menu();
        fileNewItem = new java.awt.MenuItem();
        fileOpenItem = new java.awt.MenuItem();
        fileSaveAsItem = new java.awt.MenuItem();
        fileOpenSketchFolderItem = new java.awt.MenuItem();
        insertMenu = new java.awt.Menu();
        helpMenu = new java.awt.Menu();
        projectHome = new java.awt.MenuItem();
        projectWebsite = new java.awt.MenuItem();
        userManualItem = new java.awt.MenuItem();

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

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        label1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        label1.setText("Insert ressource names:");
        jPanel1.add(label1);

        label2.setText("Sketches:");
        jPanel1.add(label2);
        jPanel1.add(sketchChoice);

        sketchInsertBtn.setLabel("Insert");
        sketchInsertBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sketchInsertBtnActionPerformed(evt);
            }
        });
        jPanel1.add(sketchInsertBtn);
        jPanel1.add(filler2);

        label3.setText("Channels:");
        jPanel1.add(label3);
        jPanel1.add(channelChoice);

        channelInsertBtn.setLabel("Insert");
        channelInsertBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                channelInsertBtnActionPerformed(evt);
            }
        });
        jPanel1.add(channelInsertBtn);
        jPanel1.add(filler3);

        add(jPanel1, java.awt.BorderLayout.EAST);

        fileManu.setLabel("File");

        fileNewItem.setLabel("New");
        fileNewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNewItemActionPerformed(evt);
            }
        });
        fileManu.add(fileNewItem);

        fileOpenItem.setEnabled(false);
        fileOpenItem.setLabel("Open...");
        fileManu.add(fileOpenItem);

        fileSaveAsItem.setEnabled(false);
        fileSaveAsItem.setLabel("Save As...");
        fileManu.add(fileSaveAsItem);
        fileManu.addSeparator();
        fileOpenSketchFolderItem.setLabel("Open Sketch Folder...");
        fileOpenSketchFolderItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileOpenSketchFolderItemActionPerformed(evt);
            }
        });
        fileManu.add(fileOpenSketchFolderItem);

        menuBar1.add(fileManu);

        insertMenu.setActionCommand("Insert Commands");
        insertMenu.setLabel("Insert");
        menuBar1.add(insertMenu);

        helpMenu.setLabel("Help");

        projectHome.setLabel("Project Home");
        projectHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectHomeActionPerformed(evt);
            }
        });
        helpMenu.add(projectHome);

        projectWebsite.setLabel("MixProcessing Website");
        projectWebsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectWebsiteActionPerformed(evt);
            }
        });
        helpMenu.add(projectWebsite);
        helpMenu.addSeparator();
        userManualItem.setLabel("User Manual");
        userManualItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userManualItemActionPerformed(evt);
            }
        });
        helpMenu.add(userManualItem);

        menuBar1.add(helpMenu);

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
        scriptErrorArea.setText(""); // clear output.
        scrRunner.exec(script, scriptErrorArea);
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

    private void sketchInsertBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sketchInsertBtnActionPerformed
        insertText("'" + sketchChoice.getSelectedItem() + "'");
    }//GEN-LAST:event_sketchInsertBtnActionPerformed

    private void channelInsertBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_channelInsertBtnActionPerformed
        insertText("'" + channelChoice.getSelectedItem() + "'");
    }//GEN-LAST:event_channelInsertBtnActionPerformed

    private void userManualItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userManualItemActionPerformed
        boolean success = InternetShortcuts.openShortcut(InternetShortcuts.USER_MANUAL);
        if(!success)
            System.err.println("Failed to open website.");
    }//GEN-LAST:event_userManualItemActionPerformed

    private void projectHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectHomeActionPerformed
        boolean success = InternetShortcuts.openShortcut(InternetShortcuts.PROJECT_HOME);
        if(!success)
            System.err.println("Failed to open website.");
    }//GEN-LAST:event_projectHomeActionPerformed

    private void projectWebsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectWebsiteActionPerformed
        boolean success = InternetShortcuts.openShortcut(InternetShortcuts.PROJECT_WEBSITE);
        if(!success)
            System.err.println("Failed to open website.");
    }//GEN-LAST:event_projectWebsiteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private java.awt.Choice channelChoice;
    private java.awt.Button channelInsertBtn;
    private java.awt.Button exec_btn;
    private java.awt.Menu fileManu;
    private java.awt.MenuItem fileNewItem;
    private java.awt.MenuItem fileOpenItem;
    private java.awt.MenuItem fileOpenSketchFolderItem;
    private java.awt.MenuItem fileSaveAsItem;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private java.awt.Menu helpMenu;
    private java.awt.Menu insertMenu;
    private javax.swing.JPanel jPanel1;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.MenuBar menuBar1;
    private java.awt.Panel panel1;
    private java.awt.MenuItem projectHome;
    private java.awt.MenuItem projectWebsite;
    private java.awt.TextArea scriptArea;
    private java.awt.TextArea scriptErrorArea;
    private java.awt.Choice sketchChoice;
    private java.awt.Button sketchInsertBtn;
    private java.awt.MenuItem userManualItem;
    // End of variables declaration//GEN-END:variables
}
