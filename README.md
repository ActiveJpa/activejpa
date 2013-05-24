ActiveJpa
=========

ActiveJpa is a java library that attempts to implement the active record pattern on top of JPA. The goal of this library is to eliminate the need to create DAO or Repository classes and make programming DAL a lot more simpler. 

What can I do with ActiveJpa
----------------------------
AcitveJpa abstracts out some of the most common functionalities you might need in your DAL. You should be able to do,

```java
	// Get order by id
	Order order = Order.findById(12345L);
	
	// Get all orders for a customer that are shipped
	List<Order> orders = Order.where("customer_email", "dummyemail@dummy.com", "status", "shipped");
	
	// Get all orders for the product category 'books' and paginate it
	Filter filter = new Filter();
	filter.setPageNo(1);
	filter.setPerPage(25);
	filter.addCondition(new Condition("orderItems.product.category", Operator.eq, "books");
	List<Order> orders = Order.where(filter);
	
```

Getting Started
---------------
### Setting Up Maven

ActiveJpa is available as a Maven artifact and should be fairly simpler to integrate with your application. Just add the below maven dependency to your pom.xml file,

```xml
   <dependency>
      <groupId>org.activejpa</groupId>
      <artifactId>activejpa-core</artifactId>
      <version>0.0.1</version>
   </dependency>
```

If you are on a non-maven project, you will have to include these additional dependencies in addition to activejpa-core,

* Javassist-3.17.1-GA.jar
* hibernate-jpa-2.0-api.1.0.1-Final.jar
* commons-collection-1.8.3.jar
* slf4j-log4j12-1.7.5.jar

### Hooking into your application

ActiveJPA does some runtime bytecode enhancement to simplify development and madates you to hook it before your models are loaded by the classloader. There are two ways you can hook it to your application,

* Run your applicaiton with the activejpa java agent by the below jvm option "-javaagent:activejpa-core.jar"
* At the bootstrap of your application, before your classes are loaded, manually load the java agent using the following code. This will require you to add tools.jar to your dependency list.

```java
	ActiveJpaAgentLoader.instance().loadAgent();
```
	
### Enhancing your Entities

ActiveJpa enhances all the classes that is a subclass of org.activejpa.entity.Model and has java.persistence.Entity annotation. So ensure all your JPA entities extend org.activejpa.entity.Model class 

```java
	@java.persistence.Entity
	public class Order extends org.activejpa.entity.Model {
		
		private Long id;
		
		@javax.persistence.Id
		@javax.persistence.GeneratedValue(strategy=javax.persistence.GenerationType.AUTO)
		public Long getId() {
			return id;
		}
	}
```
