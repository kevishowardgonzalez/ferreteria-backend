package cl.duoc.ferreteria_contacto.service;

import cl.duoc.ferreteria_contacto.dto.ContactoRequest;
import cl.duoc.ferreteria_contacto.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_contacto.model.MensajeContacto;
import cl.duoc.ferreteria_contacto.repository.ContactoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactoService {

    private final ContactoRepository repository;

    public ContactoService(ContactoRepository repository) {
        this.repository = repository;
    }

    public List<MensajeContacto> listar() {
        return repository.findAllByOrderByFechaDesc();
    }

    public MensajeContacto enviar(ContactoRequest req) {
        MensajeContacto m = new MensajeContacto(
                null,
                req.getNombre().trim(),
                req.getCorreo().trim().toLowerCase(),
                req.getAsunto().trim(),
                req.getMensaje().trim(),
                LocalDateTime.now(),
                false);
        return repository.save(m);
    }

    public MensajeContacto marcarRespondido(Long id) {
        MensajeContacto m = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mensaje no encontrado: " + id));
        m.setRespondido(true);
        return repository.save(m);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Mensaje no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}