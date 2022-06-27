package it.prova.triage.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;


import it.prova.triage.dto.DottoreDTO;
import it.prova.triage.dto.DottoreResponseDTO;
import it.prova.triage.dto.DottoreRequestDTO;
import it.prova.triage.model.Dottore;
import it.prova.triage.service.DottoreService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/dottore")
public class DottoreController {
	@Autowired
	private DottoreService dottoreService;
	@Autowired
	private WebClient webClient;

	@GetMapping
	public List<DottoreDTO> getAll() {
		return DottoreDTO.createDottoreDTOListFromModelList(dottoreService.listAllElements());
	}

	@GetMapping("/{id}")
	public DottoreDTO findById(@PathVariable(value = "id", required = true) long id) {
		Dottore dottore = dottoreService.caricaSingoloElemento(id);

		if (dottore == null)
			throw new RuntimeException("Dottore not found con id: " + id);

		return DottoreDTO.buildDottoreDTOFromModel(dottore);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DottoreDTO createNew(@RequestBody DottoreDTO input) {
		if (input.getId() != null)
			throw new RuntimeException("Non è ammesso fornire un id per la creazione");

		Dottore dottoreInserito = dottoreService.inserisciNuovo(input.buildDottoreModel());
		ResponseEntity<DottoreResponseDTO> response = webClient.post().uri("")
				.body(Mono.just(new DottoreRequestDTO(input.getNome(), input.getCognome())), DottoreRequestDTO.class)
				.retrieve().toEntity(DottoreResponseDTO.class).block();
		
		if (response.getStatusCode() != HttpStatus.CREATED)
			throw new RuntimeException("Errore nella creazione della nuova voce tramite api esterna!!!");
		
		return DottoreDTO.buildDottoreDTOFromModel(dottoreInserito);
	}

	@PutMapping("/{id}")
	public DottoreDTO update(@RequestBody DottoreDTO input, @PathVariable(required = true) Long id) {
		Dottore dottore = dottoreService.caricaSingoloElemento(id);

		if (dottore == null)
			throw new RuntimeException("Dottore not found con id: " + id);

		input.setId(id);
		Dottore dottoreAggiornato = dottoreService.aggiorna(input.buildDottoreModel());
		return DottoreDTO.buildDottoreDTOFromModel(dottoreAggiornato);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable(required = true) Long id) {
		Dottore dottore = dottoreService.caricaSingoloElemento(id);

		if (dottore == null)
			throw new RuntimeException("Dottore not found con id: " + id);
		
		String codiceDipendente = dottore.getNome().toLowerCase().charAt(0) + dottore.getCognome().toLowerCase();
		DottoreResponseDTO dottoreFromApp = webClient.get().uri("/verifica/" + codiceDipendente)
				.retrieve().bodyToMono(DottoreResponseDTO.class).block();
		
		if (dottoreFromApp.getInServizio()) {
			throw new RuntimeException("Impossibile eliminare Dottore in servizio");
		}
		if (dottoreFromApp.getInVisita()) {
			throw new RuntimeException("Impossibile eliminare Dottore in visita");
		}

		dottoreService.rimuovi(dottore);
	}
}
