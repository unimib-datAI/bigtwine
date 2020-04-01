package it.unimib.disco.bigtwine.services.linkresolver.processors;

import it.unimib.disco.bigtwine.services.linkresolver.QueryType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface QueryBasedProcessor {
    QueryType[] supportedQueryTypes();
}
