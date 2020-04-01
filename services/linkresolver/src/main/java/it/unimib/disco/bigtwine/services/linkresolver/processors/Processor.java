package it.unimib.disco.bigtwine.services.linkresolver.processors;

import it.unimib.disco.bigtwine.services.linkresolver.domain.ExtraField;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Link;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Resource;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;

public interface Processor extends GenericProcessor<Link, Resource> {
    boolean process(String tag, Link item, ExtraField[] extraFields, boolean skipCache);
    boolean process(String tag, Link[] items, ExtraField[] extraFields, boolean skipCache);
}
