/**
 * 
 */
package org.activejpa.enhancer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.sun.tools.doclint.Entity;

import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader.ChildFirst;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.pool.TypePool;

/**
 * A class loader that emulates the functionality of the agent. To be used for testing in an environment where loading agent is not possible
 * 
 * WARNING: Use it only for testing. Not recommended for production use. 
 * 
 * @author ganeshs
 *
 */
public class ModelClassLoader extends ChildFirst {
	
    private Set<String> excludedPackages = Sets.newHashSet("java.", "javax.", "sun.", "org.mockito");
	
	private Logger logger = LoggerFactory.getLogger(ModelClassLoader.class);
	
    public ModelClassLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ModelClassLoader(ClassLoader loader) {
        this(loader, Sets.newHashSet());
    }
	
	/**
	 * @param loader
	 * @param excludedPackages
	 */
	public ModelClassLoader(ClassLoader loader, Set<String> excludedPackages) {
		super(loader, new HashMap<>(), ClassLoadingStrategy.NO_PROTECTION_DOMAIN, PersistenceHandler.MANIFEST, PackageDefinitionStrategy.Trivial.INSTANCE, new ModelClassEnhancer().getTransformer());
		if (excludedPackages != null) {
		    this.excludedPackages.addAll(excludedPackages);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
	    try {
            if (! super.typeDefinitions.containsKey(name) && !isExcluded(name)) {
                Class clazz = getParent().loadClass(name);
                if (clazz.isAnnotationPresent(Entity.class)) {
                    logger.debug("Extracting the class - {}", name);
                    byte[] bytes = extract(clazz);
                    super.typeDefinitions.put(name, bytes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	    return super.loadClass(name, false);
	}
	
	
    /**
     * @param name
     * @return
     */
    private boolean isExcluded(String name) {
        return excludedPackages.stream().anyMatch(pkg -> name.startsWith(pkg));
    }
    
    /**
     * @param type
     * @return
     * @throws IOException
     */
    private byte[] extract(Class<?> type) throws IOException {
        ClassReader classReader = new ClassReader(type.getName());
        ClassWriter classWriter = new ClassWriter(classReader, AsmVisitorWrapper.NO_FLAGS);
        classReader.accept(new AsmVisitorWrapper.Compound().wrap(new TypeDescription.ForLoadedType(type),
                classWriter,
                new IllegalContext(),
                TypePool.Empty.INSTANCE,
                new FieldList.Empty<FieldDescription.InDefinedShape>(),
                new MethodList.Empty<MethodDescription>(),
                AsmVisitorWrapper.NO_FLAGS,
                AsmVisitorWrapper.NO_FLAGS), AsmVisitorWrapper.NO_FLAGS);
        return classWriter.toByteArray();
    }
    
    /**
     * @author ganeshs
     *
     */
    private static class IllegalContext implements Implementation.Context {

        @Override
        public TypeDescription register(AuxiliaryType auxiliaryType) {
            throw new AssertionError("Did not expect method call");
        }

        @Override
        public FieldDescription.InDefinedShape cache(StackManipulation fieldValue, TypeDescription fieldType) {
            throw new AssertionError("Did not expect method call");
        }

        @Override
        public TypeDescription getInstrumentedType() {
            throw new AssertionError("Did not expect method call");
        }

        @Override
        public ClassFileVersion getClassFileVersion() {
            throw new AssertionError("Did not expect method call");
        }

        @Override
        public MethodDescription.InDefinedShape registerAccessorFor(Implementation.SpecialMethodInvocation specialMethodInvocation, AccessType accessType) {
            throw new AssertionError("Did not expect method call");
        }

        @Override
        public MethodDescription.InDefinedShape registerGetterFor(FieldDescription fieldDescription, AccessType accessType) {
            throw new AssertionError("Did not expect method call");
        }

        @Override
        public MethodDescription.InDefinedShape registerSetterFor(FieldDescription fieldDescription, AccessType accessType) {
            throw new AssertionError("Did not expect method call");
        }
    }
}
