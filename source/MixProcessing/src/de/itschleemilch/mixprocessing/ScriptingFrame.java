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
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Public console to access the Scripting API.
 *
 * @author Sebastian Schleemilch
 */
public class ScriptingFrame extends javax.swing.JFrame 
implements ActionListener, ChannelsChangedListener, SketchesChangedListener {
    private final EventManager eventManager;
    private final ScriptRunner scrRunner;

    public ScriptingFrame(EventManager eventManager, ScriptRunner scrRunner) throws HeadlessException {
        this.eventManager = eventManager;
        this.scrRunner = scrRunner;
        
        initComponents();
        try {
            buildMenu();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace(System.err);
        }
        rebuildSketchesChannelsList();
        
        eventManager.addChannelsChangedListener(this);
        eventManager.addSketchesChangedListener(this);
        
        setSize(500, 300);
    }

    /**
     * Loades XML Menu Configuration from Ressource-Files
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    private void buildMenu() 
    throws ParserConfigurationException, SAXException, IOException {
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
                JMenu subMenu = new JMenu(menu.getAttribute("text"));
                NodeList items = menu.getElementsByTagName("item");
                for(int j = 0; j < items.getLength(); j++)
                {
                    Node n = items.item(j);
                    if(n instanceof Element) {
                        String itemText = n.getTextContent();
                        if( ((Element)n).hasAttribute("separator") ) {
                            subMenu.add(new JSeparator());
                        }
                        else if( ((Element)n).hasAttribute("label") ) {
                            String label = ((Element)n).getAttribute("label");
                            JMenuItem menuItem = new JMenuItem(label);
                            menuItem.setEnabled(false);
                            subMenu.add(menuItem);
                            
                        }
                        else {
                            String itemInsertValue = ((Element)n).getAttribute("insert");
                            JMenuItem menuItem = new JMenuItem(itemText);
                            menuItem.setActionCommand("I:"+itemInsertValue);
                            menuItem.addActionListener(this);
                            subMenu.add(menuItem);
                        }
                    }
                }
                insertCmdMenu.add(subMenu);
            } // if node
        } // loop menu items
    }
    
    private void rebuildSketchList() {
        try{ SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                sketchListMenu.removeAll();
            }
        }); } catch(InterruptedException | InvocationTargetException e) {
            e.printStackTrace(System.err);
        }
        
        Sketch[] sketches = eventManager.getAPI().getSketches().getAllSketches();
        Arrays.sort(sketches);
        for (Sketch sketch : sketches) {
            JMenuItem item = new JMenuItem(sketch.getName());
            item.setActionCommand("I:" + sketch.getName());
            item.addActionListener(this);
            
            try{ SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    sketchListMenu.add(item);
                }
            }); } catch(InterruptedException | InvocationTargetException e) {
                e.printStackTrace(System.err);
            }
        }
    }
    
    private void rebuildChannelList() {
        try{ SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                channelListMenu.removeAll();
            }
        }); } catch(InterruptedException | InvocationTargetException e) {
            e.printStackTrace(System.err);
        }
        
        SingleChannel[] channels = eventManager.getAPI().getChannels().getAllChannels();
        Arrays.sort(channels);
        for (SingleChannel channel : channels) {
            String text = channel.getChannelName();
            if(channel instanceof GroupChannel) {
                text += " (G)";
            }
            final JMenuItem item = new JMenuItem(text);
            item.setActionCommand("I:" + channel.getChannelName());
            item.addActionListener(this);
            
            try{ SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    channelListMenu.add(item);
                }
            }); } catch(InterruptedException | InvocationTargetException e) {
                e.printStackTrace(System.err);
            }
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
        if(scriptInputField.getSelectionStart() == 
                scriptInputField.getSelectionEnd()) {
            scriptInputField.replaceSelection(text);
        }
        else { // Replace selected Text
            scriptInputField.replaceSelection(text);
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        consoleOutputArea = new javax.swing.JTextArea();
        scriptInputField = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        insertCmdMenu = new javax.swing.JMenu();
        insertNamesMenu = new javax.swing.JMenu();
        channelListMenu = new javax.swing.JMenu();
        sketchListMenu = new javax.swing.JMenu();

        setTitle("Scripting Interface");

        consoleOutputArea.setEditable(false);
        consoleOutputArea.setBackground(new java.awt.Color(0, 0, 0));
        consoleOutputArea.setColumns(20);
        consoleOutputArea.setForeground(new java.awt.Color(0, 204, 51));
        consoleOutputArea.setRows(5);
        consoleOutputArea.setText("Scripting API Console\n\n");
        consoleOutputArea.setToolTipText("");
        jScrollPane1.setViewportView(consoleOutputArea);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        scriptInputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scriptInputFieldActionPerformed(evt);
            }
        });
        getContentPane().add(scriptInputField, java.awt.BorderLayout.SOUTH);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        insertCmdMenu.setText("Commands");
        jMenuBar1.add(insertCmdMenu);

        insertNamesMenu.setText("Names");

        channelListMenu.setText("Channels");
        insertNamesMenu.add(channelListMenu);

        sketchListMenu.setText("Sketches");
        insertNamesMenu.add(sketchListMenu);

        jMenuBar1.add(insertNamesMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void scriptInputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scriptInputFieldActionPerformed
        final String script = scriptInputField.getText();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                consoleOutputArea.append(">" + script + "\n");
                scriptInputField.setText(""); // clear output.
                scrRunner.exec(script, consoleOutputArea); // runs in seperate thread
            }
        });
    }//GEN-LAST:event_scriptInputFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu channelListMenu;
    private javax.swing.JTextArea consoleOutputArea;
    private javax.swing.JMenu insertCmdMenu;
    private javax.swing.JMenu insertNamesMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField scriptInputField;
    private javax.swing.JMenu sketchListMenu;
    // End of variables declaration//GEN-END:variables
    
    private static final long serialVersionUID = 1L;
}
