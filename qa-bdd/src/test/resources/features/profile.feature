@perfil
Feature: Perfil de usuario

  Como usuario autenticado
  Quiero consultar mi perfil
  Para ver mi información personal

  @happy-path
  Scenario: Obtener perfil propio
    Given que estoy autenticado como "ana@peribook.com"
    When consulto mi perfil
    Then el servicio responde con código 200
    And el alias es "ana_writer"
    And los nombres y apellidos están presentes

  @error
  Scenario: Consultar perfil inexistente
    Given que estoy autenticado como "ana@peribook.com"
    When consulto el perfil con ID "00000000-0000-0000-0000-000000000000"
    Then el servicio responde con código 404
<!-- 2026-07-09 -->
