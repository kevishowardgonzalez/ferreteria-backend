package cl.duoc.ferreteria_boletas.service;

import cl.duoc.ferreteria_boletas.dto.BoletaRequest;
import cl.duoc.ferreteria_boletas.dto.DetalleBoletaRequest;
import cl.duoc.ferreteria_boletas.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_boletas.model.Boleta;
import cl.duoc.ferreteria_boletas.model.DetalleBoleta;
import cl.duoc.ferreteria_boletas.repository.BoletaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BoletaService {

    private final BoletaRepository repository;

    public BoletaService(BoletaRepository repository) {
        this.repository = repository;
    }

    public List<Boleta> listarTodas() {
        return repository.findAll();
    }

    public List<Boleta> listarPorUsuario(String correo) {
        return repository.findByUsuarioCorreoIgnoreCaseOrderByFechaDesc(correo);
    }

    public Boleta buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Boleta no encontrada: " + id));
    }

    @Transactional
    public Boleta emitir(String correoUsuario, BoletaRequest req) {
        Boleta boleta = new Boleta();
        boleta.setUsuarioCorreo(correoUsuario);
        boleta.setUsuarioNombre(req.getUsuarioNombre().trim());
        boleta.setFecha(LocalDateTime.now());
        boleta.setEstado("EMITIDA");

        int total = 0;
        for (DetalleBoletaRequest item : req.getDetalles()) {
            int subtotal = item.getCantidad() * item.getPrecioUnitario();
            total += subtotal;

            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(boleta);
            detalle.setProductoId(item.getProductoId());
            detalle.setProductoNombre(item.getProductoNombre().trim());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(subtotal);
            boleta.getDetalles().add(detalle);
        }

        int neto = Math.round(total / 1.19f);
        int iva = total - neto;

        boleta.setNeto(neto);
        boleta.setIva(iva);
        boleta.setTotal(total);

        return repository.save(boleta);
    }

    public Boleta anular(Long id) {
        Boleta boleta = buscar(id);
        boleta.setEstado("ANULADA");
        return repository.save(boleta);
    }
}