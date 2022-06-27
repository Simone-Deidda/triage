package it.prova.triage.web.api;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import it.prova.triage.dto.DottoreResponseDTO;
import it.prova.triage.dto.PazienteDTO;
import it.prova.triage.model.Dottore;
import it.prova.triage.model.Paziente;
import it.prova.triage.model.StatoPaziente;
import it.prova.triage.service.DottoreService;
import it.prova.triage.service.PazienteService;

@RestController
@RequestMapping("api/paziente")
public class PazienteController {
	@Autowired
	private PazienteService pazienteService;
	@Autowired
	private DottoreService dottoreService;
	@Autowired
	private WebClient webClient;

	@GetMapping
	public List<PazienteDTO> getAll() {
		return PazienteDTO.createPazienteDTOListFromModelList(pazienteService.listAllElements());
	}

	@GetMapping("/{id}")
	public PazienteDTO findById(@PathVariable(value = "id", required = true) long id) {
		Paziente paziente = pazienteService.caricaSingoloElemento(id);

		if (paziente == null)
			throw new RuntimeException("Paziente not found con id: " + id);

		return PazienteDTO.buildPazienteDTOFromModel(paziente);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PazienteDTO createNew(@RequestBody PazienteDTO input) {
		if (input.getId() != null)
			throw new RuntimeException("Non è ammesso fornire un id per la creazione");

		Paziente pazienteInserito = pazienteService.inserisciNuovo(input.buildPazienteModel());
		return PazienteDTO.buildPazienteDTOFromModel(pazienteInserito);
	}

	@PutMapping("/{id}")
	public PazienteDTO update(@RequestBody PazienteDTO input, @PathVariable(required = true) Long id) {
		Paziente paziente = pazienteService.caricaSingoloElemento(id);

		if (paziente == null)
			throw new RuntimeException("Paziente not found con id: " + id);

		input.setDataRegistrazione(paziente.getDataRegistrazione());
		input.setId(id);
		Paziente pazienteAggiornato = pazienteService.aggiorna(input.buildPazienteModel());
		return PazienteDTO.buildPazienteDTOFromModel(pazienteAggiornato);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable(required = true) Long id) {
		Paziente paziente = pazienteService.caricaSingoloElemento(id);

		if (paziente == null)
			throw new RuntimeException("Paziente not found con id: " + id);
		if (paziente.getStato() != StatoPaziente.DIMESSO) {
			throw new RuntimeException("Impossibile eliminare Paziente con stato: " + paziente.getStato());
		}

		pazienteService.rimuovi(paziente);
	}

	@GetMapping("/assegnaPaziente")
	public PazienteDTO assegnaPaziente(@PathParam(value = "idPaziente") long idPaziente,
			@PathParam(value = "idDottore") Long idDottore) {
		Paziente paziente = pazienteService.caricaSingoloElemento(idPaziente);
		if (paziente == null)
			throw new RuntimeException("Paziente not found con id: " + idPaziente);
		if (paziente.getStato() != StatoPaziente.IN_ATTESA_VISITA) {
			throw new RuntimeException("Paziente non è in attesa di essere visitato");
		}

		Dottore dottore = dottoreService.caricaSingoloElemento(idDottore);
		if (dottore == null)
			throw new RuntimeException("Dottore not found con id: " + idDottore);

		String codiceDipendente = dottore.getNome().toLowerCase().charAt(0) + dottore.getCognome().toLowerCase();
		DottoreResponseDTO dottoreFromApp = webClient.get().uri("/verifica/" + codiceDipendente).retrieve()
				.bodyToMono(DottoreResponseDTO.class).block();
		if (!dottoreFromApp.getInServizio()) {
			throw new RuntimeException("Impossibile assegnare Dottore non in servizio");
		}
		if (dottoreFromApp.getInVisita()) {
			throw new RuntimeException("Impossibile assegnare Dottore in visita");
		}

		pazienteService.assegnaDottore(dottore, paziente);
		webClient.post().uri("/impostaInVisita/" + codiceDipendente).retrieve().bodyToMono(DottoreResponseDTO.class).block();

		return PazienteDTO.buildPazienteDTOFromModel(paziente);
	}
}
