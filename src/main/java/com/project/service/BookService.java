package com.project.service;

import com.project.entity.Book;
import com.project.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class BookService {

    final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Book> searchBooks(String keyword) {
        if (keyword != null) {
            return bookRepository.search(keyword);
        }
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Book findBookById(Long id) throws Exception {
        return bookRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Book not found with ID %d", id)));
    }

    public void createBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(Long id) throws Exception {
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Book not found with ID %d", id)));

        bookRepository.deleteById(book.getId());
    }

    public Page<Book> findPaginated(Pageable pageable) {

        var pageSize = pageable.getPageSize();
        var currentPage = pageable.getPageNumber();
        var startItem = currentPage * pageSize;
        List<Book> list;

        if (findAllBooks().size() < startItem) {
            list = Collections.emptyList();
        } else {
            var toIndex = Math.min(startItem + pageSize, findAllBooks().size());
            list = findAllBooks().subList(startItem, toIndex);
        }

        return new PageImpl<Book>(list, PageRequest.of(currentPage, pageSize), findAllBooks().size());
    }

}
