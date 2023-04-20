package ru.kasatchikov.library.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kasatchikov.library.dao.BookDAO;
import ru.kasatchikov.library.dao.PersonDAO;
import ru.kasatchikov.library.models.Book;
import ru.kasatchikov.library.models.Person;
import ru.kasatchikov.library.services.BookService;
import ru.kasatchikov.library.services.PeopleService;
import ru.kasatchikov.library.util.BookValidator;

import java.util.Optional;

@Controller
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;
    private final PeopleService peopleService;
    private final BookValidator bookValidator;
    @Autowired
    public BookController(BookService bookService, PeopleService peopleService, BookValidator bookValidator) {
        this.bookService = bookService;
        this.peopleService = peopleService;
        this.bookValidator = bookValidator;
    }
    @GetMapping()
    public String index(Model model, @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {
        if(page == null || booksPerPage == null)
            model.addAttribute("books", bookService.findAll(sortByYear));
        else
            model.addAttribute("books", bookService.findWithPaginating(page, booksPerPage, sortByYear));
        return "/book/index";
    }
    @GetMapping("/{id}")
    public String show(Model model, @PathVariable("id") int id, @ModelAttribute("person") Person person){
        model.addAttribute("book", bookService.findById(id).get());
        Optional<Person> bookOwner = Optional.ofNullable(bookService.bookOwner(id));

        if(bookOwner.isPresent())
            model.addAttribute("owner", bookOwner.get());
        else
            model.addAttribute("people", peopleService.findAll());

        return "/book/show";
    }
    @GetMapping("/new")
    public String newBook(Model model){
        model.addAttribute("book", new Book());
        return "/book/new";
    }
    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult){
        bookValidator.validate(book, bindingResult);
        if(bindingResult.hasErrors())
            return "/book/new";
        bookService.save(book);
        return "redirect:/book";
    }
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id){
        model.addAttribute("book", bookService.findById(id).get());
        return "/book/edit";
    }
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id){
        bookValidator.validate(book, bindingResult);
        if(bindingResult.hasErrors())
            return "/book/edit";
        bookService.update(id, book);
        return "redirect:/book";
    }
    @DeleteMapping("/{id}/edit")
    public String delete(@PathVariable("id") int id){
        bookService.delete(id);
        return "redirect:/book";
    }
    @PatchMapping("/{id}/release")
    public String releaseBook(@PathVariable("id") int id){
        bookService.releaseBook(id);
        return "redirect:/book/" + id;
    }
    @PatchMapping("/{id}/assign")
    public String assign(@ModelAttribute("person") Person selectedPerson, @PathVariable("id") int id){
        bookService.assignBook(id, selectedPerson);
        return "redirect:/book/" + id;
    }
    @GetMapping("/search")
    public String searchPage() {
        return "/book/search";
    }
    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam(value = "query") String query) {
        model.addAttribute("books", bookService.searchByName(query));
        return "/book/search";
    }
}