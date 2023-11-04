import {inject, Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from "@angular/router";
import {AuthenticationResponse} from "../../models/authentication-response";
import {JwtHelperService} from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})
export class AccessGuardService {
  constructor(
    private router: Router
  ) { }

  canActivate(): boolean {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      // Convert to JSON object
      const authResponse: AuthenticationResponse = JSON.parse(storedUser);
      const token = authResponse.token;
      if (token) {
        // Check if token is not expired
        const jwtHelper = new JwtHelperService();
        const isTokenNonExpired = !jwtHelper.isTokenExpired(token);
        if(isTokenNonExpired) {
          return true;
        }
      }
    }
    // redirect to login page
    this.router.navigate(['login']);
    return false;
  }
  canMatch(): boolean {
    return true;
  }
}

export const canActivateCustomers: CanActivateFn =
  (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    return inject(AccessGuardService).canActivate();
  };
