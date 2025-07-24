import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { ToastService } from '../services/toast.service';
@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  standalone: true,
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  formLogin!: FormGroup;
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toast: ToastService,
  ) {}

  ngOnInit(): void {
    this.formLogin = this.fb.group({
      username: this.fb.control(''),
      password: this.fb.control(''),
    });
  }

  handleLogin() {
    let username = this.formLogin.value.username;
    let password = this.formLogin.value.password;
    this.authService.login(username, password).subscribe({
      next: (data) => {
        this.authService.loadProfile(data);
        this.router.navigateByUrl('/admin/dashboard');
      },
      error: (err) => {
        this.toast.showError(err.error || 'Username or password incorrect');
      },
    });
  }
}
