package org.specrunner.impl.pipes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

public class PipeTimestamp extends AbstractPipeModel {

    /**
     * Time model information.
     */
    public static final String DATE = "date";
    /**
     * Report date format.
     */
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public IChannel process(IChannel channel, Map<String, Object> model) throws PipelineException {
        model.put(PipeTime.TIME, System.currentTimeMillis() - (Long) model.get(PipeTime.TIME));
        model.put(DATE, sdf.format(new Date()));
        return channel;
    }

    public static Long recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return (Long) PipeModel.recover(channel).get(DATE);
    }
}