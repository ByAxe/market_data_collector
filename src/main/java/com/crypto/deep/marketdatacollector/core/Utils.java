package com.crypto.deep.marketdatacollector.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.time.temporal.ChronoUnit.MONTHS;

public class Utils {

    public static final LocalDateTime THRESHOLD = LocalDateTime.of(2017, 12, 28, 0, 0, 0);

    /**
     * Преобразование {@link LocalDateTime} в JSON формат
     */
    private final static String TIME_TEMPLATE = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    private final static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
    }

    /**
     * Конвертирует {@link LocalDate} в {@link Date} с применением текущей локали
     * <p>
     *
     * @param source Исходная дата в {@link LocalDate}
     * @return Результативная дата в {@link Date}
     */
    public static Date convertLocalDateToDate(LocalDate source) {
        if (source == null) return null;
        return Date.from(source.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Конвертирует {@link Date} в {@link LocalDate} с применением текущей локали
     * <p>
     *
     * @param source Исходная дата в {@link Date}
     * @return Результативная дата в {@link LocalDateTime}
     */
    public static LocalDate convertDateToLocalDate(Date source) {
        if (source == null) return null;
        return LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Возвращает количество дней с начала эпохи по определенной дате
     *
     * @param date Дата
     * @return Дата в днях с начала эпохи
     */
    public static long getEpochDays(LocalDate date) {
        return date.atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay();
    }

    /**
     * Возвращает количество дней с начала эпохи по определенной дате
     *
     * @param date Дата
     * @return Дата в днях с начала эпохи
     */
    public static long getEpochDays(LocalDateTime date) {
        return date.atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay();
    }

    /**
     * Конвертирует {@link Date} в {@link LocalDateTime} с применением текущей локали
     * <p>
     *
     * @param source Исходная дата в {@link Date}
     * @return Результативная дата в {@link LocalDateTime}
     */
    public static LocalDateTime convertDateToLocalDateTime(Date source) {
        if (source == null) return null;
        return LocalDateTime.ofInstant(new Date(source.getTime()).toInstant(), ZoneId.systemDefault());
    }

    /**
     * Конвертирует {@link LocalDateTime} в {@link Date} с применением текущей локали
     * <p>
     *
     * @param source Исходная дата в {@link LocalDateTime}
     * @return Результативная дата в {@link Date}
     */
    public static Date convertLocalDateTimeToDate(LocalDateTime source) {
        if (source == null) return null;
        return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Конвертирует {@link LocalDateTime} в {@link Long}
     *
     * @param source Исходная дата в {@link LocalDateTime}
     * @return Результативная дата в {@link Long}
     */
    public static long convertLocalDateTimeToMills(LocalDateTime source) {
        if (source == null) return 0;
        return Utils.convertLocalDateTimeToDate(source).getTime();
    }

    /**
     * Конвертирует {@link LocalDate} в {@link LocalDateTime}
     *
     * @param source исходная дата в {@link LocalDate}
     * @return результат в виде {@link LocalDateTime}
     */
    public static LocalDateTime convertLocalDateToLocalDateTime(LocalDate source) {
        if (source == null) return null;
        return Timestamp.valueOf(source.atStartOfDay()).toLocalDateTime();
    }

    /**
     * Конвертирует количество дней с начала эпохи {@link Long} в {@link LocalDate} с применением текущей локали
     *
     * @param epochDay Дата в днях с начала эпохи {@link Long}
     * @return Результат в {@link LocalDate}
     */
    public static LocalDate convertEpochDaysToLocalDate(long epochDay) {
        return convertDateToLocalDate(new Date(LocalDate.ofEpochDay(epochDay)
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1000));
    }

    /**
     * Конвернтируем дату в милисекундах {@link Long} в {@link LocalDate}
     *
     * @param mills Дата в милисекундах
     * @return Результат в {@link LocalDate}
     */
    public static LocalDate longToLocalDate(Long mills) {
        return Optional.ofNullable(mills)
                .map(Date::new)
                .map(Utils::convertDateToLocalDate)
                .orElseThrow(RuntimeException::new);
    }

    /**
     * Конвертация указанной даты в милисекунды
     *
     * @param date исходная дата
     * @return
     */
    public static long localDateToLong(LocalDate date) {
        ZoneId zoneId = ZoneId.systemDefault();
        return date.atStartOfDay(zoneId).toEpochSecond() * 1000;
    }


    /**
     * Конвертирует json {@link String} в необходимый нам формат {@link F}
     *
     * @param s   json в виде {@link String}
     * @param <F> Тип в который мы конвертируем
     * @return необходимый нам формат {@link F}
     */
    public static <F> F convertJsonToObject(String s) throws Exception {
        return mapper.readValue(s, new TypeReference<F>() {
        });
    }

    /**
     * Превращаем какой то обьект в json строку
     *
     * @param o исходный обьект
     * @return строка json
     */
    public static String convertObjectToJson(Object o) throws Exception {
        return mapper.writeValueAsString(o);
    }

    /**
     * Конвертируем дни с начала эпохи в миллисекунды с начала эпохи
     *
     * @param epochDay количество дней с начала эпохи
     * @return количество миллисекунд с начала эпохи
     */
    public static long convertEpochDayToEpochMills(long epochDay) {
        return LocalDate.ofEpochDay(epochDay).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
    }

    /**
     * Конвертируем дни с начала эпохи в месяцы с начала эпохи
     *
     * @param epochDays дни с начала эпохи
     * @return месяцы с начала эпохи
     */
    public static int convertEpochDaysToEpochMonth(long epochDays) {
        final LocalDate localDate = convertEpochDaysToLocalDate(epochDays);
        return convertLocalDateToEpochMonth(localDate);
    }

    /**
     * Конвертируем {@link LocalDate} в месяцы с начала эпохи
     *
     * @param localDate дата в {@link LocalDate}
     * @return количество месяцев с начала эпохи
     */
    public static int convertLocalDateToEpochMonth(LocalDate localDate) {
        return Math.toIntExact(MONTHS.between(LocalDate.ofEpochDay(0), localDate));
    }

    /**
     * Превращаем {@link Date} в {@link java.sql.Date}
     *
     * @param date java формат даты в {@link Date}
     * @return jdbc (sql) формат даты в {@link java.sql.Date}
     */
    public static java.sql.Date convertJavaDateToSqlDate(Date date) {
        return date != null ? new java.sql.Date(date.getTime()) : null;
    }

    public static DateTimeFormatter getFormat() {
        return DateTimeFormatter.ofPattern(Utils.TIME_TEMPLATE);
    }

    public static LocalDateTime convertMillsToLocalDateTime(long l) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
    }

    public static boolean atLeastOneIsNull(Object... o) {
        List<Object> nullableObjects;
        if (o != null) {
            nullableObjects = Arrays.asList(o);

            for (Object nullableObject : nullableObjects) {
                if (nullableObject == null) return true;
            }
        }
        return false;
    }

    public static XMLGregorianCalendar convertLocalDateTimeToXMLGregorianCalendar(LocalDateTime localDateTime) throws Exception {
        String iso = localDateTime.toString();
        if (localDateTime.getSecond() == 0 && localDateTime.getNano() == 0) {
            iso += ":00"; // necessary hack because the second part is not optional in XML
        }
        XMLGregorianCalendar xml =
                DatatypeFactory.newInstance().newXMLGregorianCalendar(iso);

        return xml;
    }

    public static void unzip(byte[] content, String folderName) throws Exception {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content));
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {

            String fileName = ze.getName();
            File newFile = new File(folderName + File.separator + fileName);

            System.out.println("file unzip : " + newFile.getAbsoluteFile());

            //create all non exists folders
            //else you will hit FileNotFoundException for compressed folder
            new File(newFile.getParent()).mkdirs();

            FileOutputStream fos = new FileOutputStream(newFile);

            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();
            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();

        System.out.println("Done");
    }

    public static byte[] unzip(byte[] binContent) throws IOException {
        final int BUFFER = 2048;

        ByteArrayOutputStream dest = new ByteArrayOutputStream();

        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(binContent));

        ZipEntry zipEntry = zipInputStream.getNextEntry();
        int count;
        byte data[] = new byte[BUFFER];

        while (zipEntry != null) {
            while ((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
        return dest.toByteArray();
    }

    public static void flow(InputStream is, OutputStream os, byte[] buf) throws IOException {
        int numRead;
        while ((numRead = is.read(buf)) >= 0) {
            os.write(buf, 0, numRead);
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal(String.valueOf(value));
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }

}
