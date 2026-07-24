@likes-tiempo-real
Feature: Likes en tiempo real

  Como usuario de PeriBook
  Quiero que los likes se reflejen en tiempo real
  Para ver la interacción con mis publicaciones sin recargar la página

  @happy-path
  Scenario: Dar like a una publicación
    Given que estoy autenticado como "ana@peribook.com"
    And existe una publicación con ID conocido
    When doy like a la publicación
    Then el servicio responde con código 201 o 200
    And la respuesta indica si el like es nuevo

  @idempotency
  Scenario: Like duplicado es idempotente
    Given que estoy autenticado como "ana@peribook.com"
    And existe una publicación con ID conocido
    And ya di like a una publicación
    When vuelvo a dar like a la misma publicación
    Then el servicio responde con código 200
    And la respuesta indica que el like NO es nuevo
    And el contador de likes no se incrementa

  @websocket @manual
  Scenario: El evento de like se recibe por WebSocket
    Given que estoy autenticado como "carlos@peribook.com"
    And estoy conectado al WebSocket
    And estoy suscrito al canal "/topic/publicacion.{id}.likes"
    When "ana@peribook.com" da like a mi publicación
    Then recibo un evento LikeRegistrado por WebSocket en menos de 2 segundos
    And el evento contiene el publicacionId correcto

  @websocket @two-tabs @manual
  Scenario: Dos pestañas — el contador se actualiza en ambas
    Given que tengo dos pestañas abiertas con el feed
    And en la pestaña 1 estoy autenticado como "ana@peribook.com"
    And en la pestaña 2 estoy autenticado como "carlos@peribook.com"
    When "carlos@peribook.com" da like a una publicación de "ana@peribook.com" desde la pestaña 2
    Then en la pestaña 1 el contador de likes se incrementa automáticamente sin recargar
