Petclinic
=========
The petclinic example is taken from the spring-projects [petclinic example](https://github.com/spring-projects/spring-petclinic/). This example shows how to integrate activejpa with spring.

## Maven Dependencies
Add the activejpa-core to the maven dependencies in the pom.xml.

## Hooking ActiveJpa to Spring application
The petclinic application is a web application deployed to servlet container. ActiveJpa has to be hooked before spring context is loaded. The best place to do this in a servlet environment is ContextListener. You can probably extend the spring ContextLoaderListener and initialize ActiveJpa.

```java
	/**
   * @author ganeshs
   *
   */
  public class CustomContextListener extends ContextLoaderListener {
  	
  	@Override
  	public void contextInitialized(ServletContextEvent event) {
  		try {
  		  // This loads the javaagent dynamically
  			ActiveJpaAgentLoader.instance().loadAgent();
  		} catch (Exception e) {
  			throw new RuntimeException(e);
  		}
  		super.contextInitialized(event);
  		JPA.instance.addPersistenceUnit("default", getCurrentWebApplicationContext().getBean(EntityManagerFactory.class), true);
  	}
  }
```
## Entity classes
Ensure your entity classes extend org.activejpa.entity.Model

## Starting the example application
Just run `mvn tomcat7:run`

Hit http://localhost:9966/petclinic/ and playaround with the petclinic application
