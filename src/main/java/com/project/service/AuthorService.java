package com.project.service;

import com.project.entity.Author;
import com.project.repository.AuthorRepository;
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
public class AuthorService {

    final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Author findAuthorById(Long id) throws Exception {
        return authorRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Author not found with ID %d", id)));
    }

    public void createAuthor(Author author) {
        authorRepository.save(author);
    }

    public void updateAuthor(Author author) {
        authorRepository.save(author);
    }

    public void deleteAuthor(Long id) throws Exception {
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Author not found with ID %d", id)));

        authorRepository.deleteById(author.getId());
    }

    public Page<Author> findPaginated(Pageable pageable) {

        var pageSize = pageable.getPageSize();
        var currentPage = pageable.getPageNumber();
        var startItem = currentPage * pageSize;
        List<Author> list;

        if (findAllAuthors().size() < startItem) {
            list = Collections.emptyList();
        } else {
            var toIndex = Math.min(startItem + pageSize, findAllAuthors().size());
            list = findAllAuthors().subList(startItem, toIndex);
        }

        return new PageImpl<Author>(list, PageRequest.of(currentPage, pageSize), findAllAuthors().size());

    }
}
