package com.malugu.springboot_starter_project.utils.pagination;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import com.malugu.springboot_starter_project.utils.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class GenericSpecificationSearch<T> {
    public Specification<T> getSearchSpecification (List<SearchFieldsDto> searchFieldsDtos, SearchCombinationType searchCombinationType) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (SearchFieldsDto searchFieldsDto : searchFieldsDtos) {

                switch (searchFieldsDto.getSearchType()) {
                    case In -> predicates.add(getPath(root, searchFieldsDto.getFieldName()).as(String.class)
                            .in(searchFieldsDto.getFieldValues()));
                    case NotIn -> predicates.add(getPath(root, searchFieldsDto.getFieldName()).as(String.class)
                            .in(searchFieldsDto.getFieldValues()).not());
                    case Equals -> predicates.add(criteriaBuilder.equal(getPath(root, searchFieldsDto.getFieldName()),
                            searchFieldsDto.getFieldValue()));
                    case GreaterThan ->
                            predicates.add(criteriaBuilder.greaterThan(root.get(searchFieldsDto.getFieldName()),
                                    searchFieldsDto.getFieldValue()));
                    case LessThan -> predicates.add(criteriaBuilder.lessThan(root.get(searchFieldsDto.getFieldName()),
                            searchFieldsDto.getFieldValue()));
                    case GreaterThanEqual ->
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(searchFieldsDto.getFieldName()),
                                    searchFieldsDto.getFieldValue()));
                    case LessThanEqual ->
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(searchFieldsDto.getFieldName()),
                                    searchFieldsDto.getFieldValue()));
                    case Like -> predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(getPath(root, searchFieldsDto.getFieldName()).as(String.class)),
                            "%" + searchFieldsDto.getFieldValue().toLowerCase() + "%"));
                    case IsNull -> predicates.add(criteriaBuilder.isNull(root.get(searchFieldsDto.getFieldName())));
                    case IsNotNull ->
                            predicates.add(criteriaBuilder.isNotNull(root.get(searchFieldsDto.getFieldName())));
                    case NotEqual -> predicates.add(criteriaBuilder.notLike(
                            criteriaBuilder.lower(getPath(root, searchFieldsDto.getFieldName()).as(String.class)),
                            "%" + searchFieldsDto.getFieldValue().toLowerCase() + "%"));
                    case Between -> {
                        if (root.get(searchFieldsDto.getFieldName()).getJavaType().equals(Date.class)) {
                            try {
                                Date startDate = Utility.stringToOldDate(searchFieldsDto.getFieldValues().get(0));
                                Date endDate = Utility.stringToOldDate(searchFieldsDto.getFieldValues().get(1));
                                predicates.add(criteriaBuilder.between(root.get(searchFieldsDto.getFieldName()), startDate, endDate));
                            } catch (Exception ex) {
                                Utility.formatException(ex);
                                return null;
                            }
                        } else if (root.get(searchFieldsDto.getFieldName()).getJavaType().equals(LocalDate.class)) {
                            LocalDate startDate = Utility.parseToLocalDate(searchFieldsDto.getFieldValues().get(0));
                            LocalDate endDate = Utility.parseToLocalDate(searchFieldsDto.getFieldValues().get(1));
                            predicates.add(
                                    criteriaBuilder.between(root.get(searchFieldsDto.getFieldName()), startDate, endDate));
                        } else {
                            LocalDateTime startDate = Utility
                                    .parseDate(searchFieldsDto.getFieldValues().get(0) + " 00:00:00.0");
                            LocalDateTime endDate = Utility
                                    .parseDate(searchFieldsDto.getFieldValues().get(1) + " 23:59:00.0");
                            predicates.add(
                                    criteriaBuilder.between(root.get(searchFieldsDto.getFieldName()), startDate, endDate));
                        }
                    }
                    default -> predicates.add(criteriaBuilder.equal(getPath(root, searchFieldsDto.getFieldName()),
                            searchFieldsDto.getFieldValue()));
                }
            }

            if (searchCombinationType != null) {
                if (searchCombinationType.equals(SearchCombinationType.AND)) {
                    return !searchFieldsDtos.isEmpty() ? criteriaBuilder.and(predicates.toArray(new Predicate[0])) : null;
                }
            }
            return !searchFieldsDtos.isEmpty() ? criteriaBuilder.or(predicates.toArray(new Predicate[0])) : null;
        };
    }

    public Specification<T> getEqualsSpecification(String fieldName, Object fieldValue) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(getPath(root, fieldName), fieldValue);
    }

    public Specification<T> getNotEqualSpecification(String fieldName, Object fieldValue) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(getPath(root, fieldName), fieldValue);

    }

    public Specification<T> getIsNotNullSpecification(String fieldName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(fieldName));
    }

    public Specification<T> getIsNullSpecification(String fieldName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(fieldName));
    }

    public Specification<T> getUserNotInSpecification(String fieldName, Long userAccountId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(getPath(root, fieldName), userAccountId));
            predicates.add(criteriaBuilder.isNull(getPath(root, fieldName)));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<T> getDateRangeSpecification(String fieldName, LocalDate startDate, LocalDate endDate) {
        LocalDateTime dateOne = Utility.parseDate(startDate + " 00:00:00.0");
        LocalDateTime dateTwo = Utility.parseDate(endDate + " 23:59:00.0");
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(fieldName), dateOne, dateTwo);
    }

    public Specification<T> getDateOnlyRangeSpecification(String fieldName, LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(fieldName), startDate, endDate);
    }

    public Specification<T> getLocalDateTimeRangeSpecification(String fieldName, LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(fieldName), startDate, endDate);
    }

    public Specification<T> getDateGreaterThanOrEqualSpecification(String fieldName, LocalDate fieldValue) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), fieldValue);
    }

    public Specification<T> getDateStringGreaterThanOrEqualSpecification(String fieldName, String fieldValue) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), fieldValue);
    }

    public Specification<T> getDateGreaterThanSpecification(String fieldName, LocalDate fieldValue) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(fieldName), fieldValue);
    }

    public Specification<T> getDateLessThanOrEqualSpecification(String fieldName, LocalDate fieldValue) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), fieldValue);
    }

    public Specification<T> getDateStringLessThanOrEqualSpecification(String fieldName, String fieldValue) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), fieldValue);
    }

    public Specification<T> getDateLessThanSpecification(String fieldName, LocalDate fieldValue) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(fieldName), fieldValue);
    }

    public Specification<T> getInSpecification(String fieldName, List<Long> fieldValue) {
        return (root, query, criteriaBuilder) -> root.get(fieldName).in(fieldValue);
    }

    public Specification<T> getNotInSpecification(String fieldName, List<Long> fieldValue) {
        return (root, query, criteriaBuilder) -> root.get(fieldName).in(fieldValue).not();
    }

    public Specification<T> getDateGreaterThanOrEqualToSpecification(String fieldName, LocalDate date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), date);
    }


    public Path<T> getPath(Root<T> root, String attributeName) {
        if (attributeName.indexOf(".") > 0) {
            String[] parts = attributeName.split("\\.");
            From<?, ?> join = root.join(parts[0]);

            for (int i = 1; i < parts.length - 1; i++) {
                join = join.join(parts[i], JoinType.LEFT);
            }

            return join.get(parts[parts.length - 1]);
        }

        return root.get(attributeName);
    }
}
