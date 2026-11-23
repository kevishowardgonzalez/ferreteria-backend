package cl.duoc.ferreteria_categorias.repository;

import cl.duoc.ferreteria_categorias.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNombreIgnoreCase(String nombre);

    List<Categoria> findByActivoTrue();
}