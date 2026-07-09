@login
Feature: Autenticación de usuarios

  Como usuario de PeriBook
  Quiero iniciar sesión con mi email y contraseña
  Para acceder a mi feed personalizado

  @happy-path
  Scenario: Login exitoso con credenciales válidas
    Given que el usuario "ana@peribook.com" existe en el sistema
    When intento iniciar sesión con email "ana@peribook.com" y contraseña "secreto123"
    Then el servicio responde con código 200
    And la respuesta contiene un token JWT válido
    And la respuesta contiene el alias "ana_writer"
    And la respuesta contiene el userId del usuario

  @error
  Scenario: Login fallido con contraseña incorrecta
    Given que el usuario "ana@peribook.com" existe en el sistema
    When intento iniciar sesión con email "ana@peribook.com" y contraseña "wrong-password"
    Then el servicio responde con código 401
    And el mensaje de error es "Credenciales inválidas"

  @error
  Scenario: Login fallido con email que no existe
    When intento iniciar sesión con email "noexiste@peribook.com" y contraseña "cualquiera"
    Then el servicio responde con código 401

  @validation
  Scenario: Login con email en formato inválido
    When intento iniciar sesión con email "no-es-un-email" y contraseña "secreto123"
    Then el servicio responde con código 400
<!-- 2026-07-09 -->
