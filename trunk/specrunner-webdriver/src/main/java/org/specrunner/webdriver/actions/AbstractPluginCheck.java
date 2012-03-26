package org.specrunner.webdriver.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginFind;
import org.specrunner.webdriver.util.WritablePage;

public abstract class AbstractPluginCheck extends AbstractPluginFind {

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement[] elements) throws PluginException {
        boolean success = true;
        for (int i = 0; i < elements.length; i++) {
            WebElement e = elements[i];
            if (!isCheckbox(e)) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element " + e + " is not a checkbox."), new WritablePage(client));
                success = false;
                break;
            }
            doSomething(e);
        }
        if (success) {
            result.addResult(Status.SUCCESS, context.peek());
        }
    }

    /**
     * Check if is checkbox.
     * 
     * @param e
     *            The element.
     * @return true, if checkbox, false, otherwise.
     */
    protected boolean isCheckbox(WebElement e) {
        return "input".equals(e.getTagName()) && "checkbox".equals(e.getAttribute("type"));
    }

    /**
     * Perform something with the checkbox.
     * 
     * @param checkbox
     *            The element.
     */
    protected abstract void doSomething(WebElement checkbox);

}