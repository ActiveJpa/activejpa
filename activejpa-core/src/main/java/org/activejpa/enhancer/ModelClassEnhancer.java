/**
 * 
 */
package org.activejpa.enhancer;

import java.io.Serializable;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.activejpa.entity.Filter;
import org.activejpa.entity.Model;
import org.activejpa.entity.ModelInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.description.type.TypeDescription.Generic.OfParameterizedType.ForGenerifiedErasure;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

/**
 * @author ganeshs
 *
 */
public class ModelClassEnhancer implements AgentBuilder.Transformer {
    
    private Instrumentation instrumentation;
    
    private static Set<String> transformedClasses = Sets.newHashSet();
	
    private static final Logger logger = LoggerFactory.getLogger(ModelClassEnhancer.class);
    
    /**
     * Default constructor
     */
    public ModelClassEnhancer() {
    }
    
    /**
     * @param instrumentation
     */
    public ModelClassEnhancer(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }
    
    /**
     * @return
     */
    public ClassFileTransformer getTransformer() {
        logger.info("Creating a class file transformer");
        AgentBuilder builder = new AgentBuilder.Default()
                .with(AgentBuilder.PoolStrategy.Default.FAST)
                .type(ElementMatchers.isSubTypeOf(Model.class).and(ElementMatchers.isAnnotatedWith(Entity.class)))
                .transform(this);
        return instrumentation != null ? builder.installOn(instrumentation) : builder.installOnByteBuddyAgent();
    }
    
    @Override
    public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        logger.info("Transforming the model class - {}", typeDescription.getActualName());
        if (transformedClasses.contains(typeDescription.getActualName())) {
            logger.info("Model class - {} is already transformed. Skipping", typeDescription.getActualName());
            return builder;
        }
        
        builder = defineMethod(builder, typeDescription, classLoader, "all", TargetType.resolve(new ForLoadedType(List.class), typeDescription));
        builder = defineMethod(builder, typeDescription, classLoader, "count", new ForLoadedType(long.class));
        builder = defineMethod(builder, typeDescription, classLoader, "count", new ForLoadedType(long.class), new TypeDescription.ForLoadedType(Filter.class));
        builder = defineMethod(builder, typeDescription, classLoader, "findById", ForGenerifiedErasure.of(new ForLoadedType(Model.class)), new ForLoadedType(Serializable.class));
        builder = defineMethod(builder, typeDescription, classLoader, "deleteAll", TypeDescription.VOID, new TypeDescription.ForLoadedType(Filter.class));
        builder = defineMethod(builder, typeDescription, classLoader, "deleteAll", TypeDescription.VOID);
        builder = defineMethod(builder, typeDescription, classLoader, "exists", new ForLoadedType(boolean.class), new ForLoadedType(Serializable.class));
        builder = defineMethod(builder, typeDescription, classLoader, "where", TargetType.resolve(new ForLoadedType(List.class), typeDescription), new ForLoadedType(Object[].class));
        builder = defineMethod(builder, typeDescription, classLoader, "where", TargetType.resolve(new ForLoadedType(List.class), typeDescription), new ForLoadedType(Filter.class));
        builder = defineMethod(builder, typeDescription, classLoader, "one", ForGenerifiedErasure.of(new ForLoadedType(Model.class)), new ForLoadedType(Object[].class));
        builder = defineMethod(builder, typeDescription, classLoader, "first", ForGenerifiedErasure.of(new ForLoadedType(Model.class)), new ForLoadedType(Object[].class));
        transformedClasses.add(typeDescription.getActualName());
        return builder;
    }
    
    /**
     * @param builder
     * @param typeDescription
     * @param classLoader
     * @param name
     * @param targetType
     * @param parameters
     * @return
     */
    private Builder<?> defineMethod(Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, String name, TypeDefinition targetType, TypeDefinition... parameters) {
        logger.info("Defining the method - {}.{} with return type - {} and parameters - {}", typeDescription.getActualName(), name, targetType.getActualName(), parameters);
        try {
            builder = builder.defineMethod(name, targetType, Ownership.STATIC, Visibility.PUBLIC).withParameters(parameters).intercept(MethodDelegation.to(ModelInterceptor.class));
            builder.make();
        } catch (Exception exception) {
            if (! (exception.getCause() instanceof NoSuchMethodException)) {
                logger.error("Failed while defining the method - {}.{}", typeDescription.getActualName(), name, exception);
                throw new AssertionError(exception);
            }
        }
        return builder;
    }
}
