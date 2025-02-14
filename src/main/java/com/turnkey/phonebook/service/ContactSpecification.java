package com.turnkey.phonebook.service;

import com.turnkey.phonebook.enitity.Contact;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ContactSpecification {
    public static Specification<Contact> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); 
            }

            String searchTerm = "%" + keyword.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchTerm));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchTerm));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchTerm));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), searchTerm));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("mobileNumber")), searchTerm));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("workPhone")), searchTerm));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}