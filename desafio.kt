import java.util.UUID

enum class Nivel { BASICO, INTERMEDIARIO, AVANCADO }
enum class Status {ABERTO, CANCELADO, FECHADO }
enum class Papel {PADRAO, ALUNO, INSTRUTOR}

data class Pessoa(val nome: String, val cpf: String, var papeis: MutableList<Papel> = mutableListOf<Papel>()){
    init {
        papeis.add(Papel.PADRAO)
    }
}

data class ConteudoEducacional(val nome: String, val nivel: Nivel, val duracaoEmHoras: Int = 1, var instrutor: Pessoa)

data class Formacao(val nome: String, var nivel: Nivel, var conteudos: List<ConteudoEducacional>)

data class Contrato(val matricula: UUID, val pessoa: Pessoa, val formacao: Formacao, var status: Status = Status.ABERTO)

class InstituicaoDeEnsino(var nome: String) {
    var contratos = mutableListOf<Contrato>()
    var formacoes = mutableListOf<Formacao>()
    
    fun adicionarFormacao(formacao: Formacao) {
        formacoes.add(formacao)
    }
    
    fun matricular(pessoa: Pessoa, formacao: Formacao): UUID {
        if (formacao !in formacoes){
            throw IllegalArgumentException("A $nome não contempla a formação \"${formacao.nome}\".")
        }
        if (contratos.any{
        	contrato -> contrato.pessoa.cpf == pessoa.cpf && 
            contrato.formacao == formacao && 
            contrato.status == Status.ABERTO}
       	){
            throw IllegalArgumentException(
                "Já existe uma matrícula do aluno ${pessoa.nome} " +
                "para a formação \"${formacao.nome}\"."
            )
        }
        
		val matricula: UUID = UUID.randomUUID()
        pessoa.papeis.add(Papel.ALUNO)
        contratos.add(Contrato(matricula, pessoa, formacao))
        
        return matricula
    }
    
    fun cancelarMatricula(matricula: UUID){
    	val contrato = contratos.find{contrato -> contrato.matricula == matricula}
       	if (contrato?.status == Status.ABERTO) {
            contrato.status = Status.CANCELADO
        }
    }
    
    fun concluirFormacao(matricula: UUID){
        val contrato = contratos.find{contrato -> contrato.matricula == matricula}
       	if (contrato?.status == Status.ABERTO) {
            contrato.status = Status.FECHADO
        }
    }
}

fun main() {
    val venilton = Pessoa("Venilton FalvoJr", "00000000000", mutableListOf(Papel.INSTRUTOR))
    val igor = Pessoa("Igor", "11111111111")
    
   	val formacaoKotlin = Formacao(
        nome="Kotlin para Back-end",
        nivel=Nivel.INTERMEDIARIO,
        conteudos=mutableListOf(
            ConteudoEducacional("Lógica de programação com Kotlin", Nivel.BASICO, 40, venilton),
            ConteudoEducacional("Padrões de projeto em Kotlin", Nivel.INTERMEDIARIO, 20, venilton),
            ConteudoEducacional("Banco de Dados SQL e NoSQL", Nivel.INTERMEDIARIO, 20, venilton),
            ConteudoEducacional("Kotlin no Back-end com Spring Boot 3", Nivel.INTERMEDIARIO, 30, venilton)
        )
    )
    
    val dio = InstituicaoDeEnsino("DIO")
    dio.adicionarFormacao(formacaoKotlin)
    
    val matricula = dio.matricular(igor, formacaoKotlin)
    dio.concluirFormacao(matricula)
}
