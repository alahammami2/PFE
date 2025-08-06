/**
 * Modèle pour les réponses paginées
 */
export interface PaginatedResponse<T> {
  content: T[];
  pageable: Pageable;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  size: number;
  number: number;
  sort: Sort;
  empty: boolean;
}

/**
 * Informations de pagination
 */
export interface Pageable {
  sort: Sort;
  pageNumber: number;
  pageSize: number;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

/**
 * Informations de tri
 */
export interface Sort {
  sorted: boolean;
  unsorted: boolean;
  empty: boolean;
}

/**
 * Paramètres de pagination pour les requêtes
 */
export interface PaginationParams {
  page: number;
  size: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

/**
 * Métadonnées de pagination pour l'affichage
 */
export interface PaginationMetadata {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
  isFirst: boolean;
  isLast: boolean;
}
