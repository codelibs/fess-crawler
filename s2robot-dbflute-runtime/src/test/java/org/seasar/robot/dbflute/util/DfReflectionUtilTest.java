package org.seasar.robot.dbflute.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.6.7 (2010/03/30 Tuesday)
 */
public class DfReflectionUtilTest extends PlainTestCase {

    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    public void test_getPublicMethod_basic() throws Exception {
        // ## Arrange ##
        String methodName = "fooNoArg";

        // ## Act ##
        Method method = DfReflectionUtil.getPublicMethod(FooTarget.class, methodName, null);

        // ## Assert ##
        assertEquals(methodName, method.getName());
    }

    public void test_invoke_basic() throws Exception {
        // ## Arrange ##
        String methodName = "fooNoArg";
        Method method = DfReflectionUtil.getPublicMethod(FooTarget.class, methodName, null);

        // ## Act ##
        Object result = DfReflectionUtil.invoke(method, new FooTarget(), null);

        // ## Assert ##
        assertEquals("foo", result);
    }

    public static class FooTarget {
        public String fooNoArg() {
            return "foo";
        }
    }

    // ===================================================================================
    //                                                                             Generic
    //                                                                             =======
    public void test_getElementType_List() throws Exception {
        // ## Arrange ##
        Type genericType = getListMethod().getGenericReturnType();

        // ## Act ##
        Class<?> elementType = DfReflectionUtil.getGenericType(genericType);

        // ## Assert ##
        log("genericType = " + genericType);
        log("elementType = " + elementType);
        assertEquals(String.class, elementType);
    }

    public void test_getElementType_Set() throws Exception {
        // ## Arrange ##
        Type genericType = getSetMethod().getGenericReturnType();

        // ## Act ##
        Class<?> elementType = DfReflectionUtil.getGenericType(genericType);

        // ## Assert ##
        log("genericType = " + genericType);
        log("elementType = " + elementType);
        assertEquals(String.class, elementType);
    }

    public void test_getElementType_Collection() throws Exception {
        // ## Arrange ##
        Type genericType = getCollectionMethod().getGenericReturnType();

        // ## Act ##
        Class<?> elementType = DfReflectionUtil.getGenericType(genericType);

        // ## Assert ##
        log("genericType = " + genericType);
        log("elementType = " + elementType);
        assertEquals(String.class, elementType);
    }

    public void test_getElementType_nestedList() throws Exception {
        // ## Arrange ##
        Type genericType = getNestedListMethod().getGenericReturnType();

        // ## Act ##
        Class<?> elementType = DfReflectionUtil.getGenericType(genericType);

        // ## Assert ##
        log("genericType = " + genericType);
        log("elementType = " + elementType);
        assertEquals(List.class, elementType);
    }

    public void test_getElementType_beanList() throws Exception {
        // ## Arrange ##
        Type genericType = getBeanListMethod().getGenericReturnType();

        // ## Act ##
        Class<?> elementType = DfReflectionUtil.getGenericType(genericType);

        // ## Assert ##
        log("genericType = " + genericType);
        log("elementType = " + elementType);
        assertEquals(FooGeneric.class, elementType);
    }

    public void test_getElementType_nonGeneric() throws Exception {
        // ## Arrange ##
        Type genericType = getNonGenericMethod().getGenericReturnType();

        // ## Act ##
        Class<?> elementType = DfReflectionUtil.getGenericType(genericType);

        // ## Assert ##
        log("genericType = " + genericType);
        log("elementType = " + elementType);
        assertNull(elementType);
    }

    protected Method getListMethod() throws Exception {
        return FooGeneric.class.getMethod("fooList", (Class<?>[]) null);
    }

    protected Method getSetMethod() throws Exception {
        return FooGeneric.class.getMethod("fooSet", (Class<?>[]) null);
    }

    protected Method getCollectionMethod() throws Exception {
        return FooGeneric.class.getMethod("fooCollection", (Class<?>[]) null);
    }

    protected Method getNestedListMethod() throws Exception {
        return FooGeneric.class.getMethod("fooNestedList", (Class<?>[]) null);
    }

    protected Method getBeanListMethod() throws Exception {
        return BarGeneric.class.getMethod("barBeanList", (Class<?>[]) null);
    }

    protected Method getNonGenericMethod() throws Exception {
        return FooGeneric.class.getMethod("fooNonGeneric", (Class<?>[]) null);
    }

    public static class FooGeneric {
        public List<String> fooList() {
            return new ArrayList<String>();
        }

        public Set<String> fooSet() {
            return new HashSet<String>();
        }

        public Collection<String> fooCollection() {
            return new ArrayList<String>();
        }

        public List<List<String>> fooNestedList() {
            return new ArrayList<List<String>>();
        }

        public String fooNonGeneric() {
            return "foo";
        }
    }

    public static class BarGeneric {
        public List<FooGeneric> barBeanList() {
            return new ArrayList<FooGeneric>();
        }
    }
}
