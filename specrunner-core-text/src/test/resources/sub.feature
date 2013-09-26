# language: pt-BR

Funcionalidade: Subtrair
    Como usuário
    Quero que minha calculadora saiba subtrair
    Para eu saber quanto sobra depois de pagar as contas.

  Cenário: Subtração simples
    Dado que eu digito 10
    e depois eu informo 7
    Quando eu escolho -
    Então o resultado é 3

  Cenário: Subtração de negativo
    Dado que eu digito 10
    e depois eu coloco -7
    Quando eu seleciono -
    Então aparece 17.
    