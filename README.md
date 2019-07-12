ActiveJpa [![Build Status](https://travis-ci.org/ActiveJpa/activejpa.png?branch=master)](https://travis-ci.org/ActiveJpa/activejpa)  [![Coverage Status](https://img.shields.io/coveralls/ActiveJpa/activejpa.svg)](https://coveralls.io/r/ActiveJpa/activejpa)
=========

ActiveJpa is a java library that attempts to implement the active record pattern on top of JPA. The goal of this library is to eliminate the need to create DAO or Repository classes and make programming DAL a lot more simpler. 

What can you do with ActiveJpa?
----------------------------
AcitveJpa abstracts out some of the most common functionalities you might need in your DAL. You should be able to do,

```java
	// Get order by id
	Order order = Order.findById(12345L);
	
	// Get all orders for a customer that are shipped
	List<Order> orders = Order.where("customer_email", "dummyemail@dummy.com", "status", "shipped");
	
	// Get all orders for the product category 'books' and paginate it
	Filter filter = new Filter()
			.page(1, 25)
			.condition("orderItems.product.category", Operator.eq, "books")
			.sortBy("status", true);
	List<Order> orders = Order.where(filter);
	
	// Or rather use filter on the model itself
	List<Order> orders = Order.filter().page(1, 25).condition("orderItems.product.category", Operator.eq, "books").sortBy("status", true).list();
	
	// Count of orders matching the filter
	Long count = Order.count(filter);
	
	// Get the first order matching the filter
	Long count = Order.first("customer_email", "dummyemail@dummy.com", "status", "shipped");
	
	// Get the unique order matching the conditions
	Long count = Order.one("customer_email", "dummyemail@dummy.com", "status", "shipped");
	
	// Dump everything
	List<Order> orders = Order.all();
	
	// Delete all orders matching the filter
	Long count = Order.deleteAll(filter);
	
	// Check if order exists with the given identifier
	boolean exists = Order.exists(1234L);
	
	// Save order
	order.setBillingAmount(1000.0);
	order.persist();
	
	// Delete order
	order.delete();
	
	// Update attributes
	Map<String, Object> attributes = new HashMap<String, Object>();
	attributes.put("billingAmount", 1000.0);
	order.updateAttributes(attributes);
	
	// Find order item by id within an order
	order.collections("order_items").findById(123L);
	
	// Search order items by filter with an order
	order.collections("order_items").where(filter);
	order.collections("order_items").filter().page(1, 25).sortBy("status", "true");
	
	....
	....
	
```

Getting Started
---------------
### Setting Up Maven

ActiveJpa is available as a Maven artifact and should be fairly simpler to integrate with your application. Just add the below maven dependency to your pom.xml file,

```xml
   <dependencies>
     <dependency>
       <groupId>org.activejpa</groupId>
       <artifactId>activejpa-core</artifactId>
       <version>1.1.0</version>
     </dependency>
   </dependencies>
   
   <repositories>
     <repository>
       <id>jcenter</id>
       <url>https://jcenter.bintray.com</url>
       <snapshots>
         <enabled>false</enabled>
       </snapshots>
     </repository>
   </repositories>
```

### Hooking into your application

ActiveJPA does some runtime bytecode enhancement to simplify development and madates you to hook it before your models are loaded by the classloader. There are two ways you can hook it to your application,

* Run your applicaiton with the activejpa java agent by the below jvm option "-javaagent:activejpa-core.jar"
* At the bootstrap of your application, before your classes are loaded, manually load the java agent using the following code. This will require you to add tools.jar to your dependency list.

```java
	ActiveJpaAgentLoader.instance().loadAgent();
```

### Setup EntityManagerFactory

You have to feed in the persistence unit to ActiveJpa to do the magic. There are multiple ways you can do this,

```java
	// Add the persistence unit defined by persistence.xml identified by the name 'order'. The persistence.xml should be available in the classpath
	JPA.addPersistenceUnit("order");
	
	// If you have entity manager factory already created, you can attach the same to ActiveJpa
	JPA.addPersistenceUnit("order", entityManagerFactory);
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

### Managing transactions

All the update operations in the model class will open up a transaction if one is not found in the current context. Below code demonstrates wrapping a unit of work under a transaction,

```java
	JPAContext context = JPA.instance.getDefaultConfig().getContext();
	context.beginTxn();
	boolean failed = true;
	try {
		// Your unit of work here
		failed = false;
	} finally {
		// Commit or rollback the transaction
		context.closeTxn(failed);
	}
	
```

### Testing your models

The setup done for taking care of byte code instrumentation applies for your test cases. But most of the IDE's support running individual test cases and adding the -javaagent option to every such run is a pain.

ActiveJpa provides an abstract model test class for tesng that enables instrumentation for all your modles without specifying -javaagent option to your test runs. To use this you will have to extend org.activejpa.entity.testng.BaseModelTest class,

```java
	public class OrderTest extends BaseModelTest {
		
		@Test
		public void testCreateOrder() {
			Order order = new Order();
			order.setCustomerEmail("dummyemail@dummy.com");
			...
			...
			order.persist();
			Assert.assertEquals(Order.where("customer_email", "dummyemail@dummy.com").get(0), order);
		}
	}
```

### Examples
#### Vanilla application
Checkout the [vanilla application](https://github.com/ActiveJpa/activejpa/tree/master/activejpa-examples/activejpa-examples-vanilla) using ActiveJpa
#### Spring integration
There's a [sample application](https://github.com/ActiveJpa/activejpa/tree/master/activejpa-examples/activejpa-examples-spring) that demonstrates the Spring-ActiveJpa integration

How to ask for help?
-------------------
Create an [issue](https://github.com/ActiveJpa/activejpa/issues) explaining your issue as detail as possible.


License
-------
ActiveJPA is offered under Apache License, Version 2.0
