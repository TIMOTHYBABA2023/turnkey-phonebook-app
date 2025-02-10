package com.turnkey.phonebook.repository;

import com.turnkey.phonebook.enitity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {
    Optional<Contact> findContactByPhoneNumber(String phoneNumber);
    Page<Contact> findAllBy(Pageable pageable);

    void deleteAllByIdIsIn(Collection<Long> ids);

    Page<Contact> findContactsByFavorite(boolean favorite, Pageable pageable);
}
