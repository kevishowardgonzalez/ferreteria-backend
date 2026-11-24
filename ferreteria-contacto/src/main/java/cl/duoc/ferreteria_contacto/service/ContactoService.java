package cl.duoc.ferreteria_contacto.service;

import cl.duoc.ferreteria_contacto.dto.ContactoRequest;
import cl.duoc.ferreteria_contacto.dto.ContactoResponse;
import cl.duoc.ferreteria_contacto.model.Contacto;
import cl.duoc.ferreteria_contacto.repository.ContactoRepository;
import cl.duoc.ferreteria_contacto.exception.RecursoNoEncontradoException;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContactoService {

    private final ContactoRepository repository;

    public ContactoService(ContactoRepository repository) {
        this.repository = repository;
    }

    // LISTAR CONTACTOS
    public List<ContactoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // BUSCAR CONTACTO POR ID
    public ContactoResponse buscarPorId(Long id) {
        Contacto contacto = buscarEntidadPorId(id);
        return convertirAResponse(contacto);
    }

    // CREAR CONTACTO
    public ContactoResponse crear(ContactoRequest request) {

        Contacto contacto = new Contacto();

        contacto.setNombre(request.nombre());
        contacto.setCorreo(request.correo());
        contacto.setAsunto(request.asunto());
        contacto.setMensaje(request.mensaje());

        Contacto guardado = repository.save(contacto);

        return convertirAResponse(guardado);
    }

    // ACTUALIZAR CONTACTO
    public ContactoResponse actualizar(Long id, ContactoRequest request) {

        Contacto contacto = buscarEntidadPorId(id);

        contacto.setNombre(request.nombre());
        contacto.setCorreo(request.correo());
        contacto.setAsunto(request.asunto());
        contacto.setMensaje(request.mensaje());

        Contacto actualizado = repository.save(contacto);

        return convertirAResponse(actualizado);
    }

    // ELIMINAR CONTACTO
    public void eliminar(Long id) {
        Contacto contacto = buscarEntidadPorId(id);
        repository.delete(contacto);
    }

    // BUSCAR ENTIDAD EN LA BASE DE DATOS
    private Contacto buscarEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contacto no encontrado con ID: " + id
                ));
    }

    // CONVERTIR ENTIDAD A DTO DE RESPUESTA
    private ContactoResponse convertirAResponse(Contacto contacto) {

        return new ContactoResponse(
                contacto.getId(),
                contacto.getNombre(),
                contacto.getCorreo(),
                contacto.getAsunto(),
                contacto.getMensaje(),
                contacto.getFechaCreacion()
        );
    }
}
