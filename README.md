# parsimus

Parsimus is a small logging enhancement framework meant to offer an alternative to having to face the trade-off between low log levels (which offer ample information but also abundant noise) and high log levels (which offer minimal noise but also little information).

It is built on top of slf4j, to be used in a servlet context and alongside an actual slf4j binding (e.g. logback or log4j). When using the `getLogger(class)` method of Parsimus's LoggerFactory instead of the slf4j binding LoggerFactory, normal logging will proceed as per usual (with the actual slf4j binding doing the logging under the hood), while for a request thread that has encountered an unexpected exception, a full log stack (across all log levels) will be printed. In this manner, normal logging can be kept to its required minimum (to avoid noise), while providing you with the maximum amount of information if an unexpected exception occurs.

Note that this technique can only be applied to the classes which you yourself control, since other classes will typically make use of the slf4j binding. Although, in theory, a Parsimus LoggerFactory could be made and used for slf4j binding, that would mean Parsimus would need to have its own fullscale slf4j implementation, and as such would be an alternative to other slf4j implementations, rather than the implementation agnostic enhancement that it's meant to be. 

## Servlet filter

To be functional, make sure the servlet filter is hooked up. In e.g. Spring, this could be done using Java config:

```Java
@Bean
public Filter parsimusLoggingFilter(){
    return new ParsimusLoggingFilter();
}
```

## Setting the log level for `ParsimusLoggingManager`

You also have to ensure that the ParsimusLoggingManager slf4j logger logs at all log levels. Spring Boot offers a shortcut to that by setting the following property.

```
logging.level.parsimus.ParsimusLoggingManager=TRACE
```

Note that based on the slf4j implementation that you are using, you could also declare a separate file appender for `ParsimusLoggingManager`, if you want the full log stacks to be printed to a separate log file.

## Manually or automatically causing a full log stack to be printed

If you manually want to cause a full lock stack to be printed, you need to call `ParsimusLoggingManager.setActive(true)`. In Spring, this could be done via the following controller advice.

```Java
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOG = parsimus.LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handler(Exception exception) {
        // making sure the exception itself is also added to the full log stack
        LOG.error("Encountered exception", exception);

        ParsimusLoggingManager.setActive(true);
    }
}
```

Alternatively, if any exceptions in your application will bubble up across your request filters, you could let the Parsimus filter handle these exceptions by
1. catching the exceptions
2. causing the full log stack to be printed
3. rethrowing the exception

This can be done by performing a static call to `ParsimusLoggingFilter.setActiveOnException(true)` at any point during the application startup phase (i.e. before the servlet requests have taken off), or by setting the system property `parsimus.activeOnException=true`.
