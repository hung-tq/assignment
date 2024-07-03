package com.externalLib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * csvLib library helps to read data from text file, handle Exception after call <p>
 * Example: <p>
 * <pre>
 * csvLib StaffRead = new csvLib(); <p>
 *StaffRead.setFieldOrder(new String[] { "StaffID", "FullName", "DateOfBirth", "PhoneNumber" }); <p>
 *StaffRead.setCsvSeperator(":");
 * </pre>
 * For reading: <p>
 * <pre>
 * setStaffArrayList((ArrayList<Staff>) StaffRead.readCsvFile("staff.txt", Staff.class, isHeaderSkipped));
 * </pre>
 * For writing: <p>
 * <pre>
 * String csv = StaffSave.toCsv(StaffArrayList); <p>
 *StaffSave.writeCsvToFile(csv, "staff_Backup.txt");
 * </pre>
 * 
 * @author hung-tq
 */
public class csvLib
{
    private String[] FIELD_ORDER;
    private String CSV_SEPARATOR = ",";

    /**
     * Method to set headers from left to right, which name match behind {@code get...()} method in class. <p>
     * Example: <p>
     * <pre> getAge(), getFullName(), ... </pre> then call <pre> .setFieldOrder(new String[] { "Age", "FullName", ... }) </pre>
     * @param FieldOrder list of names of getters or setters
     */
    public void setFieldOrder(String[] FieldOrder)
    {
        FIELD_ORDER = FieldOrder;
    }

    /**
     * Method to set CSV Seperator, default is comma. <p>
     * Example: <pre> .setCsvSeperator(","); </pre>
     * @param sepPattern pattern of seperator, like "," or ":" , ...
     */
    public void setCsvSeperator(String sepPattern)
    {
        CSV_SEPARATOR = sepPattern;
    }

    /**
     * Method to convert list of objects to CSV format. <p>
     * Example: <pre> .toCsv(listOfObjects); </pre>
     * @param <T> Class type
     * @param objectList list of objects
     * @return CSV format string
     */
    public <T> String toCsv(List<T> objectList)
    {
        if (objectList == null || objectList.isEmpty())
            return "";

        StringBuilder csv = new StringBuilder();

        // Add header
        boolean firstField = true;
        for (String fieldName : FIELD_ORDER)
        {
            Method getter = findGetterMethod(objectList.get(0).getClass(), fieldName);
            if (getter != null)
            {
                if (!firstField)
                    csv.append(CSV_SEPARATOR);
                csv.append(fieldName);
                firstField = false;
            }
        }
        csv.append("\n");

        // Add rows
        for (T obj : objectList)
        {
            firstField = true;
            for (String fieldName : FIELD_ORDER)
            {
                Method getter = findGetterMethod(obj.getClass(), fieldName);
                if (getter != null)
                {
                    if (!firstField)
                        csv.append(CSV_SEPARATOR);
                        
                    try
                    {
                        Object value = getter.invoke(obj);
                        csv.append(value != null ? value.toString() : "");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    firstField = false;
                }
            }
            csv.append("\n");
        }

        return csv.toString();
    }

    /**
     * Method to write CSV string to file. <p>
     * Example: <pre> .writeCsvToFile(csvString, "staff.txt"); </pre>
     * @param csv CSV string (to create, call {@code toCsv()} method first
     * @param filePath path of file
     * @throws IOException
     */
    public void writeCsvToFile(String csv, String filePath) throws IOException
    {
        File file = new File(filePath);
        file.createNewFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            writer.write(csv);
        }
    }

    /**
     * Method to find getter method of a field in a class. <p>
     * Example: <pre> .findGetterMethod(Staff.class, "Age"); </pre>
     * @param clazz Class type
     * @param fieldName Name of field
     * @return Id of getter method (example: <code> public java.lang.String com.lab6.model.Staff.getStaffID() </code>)
     */
    public Method findGetterMethod(Class<?> clazz, String fieldName)
    {
        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try
        {
            return clazz.getMethod(getterName);
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            return null;
        }
    }

    /**
     * Method to read CSV file to list of objects. <p>
     * Example: <pre> .readCsvFile("staff.txt", Staff.class, true); </pre>
     * @param <T> <strong>Class</strong> type
     * @param filePath Path of file
     * @param clazz Class type
     * @param isHeaderSkipped True if CSV file has no header line
     * @return list of <strong>Class</strong> type objects
     * @throws IOException
     */
    public <T> List<T> readCsvFile(String filePath, Class<T> clazz, boolean isHeaderSkipped) throws IOException
    {
        List<T> resultList = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile())))
        {
            String line;
            String[] headers = FIELD_ORDER;
            while ((line = br.readLine()) != null)
            {
                if (!isHeaderSkipped)
                {
                    headers = line.split(CSV_SEPARATOR);
                    isHeaderSkipped = true;
                    continue;
                }

                String[] fields = line.split(CSV_SEPARATOR);

                if (fields.length != headers.length) 
                    continue;

                T obj = createObjectFromCsv(fields, headers, clazz);
                resultList.add(obj);
            }
        }

        return resultList;
    }

    /**
     * Method to create object from CSV fields. <p>
     * Example: <pre> .createObjectFromCsv(fields, headers, Staff.class); </pre>
     * @param <T> <strong>Class</strong> type
     * @param fields list of fields
     * @param headers list of headers
     * @param clazz Class type
     * @return Object of <strong>Class</strong> type
     */
    public <T> T createObjectFromCsv(String[] fields, String[] headers, Class<T> clazz)
    {
        try
        {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            T obj = constructor.newInstance();

            for (int i = 0; i < headers.length; i++)
            {
                String fieldName = headers[i].trim();
                String fieldValue = fields[i].trim();
                Method setter = findSetterMethod(clazz, fieldName);
                if (setter != null)
                {
                    // System.out.println(fieldValue);
                    Class<?> paramType = setter.getParameterTypes()[0];
                    Object parsedValue = parseValue(paramType, fieldValue);
                    setter.invoke(obj, parsedValue);
                }
            }

            return obj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to find setter method of a field in a class. <p>
     * Example: <pre> .findSetterMethod(Staff.class, "Age"); </pre>
     * @param clazz Class type
     * @param fieldName Name of field
     * @return Id of setter method (example: <code> public void com.lab6.model.Staff.setStaffID(java.lang.String) </code>)
     */
    public Method findSetterMethod(Class<?> clazz, String fieldName)
    {
        String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        Method[] methods = clazz.getMethods();

        for (Method method : methods)
            if (method.getName().equals(setterName) && method.getParameterCount() == 1)
                return method;
        
        return null;
    }

    /**
     * Method to parse value from string to object type. <p>
     * Example: <pre> .parseValue(Integer.class, "123"); </pre>
     * @param targetType Class type
     * @param value String value
     * @return Object value that parsed from string to suitable type
     * @throws ParseException
     */
    public Object parseValue(Class<?> targetType, String value) throws ParseException
    {
        if (targetType == String.class)
            return value;
        
        else if (targetType == Integer.class || targetType == int.class)
            return Integer.parseInt(value);

        else if (targetType == Boolean.class || targetType == boolean.class)
            return Boolean.parseBoolean(value);
        
        else if (targetType == Double.class || targetType == double.class)
            return Double.parseDouble(value);
        
        else if (targetType == Float.class || targetType == float.class)
            return Float.parseFloat(value);
        
        else if (targetType == Long.class || targetType == long.class)
            return Long.parseLong(value);
        
        else if (targetType == Short.class || targetType == short.class)
            return Short.parseShort(value);
        
        else if (targetType == Byte.class || targetType == byte.class)
            return Byte.parseByte(value);

        else if (targetType == LocalDate.class || targetType == LocalDate.class)
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        else
            return value;
    }
}