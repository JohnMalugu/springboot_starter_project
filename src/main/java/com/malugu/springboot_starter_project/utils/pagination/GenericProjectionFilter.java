package com.malugu.springboot_starter_project.utils.pagination;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

public class GenericProjectionFilter<T> implements Predicate<T> {

    private final List<SearchFieldsDto> searchFields;
    private final SearchCombinationType combinationType;

    public GenericProjectionFilter(List<SearchFieldsDto> searchFields, SearchCombinationType combinationType) {
        this.searchFields = searchFields;
        this.combinationType = combinationType;
    }

    @Override
    public boolean test(T t) {
        if (searchFields == null || searchFields.isEmpty()) {
            return true;
        }

        Boolean[] results = searchFields.stream()
                .map(field -> testField(t, field))
                .map(Boolean::booleanValue)
                .toArray(Boolean[]::new);

        if (SearchCombinationType.AND.equals(combinationType)) {
            for (boolean res : results) if (!res) return false;
            return true;
        } else {
            for (boolean res : results) if (res) return true;
            return false;
        }
    }

    private boolean testField(T obj, SearchFieldsDto field) {
        try {
            String fieldName = field.getFieldName();
            String methodName = "get" + capitalize(fieldName);
            Method method = obj.getClass().getMethod(methodName);
            Object value = method.invoke(obj);

            String fieldValue = value != null ? value.toString().toLowerCase() : null;
            String searchValue = field.getFieldValue() != null ? field.getFieldValue().toLowerCase() : null;

            return switch (field.getSearchType()) {
                case Equals -> fieldValue != null && fieldValue.equals(searchValue);
                case Like -> fieldValue != null && searchValue != null && fieldValue.contains(searchValue);
                case NotEqual -> fieldValue != null && !fieldValue.equals(searchValue);
                case IsNull -> value == null;
                case IsNotNull -> value != null;
                case In -> field.getFieldValues() != null && field.getFieldValues().stream()
                        .map(String::toLowerCase).anyMatch(v -> v.equals(fieldValue));
                case NotIn -> field.getFieldValues() == null || field.getFieldValues().stream()
                        .map(String::toLowerCase).noneMatch(v -> v.equals(fieldValue));
                // Add more cases if needed...
                default -> fieldValue != null && fieldValue.equals(searchValue);
            };
        } catch (Exception e) {
            return false;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
