package com.msl.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @Discription: 强制读取和操作对象属性工具类
 * @Author Zaki Chen
 * @date 2019/9/30 14:15
 **/
public class ObjectManipulationUtil {

    private static volatile ObjectManipulationUtil util;

    private Unsafe unsafe;

    private ObjectManipulationUtil() {
        try {
            Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            unsafe = (Unsafe) theUnsafeField.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static ObjectManipulationUtil getInstance() {
        if (util == null) {
            synchronized (ObjectManipulationUtil.class) {
                util = new ObjectManipulationUtil();
            }
        }
        return util;

    }

    /**
     * 获取对象的成员变量属性值
     *
     * @param object       对象实例
     * @param propertyName 对象声明的属性名
     * @param dataType     获取的对象属性的声明类型
     * @return
     * @throws NoSuchFieldException
     */
    public Object getInstanceFieldValue(Object object, String propertyName, Type dataType) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(propertyName);
        long offset = unsafe.objectFieldOffset(field);
        return getValue(object, offset, dataType);
    }

    /**
     * 更新对象实例属性的值
     *
     * @param object       实例对象
     * @param propertyName 实例属性名 非static关键字声明的属性
     * @param val          要修改的值
     * @return
     */
    public void setInstanceFieldValue(Object object, String propertyName, Object val) throws NoSuchFieldException {
        Field propNameField = object.getClass().getDeclaredField(propertyName);
        long offset = unsafe.objectFieldOffset(propNameField);
        Type dataType = getType(val);
        setValue(object, offset, val, dataType);

    }

    /**
     * 修改类属性变量的值
     *
     * @param clazz        类
     * @param propertyName 类属性 static关键字声明的属性
     * @param val          值
     * @throws NoSuchFieldException
     */
    public void setStaticFieldValue(Class clazz, String propertyName, Object val) throws NoSuchFieldException {
        Field propNameField = clazz.getDeclaredField(propertyName);
        long offset = unsafe.staticFieldOffset(propNameField);
        Type dataType = getType(val);
        setValue(clazz, offset, val, dataType);
    }

    /**
     * 获取对象的类变量属性值
     *
     * @param clazz        Class对象
     * @param propertyName 对象类声明的属性名
     * @param dataType     获取的对象类属性的声明类型
     * @return
     * @throws NoSuchFieldException
     */
    public Object getStaticFieldValue(Class clazz, String propertyName, Type dataType) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(propertyName);
        long offset = unsafe.staticFieldOffset(field);
        return getValue(clazz, offset, dataType);
    }


    /**
     * 数据值类型枚举
     */
    public enum Type {
        /**
         * 引用类型 如String
         */
        Object,
        /**
         * Char类型
         */
        Char,
        /**
         * Byte类型
         */
        Byte,
        /**
         * Integer类型
         */
        Integer,
        /**
         * Double类型
         */
        Double,
        /**
         * Float类型
         */
        Float,
        /**
         * Long类型
         */
        Long,
        /**
         * Boolean类型
         */
        Boolean
    }

    private Type getType(Object val) {
        Type dataType = Type.Object;
        if (val instanceof Character) {
            dataType = Type.Char;
        } else if (val instanceof Byte) {
            dataType = Type.Byte;
        } else if (val instanceof Integer) {
            dataType = Type.Integer;
        } else if (val instanceof Double) {
            dataType = Type.Double;
        } else if (val instanceof Float) {
            dataType = Type.Float;
        } else if (val instanceof Long) {
            dataType = Type.Long;
        } else if (val instanceof Boolean) {
            dataType = Type.Boolean;
        }
        return dataType;
    }

    /**
     * 取对象数据
     *
     * @param object 对象：可以是一个实例对象，也可以是一个类对象
     * @param offset 内存偏移地址
     * @param type   返回数据的类型
     * @return
     */
    private Object getValue(Object object, long offset, Type type) {
        switch (type) {
            case Object:
                return unsafe.getObject(object, offset);
            case Char:
                return unsafe.getChar(object, offset);
            case Byte:
                return unsafe.getByte(object, offset);
            case Integer:
                return unsafe.getInt(object, offset);
            case Double:
                return unsafe.getDouble(object, offset);
            case Float:
                return unsafe.getFloat(object, offset);
            case Long:
                return unsafe.getLong(object, offset);
            case Boolean:
                return unsafe.getBoolean(object, offset);
            default:
                return null;
        }

    }

    /**
     * 放对象数据
     *
     * @param object 对象：可以是一个实例对象，也可以是一个类对象
     * @param offset 内存偏移地址
     * @param val    修改后的值
     * @param type   被修改属性的声明类型
     */
    private void setValue(Object object, long offset, Object val, Type type) {
        switch (type) {
            case Object:
                unsafe.putObjectVolatile(object, offset, val);
                break;
            case Char:
                unsafe.putCharVolatile(object, offset, (Character) val);
                break;
            case Byte:
                unsafe.putByteVolatile(object, offset, (Byte) val);
                break;
            case Integer:
                unsafe.putIntVolatile(object, offset, (Integer) val);
                break;
            case Double:
                unsafe.putDoubleVolatile(object, offset, (Double) val);
                break;
            case Float:
                unsafe.putFloatVolatile(object, offset, (Float) val);
                break;
            case Long:
                unsafe.putLongVolatile(object, offset, (Long) val);
                break;
            case Boolean:
                unsafe.putBooleanVolatile(object, offset, (Boolean) val);
                break;
        }
    }
}
