import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Entrainement {
  id?: number;
  titre: string;
  description: string;
  date: string;
  heureDebut: string;
  heureFin: string;
  lieu: string;
  type: 'TECHNIQUE' | 'PHYSIQUE' | 'TACTIQUE' | 'MENTAL' | 'MATCH';
  intensite: number;
  statut: 'PLANIFIE' | 'EN_COURS' | 'TERMINE' | 'ANNULE';
  objectifs: string[];
  equipementNecessaire: string[];
  nombreMaxParticipants: number;
  coachId?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Participation {
  id?: number;
  entrainementId: number;
  joueurId: number;
  statut: 'PRESENT' | 'ABSENT' | 'RETARD' | 'EXCUSE';
  heureArrivee?: string;
  heureDepart?: string;
  commentaire?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Performance {
  id?: number;
  entrainementId: number;
  joueurId: number;
  evaluations: {
    technique: number;
    physique: number;
    tactique: number;
    mental: number;
  };
  commentaires: string;
  objectifsAtteints: string[];
  pointsAmeliorer: string[];
  noteGlobale: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Objectif {
  id?: number;
  joueurId: number;
  titre: string;
  description: string;
  type: 'TECHNIQUE' | 'PHYSIQUE' | 'TACTIQUE' | 'MENTAL';
  priorite: 'BASSE' | 'MOYENNE' | 'HAUTE' | 'CRITIQUE';
  statut: 'EN_COURS' | 'ATTEINT' | 'ABANDONNE' | 'REPORTE';
  dateCreation: string;
  dateEcheance: string;
  progression: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Statistiques {
  joueurId: number;
  periode: {
    debut: string;
    fin: string;
  };
  entrainements: {
    total: number;
    presents: number;
    absents: number;
    retards: number;
    tauxPresence: number;
  };
  performances: {
    moyenneTechnique: number;
    moyennePhysique: number;
    moyenneTactique: number;
    moyenneMental: number;
    moyenneGlobale: number;
    evolution: {
      technique: number;
      physique: number;
      tactique: number;
      mental: number;
    };
  };
  objectifs: {
    total: number;
    atteints: number;
    enCours: number;
    abandonnes: number;
    tauxReussite: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class PlanningPerformanceService {
  private readonly baseUrl = `${environment.authServiceUrl.replace('/auth-user-service', '')}/planning-performance-service/api`;

  constructor(private http: HttpClient) {}

  // ===== ENTRAINEMENTS =====
  
  getEntrainements(params?: {
    page?: number;
    size?: number;
    sort?: string;
    type?: string;
    dateDebut?: string;
    dateFin?: string;
  }): Observable<any> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(key => {
        const value = (params as any)[key];
        if (value !== undefined && value !== null) {
          httpParams = httpParams.set(key, value.toString());
        }
      });
    }
    return this.http.get(`${this.baseUrl}/entrainements`, { params: httpParams });
  }

  getEntrainement(id: number): Observable<Entrainement> {
    return this.http.get<Entrainement>(`${this.baseUrl}/entrainements/${id}`);
  }

  createEntrainement(entrainement: Entrainement): Observable<Entrainement> {
    return this.http.post<Entrainement>(`${this.baseUrl}/entrainements`, entrainement);
  }

  updateEntrainement(id: number, entrainement: Entrainement): Observable<Entrainement> {
    return this.http.put<Entrainement>(`${this.baseUrl}/entrainements/${id}`, entrainement);
  }

  deleteEntrainement(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/entrainements/${id}`);
  }

  // ===== PARTICIPATIONS =====
  
  getParticipations(entrainementId: number): Observable<Participation[]> {
    return this.http.get<Participation[]>(`${this.baseUrl}/entrainements/${entrainementId}/participations`);
  }

  updateParticipation(entrainementId: number, joueurId: number, participation: Participation): Observable<Participation> {
    return this.http.put<Participation>(`${this.baseUrl}/entrainements/${entrainementId}/participations/${joueurId}`, participation);
  }

  // ===== PERFORMANCES =====
  
  getPerformances(entrainementId: number): Observable<Performance[]> {
    return this.http.get<Performance[]>(`${this.baseUrl}/entrainements/${entrainementId}/performances`);
  }

  createPerformance(performance: Performance): Observable<Performance> {
    return this.http.post<Performance>(`${this.baseUrl}/performances`, performance);
  }

  updatePerformance(id: number, performance: Performance): Observable<Performance> {
    return this.http.put<Performance>(`${this.baseUrl}/performances/${id}`, performance);
  }

  // ===== OBJECTIFS =====
  
  getObjectifs(joueurId?: number): Observable<Objectif[]> {
    const url = joueurId ? `${this.baseUrl}/objectifs?joueurId=${joueurId}` : `${this.baseUrl}/objectifs`;
    return this.http.get<Objectif[]>(url);
  }

  createObjectif(objectif: Objectif): Observable<Objectif> {
    return this.http.post<Objectif>(`${this.baseUrl}/objectifs`, objectif);
  }

  updateObjectif(id: number, objectif: Objectif): Observable<Objectif> {
    return this.http.put<Objectif>(`${this.baseUrl}/objectifs/${id}`, objectif);
  }

  deleteObjectif(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/objectifs/${id}`);
  }

  // ===== STATISTIQUES =====
  
  getStatistiques(joueurId: number, dateDebut: string, dateFin: string): Observable<Statistiques> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    return this.http.get<Statistiques>(`${this.baseUrl}/statistiques/joueur/${joueurId}`, { params });
  }

  getStatistiquesEquipe(dateDebut: string, dateFin: string): Observable<any> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    return this.http.get(`${this.baseUrl}/statistiques/equipe`, { params });
  }
}
