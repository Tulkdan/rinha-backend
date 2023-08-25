POST http://localhost:9999/pessoas
{
    "name": "Teste",
    "nickname": "testinho",
    "birthdate": "2020-01-01",
    "stack": ["C#", "JS"]
}

HTTP 201
[Captures]
id: body

GET http://localhost:9999/pessoas/{{id}}

HTTP 200

GET http://localhost:9999/pessoas?t=este

HTTP 200

GET http://localhost:9999/pessoas?t=C

HTTP 200