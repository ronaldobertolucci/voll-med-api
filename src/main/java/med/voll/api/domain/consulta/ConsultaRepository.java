package med.voll.api.domain.consulta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    Boolean existsByPacienteIdAndMotivoCancelamentoAndDataBetween(Long idPaciente, MotivoCancelamento motivo, LocalDateTime primeiroHorario, LocalDateTime ultimoHorario);

    List<Consulta> findAllByMedicoIdAndDataBetween(Long aLong, LocalDateTime inicioDoIntervalo, LocalDateTime fimDoIntervalo);
}
