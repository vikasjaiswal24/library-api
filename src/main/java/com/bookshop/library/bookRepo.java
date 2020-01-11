package com.bookshop.library;

import org.springframework.data.repository.CrudRepository;

public interface bookRepo extends CrudRepository<book, Integer> {

}
