import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import LoginComponent from '../login/login.component';
import { LeaveRequestComponent } from '../entities/leave-request/list/leave-request.component';

@Component({
  standalone: true,
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [SharedModule, RouterModule, LoginComponent, HasAnyAuthorityDirective, LeaveRequestComponent],
})
export default class HomeComponent implements OnInit, OnDestroy {
  account = signal<Account | null>(null);

  private readonly destroy$ = new Subject<void>();

  private accountService = inject(AccountService);
  private router = inject(Router);
  showLoginMessage: boolean = true;
  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {
        this.account.set(account);
        if (account) {
          this.hideMessageAfterDelay(); // Ajout de l'appel ici
        }
      });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  hideMessageAfterDelay(): void {
    setTimeout(() => {
      const alertElement = document.querySelector('.alert');
      if (alertElement) {
        alertElement.classList.add('hidden');
      }
      setTimeout(() => {
        this.showLoginMessage = false;
      }, 300); // Temps pour l'animation CSS (doit correspondre à la durée de transition dans le CSS)
    }, 5000); // Temps en millisecondes avant de masquer le message (ici 5000ms = 5 secondes)
  }
}
