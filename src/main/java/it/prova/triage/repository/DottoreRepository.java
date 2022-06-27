package it.prova.triage.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.prova.triage.model.Dottore;

public interface DottoreRepository extends CrudRepository<Dottore, Long> {

	Optional<Dottore> findByNomeAndCognome(String string, String string2);

}
