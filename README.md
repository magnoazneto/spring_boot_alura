# Sobre esse Projeto
Esse projeto foi feito para fixar conteúdo e podem ser encontrados alguns exemplos de uso do Spring Boot. Os
comentários nesse README representam os pontos que julguei merecerem mais atenção frente ao
meu nível de conhecimento técnico da época. Se encontrar algum erro ou ponto de melhoria, sinta-se livre para
me dar um toque :) minhas informações de contato estão no meu perfil do github.

# Spring Boot: REST API

## Classes Dto

Algumas classes são criadas exclusivamente para transferência de dados, evitando assim que a própria entidade modelo seja 
enviada na resposta de uma requisição e que apenas os dados realmente necessários na resposta sejam devolvidos.
Ponto de atenção:
~~~{Java}
public static List<TopicoDto> converter(List<Topico> topicos){
        return topicos.stream().map(TopicoDto::new).collect(Collectors.toList());
    }
~~~

O método acima, presente na classe TopicoDto, utiliza uma sintaxe presente do Java 8 em diante.
Útil para transformar uma Lista de Entidades numa lista de Dtos de forma rápida.
Dto = Data Transfer Object. Nesse projeto é um padrão utilizado para representar o que saí da API para
o client. O que vem do client para a API poderia ser representado como Form, mas aqui é usado um padrão de -request.
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

## Tratamento de Erros

Existem diferentes modos de tratar diferentes tipos de erros. Nesse projeto, existem tratamentos para:
- MethodArgumentNotValidException
- AuthenticationException

## PUT e PATCH

PUT deveria ser oficialmente usado quando se deseja atualizar sobrescrevendo um recurso INTEIRO.
PATCH deveria ser oficialmente usado quando se deseja atualizar apenas uma PARTE daquele recurso.

## Error Handling Return Syntax

Nesse projeto também foi usada uma estrutura de retorno condicionada a existência ou não de um ID
na hora de executar métodos que buscam por tal ID no banco de dados. Esse tipo de estrutura foca em apenas 1 return no método
do Controller, o que em minha visão é de melhor prática do que 2 ou mais returns no mesmo método.

~~~{Java}
public ResponseEntity<TopicoDetalheDto> detalhaTopico(@PathVariable Long id){
    Optional<Topico> topico = topicoRepo.findById(id);
    return topico.map(value -> {
        return ResponseEntity.ok(new TopicoDetalheDto(value));
    }).orElseGet(() -> ResponseEntity.notFound().build());

}
~~~

A estrutura é basicamente retornar algo caso o Optional<?> exista, e caso contrário, retornar o status notFound.
É possível ainda executar uma função ao entrar no map e por exemplo, atualizar ou deletar algo no Banco de Dados, 
como pode ser visto nos métodos PUT e DELETE desse mesmo controller.

# Spring Boot: Segurança, Cache e Monitoramento

Dessa parte em diante são tratadas funcionalidades mais intermediárias como paginacao, cache, segurança e monitoramento

## Paginação

No TopicoController há um exemplo de como develver resultados paginados. Há uma forma mais "manual" de fazer isso, e está inclusive no
histórico de Commits. Mas há usa apenas o objeto Pageable direto nos parâmetros do método POST é adequada e flexível o suficiente.
É possível também usar uma anotação chamada de @PageableDefault para definir DefaultValues. Para que a paginação funcione corretamente, a anotação
@EnableSpringDataWebSupport é necessária na classe main.

## Cache

A maneira simples de implementar Cache na aplicação é através da anotação
@EnableCaching na classe main e da anotação @Cacheable com uma chave que servirá para identificar
aquele recurso em cache (Vide TopicoController).
- Ponto de atenção: O spring é esperto o suficiente para guardar os parâmetros e os valores desses parâmetros e devolver uma busca
que já esteja armazenada em cache com tais valores. Ou seja: por trás dos panos existem múltiplos registros em cache para um mesmo endpoint
  
- É possível limpar o cache em algumas operações com:
> @CacheEvict(value = "listaDeTopicos", allEntries = true)

Onde o value representa o id do cache e o allEntries configura a limpeza de todos os endpoints armazenados naquele cacheId

### Quando usar Cache? Onde faz sentido?

Muitas operações de limpeza/invalidação de cache podem até piorar a performance. O ideal é utilizar cache em tabelas ESTÁVEIS! 
Onde raramente os dados daquela tabela serão atualizados.

## Segurança

Para ativar o módulo de segurança do Spring alguns passos podem ser seguidos:

1. Verificar a dependëncia do Spring Security no pom.xml:
>		<dependency>
>			<groupId>org.springframework.boot</groupId>
>			<artifactId>spring-boot-starter-security</artifactId>
>       </dependency>

Atente-se para qualquer tag de scope indevida caso encontre algum problema com Maven.

2. Crie sua classe de configurações de segurança, anote-a com @EnableWebSecurity e @Configuration e faça-a herdar da classe
   WebSecurityConfigurerAdapter
   
Com isso feito, chamadas aos endpoins da aplicação já devem estar bloqueadas.

### O arquivo de configurações

No arquivo SecurityConfigurations, deve-se sobrescrever 3 chamadas do método configure que são usados para as configurações do
Spring Security. No arquivo desse projeto há um comentário acima de cada uma das chamadas desse método indicando que funções eles
exercem.

- Pontos de atenção:
>   .antMatchers() é usado para definir um método HTTP, uma rota, e nele pode ser chamado o método permitAll() para tornar esse endpoint "publico".

>   .anyRequest().authenticated() também é uma combinação útil para fazer todas as outras endpoints não listados nos métodos .antMatchers serem liberadas somente
> mediante autenticação.


### A idéia de Usuário

Para usar autenticação, a classe de Usuário precisa implementar a interface UserDetails do Spring Security.
Essa classe tem alguns métodos que precisam ser sobrescritos, mas apenas alguns são realmente usados nesse projeto. São eles:
1. getPassword() -> sempre deve devolver o atributo que representa o password naquela entidade.
2. getUserName() -> no caso dessa aplicação, ela vai recuperar o email do Usuário, mas esse método sempre deve devolver o atributo que representa o "login" do usuário.
3. getAuthorities() -> deve retornar uma List com os Perfis de Usuário. Veja abaixo nesse Readme.

Todos os outros métodos de sobrescrita obrigatória são setados para retornar True por padrão.

- Classe Perfil: Essa classe precisa ser anotada com @Entity porque também terá que ser armazenada no Banco de Dados. Ela deve também
implementar a interface GrantedAuthority e sobrescrever seu método getAuthority() que deve retornar o nome daquele perfil.
  - Ponto de atenção: na classe Usuário, o atributo de perfis é anotado com @ManyToMany. Por padrão o Spring faz isso ser Lazy Load, é interessante
    lembrar de mudar isso para alguma outra coisa, como um (fetch = FetchType.EAGER) caso seja necessário, por exemplo.

### Autenticação

A autenticação nessa aplicação é feita seguindo o padrão Stateless das APIs REST.
Autenticações devem ser feitas pela aplicação clienete enviando email e senha por um método POST na rota
"/auth". Isso gera uma série de eventos do Spring Security que foram configurados nesse projeto:

### Geração de Token

Quando um usuário envia o login e senha (no caso desse projeto login é o email) pela rota de post /auth,
ele cai no AuthenticationController no método autenticar. Esse método recebe apenas 1 parâmetro:

~~~{Java}
@PostMapping
public ResponseEntity<TokenDto> autenticar(@RequestBody @Valid LoginRequest request){
    UsernamePasswordAuthenticationToken dadosLogin = request.converter();

    Authentication authentication = authManager.authenticate(dadosLogin);
    String token = tokenService.gerarToken(authentication);

    return ResponseEntity.ok(new TokenDto(token, "Bearer"));
}
~~~

Esse parâmetro também precisa, como todas as nossas classes que chegam pelo Client, ser uma espécie de Dto para Request. É necessário instanciar um objeto do tipo
UsernamePasswordAuthenticationToken com os dados de login e senha. Nesse projeto existe 
um método converter(). Tudo que esse método faz é chamar o construtor de UsernamePasswordAuthenticationToken,
que recebe como argumentos login e senha (Object principal, Object credentials).

Com isso, pode-se passar o objeto dadosLogin para o método .authenticate de um AuthenticationManager. Isso fará o Spring Security verificar se o usuário e senha recebidos pela request realmente
são válidos. Se não forem, uma AuthenticationException será disparada e tratada pelo ExceptionHandler global.
O próximo passo é gerar o token, e isso é feito por um service nesse projeto.

### TokenService

Essa é uma classe que deve ser anotada com @Service e será responsável por gerar o token propriamente dito.
Antes de tudo, ela usará dois atributos armazenados no arquivo application.properties. São eles:

~~~{Java}
@Value("${forum.jwt.expiration}")
private String expiration;

@Value("${forum.jwt.secret}")
private String secret;
~~~

A anotação @Value passando o nome do atributo do .properties injeta esses valores para a classe.

Na classe TokenService existem 3 métodos que são usados por diferentes outras classes do projeto. São elas:

- gerarToken(Authentication authentication)

a primeira coisa que acontece aqui é a recuperação do usuário que se logou:

>  Usuario logado = (Usuario) authentication.getPrincipal();

Depois, basta retornar o token buildado pelo Jwts. No caso desse projeto, usando alguns métodos:

> .setIssuer() -> Quem é a aplicação que está gerando o token? (String)

> .setSubject() -> Qual o id do usuário dono do Token? (String)

É possível recuperar o Usuário logado pelo método .getPrincipal() da classe Authentication.

> .setIssuedAt() -> Data que o token foi gerado

> .setExpiration() -> Data que o token vai expirar. É aqui que usamos aquela injeção de expiration do .properties. (Date, em mili)

> .signWith(alg, secretKey) -> é aqui que acontece a criptografia. Nessa aplicação se passa o algoritmo SignatureAlgorithm.HS256. Também se usa o secret do .properties.

por fim é só chamar o método .compact(), e o token deve ser gerado. E no caso desse projeto, pronto para uso no AuthController :)


- isTokenValid(String token)

Esse método apenas faz a verificação, chamando o próprio Jwts, da autenticidade do token recebido pela aplicação (para ver se bate com o token gerado em momento anterior
pelo método .gerarToken())

Isso é feito com a chamada de um parser passando o secret e o token.
> Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);

Importante lembrar que essa chamada, em caso de falha na validação, irá disparar exceções de diferentes tipo. Então aqui seria interessante
realmente um Try catch, dependendo do caso.

- getUsuario(String token)

Esse método usa uma chamada bem semelhante a anterior para recuperar o usuário dono do token.

### O controle Stateless de requests por token

Uma vez que a aplicação cliente já consegue acessar uma rota de autenticação, ele consegue receber um token gerado pelo tokenService
do projeto e realizar novas requests, dessa vez enviando o token como método de autenticação Bearer. Existem vários códigos necessários para que isso aconteça, 
mas de forma resumida, o caminho inteiro acontece dentro das classes do package chamado de "autenticacao" desse projeto:
1. Um filtro de requests intercepta a request antes de chegar nos controllers
2. O token que é enviado no Header da requisição é recuperado
3. O usuário é então confirmado/autenticado agora via token, se esse for válido, e seu acesso é liberado de acordo com os perfis de autoridade.


## Swagger :D

Essa aplicação faz uso do swagger para documentação. É bem simples fazer essa configuração. Bastando uma classe que também está no package de configs
e a liberação dos recursos que o swagger utiliza na classe de configurações de segurança.