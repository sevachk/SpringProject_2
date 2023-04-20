package ru.kasatchikov.library.util;

import org.hibernate.validator.internal.engine.groups.ValidationOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kasatchikov.library.dao.PersonDAO;
import ru.kasatchikov.library.models.Person;
import ru.kasatchikov.library.repositories.PeopleRepository;

@Component
public class PersonValidator implements Validator {
    private final PeopleRepository peopleRepository;
    @Autowired
    public PersonValidator(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if(peopleRepository.findByName(person.getName()).isPresent())
            errors.rejectValue("name", "", "Данное имя уже занято");
    }
}
