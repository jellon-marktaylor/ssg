package jellon.ssg.engine.flagship.integ;

import jellon.ssg.io.spi.IOutputStreamResources;

import javax.inject.Inject;

/**
 * A Guice-Scala integration work-around. There doesn't appear to be a way to annotate an idiomatic scala constructor
 * with Inject.
 */
public class FlagshipHintHandlers extends jellon.ssg.engine.flagship.hints.FlagshipHintHandlers {
    @Inject
    public FlagshipHintHandlers(IOutputStreamResources resources) {
        super(resources);
    }
}
