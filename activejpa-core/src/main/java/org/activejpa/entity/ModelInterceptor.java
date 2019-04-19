/**
 * 
 */
package org.activejpa.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Origin;

/**
 * @author ganeshs
 *
 */
public class ModelInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(ModelInterceptor.class);
    
    /**
     * @param method
     * @param id
     * @return
     * @throws Throwable
     */
    public static Model findById(@Origin Method method, @Argument(0) Serializable id) throws Throwable {
        return invoke(method, new Class[] {Class.class, Serializable.class}, new Object[]{method.getDeclaringClass(), id});
    }
    
    /**
     * @param method
     * @param id
     * @return
     * @throws Throwable
     */
    public static boolean exists(@Origin Method method, @Argument(0) Serializable id) throws Throwable {
        return invoke(method, new Class[] {Class.class, Serializable.class}, new Object[]{method.getDeclaringClass(), id});
    }
    
    /**
     * @param method
     * @param paramValues
     * @return
     * @throws Throwable
     */
    public static Model one(@Origin Method method, @Argument(0) Object[] paramValues) throws Throwable {
        return invoke(method, new Class[] {Class.class, Object[].class}, new Object[]{method.getDeclaringClass(), paramValues});
    }
    
    /**
     * @param method
     * @param paramValues
     * @return
     * @throws Throwable
     */
    public static Model first(@Origin Method method, @Argument(0) Object[] paramValues) throws Throwable {
        return invoke(method, new Class[] {Class.class, Object[].class}, new Object[]{method.getDeclaringClass(), paramValues});
    }
    
    /**
     * @param method
     * @return
     * @throws Throwable
     */
    public static List<Model> all(@Origin Method method) throws Throwable {
        return invoke(method, new Class[] {Class.class}, new Object[]{method.getDeclaringClass()});
    }
    
    /**
     * @param method
     * @param paramValues
     * @return
     * @throws Throwable
     */
    public static List<Model> where(@Origin Method method, @Argument(0) Object[] paramValues) throws Throwable {
        return invoke(method, new Class[] {Class.class, Object[].class}, new Object[]{method.getDeclaringClass(), paramValues});
    }
    
    /**
     * @param method
     * @param filter
     * @return
     * @throws Throwable
     */
    public static List<Model> where(@Origin Method method, @Argument(0) Filter filter) throws Throwable {
        return invoke(method, new Class[] {Class.class, Filter.class}, new Object[]{method.getDeclaringClass(), filter});
    }
    
    /**
     * @param method
     * @param filter
     * @return
     * @throws Throwable
     */
    public static long count(@Origin Method method, @Argument(0) Filter filter) throws Throwable {
        return invoke(method, new Class[] {Class.class, Filter.class}, new Object[]{method.getDeclaringClass(), filter});
    }
    
    /**
     * @param method
     * @return
     * @throws Throwable
     */
    public static long count(@Origin Method method) throws Throwable {
        return invoke(method, new Class[] {Class.class}, new Object[]{method.getDeclaringClass()});
    }
    
    /**
     * @param method
     * @param filter
     * @throws Throwable
     */
    public static void deleteAll(@Origin Method method, @Argument(0) Filter filter) throws Throwable {
        invoke(method, new Class[] {Class.class, Filter.class}, new Object[]{method.getDeclaringClass(), filter});
    }
    
    /**
     * @param method
     * @throws Throwable
     */
    public static void deleteAll(@Origin Method method) throws Throwable {
        invoke(method, new Class[] {Class.class}, new Object[]{method.getDeclaringClass()});
    }
    
    public static Filter filter(@Origin Method method) throws Throwable {
    		return invoke(method, new Class[] {Class.class}, new Object[]{method.getDeclaringClass()});
    }
    
    /**
     * @param method
     * @param paramTypes
     * @param paramValues
     * @return
     * @throws Throwable
     */
    @SuppressWarnings("unchecked")
    private static <R> R invoke(Method method, Class<?>[] paramTypes, Object[] paramValues) throws Throwable {
        logger.trace("Invoking the method - {} in the class - {} with the parameter types - {} and parameter values - {}", method.getName(), method.getDeclaringClass(), paramTypes, paramValues);
        Class<?> modelClass = getModelClass(method.getDeclaringClass());
        if (modelClass == null) {
            // Shouldn't come here
            throw new IllegalStateException("Unable to find the super class " + Model.class);
        }
        try {
            return (R) modelClass.getDeclaredMethod(method.getName(), paramTypes).invoke(null, paramValues);
        } catch (InvocationTargetException e) {
            logger.debug("Failed while invoking the method - {} in the class - {}", method.getName(), method.getDeclaringClass(), e);
            throw e.getCause();
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
            logger.error("Failed while invoking the method - {} in the class - {}", method.getName(), method.getDeclaringClass(), e);
        }
        return null;
    }
    
    /**
     * @param clazz
     * @return
     */
    private static Class<?> getModelClass(Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return null;
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Model.class)) {
            return superClass;
        }
        return getModelClass(superClass);
    }

}