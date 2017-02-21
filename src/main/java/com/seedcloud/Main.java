package com.seedcloud;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

  private static ApplicationContext appContext;

  private static void initSpringContext() {
    String[] locations = {"beans.xml"};
    appContext = new ClassPathXmlApplicationContext(locations);
    if (appContext instanceof AbstractApplicationContext) {
      AbstractApplicationContext abstractAppContext = (AbstractApplicationContext) appContext;
      abstractAppContext.registerShutdownHook();
    }
  }

  public static void main(String[] args) {
    initSpringContext();

    Runtime.getRuntime().addShutdownHook(new Thread() {


      @Override
      public void run() {
        shutdown();
      }
    });
  }

  private static void shutdown() {
    System.out.println("Shutdown successfull");
  }
}
