# Cenários de testes service:

## "get/{id}"
   
### Request:
PathVariable: {id}

### Response:
   1. succes:

        STATUS: 200
        
        BODY: RegistrationDtoResponse
   2. not found:

      STATUS: 404
   
      EXCEPTION: RegistrationNotFoundException

      BODY: "Não foi possível encontrar o registro com o id informado."
   3. internal server error:
      STATUS: 500

      EXCEPTION: Qualquer Exception exceto a RegistrationNotFoundException

      BODY: Vazio 
## "get/{email}"

### Request:
PathVariable: {Email}

### Response:
   1. succes:

      STATUS: 200

      BODY: Page<RegistrationDtoResponse>
   2. internal server error:

      STATUS: 500

      EXCEPTION: Qualquer Exception.

      BODY: Vazio

## "/get"

### Request:
   Vazio.

### Response
1. succes:

   STATUS: 200

   BODY: Page<RegistrationDtoResponse>
2. internal server error:

   STATUS: 500

   EXCEPTION: Qualquer Exception.

   BODY: Vazio

## "/post"

### Request:
   Body: RegistrationDtoRequest

### Response:
   1. created:

      STATUS: 201

      BODY: RegistrationDTOResponse
   2. bad request:

       STATUS: 400

       EXCEPTION: EmailAlreadyExistsException

       BODY: "Já existe um usuário cadastrado com esse email."
   3. bad request:

       STATUS: 400

       EXCEPTION: Validation Constraint

       BODY: Validation constraint message
   4. bad request:

      STATUS: 400

      EXCEPTION: Validation Constraint

      BODY: "Validation constraint message
   5. internal server error:
      STATUS: 500

      EXCEPTION: Qualquer Exception exceto as acima

      BODY: Vazio

## "put/{registration}"

## Request:
   PathVariable : Registration
   Body: RegistrationDtoRequest

## Response:
  1. created (se o registration não existir):

     STATUS: 201

     BODY: RegistrationDTOResponse
  2.ok (se o registration existir):

     STATUS: 200

     BODY: RegistrationDTOResponse
  2. bad request:

     STATUS: 400

     EXCEPTION: RegistrationAlreadyExistsException

     BODY: "Já existe um registro cadastrado com esses dados."
  3. bad request:

        STATUS: 400

        EXCEPTION: EmptyEmailException

        BODY: "O campo email é obrigatório."
  4. bad request:

        STATUS: 400

        EXCEPTION: EmptyNameException

        BODY: "O campo nome é obrigatório."
  5. internal server error:
        STATUS: 500

        EXCEPTION: Qualquer Exception exceto as acima

        BODY: Vazio

## "/delete/{registration}"

## Request:
   PathVariable: Registration

## Response:

 1. no content
   
    STATUS: 204

    BODY: Vazio
 2. bad request:

    STATUS: 400

    EXCEPTION: IllegalArgumentException

    BODY: "Registro ou registro_id não podem ser nulos!!"
 3. not found:

    STATUS: 404

    EXCEPTION: RegistrationNotFoundException

    BODY: Registro não encontrado!"
 4. internal server error:
   
    STATUS: 500

    EXCEPTION: RegistrationFoundButNotDeletedException

    BODY: "Não foi possível excluir o registro!"

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.5/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.5/gradle-plugin/reference/html/#build-image)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.6.5/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.5/reference/htmlsingle/#boot-features-developing-web-applications)
* [Validation](https://docs.spring.io/spring-boot/docs/2.6.5/reference/htmlsingle/#boot-features-validation)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

