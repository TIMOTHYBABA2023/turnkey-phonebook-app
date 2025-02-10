package com.turnkey.phonebook.repository;

import com.turnkey.phonebook.enitity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Contact, Long> {
}
