package ru.kasatchikov.library.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kasatchikov.library.models.Book;
import ru.kasatchikov.library.models.Person;
import ru.kasatchikov.library.repositories.BookRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    public List<Book> findAll(boolean sortByYear) {
        if(sortByYear)
            return bookRepository.findAll(Sort.by("year"));
        else
            return bookRepository.findAll();
    }
    public Optional<Book> findById(int id) {
        return bookRepository.findById(id);
    }
    public List<Book> findWithPaginating(Integer page, Integer booksPerPage, boolean sortByYear) {
        if(sortByYear)
            return bookRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else
            return bookRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }
    public Optional<Book> findByName(String name) {
        return bookRepository.findByName(name);
    }
    public Person bookOwner(int id) {
        Book book = findById(id).get();
        return book.getOwner();
    }
    public List<Book> searchByName(String query) {
        return bookRepository.findByNameStartingWith(query);
    }
    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }
    @Transactional
    public void update(int id, Book bookToUpdate) {
        bookToUpdate.setId(id);
        bookToUpdate.setOwner(findById(id).get().getOwner());
        bookRepository.save(bookToUpdate);
    }
    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }
    @Transactional
    public void assignBook(int id, Person selectedPerson) {
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setTakenAt(new Date());
                });
    }
    @Transactional
    public void releaseBook(int id) {
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setTakenAt(null);
                });
    }
}
