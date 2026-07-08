export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: string;
  alias: string;
}

export interface FeedItem {
  publicacionId: string;
  autorId: string;
  contenido: string;
  creadaEn: string;
  autorAlias: string;
  totalLikes: number;
}

export interface PerfilResponse {
  id: string;
  alias: string;
  nombres: string;
  apellidos: string;
  fechaNacimiento: string | null;
}

export interface PublicacionCreadaEvent {
  publicacionId: string;
  autorId: string;
  contenido: string;
  creadaEn: string;
}

export interface LikeRegistradoEvent {
  likeId: string;
  publicacionId: string;
  usuarioId: string;
  creadoEn: string;
}
