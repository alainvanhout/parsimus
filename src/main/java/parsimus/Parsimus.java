package parsimus;

import parsimus.servlet.ParsimusLoggingFilter;

/**
 * Facade for interactions with Parsimus
 */
public class Parsimus {
    public static void print(){
        ParsimusLoggingManager.printAll();
    }

    public static void activate(){
        ParsimusLoggingManager.setActive(true);
    }

    public static void reset(){
        ParsimusLoggingManager.reset();
    }

    public static void activateOnException(){
        ParsimusLoggingFilter.setActiveOnException(true);
    }
}
