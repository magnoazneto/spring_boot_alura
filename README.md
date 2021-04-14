# Spring boot Alura project
Esse projeto feito para recuperação de conteúdo e podem ser encontrados alguns exemplos de uso do Spring Boot.

## Classes Dto

Algumas classes são criadas exclusivamente para transferência de dados, evitando assim que a própria entidade modelo seja 
enviada na resposta de uma requisição e que apenas os dados realmente necessários na resposta sejam devolvidos.
Ponde de atenção:
~~~{Java}
public static List<TopicoDto> converter(List<Topico> topicos){
        return topicos.stream().map(TopicoDto::new).collect(Collectors.toList());
    }
~~~

O método acima, presente na classe TopicoDto, utiliza uma sintaxe presente do Java 8 em diante.
Útil para transformar uma Lista de Entidades numa lista de Dtos de forma rápida.

## OneToMany

Na classe Topico, acima do atributo respostas, é possível ver um exemplo de implementação desse modo de cardinalidade 
onde é necessário definir de onde aquele relacionamento está sendo mapeado, para que o Spring não tente mapeá-lo duas vezes.
o atributo mappedBy = [nome do atributo na outra Entidade] é usado para isso.

## Criação de Query com JPA

É possível ver na classe TopicoRepository um exemplo de método que faz o Spring entender e construir
uma query, mesmo que ela navegue por relacionamentos! Foi usado aqui um _ para deixar explicito a navegação
por Entidades, mas ele não é obrigatório.

## Get "smart"

No TopicoController também existe um método Get que suporta tanto ser chamado sem uma extensão ao endpoint do Controller
quanto com uma extensão de queryParam padrão HTTP. Útil para buscas mais genéricas por um termo.


## Boas práticas com POST

~~~{Java}
@PostMapping
public ResponseEntity<TopicoDto> criaTopico(@RequestBody TopicoRequest request, UriComponentsBuilder uriBuilder){
    Topico topico = request.toModel(cursoRepo);
    topicoRepo.save(topico);

    URI uri = uriBuilder.path("/topico/{id}").buildAndExpand(topico.getId()).toUri();
    return ResponseEntity.created(uri).body(new TopicoDto(topico));
}
~~~

O método acima, criado no TopicoController, tem um tipo ResponseEntity como resposta. Como essa é uma 
rota do tipo POST, devemos devolver o status 201 (Created) na nossa resposta, e aqui cabem algumas observações sobre
o que esse código está realmente fazendo:

- no retorno, o método ResponseEntity.created() recebe como parâmetro uma URI. Essa URI deve ser construída de forma adequada, e é para isso que serve o parâmetro chamado UriComponentsBuilder.
- com esse parâmetro podemos acessar o método path(), passar uma String que representa o recurso para onde aquela requisição poderá ser direcionada
em caso de necessidade de verificar a Entidade que acabou de ser criada nesse método POST, e o método vai construir a URI completa com o prefixo (que pode variar dependendo
  do cenário entre produção/desenvolvimento/homologação).
- Depois, deve-se chamar o método buildAndExpand(), que recebe o valor que deverá ser substituído na String passada para o método path()
- Por fim, é só chamar o método toUri() e passar esse objeto URI dentro do método created().

