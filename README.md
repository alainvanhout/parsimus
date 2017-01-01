# parsimus
Small logging framework built on top of slf4j which in a servlet context will print all log calls made during that request (for all levels). This will supply you with the maximum amount of information if an unexpected exception occurs while having lower levels of logging when a request runs as expected.

To be functional, make sure the servlet filter is hooked up. In e.g. Spring, this could be done using Java config:

```Java
	@Bean
    public Filter parsimusLoggingFilter(){
	    return new ParsimusLoggingFilter();
    }
```

You also have to ensure that the ThreadLoggingManager slf4j logger logs at all log levels. In Spring Boot, that can be done by setting the following property.

```
logging.level.parsimus.ThreadLoggingManager=TRACE
```

Finally, you need to call `ThreadLoggingManager.setActive(true)` when a full logging print is needed for a given request. In Spring, this could be done via the following controller advice.

```Java
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handler(Exception exception) {
        LOG.error("Encountered exception", exception);

        ThreadLoggingManager.setActive(true);
    }
}
```
