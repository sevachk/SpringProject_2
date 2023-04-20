package ru.kasatchikov.library.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kasatchikov.library.models.Book;
import ru.kasatchikov.library.models.Person;
import ru.kasatchikov.library.repositories.PeopleRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;
    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }
    public List<Person> findAll() { return peopleRepository.findAll();}
    public Optional<Person> findById(int id) { return peopleRepository.findById(id);}
    public List<Book> booksByPersonId(int id) {
        Optional<Person> person = findById(id);
        if(person.isPresent()){
            Hibernate.initialize(person.get().getBooks());
            person.get().getBooks().forEach(book -> {
                long different= System.currentTimeMillis() - book.getTakenAt().getTime();
                if(different > 864000000)
                    book.setExpired(true);
            });
            return person.get().getBooks();
        }
        return Collections.emptyList();
    }
    public Optional<Person> findByName(String name) {
        return peopleRepository.findByName(name);
    }
    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }
    @Transactional
    public void update(int id, Person personToUpdate) {
        personToUpdate.setId(id);
        peopleRepository.save(personToUpdate);
    }
    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }
}
