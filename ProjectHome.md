## Início rápido ##
Aqui o guia rápidos para os apressados. Não esqueça de ler o resto da página e entender por quê o SpecRunner (resumidamente SR) pode fazer por você, lembrando que esse texto é só o começo, há um mundo de possibilidades mostrado rapidamente através dos exemplos abaixo.

Para baixar todo o código via SVN veja https://code.google.com/p/specrunner/source/checkout.

### Aprendendo através dos exemplos ###
Veja a apresentação SpecRunner.pptx, e baixe o pacote de exemplos specrunner-userguide-XXX-clean.zip mais recente de (https://code.google.com/p/specrunner/source/browse/trunk/userguide/downloads/). Descompacte todos os arquivos, e importe-os no Eclipse (Import -> Maven Projects...) . Sugiro usar o Eclipse Kepler (mais novo que já tem o plugin Maven integrado).

### Iniciando do zero ###
Crie um projeto Maven, e adicione os módulos que desejar:
```
<dependency>
    <groupId>org.specrunner</groupId>
    <artifactId>specrunner-core</artifactId>
    <version>VERSAO</version>
</dependency>
```
... coloque os demais módulos que desejar.

Crie uma classe de teste e coloque para rodar com o SRRunner.class:
```
    @RunWith(SRRunner.class)
    public class TestCobaia {
    }
```

Crie um arquivo .HTML na mesma pasta do TestCobaia.java, ele pode se chamar: TestCobaia.html,TestCobaia.htm, Cobaia.html ou Cobaia.htm.

Obs: Caso você opte por escrever os testes em arquivo plano, ele será TestCobaia.txt, TestCobaia.feature, Cobaia.txt ou Cobaia.feature. Da mesma forma, se optar por Excel, ele será TestCobaia.xls, TestCobaia.xlsx, Cobaia.xls ou Cobaia.xlsx.

Agora é só mandar rodar como test JUnit.

# SpecRunner #
é uma ferramenta de automação de testes que acredita que o formato da especificação não deve ser fixo durante o projeto, ou precisa ser escolhido previamente. **SpecRunner** é a junção das palavras _Specification_, 'especificação' em inglês, com _Runner_, 'executor'.

Com **SpecRunner** os estilos de escrita de testes de aceitação não precisam ser fixos.

### O que se quer dizer por **formato da especificação**? ###
Isto significa que **SpecRunner** lhe permitirá escrever testes de aceitação usando uma linguagem mais natural (como Concordion), uma linguagem de scripts - passo-a-passo - (como      Robotframework ou Fit/Fitness), testes baseados em exemplo/comportamento (como JBehave ou Cucumber), ou qualquer estilo de especificação que você quiser(veja Estilos), todos serão automatizáveis.

**SpecRunner** não leva em consideração o formato da escrita, o que conta é a instrumentação da especificação; que entre outras dezenas de funcionalidades do **SpecRunner** pode ser feita de forma automática.

### **PERGUNTA**: por que eu tenho que escolher o formato da especificação previamente? ###
A resposta razoável é: **VOCÊ NÃO TEM QUE FAZER ISSO!!!** apenas especifique.

## Funcionalidades: ##
  1. **Estilo livre de especificação**: escreva, depois instrumente (ou não);
  1. **Inclusão automática de arquivos**: se um arquivo faz uma referência a outro ele pode ser automaticamente incluído, ou você pode escolher apenas aquele você quiser. Funciona como uma expansão macro, o conteúdo é enxertado automaticamente na especificação ponto de partida, de forma recursiva e livre de referência cíclica. A inclusão automática de arquivos pode também chamar arquivos Excel e texto, desde que sejam adicionados os módulos (specrunner-core-excel e specrunner-core-text);
  1. **Relatório de erros completo**: além de apontar os erros como os concorrentes, um índice de erros é criado permitindo ao desenvolvedor a rápida identificação dos pontos de falha, bem como a apresentação dos erros pode ser feita de forma customizada pelo desenvolvedor através da implementação da interface `IPresentation`, como é o caso de comparação de Strings que mostra detalhadamente o que está diferente;
  1. **Preservação de estilo e imagens no relatório de resultado**: o resultado da execução de testes mantem a formatação original do arquivo incluindo suas imagens e CSSs;
  1. **Níveis de relatório diferenciados**: ao ajustar o log do SpecRunner você também está definindo o nível de detalhamento do relatório de saída, no modo DEBUG o relatório de erros inclui estilos que tornam mais evidentes as instrumentações realizadas, bem como a pilha de erros completa é mostrada no console;
  1. **Extensibilidade**: você pode adicionar incrementalmente perfis que servem para realizar testes específicos da sua aplicação ou reusá-los. Por exemplo, a realização de testes em interfaces Swing seria um perfil interessante para inclusão no SpecRunner;
  1. **Modos de execução**: diferentes modos de execução podem ser selecionados programaticamente, por exemplo, os perfis SpecRunnerHtmlUnit e SpecRunnerWebDriver podem tirar screenshots das aplicações sob teste, ou apenas em caso de erro para a aumentar a performance na realização dos testes. Existem duas formas de ajustes de parâmetros, de forma local ao teste ou de forma generalizada para todos os testes;
  1. **Ações pré-definidas**: nas especificações SpecRunner é possível realizar, por exemplo,  declaração de variáveis locais ou globais, definição e uso de macros, fluxos de controle condicional e iterações. Isso quer dizer que, por exemplo, se você quer dizer que um conjunto é composto de mil registros você não precisa escrever os mil registros na especificação, usando um iteração você pode criá-lo automaticamente;
  1. **Uso de código Java direto na especificação**: em qualquer lugar na especificação é possível usar expressões Java como `${System.currentTimeMilis()}` ou `${new java.util.Date()}`. Isso mesmo, a especificação é dinâmica, sendo gerada sob demanda no momento da execução;
  1. **Reuso de objetos entre diferentes testes**: A API já possui suporte para reuso de objetos entre diferentes especificações, por exemplo, se você especifica em uma arquivo que o Jetty será ligado usando o arquivo de configuração `/jetty.xml`, se você tem outro teste que também faz isso, o SpecRunner pode simplesmente reaproveitar o Jetty já criado maximizando a velocidade de execução do teste e também economizando recursos computacionais;
  1. **Liberação automática de recursos reutilizados**: uma vez que o recurso é reutilizável você não precisa se preocupar com a finalização dele, ao término do teste o SpecRunner automaticamente libera esses recursos para você, por exemplo, uma conexão com banco de dados reutilizada vai ser fechada quando o teste terminar, sem necessidade de uma linha extra de código;
  1. **Suporte a execução paralela**: Toda a API é Thread-safe, ou seja você pode executar testes de forma paralela sem risco de interferência mútua;
  1. **Modelos de eventos integrado**: É possível monitorar cada passo da execução das específicações através de listeners. Por exemplo, para realizar uma pausa em caso de falha de um passo do teste, um listener `FailurePausePluginListener` foi implementado;
  1. **Interrupção automática do testes em caso de erro**: é possível informar ao SpecRunner para realizar uma pausa no momento que uma falha na especificação ocorre. Para entender melhor quão útil é isso, imagine o cenário onde o teste de uma aplicação web é realizado, uma especificação pode simplesmente ser interrompida no momento exato da falha, ou seja, o browser irá realizar todas as ações desejadas da aplicação deixando-o pronto para você realizar o debug naquele momento onde a falha ocorreu.
  1. **Criação de objetos de negócio integrada**: permite criar objetos Java a partir da especificação realizada, criando e gerenciando os objetos usando Reflection, é o ponto de partida para manipulação de objetos em memória ou extensões como o módulo Hibernate3

## Perfis disponíveis: ##
Por perfil chamamos um conjunto de plugins/fixtures que realizam testes sob um aspecto da aplicação, por exemplo banco de dados, clientes web, etc e para o teste de uma aplicação é possível selecionar os perfis de interesse. Atualmente o conjunto disponível é:
  * **specrunner-core-text**: permite a leitura de arquivos planos no estilo de Cucumber/JBehave;
  * **specrunner-core-excel**: permite a inclusão transparente de recursos em arquivos Excel, como por exemplo, dados para a preparação de cenários de teste;
  * **specrunner-core-spring**: extensão para permitir maior integração com as ferramentas de teste do Spring, possui, por exemplo, um executor JUnit chamado SRRunnerSpring;
  * **specrunner-concordion-emulator**: permite que arquivos Concordion seja lidos pela ferramenta, traduzindo os comandos do Concordion em comandos interpretáveis pelo SpecRunner (que são mais completos);
  * **specrunner-ant**: permite chamar o Ant para realizar tarefas dentro do teste;
  * **specrunner-sql**: é um conjunto de plugins que permite realizar conexões em bancos de dados e executar scripts SQL sobre essas conexões;
  * **specrunner-hibernate3**: um conjunto de plugins que permite salvar e verificar se objetos estão na base de dados usando mapeamentos Hibernate3;
  * **specrunner-hibernate4**: um conjunto de plugins que permite salvar e verificar se objetos estão na base de dados usando mapeamentos Hibernate4;
  * **specrunner-jpa**: realiza persistência usando JPA (incipiente);
  * **specrunner-webdriver**: um conjunto de plugins que permite criar instancias WebDriver e interagir com elas a partir da especificação, por exemplo, você pode abrir um Chrome e dizer para interagir com, ou verificar, os campos em tela, ou mesmo fazer Drag and Drop no browser;
  * **specrunner-htmlunit**: um conjunto de plugins que permite interagir com um cliente HtmlUnit, que é um navegador 'headless'. A interação com HtmlUnit também é possível usando-se o plugin SpecRunnerWebDriver, lá o HtmlUnit é o driver padrão;
  * **specrunner-jetty**: permite ligar e desligar um Jetty durante a realização do teste;
  * **specrunner-tomcat**: permite ligar e desligar um Tomcat durante a realização do teste;

Com esse conjunto de perfis, por exemplo, você pode:
  1. Ligar sua aplicação Web;
  1. Preencher o banco de dados com tudo que você precisa;
  1. Interagir com a aplicação sob teste, realizando ações ou verificações;
  1. Verificar tanto o comportamento em tela quanto o resultado final no banco de dados.

## Novidades: ##
...

**Obs: English version will be soon available. All Javadoc is already in English.**