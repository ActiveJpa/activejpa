/**
 * 
 */
package org.activejpa.enhancer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import javax.persistence.Entity;

import org.activejpa.entity.Filter;
import org.activejpa.entity.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class DomainClassEnhancer {
	
	private ClassPool classPool;
	
	private CtClass modelClass;
	
    private static final Logger logger = LoggerFactory.getLogger(DomainClassEnhancer.class);
    
    public DomainClassEnhancer(ClassPool classPool) {
    	this.classPool = classPool;
		try {
			modelClass = classPool.get(Model.class.getName());
		} catch (Exception e) {
			// Shouldn't get here
			logger.error("Class " + Model.class.getName() + " is not found", e);
			throw new IllegalStateException("Class " + Model.class.getName() + " is not found");
		}
	}
    
    public DomainClassEnhancer() {
    	this(ClassPool.getDefault());
	}
    
	public byte[] enhance(ClassLoader loader, String className) {
		try {
			logger.trace("Attempting to enahce the class - " + className);
			CtClass ctClass = classPool.get(className.replace("/", "."));
			if (! canEnhance(ctClass)) {
				return null;
			}
			logger.info("Transforming the class - " + className);
			ctClass.defrost();
			createModelMethods(ctClass);
			return ctClass.toBytecode();
		} catch (NotFoundException e) {
			// Can't do much. Just log and ignore
			logger.trace("Failed while transforming the class " + className, e);
		} catch (Exception e) {
			logger.error("Failed while transforming the class " + className, e);
			throw new RuntimeException("Failed while transforming the class " + className, e);
		}
		return null;
	}
	
	public boolean canEnhance(String className) {
		try {
			return canEnhance(classPool.get(className));
		} catch (Exception e) {
			logger.trace("Error while checking is the class can be enhanced", e);
			return false;
		}
	}
	
	protected boolean canEnhance(CtClass ctClass) throws IOException, NotFoundException {
		return isEntity(ctClass) && isExtendingModel(ctClass);
	}
	
	private void createModelMethods(CtClass ctClass) throws CannotCompileException {
		createMethod(ctClass, "findById", Model.class.getName(), "java.io.Serializable id");
		createMethod(ctClass, "all", "java.util.List");
		createMethod(ctClass, "count", "long");
		createMethod(ctClass, "deleteAll", "void");
		createMethod(ctClass, "exists", "boolean", "java.io.Serializable id");
		createMethod(ctClass, "where", "java.util.List", "Object[] paramValues");
		createMethod(ctClass, "where", "java.util.List", Filter.class.getName() + " filter");
		createMethod(ctClass, "one", Model.class.getName(), "Object[] paramValues");
		createMethod(ctClass, "first", Model.class.getName(), "Object[] paramValues");
	}
	
	private void createMethod(CtClass ctClass, String methodName, String returnType, String... arguments) throws CannotCompileException {
		logger.info("Creating the method - " + methodName + " under the class - " + ctClass.getName());
		StringWriter writer = new StringWriter();
		writer.append("public static ").append(returnType).append(" ").append(methodName).append("(");
		if (arguments != null && arguments.length > 0) {
			for (int i = 0; i < arguments.length - 1; i++) {
				writer.append(arguments[i]).append(", ");
			}
			writer.append(arguments[arguments.length - 1]);
		}
		writer.append(") {");
		if (! returnType.equals("void")) {
			writer.append("return (" + returnType + ")");
		}
		writer.append(methodName).append("(").append(ctClass.getName()).append(".class");
		if (arguments != null && arguments.length > 0) {
			for (int i = 0; i < arguments.length; i++) {
				writer.append(", ").append(arguments[i].split(" ")[1]);
			}
		}
		writer.append(");}");
		
		CtMethod method = null;
		try {
			method = getMethod(ctClass, methodName, arguments);
			if (method != null) {
				ctClass.removeMethod(method);
			}
		} catch (NotFoundException e) {
			logger.trace("Failed to get the method " + methodName, e);
			// Just ignore if the method doesn't exist already
		}
		logger.debug("Method src - " + writer.toString());
		
		method = CtNewMethod.make(writer.toString(), ctClass);
        ctClass.addMethod(method);
	}
	
	private CtMethod getMethod(CtClass ctClass, String methodName, String... arguments) throws NotFoundException {
		List<CtClass> paramTypes = new ArrayList<CtClass>();
		if (arguments != null) {
			for (String argument : arguments) {
				paramTypes.add(classPool.get(argument.split(" ")[0]));
			}
		}
		return ctClass.getDeclaredMethod(methodName, paramTypes.toArray(new CtClass[0]));
	}
	
	protected boolean isEntity(CtClass ctClass) throws IOException {
		return ctClass.hasAnnotation(Entity.class);
	}
	
	protected boolean isExtendingModel(CtClass ctClass) throws NotFoundException {
		return getSuperClasses(ctClass).contains(modelClass);
	}
	
	/**
	 * Returns the super classes from top to bottom. The {@link Object} class name will always be returned at index 0.
	 * 
	 * @param className
	 * @return
	 * @throws IOException
	 */
	protected List<CtClass> getSuperClasses(CtClass modelClass) throws NotFoundException {
		List<CtClass> superClasses = new ArrayList<CtClass>();
		CtClass superClass = getSuperClass(modelClass);
		if (superClass != null) {
			superClasses.addAll(getSuperClasses(superClass));
			superClasses.add(superClass);
		}
		return superClasses;
	}
	
	protected CtClass getSuperClass(CtClass modelClass) throws NotFoundException {
		return modelClass.getSuperclass();
	}
}
