package com.example.demo.filter;

import com.google.common.base.CaseFormat;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SearchSpecification<T> implements Specification<T> {
    private final List<SearchCriteria> params;

    public SearchSpecification(List<SearchCriteria> params) {
        this.params = params;
    }

    @Override
    public Predicate toPredicate(@Nullable Root<T> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (params != null) {
            SearchCriteria isGlobalFound = null;
            boolean isGlobalItems = false;
            List<Predicate> predicates = new ArrayList<>();
            for (SearchCriteria param : params) {
                if (!param.getKey().equals("status")) {

                    String[] keys = param.getKey().split("\\.");
                    if (param.getKey().split("_").length > 1)
                        param.setKey(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, param.getKey()));

                    if (keys.length > 1) {
                        Join<?, ?> join = root.join(keys[0]);
                        for (int i = 1; i < keys.length - 1; i++) {
                            join = join.join(keys[i]);
                        }

                        param.setKey(keys[keys.length - 1]);
//                        predicates.add(getPredicate(param, criteriaBuilder, join));
                    } else {
//                        predicates.add(getPredicate(param, criteriaBuilder, root));
                    }

                    if (param.getIsGlobal()) {
                        isGlobalItems = true;
                    }

                } else if (isGlobalFound == null && param.getKey().equals("status")) {
                    isGlobalFound = param;
                }
            }

            Predicate predicate1 = null;

            if (isGlobalItems) {
                predicate1 = criteriaBuilder.or(predicates.toArray(Predicate[]::new));
                predicate = criteriaBuilder.and(predicate, predicate1);
            } else {
                predicate1 = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return criteriaBuilder.and(predicate, predicate1);
            }

            if (isGlobalFound != null) {
                Predicate predicateParam = null;
//                predicateParam = getPredicate(isGlobalFound, criteriaBuilder, root);
                return criteriaBuilder.and(predicate1, predicateParam);
            }

        }
        return predicate;
    }

//    private Predicate getPredicate(SearchCriteria param, CriteriaBuilder builder, Root<T> root) {
//        return switch (param.getOperation()) {
//            case ">" -> builder.greaterThan(root.get(param.getKey()), param.getValue().toString());
//            case ">=" -> builder.greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString());
//            case "<" -> builder.lessThan(root.get(param.getKey()), param.getValue().toString());
//            case "<=" -> builder.lessThanOrEqualTo(root.get(param.getKey()), param.getValue().toString());
//            case "%_" -> builder.like(builder.lower(root.get(param.getKey())), "%" + param.getValue().toString().toLowerCase());
//            case "_%" -> builder.like(builder.lower(root.get(param.getKey())), param.getValue().toString().toLowerCase() + "%");
//            case "%_%" -> getPredicateOnContains(param, builder, root);
//            case "!=" -> getPredicateOnNotEqual(param, builder, root);
//            default -> getPredicateOnEqual(param, builder, root);
//        };
//    }
//
//    private Predicate getPredicate(SearchCriteria param, CriteriaBuilder builder, Join<?, ?> root) {
//        return switch (param.getOperation()) {
//            case ">" -> builder.greaterThan(root.get(param.getKey()), param.getValue().toString());
//            case ">=" -> builder.greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString());
//            case "<" -> builder.lessThan(root.get(param.getKey()), param.getValue().toString());
//            case "<=" -> builder.lessThanOrEqualTo(root.get(param.getKey()), param.getValue().toString());
//            case "%_" -> builder.like(builder.lower(root.get(param.getKey())), "%" + param.getValue().toString().toLowerCase());
//            case "_%" -> builder.like(builder.lower(root.get(param.getKey())), param.getValue().toString().toLowerCase() + "%");
//            case "%_%" -> getPredicateOnContains(param, builder, root);
//            case "!=" -> getPredicateOnNotEqual(param, builder, root);
//            default -> getPredicateOnEqual(param, builder, root);
//        };
//    }

    private Predicate getPredicateOnEqual(SearchCriteria param, CriteriaBuilder builder, Root<T> root) {
        String[] keys = param.getKey().split("\\.");
        Class<?> paramType = root.get(keys[0]).getJavaType();
        String value = param.getValue() == null ? null : (String) param.getValue();
        if (keys.length >= 2) {
//            Join<?, ?> join = root.join(keys[0]);
//            for (int i = 1; i < keys.length - 1; i++) {
//                join = join.join(keys[i]);
//            }
//
//            return builder.equal(join.get(keys[keys.length - 1]), value);
            return builder.equal(builder.function(
                    "jsonb_extract_path_text",
                    String.class,
                    root.get(keys[0]), builder.literal(keys[1])), value);
        } else if (value == null)
            return builder.isNull(root.get(keys[0]));
        else if (paramType.equals(String.class))
            return builder.equal(root.get(keys[0]), value);
        else if (paramType.equals(UUID.class))
            return builder.equal(root.get(param.getKey()), UUID.fromString(value));
        else if (paramType.equals(HashMap.class))
            return builder.or(
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("uz"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("ru"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("en"))), "%" + value.toLowerCase() + "%"));
        else if (paramType.equals(Name.class))
            return builder.or(
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("uz"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("ru"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("en"))), "%" + value.toLowerCase() + "%"));
        else if (Enum.class.isAssignableFrom(paramType)) {
            return builder.equal(root.get(param.getKey()), Enum.valueOf((Class<Enum>) paramType, value));
        } else if (List.class.isAssignableFrom(paramType)) {
            return builder
                    .isMember(TranslationType.valueOf(value), root.get(param.getKey()));
        } else
            return builder.equal(root.get(param.getKey()), param.getValue());
    }

    private Predicate getPredicateOnEqual(SearchCriteria param, CriteriaBuilder builder, Join<?, ?> root) {
        String[] keys = param.getKey().split("\\.");
        Class<?> paramType = root.get(keys[0]).getJavaType();
        String value = param.getValue() == null ? null : (String) param.getValue();
//        if (keys.length >= 2) {
////            Join<?, ?> join = root.join(keys[0]);
////            for (int i = 1; i < keys.length - 1; i++) {
////                join = join.join(keys[i]);
////            }
////
////            return builder.equal(join.get(keys[keys.length - 1]), value);
//            return builder.equal(builder.function(
//                    "jsonb_extract_path_text",
//                    String.class,
//                    root.get(keys[0]), builder.literal(keys[1])), value);
//        } else \
        if (value == null)
            return builder.isNull(root.get(keys[0]));
        else if (paramType.equals(String.class))
            return builder.equal(root.get(keys[0]), value);
        else if (paramType.equals(UUID.class))
            return builder.equal(root.get(param.getKey()), UUID.fromString(value));
        else if (paramType.equals(HashMap.class))
            return builder.or(
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("uz"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("ru"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("en"))), "%" + value.toLowerCase() + "%"));
        else if (paramType.equals(Name.class))
            return builder.or(
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("uz"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("ru"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("en"))), "%" + value.toLowerCase() + "%"));
        else if (Enum.class.isAssignableFrom(paramType)) {
            return builder.equal(root.get(param.getKey()), Enum.valueOf((Class<Enum>) paramType, value));
        } else if (List.class.isAssignableFrom(paramType)) {
            return builder
                    .isMember(TranslationType.valueOf(value), root.get(param.getKey()));
        } else
            return builder.equal(root.get(param.getKey()), param.getValue());
    }

    private Predicate getPredicateOnContains(SearchCriteria param, CriteriaBuilder builder, Root<T> root) {
        String[] keys = param.getKey().split("\\.");
        Class<?> paramType = root.get(keys[0]).getJavaType();
        String value = param.getValue() == null ? null : (String) param.getValue();
        if (paramType.equals(Name.class))
            return builder.or(
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("uz"))
                    ), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("ru"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("en"))), "%" + value.toLowerCase() + "%"));
        else
            return builder.like(builder.lower(root.get(param.getKey())), "%" + param.getValue().toString().toLowerCase() + "%");
    }

    private Predicate getPredicateOnContains(SearchCriteria param, CriteriaBuilder builder, Join<?, ?> root) {
        String[] keys = param.getKey().split("\\.");
        Class<?> paramType = root.get(keys[0]).getJavaType();
        String value = param.getValue() == null ? null : (String) param.getValue();
        if (paramType.equals(Name.class))
            return builder.or(
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("uz"))
                    ), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("ru"))), "%" + value.toLowerCase() + "%"),
                    builder.like(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("en"))), "%" + value.toLowerCase() + "%"));
        else
            return builder.like(builder.lower(root.get(param.getKey())), "%" + param.getValue().toString().toLowerCase() + "%");
    }

    private Predicate getPredicateOnNotEqual(SearchCriteria param, CriteriaBuilder builder, Join<?, ?> root) {
        String[] keys = param.getKey().split("\\.");
        Class<?> paramType = root.get(keys[0]).getJavaType();
        String value = param.getValue() == null ? null : (String) param.getValue();
//        if (keys.length == 2) {
//            return builder.notEqual(builder.function(
//                    "jsonb_extract_path_text",
//                    String.class,
//                    root.get(keys[0]), builder.literal(keys[1])), value);
//        } else
        if (value == null)
            return builder.isNotNull(root.get(keys[0]));
        if (paramType.equals(String.class))
            return builder.notEqual(root.get(param.getKey()), value);
        else if (paramType.equals(UUID.class))
            return builder.notEqual(root.get(param.getKey()), UUID.fromString(value));
        else if (paramType.equals(HashMap.class))
            return builder.and(
                    builder.notLike(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("uz"))), "%" + value.toLowerCase() + "%"),
                    builder.notLike(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("ru"))), "%" + value.toLowerCase() + "%"),
                    builder.notLike(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("en"))), "%" + value.toLowerCase() + "%"));
        else if (paramType.isEnum())
            return builder.notEqual(root.get(param.getKey()), Enum.valueOf((Class<Enum>) paramType, value));
        else
            return builder.notEqual(root.get(param.getKey()), param.getValue());
    }

    private Predicate getPredicateOnNotEqual(SearchCriteria param, CriteriaBuilder builder, Root<T> root) {
        String[] keys = param.getKey().split("\\.");
        Class<?> paramType = root.get(keys[0]).getJavaType();
        String value = param.getValue() == null ? null : (String) param.getValue();
        if (keys.length == 2) {
            return builder.notEqual(builder.function(
                    "jsonb_extract_path_text",
                    String.class,
                    root.get(keys[0]), builder.literal(keys[1])), value);
        } else if (value == null)
            return builder.isNotNull(root.get(keys[0]));
        if (paramType.equals(String.class))
            return builder.notEqual(root.get(param.getKey()), value);
        else if (paramType.equals(UUID.class))
            return builder.notEqual(root.get(param.getKey()), UUID.fromString(value));
        else if (paramType.equals(HashMap.class))
            return builder.and(
                    builder.notLike(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("uz"))), "%" + value.toLowerCase() + "%"),
                    builder.notLike(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("ru"))), "%" + value.toLowerCase() + "%"),
                    builder.notLike(builder.lower(builder.function(
                            "jsonb_extract_path_text",
                            String.class,
                            root.get(param.getKey()), builder.literal("en"))), "%" + value.toLowerCase() + "%"));
        else if (paramType.isEnum())
            return builder.notEqual(root.get(param.getKey()), Enum.valueOf((Class<Enum>) paramType, value));
        else
            return builder.notEqual(root.get(param.getKey()), param.getValue());
    }


}
