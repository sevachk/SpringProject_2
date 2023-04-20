package ru.kasatchikov.library.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kasatchikov.library.dao.BookDAO;
import ru.kasatchikov.library.models.Book;
import ru.kasatchikov.library.repositories.BookRepository;

@Component
public class BookValidator implements Validator {
    private final BookRepository bookRepository;
    @Autowired
    public BookValidator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;
        if(bookRepository.findByName(book.getName()).isPresent())
            errors.rejectValue("name", "", "Книга с таким названием уже существует");
    }
}
