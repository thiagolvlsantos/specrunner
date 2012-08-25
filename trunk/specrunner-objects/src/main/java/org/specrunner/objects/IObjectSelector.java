package org.specrunner.objects;

import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;
import org.specrunner.util.impl.RowAdapter;

/**
 * Perform object searches.
 * 
 * @author Thiago Santos
 * @param <T>
 *            The source of selection.
 */
public interface IObjectSelector<T> {

    /**
     * The source for object selection.
     * 
     * @param plugin
     *            The plugin.
     * @param context
     *            The testing context.
     * @return The source.
     * @throws Exception
     *             On selection errors.
     */
    T getSource(AbstractPluginObject plugin, IContext context) throws Exception;

    /**
     * Performs a select on object repository to compare with the reference.
     * 
     * @param plugin
     *            The object plugin information.
     * @param context
     *            The test context.
     * @param instance
     *            The object to be compared with repository version.
     * @param row
     *            The row which was the source for object creation.
     * @param result
     *            The result set.
     * @return The corresponding objects from repository.
     * @throws Exception
     *             On selection errors.
     */
    List<Object> select(AbstractPluginObject plugin, IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception;

    /**
     * Release comparison resources. i.e. For Hibernate repositories free
     * sessions in use for comparison.
     * 
     * @throws Exception
     *             On release errors.
     */
    void release() throws Exception;
}