package edu.cibertec.taxihub.services;

import edu.cibertec.taxihub.dao.entity.ComisionPago;
import edu.cibertec.taxihub.dao.repository.ComisionPagoRepository;
import edu.cibertec.taxihub.usecase.IComisionPagoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComisionPagoUseCaseImpl implements IComisionPagoUseCase {

    private final ComisionPagoRepository comisionPagoRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ComisionPago> listarComisionesActivas(Pageable pageable) {
        return comisionPagoRepository.findAll(pageable)
                .map(comision -> comision.isActivo() ? comision : null)
                .map(Optional::ofNullable)
                .map(opt -> opt.orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComisionPago> buscarComisionPorCodigo(String codigo) {
        return comisionPagoRepository.findByCodigoAndActivoTrue(codigo);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obtenerMontoComisionPorCodigo(String codigo) {
        return buscarComisionPorCodigo(codigo)
                .map(ComisionPago::getMonto)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public ComisionPago crearComision(ComisionPago comision) {
        return comisionPagoRepository.save(comision);
    }

    @Override
    @Transactional
    public ComisionPago actualizarComision(Long id, ComisionPago comision) {
        return comisionPagoRepository.findById(id)
                .filter(ComisionPago::isActivo)
                .map(existente -> {
                    existente.setNombre(comision.getNombre());
                    existente.setMonto(comision.getMonto());
                    return comisionPagoRepository.save(existente);
                })
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void eliminarComision(Long id) {
        comisionPagoRepository.findById(id)
                .filter(ComisionPago::isActivo)
                .ifPresent(comision -> {
                    comision.setActivo(false);
                    comisionPagoRepository.save(comision);
                });
    }
}