POST http://localhost:8080/pessoas
{
    "name": "Teste",
    "nickname": "testinho",
    "birthdate": "2020-01-01",
    "stack": ["C#", "JS"]
}

HTTP 201
[Captures]
id: body

GET http://localhost:8080/pessoas/{{id}}

HTTP 200

GET http://localhost:8080/contagem-pessoas

HTTP 200