/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.listeners.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Error window.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ErrorFrame extends JFrame {
    /**
     * Estimate size of task bar.
     */
    private static final int HEIGHT_TASK_BAR = 25;
    /**
     * Get default font size.
     */
    private static final int FONT_SIZE = 12;
    /**
     * Gaps between components.
     */
    private static final int GAPS = 10;
    /**
     * Area.
     */
    private JTextArea text;
    /**
     * The auxiliary dialog.
     */
    private JDialog dialog;

    /**
     * Basic constructor.
     * 
     * @param listener
     *            The listener.
     */
    public ErrorFrame(final ErrorFrameListener listener) {
        super("Error messages");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        createText();
        createDialog(listener);
        createButtons(listener);
    }

    /**
     * Create text component.
     */
    protected void createText() {
        text = new JTextArea();
        text.setEditable(false);
        text.setFont(new Font("Courrier New", Font.PLAIN, getFontSize()));
    }

    /**
     * The error font size.
     * 
     * @return The size.
     */
    protected int getFontSize() {
        return FONT_SIZE;
    }

    /**
     * Creates the dialog.
     * 
     * @param listener
     *            A listener.
     */
    protected void createDialog(final ErrorFrameListener listener) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog = new JDialog(this, "Error messages");
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                listener.ok();
                dialog.setVisible(false);
            }
        });
        dialog.setSize(screenSize.width / 2, screenSize.height / 2);
        dialog.setLocation(screenSize.width / 2, (screenSize.height / 2) - HEIGHT_TASK_BAR);
        dialog.setLayout(new BorderLayout(GAPS, GAPS));
        dialog.add(new JScrollPane(text), BorderLayout.CENTER);
        dialog.setModal(true);
    }

    /**
     * Creates the action buttons.
     * 
     * @param listener
     *            The listener of button actions.
     */
    protected void createButtons(final ErrorFrameListener listener) {
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER, GAPS, GAPS));
        dialog.add(buttons, BorderLayout.SOUTH);

        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.ok();
                dialog.setVisible(false);
            }
        });
        buttons.add(ok);

        JButton okAll = new JButton("Ok to All");
        okAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.okToAll();
                dialog.setVisible(false);
            }
        });
        buttons.add(okAll);
    }

    /**
     * Make the information appear.
     * 
     * @param content
     *            The content.
     */
    public void setVisible(Object content) {
        text.setText(String.valueOf(content));
        setVisible(true);
        dialog.setVisible(true);
        setVisible(false);
    }
}
