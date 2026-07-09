@feed
Feature: Feed de publicaciones

  Como usuario autenticado
  Quiero ver las publicaciones recientes y crear nuevas
  Para interactuar con mi comunidad

  @happy-path
  Scenario: Obtener feed enriquecido
    Given que estoy autenticado como "ana@peribook.com"
    When consulto el feed con límite 20
    Then el servicio responde con código 200
    And la respuesta es un array de publicaciones
    And cada publicación tiene publicacionId, contenido, autorAlias y totalLikes

  @happy-path
  Scenario: Crear una nueva publicación
    Given que estoy autenticado como "ana@peribook.com"
    When creo una publicación con contenido "Mi primera publicación en PeriBook"
    Then el servicio responde con código 201
    And la respuesta contiene el contenido "Mi primera publicación en PeriBook"
    And la respuesta contiene un publicacionId

  @validation
  Scenario: Crear publicación con contenido vacío
    Given que estoy autenticado como "ana@peribook.com"
    When creo una publicación con contenido ""
    Then el servicio responde con código 400

  @validation
  Scenario: Crear publicación excediendo 500 caracteres
    Given que estoy autenticado como "ana@peribook.com"
    When creo una publicación con 501 caracteres
    Then el servicio responde con código 400

  @security
  Scenario: Consultar feed sin autenticación
    When consulto el feed sin token
    Then el servicio responde con código 401
