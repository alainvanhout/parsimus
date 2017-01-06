package parsimus;

import parsimus.servlet.ParsimusLoggingFilter;

/**
 * Facade for interactions with Parsimus
 */
public class Parsimus {
    public static void print(){
        // print log stack
        ParsimusLoggingManager.print();
        // ensure thread is in now state to inactive
        ParsimusLoggingFilter.setActive(false);
    }

    public static void activate(){
        ParsimusLoggingFilter.setActive(true);
    }

    public static void reset(){
        ParsimusLoggingManager.reset();
    }

    public static void activateOnException(){
        ParsimusLoggingFilter.setActiveOnException(true);
    }
}
