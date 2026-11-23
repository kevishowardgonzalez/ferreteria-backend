package cl.duoc.ferreteria_foro.service;

import cl.duoc.ferreteria_foro.dto.ComentarioRequest;
import cl.duoc.ferreteria_foro.dto.PublicacionRequest;
import cl.duoc.ferreteria_foro.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_foro.model.Comentario;
import cl.duoc.ferreteria_foro.model.Publicacion;
import cl.duoc.ferreteria_foro.repository.PublicacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForoService {

    private final PublicacionRepository repository;

    public ForoService(PublicacionRepository repository) {
        this.repository = repository;
    }

    public List<Publicacion> listar() {
        return repository.findAllByOrderByFechaDesc();
    }

    public Publicacion buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicación no encontrada: " + id));
    }

    public Publicacion crearPublicacion(String correoAutor, PublicacionRequest req) {
        Publicacion pub = new Publicacion();
        pub.setTitulo(req.getTitulo().trim());
        pub.setContenido(req.getContenido().trim());
        pub.setAutorCorreo(correoAutor);
        pub.setAutorNombre(req.getAutorNombre().trim());
        pub.setFecha(LocalDateTime.now());
        return repository.save(pub);
    }

    @Transactional
    public Publicacion agregarComentario(Long publicacionId, String correoAutor, ComentarioRequest req) {
        Publicacion pub = buscar(publicacionId);
        Comentario com = new Comentario();
        com.setPublicacion(pub);
        com.setContenido(req.getContenido().trim());
        com.setAutorCorreo(correoAutor);
        com.setAutorNombre(req.getAutorNombre().trim());
        com.setFecha(LocalDateTime.now());
        pub.getComentarios().add(com);
        return repository.save(pub);
    }

    public void eliminarPublicacion(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Publicación no encontrada: " + id);
        }
        repository.deleteById(id);
    }
}