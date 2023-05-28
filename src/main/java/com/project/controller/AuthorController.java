package com.project.controller;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.project.entity.Author;
import com.project.service.AuthorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorController {
    final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @RequestMapping("/authors")
    public String findAllAuthors(Model model, @RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size) {

        var currentPage = page.orElse(1);
        var pageSize = size.orElse(5);
        var bookPage = authorService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("authors", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            var pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "list_authors";
    }

    @RequestMapping("/author/{id}")
    public String findAuthorById(@PathVariable("id") Long id, Model model) throws Exception {

        model.addAttribute("author", authorService.findAuthorById(id));
        return "list_author";
    }

    @GetMapping("/addAuthor")
    public String showCreateForm(Author author) {
        return "add_author";
    }

    @RequestMapping("/addAuthor")
    public String createAuthor(Author author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add_author";
        }

        authorService.createAuthor(author);
        model.addAttribute("author", authorService.findAllAuthors());
        return "redirect:/authors";
    }

    @GetMapping("/updateAuthor/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) throws Exception {

        model.addAttribute("author", authorService.findAuthorById(id));
        return "update_author";
    }

    @RequestMapping("/updateAuthor/{id}")
    public String updateAuthor(@PathVariable("id") Long id, Author author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            author.setId(id);
            return "update_author";
        }

        authorService.updateAuthor(author);
        model.addAttribute("author", authorService.findAllAuthors());
        return "redirect:/authors";
    }

    @RequestMapping("/removeAuthor/{id}")
    public String deleteAuthor(@PathVariable("id") Long id, Model model) throws Exception {
        authorService.deleteAuthor(id);

        model.addAttribute("author", authorService.findAllAuthors());
        return "redirect:/authors";
    }
}
