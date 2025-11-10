package com.malugu.springboot_starter_project.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class Utility {

    public static String toString(Object obj) {
        return toJSON(obj, false);
    }

    public static String toString(Object obj, @NotNull boolean pretty) {
        return toJSON(obj, pretty);

    }

    public static ObjectMapper getObjectMapper() {
        SimpleModule module = new SimpleModule();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(module);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }

    public static String toJSON(Object obj, boolean pretty) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            mapper.registerModule(new JavaTimeModule());
            mapper.registerModule(module);

            mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            return pretty ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj) : mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Error serializing object to JSON", e);
            log.error(Utility.formatException(e), e);
        }
        return obj.toString();
    }

    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());

            if (srcValue == null) emptyNames.add(pd.getName());
        }
        emptyNames.add("id");
        emptyNames.add("uuid");

        return emptyNames.toArray(new String[0]);
    }

    public static String harshMethod(String string) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(string.getBytes());

        byte[] byteData = md.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xFF) + 256, 16).substring(1));
        }
        return sb.toString();
    }

    public static LocalDate parseToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static LocalDateTime parseDate(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
            } catch (Exception ex) {
                try {
                    return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (Exception ec) {
                    try {
                        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S"));
                    } catch (Exception exc) {
                        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                    }
                }
            }
        }
    }

    public static Date stringToOldDate(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
    }

    public static String incrementString(String input) {

        String numericPart = input.replaceAll("[^0-9]", "");
        int numericValue = Integer.parseInt(numericPart);

        String incrementedString = autoIncrementAndFormat(numericValue);
        return incrementedString;
    }

    public static String autoIncrementAndFormat(int input) {
        int incrementedValue = input + 1;
        return String.format("%05d", incrementedValue);
    }

    public static String GenerateUniqueID() {
        return UUID.randomUUID().toString();
    }

    public static String getWrappedRandomFileName(String fileName) {
        String fileExtension = getFileExtension(fileName);
        return getRandomString() + "-" + getRandomString() + "." + fileExtension;
    }

    public static String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse(null);
    }

    public static String getFileBaseName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String getReferenceNumber() {
        UUID idOne = UUID.randomUUID();
        UUID idTwo = UUID.randomUUID();
        UUID idThree = UUID.randomUUID();
        UUID idFour = UUID.randomUUID();

        String time = idOne.toString().replace("-", "");
        String time2 = idTwo.toString().replace("-", "");
        String time3 = idThree.toString().replace("-", "");
        String time4 = idFour.toString().replace("-", "");

        StringBuilder data = new StringBuilder();
        data.append(System.currentTimeMillis());
        data.append(time);
        data.append(time2);
        data.append(2);
        data.append(time3);
        data.append(time4);

        SecureRandom random = new SecureRandom();
        int beginIndex = random.nextInt(100);       //Begin index + length of your string < data length
        int endIndex = beginIndex + 6;            //Length of string which you want

        return data.substring(beginIndex, endIndex).toUpperCase();
    }

    public static String convertArrayToString(List<String> listOfString) {
        return listOfString.toString().replaceAll("[^a-zA-Z,]+", "");
    }

    public static String extractXmlElement(String rootElement, String fullDigitalSignedMessage) {
        int firstIndex = fullDigitalSignedMessage.indexOf("<" + rootElement + ">");
        String lastTag = "</" + rootElement + ">";
        int lastIndex = fullDigitalSignedMessage.lastIndexOf(lastTag) + lastTag.length();
        return fullDigitalSignedMessage.substring(firstIndex, lastIndex).trim();
    }

    public static String toPhoneNumber(String input) {
        input = "000000000" + input;
        input = input.replaceAll("\\s", "");
        return input.substring(input.length() - 9);
    }

    public static String formatException(Exception e) {
        try {
            StackTraceElement[] stackTrace = e.getStackTrace();
            StringBuilder formattedError = new StringBuilder();

            // Add exception message
            formattedError.append("Exception: ")
                    .append(e.getClass().getName())
                    .append(": ")
                    .append(e.getMessage())
                    .append("\n");

            // Add affected file and line number (using the first element in the stack trace that belongs to your application)
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                if (className.startsWith("com.malugu.springboot_starter_project.ods")) {
                    formattedError.append("Location: ")
                            .append(className)
                            .append(".")
                            .append(element.getMethodName())
                            .append("(")
                            .append(element.getFileName())
                            .append(":")
                            .append(element.getLineNumber())
                            .append(")\n");
                    break;
                }
            }

            // Add cause of the exception
            Throwable cause = e.getCause();
            if (cause != null) {
                formattedError.append("Caused by: ")
                        .append(cause.getClass().getName())
                        .append(": ")
                        .append(cause.getMessage());
            }
            return formattedError.toString();
        } catch (Exception ex) {
            log.error("Failed to format the exception - {}", ex.getMessage(), ex);
            return ex.getMessage();
        }
    }

    public static boolean isValidEmail(String email) {
        if (StringUtils.isBlank(email)) return false;

        Pattern emailPattern = Pattern.compile(Constants.EMAIL_PATTERN);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    public static String generateInstitutionEmail(String firstName, String surname, String institutionEmail) {
        String institutionDomain = institutionEmail.split("@")[1];
        String userDomainName = firstName + "." + surname;
        String email = "%s@%s".formatted(userDomainName, institutionDomain).toLowerCase();

        log.info("==> Generated Email for '{}': {}", firstName, email);
        return email;
    }

    public static HttpServletRequest getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest).orElse(null);
    }

    public static LocalDate stringToLocalDateFormat(String dateToConvert) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(dateToConvert, formatter);
    }

    public static String toCamelCase(String className) {
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    public static <T> Page<T> convertListToPage(Pageable pageable, List<T> list) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        List<T> subList = list.subList(start, end);

        return new PageImpl<>(subList, pageable, list.size());
    }

    public static String camelCaseToString(String camelCaseString) {
        String[] words = camelCaseString.split("(?=[A-Z])");
        return String.join(" ", words);
    }

    public static boolean validNin(String nin) {
        Pattern pattern = Pattern.compile("\\d{20}");
        return pattern.matcher(nin).matches();
    }

    public static boolean areAllFieldsFilled(Object obj, Set<String> excludeFields) {
        if (obj == null) return false;
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (excludeFields != null && excludeFields.contains(field.getName())) continue;

            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value == null) return false;
                if (value instanceof String && ((String) value).isBlank()) return false;
            } catch (IllegalAccessException e) {
                return false;
            }
        }
        return true;
    }

    private static boolean isBlank(String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
    }

    public static String generateForgotPasswordToken() {
        final SecureRandom secureRandom = new SecureRandom();
        final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

        byte[] randomBytes = new byte[48];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
