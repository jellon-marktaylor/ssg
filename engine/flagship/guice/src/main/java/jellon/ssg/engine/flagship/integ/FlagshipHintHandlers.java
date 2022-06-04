package jellon.ssg.engine.flagship.integ;

import jellon.ssg.io.spi.IOutputStreamResources;

import javax.inject.Inject;

/**
 * A Guice-Scala integration work-around
 */
public class FlagshipHintHandlers extends jellon.ssg.engine.flagship.hints.FlagshipHintHandlers {
    @Inject
    public FlagshipHintHandlers(IOutputStreamResources resources) {
        super(resources);
    }
}
