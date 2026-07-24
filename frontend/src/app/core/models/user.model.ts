/**
 * DTO para la petición de inicio de sesión.
 * Enviado al endpoint POST /api/auth/login del backend.
 *
 * @author Alexander Rubio Cáceres
 */
export interface LoginRequest {
  /** Correo electrónico del usuario. */
  email: string;
  /** Contraseña en texto plano — viaja por HTTPS, nunca por HTTP. */
  password: string;
}

/**
 * DTO con la respuesta exitosa del login.
 * El backend devuelve un token JWT, el identificador del usuario y su alias
 * público. Decidí incluir el alias aquí mismo en lugar de hacer una
 * llamada adicional al perfil para evitar una petición extra justo después
 * del login.
 *
 * @author Alexander Rubio Cáceres
 */
export interface LoginResponse {
  /** Token JWT para autenticar las siguientes peticiones. */
  token: string;
  /** Identificador único del usuario en el sistema. */
  userId: string;
  /** Alias público que se muestra en la barra de navegación y publicaciones. */
  alias: string;
}

/**
 * Modelo que representa una publicación dentro del feed.
 * Cada item del feed contiene la información necesaria para renderizarlo
 * sin tener que hacer joins adicionales desde el frontend (el alias del
 * autor ya viene incluido).
 *
 * @author Alexander Rubio Cáceres
 */
export interface FeedItem {
  /** Identificador único de la publicación. */
  publicacionId: string;
  /** ID del usuario que creó la publicación. */
  autorId: string;
  /** Contenido textual de la publicación. */
  contenido: string;
  /** Fecha de creación en formato ISO 8601. */
  creadaEn: string;
  /** Alias del autor — incluido aquí para evitar otra llamada a la API. */
  autorAlias: string;
  /** Número total de likes que tiene la publicación. */
  totalLikes: number;
}

/**
 * DTO con los datos del perfil de un usuario.
 * Se obtiene desde el endpoint /api/users/{userId}.
 *
 * @author Alexander Rubio Cáceres
 */
export interface PerfilResponse {
  /** Identificador único del usuario. */
  id: string;
  /** Alias público visible para otros usuarios. */
  alias: string;
  /** Nombres del usuario (información privada). */
  nombres: string;
  /** Apellidos del usuario (información privada). */
  apellidos: string;
  /** Fecha de nacimiento en formato ISO 8601 o null si no se registró. */
  fechaNacimiento: string | null;
}

/**
 * Evento enviado por WebSocket cuando se crea una nueva publicación.
 * Se usa para propagar en tiempo real la publicación a todos los
 * suscriptores del topic /topic/feed.
 *
 * @author Alexander Rubio Cáceres
 */
export interface PublicacionCreadaEvent {
  /** Identificador de la nueva publicación. */
  publicacionId: string;
  /** ID del autor que la creó. */
  autorId: string;
  /** Contenido de la publicación. */
  contenido: string;
  /** Marca de tiempo de creación en ISO 8601. */
  creadaEn: string;
}

/**
 * Evento enviado por WebSocket cuando un usuario da like a una publicación.
 * Permite actualizar el contador de likes en la UI sin recargar la página.
 *
 * @author Alexander Rubio Cáceres
 */
export interface LikeRegistradoEvent {
  /** Identificador único del like. */
  likeId: string;
  /** Publicación que recibió el like. */
  publicacionId: string;
  /** Usuario que dio el like. */
  usuarioId: string;
  /** Marca de tiempo del like en ISO 8601. */
  creadoEn: string;
}
