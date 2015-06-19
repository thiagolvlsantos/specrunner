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
package org.specrunner.plugins.core.flow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilIO;
import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;

/**
 * Allows a pause in execution, waiting for an 'OK' in dialog, or 'Enter' when
 * 'enter'attribute is set to true.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginPause extends AbstractPlugin {

    /**
     * Condition.
     */
    public static final String FEATURE_PAUSE_CONDITION = PluginPause.class.getName() + ".condition";

    /**
     * Model condition.
     */
    public static final String FEATURE_PAUSE_CONDITION_MODEL = PluginPause.class.getName() + ".conditionModel";

    /**
     * Enter feature.
     */
    public static final String FEATURE_ENTER = PluginPause.class.getName() + ".enter";

    /**
     * Time feature.
     */
    public static final String FEATURE_TIME = PluginPause.class.getName() + ".time";

    /**
     * Message frame screen ratio.
     */
    private static final int RATIO = 4;

    /**
     * Screen font size.
     */
    private static final int FONT_SIZE = 12;

    /**
     * Pause message.
     */
    private String msg;

    /**
     * Pause using a keyboard request.
     */
    private Boolean enter;

    /**
     * Pause time.
     */
    private Long time;

    /**
     * Get pause message.
     * 
     * @return The message.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Set pause message.
     * 
     * @param msg
     *            Set pause message.
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Get pause message.
     * 
     * @return The message.
     */
    public String getPause() {
        return msg;
    }

    /**
     * Set pause message.
     * 
     * @param msg
     *            Set pause message.
     */
    public void setPause(String msg) {
        this.msg = msg;
    }

    /**
     * Flag to indicate if pause is on dialog or 'Enter' key. Default is false.
     * 
     * @return true, if pause halt on 'Enter', false, otherwise.
     */
    public Boolean getEnter() {
        return enter;
    }

    /**
     * Set enter flag.
     * 
     * @param enter
     *            true to request for 'Enter', false, otherwise (default is a
     *            dialog request).
     */
    public void setEnter(Boolean enter) {
        this.enter = enter;
    }

    /**
     * If set, specify the time to wait. Otherwise, press any key to move on.
     * 
     * @return The time to wait.
     */
    public Long getTime() {
        return time;
    }

    /**
     * Sets the wait time.
     * 
     * @param time
     *            The time.
     */
    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_PAUSE_CONDITION, this);
        fm.set(FEATURE_PAUSE_CONDITION_MODEL, this);
        if (enter == null) {
            fm.set(FEATURE_ENTER, this);
        }
        if (time == null) {
            fm.set(FEATURE_TIME, this);
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        IOutput output = SRServices.get(IOutputFactory.class).currentOutput();
        if (getTime() != null) {
            try {
                output.println("(" + Thread.currentThread().getName() + ") sleeping for " + getTime() + "mls.");
                Thread.sleep(getTime());
                output.println("(" + Thread.currentThread().getName() + ") woke up.");
                result.addResult(Success.INSTANCE, context.peek());
            } catch (InterruptedException e) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
            }
        } else {
            try {
                if (enter != null && enter) {
                    UtilIO.pressKey();
                } else {
                    String message = msg != null ? msg : "";
                    output.println("Test (" + Thread.currentThread().getName() + ") on pause. " + message);
                    showMessage(message);
                }
                result.addResult(Success.INSTANCE, context.peek());
            } catch (IOException e) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
            }
        }
        return ENext.DEEP;
    }

    /**
     * Show a message on a dialog.
     * 
     * @param message
     *            A message.
     */
    protected void showMessage(String message) {
        final JFrame frame = new JFrame("Pause frame.");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        final JDialog dialog = new JDialog(frame, "Pause dialog");
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize(screenSize.width / RATIO, screenSize.height / RATIO);
        dialog.setLocation(screenSize.width / 2, screenSize.height / 2);
        dialog.setLayout(new BorderLayout());
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                releaseWindow(frame, dialog);
            }
        });

        JTextArea text = new JTextArea(" Pause requested." + (!message.isEmpty() ? "\n\n " + message : "") + "\n\n Press 'Ok' to continue.");
        text.setFont(new Font("Monospaced", Font.PLAIN, FONT_SIZE));
        text.setLineWrap(true);
        text.setEditable(false);
        dialog.add(new JScrollPane(text), BorderLayout.CENTER);

        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                releaseWindow(frame, dialog);
            }
        });
        dialog.add(ok, BorderLayout.SOUTH);

        frame.setVisible(true);
        dialog.setVisible(true);
    }

    /**
     * Release message frame and dialog.
     * 
     * @param frame
     *            The frame.
     * @param dialog
     *            The dialog.
     */
    protected void releaseWindow(JFrame frame, JDialog dialog) {
        dialog.setVisible(false);
        frame.setVisible(false);
    }
}