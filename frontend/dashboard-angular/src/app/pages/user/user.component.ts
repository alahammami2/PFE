import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService, Utilisateur } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  profilForm: FormGroup;
  currentUser: Utilisateur | null = null;
  loading = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private toastr: ToastrService
  ) {
    this.profilForm = this.formBuilder.group({
      nom: ['', [Validators.required, Validators.maxLength(100)]],
      prenom: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      telephone: ['', [Validators.maxLength(20)]]
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.profilForm.patchValue({
        nom: this.currentUser.nom,
        prenom: this.currentUser.prenom,
        email: this.currentUser.email,
        telephone: this.currentUser.telephone || ''
      });
    }
  }

  onSubmit(): void {
    if (this.profilForm.valid && this.currentUser) {
      this.loading = true;
      
      this.authService.updateProfil(this.currentUser.id, this.profilForm.value).subscribe({
        next: (response) => {
          if (response.success) {
            // Mettre à jour l'utilisateur courant
            this.authService.setCurrentUser(response.utilisateur);
            this.currentUser = response.utilisateur;
            this.toastr.success('Profil mis à jour avec succès!', 'Succès');
          } else {
            this.toastr.error(response.message, 'Erreur');
          }
          this.loading = false;
        },
        error: (error) => {
          this.toastr.error('Erreur lors de la mise à jour du profil', 'Erreur');
          this.loading = false;
        }
      });
    }
  }

  get f() { return this.profilForm.controls; }

  getRoleBadgeClass(): string {
    if (!this.currentUser) return 'badge-secondary';
    
    switch (this.currentUser.role) {
      case 'ADMINISTRATEUR': return 'badge-danger';
      case 'COACH': return 'badge-warning';
      case 'JOUEUR': return 'badge-success';
      case 'STAFF_MEDICAL': return 'badge-info';
      case 'RESPONSABLE_FINANCIER': return 'badge-primary';
      default: return 'badge-secondary';
    }
  }
}

