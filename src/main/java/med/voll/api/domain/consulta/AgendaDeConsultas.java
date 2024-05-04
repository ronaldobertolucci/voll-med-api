package med.voll.api.domain.consulta;

import med.voll.api.domain.consulta.validacoes.agendamentos.ValidadorAgendamentoDeConsulta;
import med.voll.api.domain.consulta.validacoes.cancelamentos.ValidadorCancelamentoDeConsulta;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AgendaDeConsultas {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private List<ValidadorAgendamentoDeConsulta> validadoresAgendamento;

    @Autowired
    private List<ValidadorCancelamentoDeConsulta> validadoresCancelamento;

    public Consulta agendar(DadosAgendamentoConsulta dados) {
        validadoresAgendamento.forEach(v -> v.validar(dados));

        try {
            var paciente = pacienteRepository.findAtivoById(dados.idPaciente()).orElseThrow();
            var medico = escolherMedico(dados);
            var consulta = new Consulta(null, medico, paciente, dados.data(), null);
            return consultaRepository.save(consulta);
        } catch (NoSuchElementException exception) {
            throw new RuntimeException("Paciente/médico está inativo ou não foi encontrado");
        }
    }

    public void cancelar(DadosCancelamentoConsulta dados) {
        validadoresCancelamento.forEach(v -> v.validar(dados));

        try {
            var consulta = consultaRepository.findById(dados.idConsulta()).orElseThrow();
            consulta.cancelar(dados.motivo());
        } catch (NoSuchElementException exception) {
            throw new RuntimeException("Consulta não encontrada");
        }
    }

    private Medico escolherMedico(DadosAgendamentoConsulta dados) {
        if (dados.idMedico() != null) {
            return medicoRepository.findAtivoById(dados.idMedico()).orElseThrow();
        }

        if (dados.especialidade() == null) {
            throw new RuntimeException("A especialidade é obrigatória quando o médico não é informado");
        }

        try {
            return medicoRepository.findByEspecialidadeComDataLivre(dados.especialidade(), dados.data()).orElseThrow();
        } catch (NoSuchElementException exception) {
            throw new RuntimeException("Não existem médicos disponíveis na especialidade e horário informados");
        }
    }
}
